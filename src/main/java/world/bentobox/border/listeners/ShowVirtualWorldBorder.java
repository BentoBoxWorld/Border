package world.bentobox.border.listeners;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.util.Util;
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
        if (player.getWorld().getEnvironment() == Environment.NETHER) {
            l.multiply(8);
        }
        WorldBorder wb = Bukkit.createWorldBorder();
        wb.setCenter(l);
        double size = Math.min(island.getRange() * 2D, (island.getProtectionRange() + addon.getSettings().getBarrierOffset()) * 2D);
        wb.setSize(size);
        wb.setWarningDistance(0);
        player.setWorldBorder(wb);
    }

    @Override
    public void hideBorder(User user) {
        user.getPlayer().setWorldBorder(null);
    }

    /**
     * Teleport player back within the island space they are in
     * @param p player
     */
    public void teleportPlayer(Player p) {
        addon.getIslands().getIslandAt(p.getLocation()).ifPresent(i -> {
            Vector unitVector = i.getCenter().toVector().subtract(p.getLocation().toVector()).normalize()
                    .multiply(new Vector(1, 0, 1));
            // Get distance from border
            Location to = p.getLocation().toVector().add(unitVector).toLocation(p.getWorld());
            to.setPitch(p.getLocation().getPitch());
            to.setYaw(p.getLocation().getYaw());
            Util.teleportAsync(p, to, TeleportCause.PLUGIN);
        });
    }

}
