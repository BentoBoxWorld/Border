package world.bentobox.border;

import java.util.*;

import org.bukkit.World;
import org.eclipse.jdt.annotation.NonNull;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.bentobox.api.configuration.RequestedSettings;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.border.commands.BorderColorCommand;
import world.bentobox.border.commands.BorderTypeCommand;
import world.bentobox.border.commands.IslandBorderCommand;
import world.bentobox.border.listeners.BorderShower;
import world.bentobox.border.listeners.PlayerListener;
import world.bentobox.border.listeners.ShowBarrier;
import world.bentobox.border.listeners.ShowWorldBorder;

/**
 * Border add-on entry point that wires commands, listeners, and per-player border
 * rendering for supported game modes.
 * <p>
 * Lifecycle:
 * <ul>
 * <li>{@link #onLoad()} loads and persists configuration.</li>
 * <li>{@link #onEnable()} hooks into available game modes, registers commands and listeners,
 * and builds the border display implementation.</li>
 * </ul>
 */
public class Border extends Addon {

    private BorderShower borderShower;

    private Settings settings;

    private final Config<Settings> config = new Config<>(this, Settings.class);

    private final @NonNull List<GameModeAddon> gameModes = new ArrayList<>();
    private final Map<GameModeAddon, Settings> gameModeSettings = new HashMap<>();
    private static final Set<String> REQUESTABLE = Set.of("bounce-back", "return-teleport",
            "return-teleport-safety-block", "use-barrier-blocks", "show-max-border", "show-particles",
            "barrier-offset");

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
        gameModeSettings.clear();
        // Register commands
        getPlugin().getAddonsManager().getGameModeAddons().forEach(gameModeAddon -> {

            if (!this.settings.getDisabledGameModes().contains(gameModeAddon.getDescription().getName())) {

                gameModes.add(gameModeAddon);
                loadRequests(gameModeAddon);

                log("Border hooking into " + gameModeAddon.getDescription().getName());
                gameModeAddon.getPlayerCommand().ifPresent(c -> new IslandBorderCommand(this, c, "border"));
                gameModeAddon.getPlayerCommand().ifPresent(c -> new BorderTypeCommand(this, c, "bordertype"));
                gameModeAddon.getPlayerCommand().ifPresent(c -> new BorderColorCommand(this, c, "bordercolor"));
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

    /**
     * Creates the border shower implementation used for per-player rendering.
     *
     * @return proxy that delegates to the configured border implementations
     */
    private BorderShower createBorder() {
        BorderShower customBorder = new ShowBarrier(this);
        BorderShower wbapiBorder = new ShowWorldBorder(this);
        return new PerPlayerBorderProxy(this, customBorder, wbapiBorder);
    }

    /**
     * @return the active border shower, or {@code null} if the addon is not enabled
     */
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
     * Gets the settings that apply in a world. These are the settings from the config unless
     * the world's game mode asked for different ones and the admin allows that. The border
     * type, color and whether it is shown are each player's choice and are not affected.
     * @param world a world, may be null
     * @return settings for the world
     */
    public Settings getSettings(World world) {
        if (world != null && !gameModeSettings.isEmpty()) {
            for (Map.Entry<GameModeAddon, Settings> en : gameModeSettings.entrySet()) {
                if (en.getKey().inWorld(world)) {
                    return en.getValue();
                }
            }
        }
        return settings;
    }

    /**
     * Settings a game mode may ask for: how the border behaves, not how it looks to a player
     * or which game modes it is used in.
     */
    static boolean isRequestable(String path) {
        return REQUESTABLE.contains(path);
    }

    /**
     * Asks a game mode which settings it needs and works out the settings for its worlds.
     */
    void loadRequests(GameModeAddon gm) {
        String name = gm.getDescription().getName();
        if (!settings.isAllowRequests() || settings.getIgnoreRequests().stream().anyMatch(name::equalsIgnoreCase)) {
            return;
        }
        Map<String, Object> requests;
        try {
            requests = gm.getRequestedSettings(getDescription().getName());
        } catch (LinkageError e) {
            // This BentoBox is too old to have requests
            return;
        }
        if (requests.isEmpty()) {
            return;
        }
        RequestedSettings.Result<Settings> result = RequestedSettings.apply(settings, requests, Border::isRequestable);
        result.refused().forEach(r -> logWarning(name + " asked for something that cannot be done: " + r));
        log(name + " asked for these settings in its worlds: "
                + (result.applied().isEmpty() ? "none that differ from the config" : String.join(", ", result.applied())));
        gameModeSettings.put(gm, result.settings());
    }

    /**
     * @param world to check
     * @return true if world is being handled by Border
     */
    public boolean inGameWorld(World world) {
        if (world.getEnvironment() == World.Environment.NETHER &&
                !getPlugin().getIWM().isIslandNether(world)) { return false;}
        if (world.getEnvironment() == World.Environment.THE_END &&
                !getPlugin().getIWM().isIslandEnd(world)) { return false;}
        return gameModes.stream().anyMatch(gm -> gm.inWorld(world));
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
        this.getPlugin().getPlaceholdersManager().registerPlaceholder(this,
                "color",
                user -> {
            if (user == null) {
                return "";
            }
            return user.getMetaData(PerPlayerBorderProxy.BORDER_COLOR_META_DATA)
                        .map(m -> m.asString().toLowerCase(Locale.ENGLISH))
                        .orElse(getSettings().getColor().name().toLowerCase());
        }
        );
    }
}
