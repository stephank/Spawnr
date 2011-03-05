package nl.kosinus.spawnr;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SpawnPlayerListener extends PlayerListener {
    public Spawnr plugin;

    public SpawnPlayerListener(Spawnr plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerJoin(PlayerEvent event) {
        Player player = event.getPlayer();
        File playersDir = new File(player.getWorld().getName(), "players");
        File datFile = new File(playersDir, player.getName() + ".dat");
        if (!datFile.exists())
            plugin.moveToSpawn(player);
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        plugin.moveToSpawn(event.getRespawnLocation(), event.getPlayer());
    }
}
