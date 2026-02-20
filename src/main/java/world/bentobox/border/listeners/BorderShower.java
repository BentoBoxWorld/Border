package world.bentobox.border.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.util.Util;
import world.bentobox.border.Border;

/**
 * A border shower class
 * @author tastybento
 *
 */
public interface BorderShower {
    String BORDER_STATE_META_DATA = "Border_state";

    /**
     * Show the barrier to the player on an island
     * @param player - player to show
     * @param island - island
     */
    void showBorder(Player player, Island island);

    /**
     * Hide the barrier
     * @param user - user
     */
    void hideBorder(User user);

    /**
     * Removes any cache
     * @param user - user
     */
    default void clearUser(User user) {
        // Do nothing
    }

    /**
     * Refreshes the barrier view, if required
     * @param user user 
     * @param island island
     */
    default void refreshView(User user, Island island){
        // Do nothing
    }

    /**
     * Teleports an entity, typically a player back within the island space they are in
     * @param entity entity
     */
    default void teleportEntity(Border addon, Entity entity) {
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
