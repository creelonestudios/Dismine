package de.creelone.dismine.cmds;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.creelone.dismine.TeleportManager;
import de.creelone.dismine.util.TeleportLocation;

public class WarpCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// /warp [name] - no arg will list all warps
		// /setwarp <name> - set a warp
		// /delwarp <name> - delete a warp
		// This command handler handles all three commands

		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command!");
			return true;
		}

		Player p = (Player) sender;

		if(command.getName().equalsIgnoreCase("warp")) {
			// /warp [name]
			if(args.length == 0) {
				TeleportLocation[] warps = TeleportManager.getWarps();

				if(warps.length == 0) {
					sender.sendMessage("§cThere are no warps!");
					return true;
				}

				sender.sendMessage("§aWarps:");
				for(TeleportLocation warp : warps) {
					sender.sendMessage("§a- " + warp.getName() + ": " + warp.getLocation().getWorld().getName() + " " + ((int)warp.getLocation().getX()) + " " +((int)warp.getLocation().getY()) + " " + ((int)warp.getLocation().getZ()));
				}
			} else if(args.length == 1) {
				Location warp = TeleportManager.getWarp(args[0]);
				if(warp == null) {
					sender.sendMessage("§cWarp not found!");
					return true;
				}
				p.teleport(warp);
				sender.sendMessage("§aTeleported to warp §e" + args[0] + "§a!");
			} else {
				sender.sendMessage("§cUsage: /warp [name]");
			}
		} else if(command.getName().equalsIgnoreCase("setwarp")) {
			// /setwarp <name>
			if(!p.hasPermission("dismine.setwarp")) {
				p.sendMessage("§cYou don't have permission to use this command!");
				return true;
			}
			if(args.length == 1) {
				if(TeleportManager.getWarp(args[0]) != null) {
					TeleportManager.delWarp(args[0]); // Maybe prompt the user to delete the old warp, they could still need it
				}
				TeleportManager.addWarp(new TeleportLocation(p.getLocation(), args[0]));
				sender.sendMessage("§aWarp §e" + args[0] + "§a set!");
			} else {
				sender.sendMessage("§cUsage: /setwarp <name>");
			}
		} else if(command.getName().equalsIgnoreCase("delwarp")) {
			// /delwarp <name>
			if(!p.hasPermission("dismine.delwarp")) {
				p.sendMessage("§cYou don't have permission to use this command!");
				return true;
			}
			if(args.length == 1) {
				TeleportManager.delWarp(args[0]);
				sender.sendMessage("§aWarp §e" + args[0] + "§a deleted!");
			} else {
				sender.sendMessage("§cUsage: /delwarp <name>");
			}
		}

		return true;
	}
	
}
