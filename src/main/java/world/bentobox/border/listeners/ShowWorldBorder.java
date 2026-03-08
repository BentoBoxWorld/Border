package world.bentobox.border.listeners;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.util.Util;
import world.bentobox.border.Border;
import world.bentobox.border.PerPlayerBorderProxy;
import world.bentobox.border.Settings.BorderColor;

/**
 * Show a border using Paper's WorldBorder API
 * @author tastybento
 *
 */
public class ShowWorldBorder implements BorderShower {

    private static final long MAX_TICKS = 107374182;
    private final Border addon;

    public ShowWorldBorder(Border addon) {
        this.addon = addon;
    }

    @Override
    public void showBorder(Player player, Island island) {
        User user = Objects.requireNonNull(User.getInstance(player));
        if (addon.getSettings().getDisabledGameModes().contains(island.getGameMode())
                || !user.getMetaData(BORDER_STATE_META_DATA).map(MetaDataValue::asBoolean).orElse(addon.getSettings().isShowByDefault())) {
            return;
        }

        if (player.getWorld().getEnvironment() == Environment.NETHER && !addon.getPlugin().getIWM().isIslandNether(player.getWorld())) {
            return;
        }
        Location l = island.getProtectionCenter().toVector().toLocation(player.getWorld());
        WorldBorder wb = Bukkit.createWorldBorder();
        wb.setCenter(l);
        double size = Math.min(island.getRange() * 2D, (island.getProtectionRange() + addon.getSettings().getBarrierOffset()) * 2D);
        wb.setSize(size);
        wb.setWarningDistance(0);
        BorderColor borderColor = user.getMetaData(PerPlayerBorderProxy.BORDER_COLOR_META_DATA)
                .map(MetaDataValue::asString)
                .map(name -> {
                    try {
                        return BorderColor.valueOf(name);
                    } catch (IllegalArgumentException e) {
                        return addon.getSettings().getColor();
                    }
                })
                .orElseGet(() -> addon.getSettings().getColor());
        switch(borderColor) {
            case RED:
                wb.changeSize(wb.getSize() - 0.1, MAX_TICKS);
                break;
            case GREEN:
                wb.changeSize(wb.getSize() + 0.1, MAX_TICKS);
                break;
            case BLUE:
                break;
        }
        player.setWorldBorder(wb);
    }

    @Override
    public void hideBorder(User user) {
        user.getPlayer().setWorldBorder(null);
    }

    @Override
    public void clearUser(User user) {
        user.getPlayer().setWorldBorder(null);
    }

    @Override
    public void refreshView(User user, Island island) {
        showBorder(user.getPlayer(), island);
    }

    /**
     * Teleport player back within the island space they are in
     * @param entity player
     */
    public void teleportEntity(Entity entity) {
        addon.getIslands().getIslandAt(entity.getLocation()).ifPresent(i -> {
            Vector unitVector = i.getCenter().toVector().subtract(entity.getLocation().toVector()).normalize()
                    .multiply(new Vector(1, 0, 1));
            // Get distance from border
            Location to = entity.getLocation().toVector().add(unitVector).toLocation(entity.getWorld());
            to.setPitch(entity.getLocation().getPitch());
            to.setYaw(entity.getLocation().getYaw());
            Util.teleportAsync(entity, to, TeleportCause.PLUGIN);
        });
    }

}
