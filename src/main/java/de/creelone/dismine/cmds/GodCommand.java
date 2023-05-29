package de.creelone.dismine.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// /god [player]
		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command!");
			return true;
		}

		Player p = (Player) sender;

		if(!p.hasPermission("dismine.god")) {
			p.sendMessage("§cYou don't have permission to use this command!");
			return true;
		}
		
		if(args.length == 0) {
			p.setInvulnerable(!p.isInvulnerable());
			if(p.isInvulnerable()) {
				p.sendMessage("§aGod mode enabled!");
			} else {
				p.sendMessage("§cGod mode disabled!");
			}
		} else if(args.length == 1) {
			Player target = p.getServer().getPlayer(args[0]);
			if(target == null) {
				p.sendMessage("Player not found!");
				return true;
			}
			target.setInvulnerable(!target.isInvulnerable());
			if(target.isInvulnerable()) {
				p.sendMessage("§aGod mode enabled for " + target.getName() + "!");
			} else {
				p.sendMessage("§cGod mode disabled for " + target.getName() + "!");
			}
		} else {
			p.sendMessage("§cUsage: /god [player]");
		}
		return true;
	}
	
}
