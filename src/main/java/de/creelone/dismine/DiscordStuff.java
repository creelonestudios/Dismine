package de.creelone.dismine;

import de.creelone.dismine.Dismine.MessageSource;
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

		String pre;
		String suf;

		private MessageType(String pre, String suf) {
			this.pre = pre;
			this.suf = suf;
		}
	}

	private Dismine bridge;
	private DiscordClient client;
	private GatewayDiscordClient gateway;
	private boolean ready = false;
	public final ArrayList<String> msgQueue;
	private static DiscordStuff instance;

	private DiscordStuff() {
		this.bridge = Dismine.instance;
		msgQueue = new ArrayList<>();
	}

	public static DiscordStuff getInstance() {
		if(instance == null) instance = new DiscordStuff();
		return instance;
	}

	public void login() {
		new Thread(this, "DiscordStuff").start();
	}

	@Override
	public void run() {
		bridge.getServer().getLogger().log(Level.INFO, "[DismineBridge] Logging in...");
		client = DiscordClient.create(bridge.TOKEN);
		var bootstrap = client.gateway();
		bootstrap.setEnabledIntents(IntentSet.of(Intent.GUILD_MEMBERS, Intent.GUILD_MESSAGES));
		this.gateway = bootstrap.login().block();

		var onReady = gateway.on(ReadyEvent.class, event -> Mono.fromRunnable(() -> {
			final User self = event.getSelf();
			bridge.getServer().getLogger().log(Level.FINE, String.format("[DismineBridge] Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));

			this.ready = true;
			purgeMsgQueue();
		})).subscribe();
		var onMessageCreate = gateway.on(MessageCreateEvent.class, event -> {
			Message message = event.getMessage();
			User author = message.getAuthor().get();
			if (author.isBot()) return Mono.empty();
			String content = message.getContent();
			String authortag = author.getTag();
			Member member = message.getAuthorAsMember().block();
			String name = "";
			try {
				name = member.getMemberData().nick().get().get();
			} catch (NoSuchElementException e) {
				name = author.getUsername();
			}
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

			bridge.getServer().sendMessage(bridge.createChatMsg(MessageSource.DISCORD, Dismine.getIdentityByDcid(author.getId()), comp, content));

			return Mono.empty();
		}).subscribe();
		var onBtnInteraction = gateway.on(ButtonInteractionEvent.class, event -> {
			if(event.getCustomId().startsWith("sync") || event.getCustomId().startsWith("notme")) {
				SyncCommand.button(event);
			}
			return Mono.empty();
		}).subscribe();
	}

	public void sendMessage(String content) {
		if (!this.ready) {
			msgQueue.add(content);
			return;
		}
		content = MsgConverter.convertToDC(content);
		Channel channel = gateway.getChannelById(bridge.CHANNEL_ID).block();
		channel.getRestChannel().createMessage(content).block();
	}

	public void sendMessage(String format, Object ...args) {
		sendMessage(String.format(format, args));
	}

	public void sendMessage(String icon, String name, MessageType type, String format, Object... args) {
		sendMessage("%s %s%s%s %s", icon, type.pre, name, type.suf, String.format(format, args));
	}

	public void sendMessage(String icon, Identity identity, MessageType type, String format, Object... args) {
		var prefix = identity.getTeamPrefixString().replaceAll("§.","");
		var suffix = identity.getTeamSuffixString().replaceAll("§.","");
		var name = identity.getPlayerName();

		sendMessage(icon, prefix + name + suffix, type, String.format(format, args));
	}

	public void sendMessage(MessageSource src, Identity identity, MessageType type, String format, Object... args) {
		sendMessage(src.dcicon, identity, type, String.format(format, args));
	}

	public User getUser(String tag) {
		var users = gateway.getUsers().collectList().block();

		for (User u : users) {
			bridge.getServer().getLogger().log(Level.INFO, u.getTag(), tag);
			if (u.getTag().equals(tag)) {
				return u;
			}
		}

		return null;
	}

	public boolean sendMessageTo(User user, String content, ActionRow row) {
		if (!this.ready) return false;
		if (user == null) return false;

		var builder = MessageCreateSpec.builder();
		builder.addComponent(row);
		builder.content(content);

		var dm = user.getPrivateChannel().block();
		if (dm == null) return false;
		dm.createMessage(builder.build()).block();

		return true;
	}

    /*public void sendMessage(String content, String emoji) {
        sendPlainMessage(String.format("%s %s", emoji, content));
    }

    public void sendMessage(Player player, String content) {
        sendMessage(String.format("**%s:** %s", player.getName(), content), "<:mcjava:997587410571501708>");
    }

    public void sendDeathMessage(Player player) {
        sendMessage(String.format("**%s %s and lost %s XP**", player.getName(), content), ":skull:");
    }

    public void sendJoinMessage(Player player, String content) {
        sendMessage(player, content, ":arrow_right:", false);
    }

    public void sendLeaveMessage(Player player, String content) {
        sendMessage(player, content, ":arrow_right:", false);
    }*/

	private void purgeMsgQueue() {
		for (String s : msgQueue) {
			sendMessage(s);
		}
		msgQueue.clear();
	}

	public void logout() {
		this.ready = false;
		var gateway = this.gateway;
		new Thread(new Runnable() {
			@Override
			public void run() {
				bridge.getServer().getLogger().log(Level.INFO, "[DismineBridge] Logging out...");
				gateway.logout().subscribe();
			}
		}, "GatewayKiller").start();
		this.gateway = null;
	}

	public GatewayDiscordClient getGateway() {
		return gateway;
	}

	public boolean isReady() {
		return ready;
	}

}
