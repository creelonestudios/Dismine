package de.creelone.dismine.completer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.creelone.dismine.TeleportManager;
import de.creelone.dismine.util.TeleportLocation;

public class HomeTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if(cmd.getName().equalsIgnoreCase("home")) {
			if(!(sender instanceof Player)) {
				return null;
			}
			Player p = (Player) sender;
			if(args.length != 1) {
				return null;
			}
			List<String> list = new ArrayList<String>();
			TeleportLocation[] homes = TeleportManager.getHomes(p.getUniqueId());
			for(TeleportLocation home : homes) {
				list.add(home.getName());
			}
			return list;
		}
		return null;
	}
	
}
