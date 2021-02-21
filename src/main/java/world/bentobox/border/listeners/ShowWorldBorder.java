package world.bentobox.border.listeners;

import org.bukkit.entity.Player;

import com.github.yannicklamprecht.worldborder.api.BorderAPI;

import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.border.Border;

/**
 * Show a border using WorldBorderAPI
 * @author tastybento
 *
 */
public class ShowWorldBorder implements BorderShower {

    private final Border addon;

    public ShowWorldBorder(Border addon) {
        this.addon = addon;
    }

    @Override
    public void showBorder(Player player, Island island) {
        BorderAPI.getApi().resetWorldBorderToGlobal(player);
        if (addon.getSettings().getDisabledGameModes().contains(island.getGameMode())
                || !User.getInstance(player).getMetaData(BORDER_STATE_META_DATA).map(MetaDataValue::asBoolean).orElse(addon.getSettings().isShowByDefault())) {
            return;
        }
        BorderAPI.getApi().setBorder(player, island.getProtectionRange() * 2, island.getProtectionCenter());
    }

    @Override
    public void hideBorder(User user) {
        BorderAPI.getApi().resetWorldBorderToGlobal(user.getPlayer());
    }

}
