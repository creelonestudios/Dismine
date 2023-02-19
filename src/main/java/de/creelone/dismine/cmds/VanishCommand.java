package de.creelone.dismine.cmds;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.creelone.dismine.Dismine;

public class VanishCommand implements CommandExecutor, Listener {

	public ArrayList<Player> vanishedPlayers = new ArrayList<Player>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// /vanish [player]
		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command!");
			return true;
		}
		
		Player p = (Player) sender;

		if(!p.hasPermission("dismine.vanish")) {
			p.sendMessage("§cYou don't have permission to use this command!");
			return true;
		}

		if(args.length == 0) {
			if(!vanishedPlayers.contains(p)) vanishedPlayers.add(p);
			else vanishedPlayers.remove(p);
			for(Player player : p.getServer().getOnlinePlayers()) {
				if(player.getUniqueId().equals(p.getUniqueId())) continue;
				if(player.hasPermission("dismine.vanish.see")) continue;
				if(!vanishedPlayers.contains(player)) {
					player.hidePlayer(Dismine.instance, p);
				} else {
					player.showPlayer(Dismine.instance, p);
				}
			}
			if(vanishedPlayers.contains(p)) {
				p.sendMessage("§aVanish mode enabled!");
			} else {
				p.sendMessage("§cVanish mode disabled!");
			}
		} else if(args.length == 1) {
			Player target = p.getServer().getPlayer(args[0]);
			if(target == null) {
				p.sendMessage("Player not found!");
				return true;
			}
			if(!vanishedPlayers.contains(target)) vanishedPlayers.add(target);
			else vanishedPlayers.remove(target);
			for(Player player : p.getServer().getOnlinePlayers()) {
				if(player.getUniqueId().equals(target.getUniqueId())) continue;
				if(player.hasPermission("dismine.vanish.see")) continue;
				if(!vanishedPlayers.contains(player)) {
					player.hidePlayer(Dismine.instance, target);
				} else {
					player.showPlayer(Dismine.instance, target);
				}
			}
			if(vanishedPlayers.contains(target)) {
				p.sendMessage("§aVanish mode enabled!");
			} else {
				p.sendMessage("§cVanish mode disabled!");
			}
		} else {
			p.sendMessage("§cUsage: /vanish [player]");
		}
		return true;
	}

	@EventHandler
	public void onPlayerJoin(Player p) {
		if(p.hasPermission("dismine.vanish.see")) return;
		for(Player player : vanishedPlayers) {
			p.hidePlayer(Dismine.instance, player);
		}
	}
	
}
