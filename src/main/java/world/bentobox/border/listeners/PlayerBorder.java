package world.bentobox.border.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import world.bentobox.bentobox.api.events.island.IslandProtectionRangeChangeEvent;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.border.Border;

/**
 * Displays a border to a player
 * @author tastybento
 *
 */
public class PlayerBorder implements Listener {

    private final Border addon;
    private final BorderShower barrier;

    /**
     * @param addon
     */
    public PlayerBorder(Border addon) {
        super();
        this.addon = addon;
        this.barrier = addon.getSettings().isUseWbapi() ? new ShowWorldBorder(addon) : new ShowBarrier(addon);
    }

    /**
     * @return the barrier
     */
    public BorderShower getBorder() {
        return barrier;
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent e) {
        // Remove head movement
        if (!addon.getSettings().isUseWbapi() && !e.getFrom().toVector().equals(e.getTo().toVector())) {
            addon.getIslands().getIslandAt(e.getPlayer().getLocation()).ifPresent(i -> barrier.enable(e.getPlayer(), i));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onVehicleMove(VehicleMoveEvent e) {
        // Remove head movement
        if (!addon.getSettings().isUseWbapi() && !e.getFrom().toVector().equals(e.getTo().toVector())) {
            e.getVehicle().getPassengers().stream().filter(Player.class::isInstance).map(Player.class::cast).forEach(p ->
            addon.getIslands().getIslandAt(p.getLocation()).ifPresent(i -> barrier.enable(p, i)));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProtectionRangeChange(IslandProtectionRangeChangeEvent e) {
        // Hide and show again
        e.getIsland().getPlayersOnIsland().forEach(player -> {
            barrier.disable(User.getInstance(player));
            barrier.enable(player, e.getIsland());
        });
    }



}
