package world.bentobox.border.listeners;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;

import world.bentobox.bentobox.api.addons.Addon.State;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.border.BorderAddon;

/**
 * Show a border using WorldBorderAPI
 * @author tastybento
 *
 */
public class ShowWorldBorder implements BorderShower {

    private final BorderAddon addon;
    private final WorldBorderApi worldBorderApi;

    public ShowWorldBorder(BorderAddon addon) {
        this.addon = addon;
        RegisteredServiceProvider<WorldBorderApi> worldBorderApiRegisteredServiceProvider = Bukkit.getServer().getServicesManager().getRegistration(WorldBorderApi.class);

        if (worldBorderApiRegisteredServiceProvider == null) {
            addon.logError("WorldBorderAPI not found");
            addon.setState(State.DISABLED);
            worldBorderApi = null;
            return;
        }

        worldBorderApi = worldBorderApiRegisteredServiceProvider.getProvider();
    }

    @Override
    public void enable(Player player, Island island) {
        worldBorderApi.resetWorldBorderToGlobal(player);
        if (addon.getSettings().getDisabledGameModes().contains(island.getGameMode())
                || !Objects.requireNonNull(User.getInstance(player)).getMetaData(BORDER_STATE_META_DATA).map(MetaDataValue::asBoolean).orElse(addon.getSettings().isShowByDefault())) {
            return;
        }
        worldBorderApi.setBorder(player, island.getProtectionRange() * 2D, island.getProtectionCenter());
    }

    @Override
    public void disable(User user) {
        worldBorderApi.resetWorldBorderToGlobal(user.getPlayer());
    }

}
