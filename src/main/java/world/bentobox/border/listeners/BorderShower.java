package world.bentobox.border.listeners;

import org.bukkit.entity.Player;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;

/**
 * A border shower class
 * @author tastybento
 *
 */
public interface BorderShower {
    public static final String BORDER_STATE_META_DATA = "Border_state";

    /**
     * Show the barrier to the player on an island
     * @param player - player to show
     * @param island - island
     */
    public void showBorder(Player player, Island island);

    /**
     * Hide the barrier
     * @param user - user
     */
    public void hideBorder(User user);

    /**
     * Removes any cache
     * @param user - user
     */
    default public void clearUser(User user) {};


}
