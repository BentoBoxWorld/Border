package world.bentobox.border.listeners;

import org.bukkit.event.Listener;
import world.bentobox.border.Border;

/**
 * Displays a border to a player
 *
 * @author tastybento
 */
public class PlayerBorder implements Listener {

    private final Border addon;
    private final BorderShower show;

    /**
     * @param addon
     */
    public PlayerBorder(Border addon) {
        super();
        this.addon = addon;
        this.show = addon.getSettings().isUseWbapi() ? new ShowWorldBorder(addon) : new ShowBarrier(addon);
    }

    /**
     * @return the barrier
     */
    public BorderShower getBorder() {
        return show;
    }
}
