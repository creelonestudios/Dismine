package de.creelone.dismine.cmds;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FeedCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(!(sender instanceof Player) && args.length == 0) {
			sender.sendMessage("§cYou need to be a player to do this!");
			return true;
		}
		if(!(sender.isOp() || sender.hasPermission("dismine.feed"))) {
			sender.sendMessage("§cYou need OP or the dismine.feed permission to do this");
			return true;
		}
		Player p = args.length == 0 ? (Player) sender : Bukkit.getPlayer(args[0]);
		try {
			p.setFoodLevel(20);
		} catch (NullPointerException ignore) {}
		if(sender instanceof Player) {
			Player ps = (Player)sender;
			if(p.getUniqueId().equals(ps.getUniqueId())) {
				p.sendMessage("§aFed");
			} else {
				ps.sendMessage("§a" + p.getName() + " has been fed");
			}
		} else {
			sender.sendMessage("§a" + p.getName() + " has been fed");
		}
		return true;
	}

}
