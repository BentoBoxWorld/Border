package world.bentobox.border;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.border.listeners.PlayerListener;

public final class Border extends Addon {
    
    @Override
    public void onLoad() {
        // Nothing to do
    }

    @Override
    public void onEnable() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldBorderAPI");

        if (plugin == null || !plugin.isEnabled()) {
            getLogger().info("WorldBorderAPI not found. Download from https://github.com/yannicklamprecht/WorldBorderAPI/releases");
            this.setState(State.DISABLED);
            return;
        }

        // Register listeners
        this.registerListener(new PlayerListener());
        
    }

    @Override
    public void onDisable() {
        // Nothing to do here
    }

}
