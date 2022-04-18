package world.bentobox.border.listeners;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.border.Border;

/**
 * Show a border using Paper's WorldBorder API
 * @author tastybento
 *
 */
public class ShowVirtualWorldBorder implements BorderShower {

    private final Border addon;

    public ShowVirtualWorldBorder(Border addon) {
        this.addon = addon;
    }

    @Override
    public void showBorder(Player player, Island island) {
        if (addon.getSettings().getDisabledGameModes().contains(island.getGameMode())
                || !Objects.requireNonNull(User.getInstance(player)).getMetaData(BORDER_STATE_META_DATA).map(MetaDataValue::asBoolean).orElse(addon.getSettings().isShowByDefault())) {
            return;
        }
        Location l = island.getProtectionCenter();
        WorldBorder wb = Bukkit.createWorldBorder();
        wb.setCenter(l);
        wb.setSize(island.getProtectionRange() * 2D);
        wb.setWarningDistance(0);
        player.setWorldBorder(wb);
    }

    @Override
    public void hideBorder(User user) {
        user.getPlayer().setWorldBorder(null);
    }

}
