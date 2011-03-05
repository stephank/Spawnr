package nl.kosinus.spawnr;

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
    private final Logger log = getServer().getLogger();

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, this.pListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, this.pListener, Event.Priority.Normal, this);

        if (!getDataFolder().isDirectory())
            getDataFolder().mkdir();
        getConfiguration().load();

        PluginDescriptionFile pdf = this.getDescription();
        log.info("[" + pdf.getName() + "] v" + pdf.getVersion() + " has been enabled");
    }

    public void onDisable() {
        PluginDescriptionFile pdf = this.getDescription();
        log.info("[" + pdf.getName() + "] v" + pdf.getVersion() + " has been disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        String cmdName = cmd.getName();
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be ingame to use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (cmdName.equalsIgnoreCase("setspawn")) {
            if (!player.isOp()) {
                player.sendMessage("You are not an OP.");
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
            player.sendMessage("Spawn point set.");
            return true;
        }

        if (cmdName.equalsIgnoreCase("spawn")) {
            moveToSpawn(player);
            player.sendMessage("Teleported!");
            return true;
        }

        return false;
    }

    /**
     * Move a player to his/her spawn point.
     *
     * @param player The player to teleport.
     */
    public void moveToSpawn(Player player) {
        Location loc = player.getLocation();
        moveToSpawn(loc, player);
        player.teleportTo(loc);
    }

    /**
     * Move a Location to the player's spawn point.
     *
     * @param location The Location object to modify.
     * @param player The player to use as a reference.
     */
    public void moveToSpawn(Location location, Player player) {
        Configuration conf = getConfiguration();
        location.setWorld(getServer().getWorld(conf.getString("world", "world")));
        location.setX(conf.getDouble("x", 0));
        location.setY(conf.getDouble("y", 64));
        location.setZ(conf.getDouble("z", 0));
        location.setYaw((float) conf.getDouble("yaw", 0));
    }
}
