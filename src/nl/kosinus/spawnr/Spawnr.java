package nl.kosinus.spawnr;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Spawnr extends JavaPlugin {
    final SpawnPlayerListener pListener = new SpawnPlayerListener(this);
    Configuration config;
    Logger log;

    public void onEnable() {
        log = getServer().getLogger();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, this.pListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, this.pListener, Event.Priority.Normal, this);

        config = Configuration.load(this);

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
            sender.sendMessage("You must be in-game to do that.");
            return true;
        }
        Player player = (Player) sender;

        // /setspawn [ global | <player> ]
        if (cmdName.equalsIgnoreCase("setspawn")) {
            Location loc = player.getLocation();
            // Check for valid usage.
            if (args.length > 1) {
                return false;
            }
            // Check for the optional parameter.
            else if (args.length == 1) {
                // All possible usages with the optional parameter require ops.
                if (!player.isOp()) {
                    player.sendMessage("You can't do that, because you're not an op.");
                }
                // Does he want to update the global spawn point?
                else if (args[0].equals("global")) {
                    config.setSpawn(loc);
                    player.sendMessage("Updated the global spawn point.");
                }
                // Or does he want to update someone else's?
                else {
                    List<Player> matches = getServer().matchPlayer(args[0]);
                    // No player by the given name.
                    if (matches.size() == 0) {
                        player.sendMessage("There's no player online named like '" + args[0] + "'.");
                    }
                    // Given name is ambiguous.
                    else if (matches.size() > 1) {
                        player.sendMessage("Multiple players are online named like '" + args[0]
                                + "'. Be more specific.");
                    }
                    // Got a match, we can go ahead.
                    else {
                        config.setSpawn(matches.get(0), loc);
                        player.sendMessage("Updated " + args[0] + "'s spawn point.");
                    }
                }
            }
            // No optional parameter, he wants to set his own spawn point.
            else {
                config.setSpawn(player, loc);
                player.sendMessage("Updated your spawn point.");
            }
            return true;
        }

        // /spawn
        if (cmdName.equalsIgnoreCase("spawn")) {
            Location loc = config.getSpawn(player);
            player.teleportTo(loc);
            player.sendMessage("Teleported!");
            return true;
        }

        return false;
    }
}
