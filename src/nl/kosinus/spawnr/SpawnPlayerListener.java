package nl.kosinus.spawnr;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;

final class SpawnPlayerListener extends PlayerListener {
    private final Spawnr spawnr;

    SpawnPlayerListener(Spawnr spawnr) {
        this.spawnr = spawnr;
    }

    @Override
    public void onPlayerJoin(PlayerEvent event) {
        Player player = event.getPlayer();
        File playersDir = new File(player.getWorld().getName(), "players");
        File datFile = new File(playersDir, player.getName() + ".dat");
        if (!datFile.exists()) {
            Location loc = spawnr.config.getSpawn(player);
            player.teleportTo(loc);
        }
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Location loc = spawnr.config.getSpawn(event.getPlayer());
        event.setRespawnLocation(loc);
    }
}
