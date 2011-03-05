package nl.kosinus.spawnr;

import org.bukkit.event.server.PluginEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

final class SpawnrServerListener extends ServerListener {
    private final Spawnr spawnr;

    SpawnrServerListener(Spawnr spawnr) {
        this.spawnr = spawnr;
    }

    @Override
    public void onPluginEnabled(PluginEvent event) {
        Plugin p = event.getPlugin();
        if (p.getDescription().getName().equals("GroupManager")) {
            spawnr.setGroupManager(p);
        }
    }

    @Override
    public void onPluginDisabled(PluginEvent event) {
        Plugin p = event.getPlugin();
        if (p.getDescription().getName().equals("GroupManager")) {
            spawnr.setGroupManager(null);
        }
    }
}
