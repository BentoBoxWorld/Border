package world.bentobox.border.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import world.bentobox.bentobox.api.events.island.IslandEvent;
import world.bentobox.bentobox.api.events.island.IslandEvent.IslandDeleteEvent;
import world.bentobox.bentobox.api.events.island.IslandEvent.IslandEnterEvent;
import world.bentobox.bentobox.api.events.island.IslandEvent.IslandExitEvent;
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
        addon.updateBorder(e.getPlayer(), e.getPlayer().getLocation());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        addon.updateBorder(e.getPlayer(), e.getPlayer().getLocation());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent e) {
        addon.uncachePlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onIslandEnterEvent(IslandEnterEvent e) {
        Player player = addon.getServer().getPlayer(e.getPlayerUUID());
        if (player == null) {
            return;
        }
        addon.updateBorder(player, e.getLocation());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Bukkit.getScheduler().runTask(addon.getPlugin(), () -> addon.updateBorder(e.getPlayer(), e.getTo()));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onIslandExitEvent(IslandExitEvent e) {
        Player player = addon.getServer().getPlayer(e.getPlayerUUID());
        if (player == null) {
            return;
        }
        addon.removeBorder(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onIslandDeleteEvent(IslandDeleteEvent e) {
        if (e.getPlayerUUID() == null) {
            return;
        }
        Player player = addon.getServer().getPlayer(e.getPlayerUUID());
        if (player == null) {
            return;
        }
        addon.removeBorder(player);
    }


    /**
     * Monitor island range change event.
     * If island protection range is changed, then update border.
     * @param e instance of IslandProtectionRangeChangeEvent.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandRangeChange(IslandEvent.IslandProtectionRangeChangeEvent e) {
        Player player = this.addon.getServer().getPlayer(e.getPlayerUUID());

        if (player == null) {
            return;
        }

        this.addon.updateBorder(player, e.getLocation());
    }
}
