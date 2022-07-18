package de.creelone.dismine.cmds;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class GmCommand implements CommandExecutor {

	public static HashMap<String, GameMode> gmMap = new HashMap<>();

	static {
		gmMap.put("0", GameMode.SURVIVAL);
		gmMap.put("1", GameMode.CREATIVE);
		gmMap.put("2", GameMode.ADVENTURE);
		gmMap.put("3", GameMode.SPECTATOR);
		gmMap.put("s", GameMode.SURVIVAL);
		gmMap.put("c", GameMode.CREATIVE);
		gmMap.put("a", GameMode.ADVENTURE);
		gmMap.put("sp", GameMode.SPECTATOR);
		gmMap.put("survival", GameMode.SURVIVAL);
		gmMap.put("creative", GameMode.CREATIVE);
		gmMap.put("adventure", GameMode.ADVENTURE);
		gmMap.put("spectator", GameMode.SPECTATOR);
		gmMap.put("su", GameMode.SURVIVAL);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("§cYou need to be a player to do this!");
			return true;
		}
		Player p = (Player) sender;
		if(!(p.isOp() || p.hasPermission("dismine.gm"))) {
			p.sendMessage("§cYou need OP or the dismine.gm permission to do this");
			return true;
		}
		if(args.length == 0) {
			p.sendMessage("§cSpecify a gamemode");
			return true;
		}
		if(!gmMap.containsKey(args[0])) {
			p.sendMessage("§cSpecify a valid gamemode");
			return true;
		}
		p.setGameMode(gmMap.get(args[0]));
		p.sendMessage("§aSet gamemode to " + gmMap.get(args[0]).toString().toLowerCase());

		return true;
	}

}
