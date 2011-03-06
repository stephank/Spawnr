package nl.kosinus.spawnr;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;

final class SpawnrPlayerListener extends PlayerListener {
    private final Spawnr spawnr;

    SpawnrPlayerListener(Spawnr spawnr) {
        this.spawnr = spawnr;
    }

    @Override
    public void onPlayerJoin(PlayerEvent event) {
        // Check if there's a data file for this player.
        Player player = event.getPlayer();
        File playersDir = new File(player.getWorld().getName(), "players");
        File datFile = new File(playersDir, player.getName() + ".dat");
        // If not, this is a new player. Make sure he lands at the correct
        // spawn. (Don't teleport a returning player from his old location.)
        if (!datFile.exists()) {
            player.teleportTo(spawnr.getSpawnLocationFor(player));
        }
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(spawnr.getSpawnLocationFor(event.getPlayer()));
    }
}
