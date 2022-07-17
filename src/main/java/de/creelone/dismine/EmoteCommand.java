package de.creelone.dismine;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EmoteCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("§cYou need to be a player to do this!");
			return true;
		}
		final String emote =
				cmd.getName().equalsIgnoreCase("shrug") ? "¯\\_(ツ)_/¯"
						: cmd.getName().equalsIgnoreCase("tableflip") ? "(╯°□°）╯︵ ┻━┻"
						: cmd.getName().equalsIgnoreCase("unflip") ? "┬─┬ ノ( ゜-゜ノ)"
						: cmd.getName().equalsIgnoreCase("doubleflip") ? "┻━┻彡 ヽ(ಠ益ಠ)ノ彡┻━┻"
						: cmd.getName().equalsIgnoreCase("owo") ? "\\(OwO)/"
						: "";
		Player p = (Player)sender;
		if(args.length == 0) p.chat(emote);
		else p.chat(String.join(" ", args) + " " + emote);
		return true;
	}

}
