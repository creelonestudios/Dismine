package de.creelone.dismine.cmds;

import de.creelone.dismine.DiscordStuff;
import de.creelone.dismine.Dismine;
import de.creelone.dismine.Identity;
import de.creelone.dismine.MySQL;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class SyncCommand implements CommandExecutor {

	public static HashMap<String, Identity> syncMap = new HashMap<>();

	public static void button(ButtonInteractionEvent event) {
		if(event.getCustomId().startsWith("sync")) {
			Identity identity = syncMap.get(event.getCustomId().substring(5));
			Dismine.identities.add(identity);
			syncMap.remove(event.getCustomId().substring(5));
			try (PreparedStatement ps = Dismine.instance.sql.prepare("INSERT INTO identities (dcid, uuid) VALUES (?, ?)")) {
				ps.setString(1, identity.getDcid().asString());
				ps.setString(2, identity.getUuid().toString());
				ps.executeUpdate();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}

			event.reply("You are now synced!").withEphemeral(true).block();
			var offlineplayer = identity.getPlayer();
			var player = offlineplayer.getPlayer();
			if (player != null) {
				player.sendMessage("§aSynced with " + identity.getTag());
			}
		} else if(event.getCustomId().startsWith("notme")) {
			Identity identity = syncMap.get(event.getCustomId().substring(6));
			syncMap.remove(event.getCustomId().substring(6));
			event.reply("Cancelled").withEphemeral(true).block();
			var offlineplayer = identity.getPlayer();
			var player = offlineplayer.getPlayer();
			if (player != null) {
				player.sendMessage("§cCancelled sync with " + identity.getTag());
			}
		}

		Button syncBtn = Button.primary("disabled-sync", "Sync").disabled(true);
		Button cancelBtn = Button.danger("disabled-notme", "Not me").disabled(true);

		if (event.getMessage().isEmpty()) return;
		event.getMessage().get().edit().withComponents(ActionRow.of(syncBtn, cancelBtn)).block();
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("§cYou need to be a player to do this!");
			return true;
		}
		Player p = (Player)sender;
		if(args.length == 0) {
			p.sendMessage("§cUsage: /sync <discord tag>");
			return true;
		}

		var tag = String.join(" ", args);
		User user = DiscordStuff.getUser(tag);
		if (user == null) {
			p.sendMessage(String.format("§cUser %s not found.§r\nNote that you need to write in Discord at least once.\nUsage: /sync <discord tag>", tag));
			return true;
		}

		Button syncBtn = Button.primary("sync-" + p.getName() + user.getTag(), "Sync");
		Button cancelBtn = Button.danger("notme-" + p.getName() + user.getTag(), "Not me");

		DiscordStuff.sendMessageTo(user, p.getName() + " requested to sync with your discord account. If you are not " + p.getName() + ", please click on [Not me]", ActionRow.of(syncBtn, cancelBtn));

		syncMap.put(p.getName() + user.getTag(), new Identity(user.getId(), p.getUniqueId()));

		p.sendMessage("§aLook in your DMs");

		return true;
	}

}
