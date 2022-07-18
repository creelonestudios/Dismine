package de.creelone.dismine;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;

import de.creelone.dismine.Dismine.MessageSource;
import de.creelone.dismine.DiscordStuff.MessageType;

public class Events implements Listener {

	public Events() {}

	@EventHandler
	public void onChat(AsyncChatEvent e) {
		String msg = PlainTextComponentSerializer.plainText().serialize(e.message());
		var identity = Dismine.getIdentityByUuid(e.getPlayer().getUniqueId());
		//dcstuff.sendMessage("<:mcjava:997587410571501708> " + e.getPlayer().getName() + ": " + e.getMessage()); identity.getPlayerDisplayName()
		//dc.sendMessage("%s **%s:** %s", "<:mcjava:997587410571501708>", e.getPlayer().getName(), msg.replace("\\", "\\\\"));
		var comp = Dismine.instance.createChatMsg(MessageSource.MCJAVA, identity, identity.getPlayerDisplayName(), msg);
		Dismine.instance.getServer().sendMessage(comp);
		e.setCancelled(true);
		DiscordStuff.sendMessage(MessageSource.MCJAVA, identity, MessageType.CHAT, "%s", msg);
	}

	@EventHandler
	public void onDead(PlayerDeathEvent e) {
		//dc.sendMessage("%s **%s and lost %s XP**", ":skull:", e.getDeathMessage(), e.getPlayer().getTotalExperience() - e.getNewTotalExp());
		var identity = Dismine.getIdentityByUuid(e.getPlayer().getUniqueId());
		var deathMsg = e.getDeathMessage();
		deathMsg = deathMsg.substring(identity.getTeamPrefixString().length() + identity.getPlayerName().length() + identity.getTeamSuffixString().length() + 1);
		DiscordStuff.sendMessage(":skull:", identity, MessageType.OTHER, "**%s and lost %s XP**", deathMsg, e.getPlayer().getTotalExperience() - e.getNewTotalExp());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		//dc.sendMessage("%s **%s joined the game**", ":arrow_right:", e.getPlayer().getName());
		var identity = Dismine.getIdentityByUuid(e.getPlayer().getUniqueId());
		DiscordStuff.sendMessage(":arrow_right:", identity, MessageType.OTHER, "**joined the game**");
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		//dc.sendMessage("%s **%s left the game**", ":arrow_left:", e.getPlayer().getName());
		var identity = Dismine.getIdentityByUuid(e.getPlayer().getUniqueId());
		DiscordStuff.sendMessage(":arrow_left:", identity, MessageType.OTHER, "**left the game**");
	}

	@EventHandler
	public void onAdvancement(PlayerAdvancementDoneEvent e) {
		if(e.getAdvancement().getRoot().equals(e.getAdvancement())) return;
		boolean isChallenge = PlainTextComponentSerializer.plainText().serialize(e.message()).contains("completed the challenge");
		//dc.sendMessage("%s **%s has %s** [%s]", isChallenge ? ":dart:" : ":trophy:", e.getPlayer().getName(), isChallenge ? "completed the challenge" : "made the advancement", PlainTextComponentSerializer.plainText().serialize(e.getAdvancement().getDisplay().title()));
		var identity = Dismine.getIdentityByUuid(e.getPlayer().getUniqueId());
		DiscordStuff.sendMessage(isChallenge ? ":dart:" : ":trophy:", identity, MessageType.OTHER, "**has %s** [%s]", isChallenge ? "completed the challenge" : "made the advancement", PlainTextComponentSerializer.plainText().serialize(e.getAdvancement().getDisplay().title()));
	}

	@EventHandler
	public void onPlayerCmd(PlayerCommandPreprocessEvent e) {
		var identity = Dismine.getIdentityByUuid(e.getPlayer().getUniqueId());
		if(e.getMessage().startsWith("/say")) {
			//dc.sendMessage("%s [**%s**] %s", "<:mcjava:997587410571501708>", e.getPlayer().getName(), e.getMessage().substring(4).replace("\\", "\\\\"));
			DiscordStuff.sendMessage(MessageSource.MCJAVA, identity, MessageType.SAY, e.getMessage().substring(4).replace("\\", "\\\\"));
		} else if(e.getMessage().startsWith("/me")) {
			//dc.sendMessage("%s * **%s** %s", "<:mcjava:997587410571501708>", e.getPlayer().getName(), e.getMessage().substring(3).replace("\\", "\\\\"));
			DiscordStuff.sendMessage(MessageSource.MCJAVA, identity, MessageType.ME, e.getMessage().substring(3).replace("\\", "\\\\"));
		}
	}

	@EventHandler
	public void onServerCmd(ServerCommandEvent e) {
		if(e.getCommand().startsWith("say")) {
			//dc.sendMessage("%s [**%s**] %s", "<:mcjava:997587410571501708>", "Server", e.getCommand().substring(4).replace("\\", "\\\\"));
			DiscordStuff.sendMessage(":pager:", "Console", MessageType.SAY, e.getCommand().substring(4).replace("\\", "\\\\"));
		} else if(e.getCommand().startsWith("me")) {
			//dc.sendMessage("%s * **%s** %s", "<:mcjava:997587410571501708>", "Server", e.getCommand().substring(3).replace("\\", "\\\\"));
			DiscordStuff.sendMessage(":pager:", "Console", MessageType.ME, e.getCommand().substring(3).replace("\\", "\\\\"));
		}
	}

}
