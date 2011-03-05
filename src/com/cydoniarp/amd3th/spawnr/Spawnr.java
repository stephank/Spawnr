package com.cydoniarp.amd3th.spawnr;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class Spawnr extends JavaPlugin {
	private final SpawnPlayerListener pListener = new SpawnPlayerListener(this);
	private static Logger log = Logger.getLogger("Minecraft");
	
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.pListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, this.pListener, Event.Priority.Normal, this);
		
		if (!getDataFolder().isDirectory())
			getDataFolder().mkdir();
		getConfiguration().load();
		
		PluginDescriptionFile pdf = this.getDescription();
		log.info("["+pdf.getName()+"] v"+pdf.getVersion()+" has been enabled");
	}
	
	public void onDisable() {
		PluginDescriptionFile pdf = this.getDescription();
		log.info("[" + pdf.getName() + "] v" + pdf.getVersion() + " has been disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		String cmdName = cmd.getName();
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be ingame to use this command.");
			return true;
		}
		
		Player player = (Player)sender;
		if (cmdName.equalsIgnoreCase("spawnr")) {
			if (!player.isOp()) {
				player.sendMessage("You are not OP");
				return true;
			}
			Configuration conf = getConfiguration();
			Location loc = player.getLocation();
			conf.setProperty("world", loc.getWorld().getName());
			conf.setProperty("x", loc.getX());
			conf.setProperty("y", loc.getY());
			conf.setProperty("z", loc.getZ());
			conf.setProperty("yaw", loc.getYaw());
			conf.save();
			player.sendMessage("Spawnr point set.");
			return true;
		}

		if (cmdName.equalsIgnoreCase("spawn")) {
			teleportToSpawn(player);
			player.sendMessage("Teleported!");
			return true;
		}

		return false;
	}
	
	public void setToSpawn(Location locS) {
		Configuration conf = getConfiguration();
		locS.setWorld(getServer().getWorld(conf.getString("world", "world")));
		locS.setX(conf.getDouble("x", 0));
		locS.setY(conf.getDouble("y", 64));
		locS.setZ(conf.getDouble("z", 0));
		locS.setYaw((float)conf.getDouble("yaw", 0));
	}
	
	public void teleportToSpawn(Player player) {
		Location loc = player.getLocation();
		setToSpawn(loc);
		player.teleportTo(loc);
	}
}
