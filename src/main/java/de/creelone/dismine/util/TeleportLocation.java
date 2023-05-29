package de.creelone.dismine.util;

import org.bukkit.Location;

public class TeleportLocation {
	
	public Location loc;
	public String name;

	public TeleportLocation(Location loc, String name) {
		this.loc = loc;
		this.name = name;
	}

	public Location getLocation() {
		return loc;
	}

	public String getName() {
		return name;
	}

}
