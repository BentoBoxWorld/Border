package world.bentobox.border.listeners;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.util.Util;
import world.bentobox.border.Border;

/**
 * @author tastybento
 */
public class PlayerListener implements Listener {

    private final Border addon;

    public PlayerListener(Border addon) {
        this.addon = addon;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        addon.getPlayerBorder().clearUser(User.getInstance(e.getPlayer()));
        addon.getIslands().getProtectedIslandAt(e.getPlayer().getLocation()).ifPresent(i ->
        addon.getPlayerBorder().showBarrier(e.getPlayer(), i));
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent e) {
        addon.getPlayerBorder().clearUser(User.getInstance(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        addon.getPlayerBorder().clearUser(User.getInstance(e.getPlayer()));
        Bukkit.getScheduler().runTask(addon.getPlugin(), () -> addon.getIslands().getProtectedIslandAt(e.getPlayer().getLocation()).ifPresent(i ->
        addon.getPlayerBorder().showBarrier(e.getPlayer(), i)));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        addon.getPlayerBorder().clearUser(User.getInstance(e.getPlayer()));
        // Check if border is on and if from is inside island and to location is outside of
        Bukkit.getScheduler().runTask(addon.getPlugin(), () -> addon.getIslands().getProtectedIslandAt(e.getPlayer().getLocation()).ifPresent(i ->
        addon.getPlayerBorder().showBarrier(e.getPlayer(), i)));
    }

    /**
     * Teleports a player back home if they use a vehicle to glitch out of the world border
     * @param event - event
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDismount(VehicleExitEvent event) {
        if (event.getExited() instanceof Player) {
            Player p = (Player) event.getExited();
            if (p.hasPermission(addon.getPermissionPrefix() + "border.on")) {
                Optional<Island> is = addon.getIslands().getProtectedIslandAt(p.getLocation());
                if (is.isPresent()) {
                    Bukkit.getScheduler().runTask(addon.getPlugin(), () -> {
                        if (!addon.getIslands().getProtectedIslandAt(p.getLocation()).isPresent()
                                && addon.getIslands().getIslandAt(p.getLocation()).equals(is)) {
                            addon.getIslands().homeTeleportAsync(Util.getWorld(p.getWorld()), p);
                        }
                    });
                }
            }
        }
    }
}
