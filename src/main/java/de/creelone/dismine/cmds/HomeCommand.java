package de.creelone.dismine.cmds;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.creelone.dismine.TeleportManager;
import de.creelone.dismine.util.TeleportLocation;

public class HomeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// /home <name>
		// /sethome <name> - set a home
		// /delhome <name> - delete a home
		// /homes - list all homes
		// This command handler handles all four commands

		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command!");
			return true;
		}

		Player p = (Player) sender;

		if(command.getName().equalsIgnoreCase("home")) {
			// /home [name]
			if(args.length == 0) {
				Location home = TeleportManager.getHome(p.getUniqueId(), "home");
				if(home == null) {
					sender.sendMessage("§cHome not found!");
					return true;
				}
				p.teleport(home);
				sender.sendMessage("§aTeleported to home!");
			} else if(args.length == 1) {
				Location home = TeleportManager.getHome(p.getUniqueId(), args[0]);
				if(home == null) {
					sender.sendMessage("§cHome not found!");
					return true;
				}
				p.teleport(home);
				sender.sendMessage("§aTeleported to home §e" + args[0] + "§a!");
			} else {
				sender.sendMessage("§cUsage: /home [name]");
			}
		} else if(command.getName().equalsIgnoreCase("sethome")) {
			// /sethome [name]
			if(args.length == 0) {
				TeleportManager.addHome(p.getUniqueId(), new TeleportLocation(p.getLocation(), "home"));
				sender.sendMessage("§aHome set!");
			} else if(args.length == 1) {
				TeleportManager.addHome(p.getUniqueId(), new TeleportLocation(p.getLocation(), args[0]));
				sender.sendMessage("§aHome §e" + args[0] + "§a set!");
			} else {
				sender.sendMessage("§cUsage: /sethome [name]");
			}
		} else if(command.getName().equalsIgnoreCase("delhome")) {
			// /delhome [name]
			if(args.length == 0) {
				TeleportManager.delHome(p.getUniqueId(), "home");
				sender.sendMessage("§aHome deleted!");
			} else if(args.length == 1) {
				TeleportManager.delHome(p.getUniqueId(), args[0]);
				sender.sendMessage("§aHome §e" + args[0] + "§a deleted!");
			} else {
				sender.sendMessage("§cUsage: /delhome [name]");
			}
		} else if(command.getName().equalsIgnoreCase("homes")) {
			// /homes
			TeleportLocation[] homes = TeleportManager.getHomes(p.getUniqueId());

			if(homes.length == 0) {
				sender.sendMessage("§cThere are no homes!");
				return true;
			}

			sender.sendMessage("§aHomes:");
			for(TeleportLocation home : homes) {
				sender.sendMessage("§a- " + home.getName() + ": " + home.getLocation().getWorld().getName() + " " + ((int)home.getLocation().getX()) + " " + ((int)home.getLocation().getY()) + " " + ((int)home.getLocation().getZ()));
			}
		}

		return true;
	}
	
}
