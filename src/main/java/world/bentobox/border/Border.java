package world.bentobox.border;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.border.commands.IslandBorderCommand;
import world.bentobox.border.listeners.PlayerBorder;
import world.bentobox.border.listeners.PlayerListener;

public final class Border extends Addon {

    private final PlayerBorder playerBorder = new PlayerBorder(this);

    @Override
    public void onEnable() {
        // Register listeners
        registerListener(new PlayerListener(this));
        registerListener(playerBorder);

        // Register commands
        getPlugin().getAddonsManager().getGameModeAddons().forEach(gameModeAddon -> {
            log("Border hooking into " + gameModeAddon.getDescription().getName());
            gameModeAddon.getPlayerCommand().ifPresent(c -> new IslandBorderCommand(this, c, "border"));
        });
    }

    @Override
    public void onDisable() {
    }

    /**
     * @return the playerBorder
     */
    public PlayerBorder getPlayerBorder() {
        return playerBorder;
    }

}
