package de.creelone.dismine.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import de.creelone.dismine.Dismine;
import net.kyori.adventure.text.Component;

public class NickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("§cYou need to be a player to do this!");
			return true;
		}
		Player p = (Player)sender;
		if(args.length == 0) {
			p.displayName(p.name());
			p.playerListName(p.name());
			p.sendMessage("§aYour nickname has been reset!");
			return true;
		}
		p.displayName(Component.text(Dismine.instance.getConfig().get("nick.prefix") + args[0]));
		p.playerListName(Component.text(Dismine.instance.getConfig().get("nick.prefix") + args[0]));
		p.sendMessage("§aYour nickname has been set to §e" + args[0] + "§a!");
		return false;
	}
	
}
