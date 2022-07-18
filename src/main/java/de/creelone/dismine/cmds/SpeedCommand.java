package de.creelone.dismine.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpeedCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("§cYou need to be a player to do this!");
			return true;
		}
		Player p = (Player) sender;
		if(!(p.isOp() || p.hasPermission("dismine.speed"))) {
			sender.sendMessage("§cYou need OP or the dismine.speed permission to do this");
			return true;
		}

		String type = cmd.getName().equals("speed") ? (p.isFlying() ? "flyspeed" : "walkspeed") : cmd.getName();

		switch(type) {
			case "walkspeed":
				if(args.length == 0) {
					p.setWalkSpeed(0.2f);
					p.sendMessage("§aReset your walking speed");
				} else {
					p.setWalkSpeed(Float.parseFloat("0." + args[0]));
					p.sendMessage("§aSet your walking speed to " + args[0]);
				}
				break;
			case "flyspeed":
				if(args.length == 0) {
					p.setFlySpeed(0.2f);
					p.sendMessage("§aReset your fly speed");
				} else {
					p.setFlySpeed(Float.parseFloat("0." + args[0]));
					p.sendMessage("§aSet your fly speed to " + args[0]);
				}
				break;
		}

		return true;
	}

}
