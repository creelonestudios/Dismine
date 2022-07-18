package de.creelone.dismine;

import de.creelone.dismine.Dismine.MessageSource;
import de.creelone.dismine.cmds.SyncCommand;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.logging.Level;

public class DiscordStuff implements Runnable {

	enum MessageType {
		CHAT ("",   ":"),
		ME   ("* ", ""),
		SAY  ("[",  "]"),
		OTHER("**", "**");

		final String pre;
		final String suf;

		MessageType(String pre, String suf) {
			this.pre = pre;
			this.suf = suf;
		}
	}
	private static GatewayDiscordClient gateway;
	private static boolean ready = false;
	public final static ArrayList<String> msgQueue;
	public final static ArrayList<String> logQueue;

	static {
		msgQueue = new ArrayList<>();
		logQueue = new ArrayList<>();
	}

	private DiscordStuff() {

	}

	public static void login() {
		if (ready) return;
		new Thread(new DiscordStuff(), "DiscordStuff").start();
	}

	@Override
	public void run() {
		Dismine.instance.getServer().getLogger().log(Level.INFO, "[DismineBridge] Logging in...");
		var client = DiscordClient.create(Config.DC_TOKEN);
		var bootstrap = client.gateway();
		bootstrap.setEnabledIntents(IntentSet.of(Intent.GUILD_MEMBERS, Intent.GUILD_MESSAGES));
		gateway = bootstrap.login().block();

		try {
			var onReady = gateway.on(ReadyEvent.class, event -> Mono.fromRunnable(() -> {
				final User self = event.getSelf();
				Dismine.instance.getServer().getLogger().log(Level.FINE, String.format("[DismineBridge] Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));

				ready = true;
				purgeLogQueue();
				purgeMsgQueue();
			})).subscribe();
			var onMessageCreate = gateway.on(MessageCreateEvent.class, event -> {
				Message message = event.getMessage();
				if (!message.getAuthor().isPresent()) return Mono.empty();
				User author = message.getAuthor().get();
				if (author.isBot()) return Mono.empty();

				//Dismine.instance.getLogger().log(Level.INFO, String.format("%s vs. %s, %s", message.getChannelId().asString(), Config.DC_CHANNEL_CHAT.asString(), Config.DC_CHANNEL_CONSOLE.asString()));
				if (message.getChannelId().asString().equals(Config.DC_CHANNEL_CHAT.asString())) onMsg(message, author);
				else if (message.getChannelId().asString().equals(Config.DC_CHANNEL_CONSOLE.asString())) onConsoleInput(message, author);
				return Mono.empty();
			}).subscribe();
			var onBtnInteraction = gateway.on(ButtonInteractionEvent.class, event -> {
				if(event.getCustomId().startsWith("sync") || event.getCustomId().startsWith("notme")) {
					SyncCommand.button(event);
				}
				return Mono.empty();
			}).subscribe();
		} catch (NullPointerException ignore) {}
	}

	private static void onMsg(Message message, User author) {
		String content = message.getContent();
		String authortag = author.getTag();
		Member member = message.getAuthorAsMember().block();
		String name = "";
		try {
			name = member.getMemberData().nick().get().get();
		} catch (NoSuchElementException e) {
			name = author.getUsername();
		} catch (NullPointerException ignore) {}
		int accentColor;
		try {
			var c = author.getAccentColor().get();
			accentColor = c.getRGB();
		} catch (NoSuchElementException e) {
			accentColor = 0xffffff;
		}
		var color = member.asFullMember().block().getColor().block().getRGB();
		if (color == 0x000000) color = accentColor;

		var comp = Component.text(name).color(TextColor.color(color));

		Dismine.instance.getServer().sendMessage(Dismine.instance.createChatMsg(MessageSource.DISCORD, Dismine.getIdentityByDcid(author.getId()), comp, content));
	}

	private static void onConsoleInput(Message message, User author) {
		String content = message.getContent();
		String authorid = author.getId().asString();
		Dismine.instance.getLogger().log(Level.INFO, "> " + content);
		/*Dismine.instance.getLogger().log(Level.INFO, Config.DC_OPERATORS.toString());
		Dismine.instance.getLogger().log(Level.INFO, authorid);*/
		if (!Config.DC_OPERATORS.contains(authorid)) return;

		Dismine.queueCommand(content);
	}

	public static void sendMessage(String content) {
		if (!ready) {
			msgQueue.add(content);
			return;
		}
		content = MsgConverter.convertToDC(content);
		Channel channel = gateway.getChannelById(Config.DC_CHANNEL_CHAT).block();
		channel.getRestChannel().createMessage(content).block();
	}

	public static void sendMessage(String format, Object ...args) {
		sendMessage(String.format(format, args));
	}

	public static void sendMessage(String icon, String name, MessageType type, String format, Object... args) {
		sendMessage("%s %s%s%s %s", icon, type.pre, name, type.suf, String.format(format, args));
	}

	public static void sendMessage(String icon, Identity identity, MessageType type, String format, Object... args) {
		var prefix = identity.getTeamPrefixString().replaceAll("§.","");
		var suffix = identity.getTeamSuffixString().replaceAll("§.","");
		var name = identity.getPlayerName();

		sendMessage(icon, prefix + name + suffix, type, String.format(format, args));
	}

	public static void sendMessage(MessageSource src, Identity identity, MessageType type, String format, Object... args) {
		sendMessage(src.dcicon, identity, type, String.format(format, args));
	}

	public static void sendLogMessage(String content) {
		if (!ready) {
			logQueue.add(content);
			return;
		}
		content = content.replace("\\","\\\\");
		content = content.replaceAll("\u007F.", ""); // apparently, Bukkit/Spigot/Paper use ASCII DEL as §
		Channel channel = gateway.getChannelById(Config.DC_CHANNEL_CONSOLE).block();
		channel.getRestChannel().createMessage(content).block();
	}

	public static User getUser(String tag) {
		var users = gateway.getUsers().collectList().block();

		for (User u : users) {
			Dismine.instance.getServer().getLogger().log(Level.INFO, u.getTag(), tag);
			if (u.getTag().equals(tag)) {
				return u;
			}
		}

		return null;
	}

	public static boolean sendMessageTo(User user, String content, ActionRow row) {
		if (!ready) return false;
		if (user == null) return false;

		var builder = MessageCreateSpec.builder();
		builder.addComponent(row);
		builder.content(content);

		var dm = user.getPrivateChannel().block();
		if (dm == null) return false;
		dm.createMessage(builder.build()).block();

		return true;
	}

	private static void purgeMsgQueue() {
		String combined = "";
		for (String s : msgQueue) {
			if (combined.length() + s.length() + 1 <= 2000) {
				combined += s + "\n";
				continue;
			}
			if (combined.endsWith("\n")) combined = combined.substring(0, combined.length() -1);
			sendMessage(combined);
		}
		if (combined.endsWith("\n")) combined = combined.substring(0, combined.length() -1);
		if (combined.length() > 0) sendMessage(combined);
		msgQueue.clear();
	}

	private static void purgeLogQueue() {
		String combined = "";
		for (String s : logQueue) {
			if (combined.length() + s.length() + 1 <= 2000) {
				combined += s + "\n";
				continue;
			}
			if (combined.endsWith("\n")) combined = combined.substring(0, combined.length() -1);
			sendLogMessage(combined);
		}
		if (combined.endsWith("\n")) combined = combined.substring(0, combined.length() -1);
		if (combined.length() > 0) sendLogMessage(combined);
		logQueue.clear();
	}

	public static void logout() {
		if (!ready) return;
		ready = false;
		var gateway = getGateway();
		new Thread(() -> {
			Dismine.instance.getServer().getLogger().log(Level.INFO, "[DismineBridge] Logging out...");
			gateway.logout().subscribe();
		}, "GatewayKiller").start();
		DiscordStuff.gateway = null;
	}

	public static GatewayDiscordClient getGateway() {
		return gateway;
	}

	public static boolean isReady() {
		return ready;
	}

}
