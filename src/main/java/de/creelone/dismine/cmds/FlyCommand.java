package de.creelone.dismine.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// /fly [player]
		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command!");
			return true;
		}

		Player p = (Player) sender;

		if(!p.hasPermission("dismine.fly")) {
			p.sendMessage("§cYou don't have permission to use this command!");
			return true;
		}
		
		if(args.length == 0) {
			p.setAllowFlight(!p.getAllowFlight());
			if(p.getAllowFlight()) {
				p.sendMessage("§aFly mode enabled!");
			} else {
				p.sendMessage("§cFly mode disabled!");
			}
		} else if(args.length == 1) {
			Player target = p.getServer().getPlayer(args[0]);
			if(target == null) {
				p.sendMessage("Player not found!");
				return true;
			}
			target.setAllowFlight(!target.getAllowFlight());
			if(target.getAllowFlight()) {
				p.sendMessage("§aFly mode enabled for " + target.getName() + "!");
			} else {
				p.sendMessage("§cFly mode disabled for " + target.getName() + "!");
			}
		} else {
			p.sendMessage("§cUsage: /fly [player]");
		}
		return true;
	}
	
}
