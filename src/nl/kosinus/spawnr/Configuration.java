package nl.kosinus.spawnr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.JavaBeanDumper;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

/**
 * A bean that lines up with the YAML format.
 */
public final class Configuration {
    /**
     * Snakeyaml constructor subclass we use.
     */
    private static class ConfigurationConstructor extends Constructor {
        public ConfigurationConstructor() {
            super(Configuration.class);

            // This stuff is normally done by JavaBeanLoader, but we can't use
            // that convenience because it uses the wrong ClassLoader.
            TypeDescription typeDescription = new TypeDescription(Configuration.class);
            typeDescription.setRoot(true);
            addTypeDescription(typeDescription);
        }

        // Need to override this, in order for snakeyaml to use the proper
        // ClassLoader.
        @Override
        protected Class<?> getClassForName(String name) throws ClassNotFoundException {
            return Class.forName(name);
        }
    }

    /**
     * A bean that describes a single spawn point.
     */
    public static class SpawnPoint {
        private String world;
        private double x;
        private double y;
        private double z;
        private float pitch;
        private float yaw;

        public SpawnPoint() {
            world = "world";
            x = 0;
            y = 64;
            z = 0;
            pitch = 0;
            yaw = 0;
        }

        public void setWorld(String world) {
            this.world = world;
        }

        public String getWorld() {
            return world;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getX() {
            return x;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getY() {
            return y;
        }

        public void setZ(double z) {
            this.z = z;
        }

        public double getZ() {
            return z;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
        }

        public float getPitch() {
            return pitch;
        }

        public void setYaw(float yaw) {
            this.yaw = yaw;
        }

        public float getYaw() {
            return yaw;
        }

        /**
         * Construct from a Location object.
         * 
         * @param l The Location object to load from.
         */
        SpawnPoint(Location l) {
            world = l.getWorld().getName();
            x = l.getX();
            y = l.getY();
            z = l.getZ();
            pitch = l.getPitch();
            yaw = l.getYaw();
        }

        /**
         * Converts the bean to a Location.
         * 
         * @param server The server the location will be used for.
         * @return A Location instance.
         */
        Location toLocation(Server server) {
            World w = server.getWorld(world);
            return new Location(w, x, y, z, yaw, pitch);
        }
    }

    private Spawnr spawnr;

    private boolean teleportToCustomSpawn = false;

    private SpawnPoint global = new SpawnPoint();
    private Map<String, SpawnPoint> players = new HashMap<String, Configuration.SpawnPoint>();

    public void setTeleportToCustomSpawn(boolean teleportToCustomSpawn) {
        this.teleportToCustomSpawn = teleportToCustomSpawn;
    }

    public boolean getTeleportToCustomSpawn() {
        return teleportToCustomSpawn;
    }

    public void setGlobal(SpawnPoint global) {
        this.global = global;
    }

    public SpawnPoint getGlobal() {
        return global;
    }

    public Map<String, SpawnPoint> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, SpawnPoint> players) {
        this.players = players;
    }

    /**
     * Get the global spawn point.
     * 
     * @return The spawn location.
     */
    Location getSpawn() {
        return global.toLocation(spawnr.getServer());
    }

    /**
     * Get a player's spawn point.
     * 
     * @param player The player to get the spawn point for.
     * @return The spawn location.
     */
    Location getSpawn(Player player) {
        SpawnPoint s = players.get(player.getName());
        if (s == null) {
            return null;
        } else {
            return s.toLocation(spawnr.getServer());
        }
    }

    /**
     * Save the global spawn point.
     * 
     * @param loc The new spawn location.
     */
    void setSpawn(Location loc) {
        setGlobal(new SpawnPoint(loc));
        save();
    }

    /**
     * Save a player's spawn point.
     * 
     * @param player The player to save the spawn point for.
     * @param loc The new spawn location.
     */
    void setSpawn(Player player, Location loc) {
        players.put(player.getName(), new SpawnPoint(loc));
        save();
    }

    /**
     * Save the configuration to the YAML file.
     */
    void save() {
        File dataFolder = spawnr.getDataFolder();
        if (!dataFolder.isDirectory())
            dataFolder.mkdir();

        try {
            FileWriter writer = new FileWriter(new File(dataFolder, "config.yml"));
            try {
                JavaBeanDumper dumper = new JavaBeanDumper();
                dumper.dump(this, writer);
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            spawnr.log.log(Level.WARNING, "Unable to save Spawnr config", e);
        }
    }

    /**
     * Load the Spawnr configuration.
     */
    static Configuration load(Spawnr spawnr) {
        File dataFolder = spawnr.getDataFolder();
        if (!dataFolder.isDirectory())
            dataFolder.mkdir();

        Configuration result;
        try {
            FileInputStream stream = new FileInputStream(new File(dataFolder, "config.yml"));
            try {
                Yaml loader = new Yaml(new ConfigurationConstructor());
                result = (Configuration) loader.load(stream);
            } finally {
                stream.close();
            }
        } catch (FileNotFoundException e) {
            spawnr.log.log(Level.INFO, "No Spawnr config found; starting afresh.");
            result = newConfig();
        } catch (IOException e) {
            result = badConfig(spawnr.log, e);
        } catch (YAMLException e) {
            result = badConfig(spawnr.log, e);
        }

        result.spawnr = spawnr;
        return result;
    }

    /**
     * Create a new config structure in memory.
     */
    static Configuration newConfig() {
        return new Configuration();
    }

    /**
     * Same as {@link #newConfig()}, but logs an exception.
     * 
     * @param cause The exception to log.
     */
    static Configuration badConfig(Logger log, Throwable cause) {
        log.log(Level.WARNING, "Couldn't read Spawnr config! Continuing with an empty config.",
                cause);
        return newConfig();
    }
}
