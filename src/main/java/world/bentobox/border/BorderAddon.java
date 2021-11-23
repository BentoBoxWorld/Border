package world.bentobox.border;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.eclipse.jdt.annotation.NonNull;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.bentobox.util.Util;
import world.bentobox.border.commands.IslandBorderCommand;
import world.bentobox.border.listeners.PlayerBorder;
import world.bentobox.border.listeners.PlayerListener;

public final class BorderAddon extends Addon {

    private PlayerBorder playerBorder;

    private Settings settings;

    private boolean hooked;

    private Config<Settings> config = new Config<>(this, Settings.class);

    private @NonNull List<GameModeAddon> gameModes = new ArrayList<>();

    @Override
    public void onLoad() {
        // Save default config.yml
        this.saveDefaultConfig();
        // Load the plugin's config
        this.loadSettings();
    }

    @Override
    public void onEnable() {
        // Check for WorldBorderAPI
        if (getSettings().isUseWbapi()) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldBorderAPI");
            if (plugin == null || !plugin.isEnabled()) {
                logError("WorldBorderAPI not found. Download from https://github.com/yannicklamprecht/WorldBorderAPI/releases");
                logError("Disabling addon");
                this.setState(State.DISABLED);
                return;
            }
        }
        gameModes.clear();
        playerBorder = new PlayerBorder(this);
        // Register commands
        getPlugin().getAddonsManager().getGameModeAddons().forEach(gameModeAddon -> {

            if (!this.settings.getDisabledGameModes().contains(gameModeAddon.getDescription().getName())) {

                hooked = true;
                gameModes.add(gameModeAddon);

                log("Border hooking into " + gameModeAddon.getDescription().getName());
                gameModeAddon.getPlayerCommand().ifPresent(c -> new IslandBorderCommand(this, c, "border"));
            }
        });

        if (hooked) {
            // Register listeners
            registerListener(new PlayerListener(this));
            registerListener(playerBorder);
        }
    }

    @Override
    public void onDisable() {
        // Nothing to do here
    }

    /**
     * @return the playerBorder
     */
    public PlayerBorder getPlayerBorder() {
        return playerBorder;
    }

    /**
     * This method loads addon configuration settings in memory.
     */
    private void loadSettings() {
        this.settings = config.loadConfigObject();

        if (this.settings == null) {
            // Disable
            this.logError("Border settings could not load! Addon disabled.");
            this.setState(State.DISABLED);
            return;
        }
        // Save new version
        this.config.saveConfigObject(settings);

    }

    public Settings getSettings() {
        return this.settings;
    }

    /**
     * @param world
     * @return true if world is being handled by Border
     */
    public boolean inGameWorld(World world) {
        return gameModes.stream().anyMatch(gm -> gm.inWorld(Util.getWorld(world)));
    }
}
