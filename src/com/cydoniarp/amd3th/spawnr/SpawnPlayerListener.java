package com.cydoniarp.amd3th.spawnr;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SpawnPlayerListener extends PlayerListener {
	public Spawnr plugin;
	
	public SpawnPlayerListener(Spawnr plugin) {
		this.plugin = plugin;
	}
	
	public void onPlayerJoin(PlayerEvent event) {
		Player player = event.getPlayer();
		Property prop = plugin.getWorldProperty(player.getWorld());
		if (!prop.keyExists("x")) {
			prop.setString("x", " ");
			prop.setString("y", " ");
			prop.setString("z", " ");
			prop.setString("yaw", " ");
			Location oLoc = player.getWorld().getSpawnLocation();
			player.teleportTo(oLoc);
			if(player.isOp()) {
				player.sendMessage("Spawnr point needs to be set.");
			}
		}
		else if (!prop.keyExists(player.getName())) {
			Location loc = player.getLocation();
			loc.setX(prop.getDouble("x"));
			loc.setY(prop.getDouble("y"));
			loc.setZ(prop.getDouble("z"));
			loc.setYaw(prop.getFloat("yaw"));
			player.teleportTo(loc);
			prop.setBoolean(player.getName(), true);
		}
	}
	
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		Property prop = plugin.getWorldProperty(player.getWorld());
		if (prop.keyExists("x")){
			Location loc = player.getLocation();
			loc.setX(prop.getDouble("x"));
			loc.setY(prop.getDouble("y"));
			loc.setZ(prop.getDouble("z"));
			loc.setYaw(prop.getFloat("yaw"));
			event.setRespawnLocation(loc);
		}
		else {
			Location oLoc = player.getWorld().getSpawnLocation();
			player.teleportTo(oLoc);
			if(player.isOp()){
				player.sendMessage("Spawnr point needs to be set.");
			}
		}
	}
}
