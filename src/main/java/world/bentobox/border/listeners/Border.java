package world.bentobox.border.listeners;

import org.bukkit.entity.Player;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;

/**
 * Border manager
 * @author tastybento
 *
 */
public interface Border {
    public static final String BORDER_STATE_META_DATA = "Border_state";

    /**
     * Enables the border to the player on an island
     * @param player - player to show
     * @param island - island
     */
    public void enable(Player player, Island island);

    /**
     * Disables the border
     * @param user - user
     */
    public void disable(User user);

    /**
     * Removes any cache
     * @param user - user
     */
    public default void clearUser(User user) {
        // Do nothing
    };


}
