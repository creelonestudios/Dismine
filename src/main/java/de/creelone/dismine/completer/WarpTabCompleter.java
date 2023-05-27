package de.creelone.dismine.completer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import de.creelone.dismine.TeleportManager;
import de.creelone.dismine.util.TeleportLocation;

public class WarpTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if(cmd.getName().equalsIgnoreCase("warp")) {
			if(args.length != 1) {
				return null;
			}
			List<String> list = new ArrayList<String>();
			TeleportLocation[] warps = TeleportManager.getWarps();
			for(TeleportLocation warp : warps) {
				list.add(warp.getName());
			}
			return list;
		}
		return null;
	}
	
}
