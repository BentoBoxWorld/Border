package world.bentobox.border;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.bukkit.World;
import org.eclipse.jdt.annotation.NonNull;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.util.Util;
import world.bentobox.border.commands.BorderTypeCommand;
import world.bentobox.border.commands.IslandBorderCommand;
import world.bentobox.border.listeners.BorderShower;
import world.bentobox.border.listeners.PlayerListener;
import world.bentobox.border.listeners.ShowBarrier;
import world.bentobox.border.listeners.ShowVirtualWorldBorder;

public class Border extends Addon {

    private BorderShower borderShower;

    private Settings settings;

    private Config<Settings> config = new Config<>(this, Settings.class);

    private @NonNull List<GameModeAddon> gameModes = new ArrayList<>();

    private final Set<BorderType> availableBorderTypes = EnumSet.of(BorderType.VANILLA, BorderType.BARRIER);

    @Override
    public void onLoad() {
        // Save default config.yml
        this.saveDefaultConfig();
        // Load the plugin's config
        this.loadSettings();
    }

    @Override
    public void onEnable() {
        gameModes.clear();
        // Register commands
        getPlugin().getAddonsManager().getGameModeAddons().forEach(gameModeAddon -> {

            if (!this.settings.getDisabledGameModes().contains(gameModeAddon.getDescription().getName())) {

                gameModes.add(gameModeAddon);

                log("Border hooking into " + gameModeAddon.getDescription().getName());
                gameModeAddon.getPlayerCommand().ifPresent(c -> new IslandBorderCommand(this, c, "border"));
                gameModeAddon.getPlayerCommand().ifPresent(c -> new BorderTypeCommand(this, c, "bordertype"));
            }
        });

        if (!gameModes.isEmpty()) {
            borderShower = this.createBorder();
            this.registerListener(new PlayerListener(this));
            this.registerPlaceholders();
        }
    }

    @Override
    public void onDisable() {
        // Nothing to do here
    }

    private BorderShower createBorder() {
        BorderShower customBorder = new ShowBarrier(this);
        BorderShower wbapiBorder = new ShowVirtualWorldBorder(this);
        return new PerPlayerBorderProxy(this, customBorder, wbapiBorder);
    }

    public BorderShower getBorderShower() {
        return borderShower;
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

    public Set<BorderType> getAvailableBorderTypesView() {
        return Collections.unmodifiableSet(availableBorderTypes);
    }


    /**
     * Placeholder registration.
     */
    private void registerPlaceholders()
    {
        if (this.getPlugin().getPlaceholdersManager() == null)
        {
            return;
        }

        // Border is per player, not per gamemode.
        this.getPlugin().getPlaceholdersManager().registerPlaceholder(this,
                "type",
                user -> BorderType.fromId(user.getMetaData(PerPlayerBorderProxy.BORDER_BORDERTYPE_META_DATA).
                        orElse(new MetaDataValue(getSettings().getType().getId())).asByte()).
                orElse(getSettings().getType()).
                getCommandLabel());
    }
}
