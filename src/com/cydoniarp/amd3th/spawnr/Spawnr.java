package com.cydoniarp.amd3th.spawnr;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Spawnr extends JavaPlugin {
	private final SpawnPlayerListener pListener = new SpawnPlayerListener(this);
	private static Logger log = Logger.getLogger("Minecraft");
	
	private HashMap<Long, Property> properties;
	
	public Property getWorldProperty(World world) {
		long worldId = world.getId();
		Property result = properties.get(worldId);
		if (result == null) {
			result = new Property(new File(getDataFolder(), worldId + ".spawn"));
			properties.put(worldId, result);
		}
		return result;
	}
	
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.pListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, this.pListener, Event.Priority.Normal, this);
		PluginDescriptionFile pdf = this.getDescription();
		log.info("["+pdf.getName()+"] v"+pdf.getVersion()+" has been enabled");
		if (!(new File("plugins/Spawnr").isDirectory())) {
			(new File("plugins/Spawnr")).mkdir();
		}
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
		Property prop = getWorldProperty(player.getWorld());
		if (cmdName.equalsIgnoreCase("spawnr")) {
			if (!player.isOp()) {
				player.sendMessage("You are not OP");
				return true;
			}
			Location loc = player.getLocation();
			prop.setDouble("x", loc.getX());
			prop.setDouble("y", loc.getY());
			prop.setDouble("z", loc.getZ());
			prop.setFloat("yaw", loc.getYaw());
			player.sendMessage("Spawnr point set.");
			return true;
		}

		if (cmdName.equalsIgnoreCase("spawn")) {
			if (!prop.keyExists("x")) {
				player.sendMessage("No point to teleport to.");
				return true;
			}
			Location locS = player.getLocation();
			locS.setX(prop.getDouble("x"));
			locS.setY(prop.getDouble("y"));
			locS.setZ(prop.getDouble("z"));
			locS.setYaw(prop.getFloat("yaw"));
			player.teleportTo(locS);
			player.sendMessage("Teleported!");
			return true;
		}

		return false;
	}
}
