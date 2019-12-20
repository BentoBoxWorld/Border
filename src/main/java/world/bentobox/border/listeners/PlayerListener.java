package world.bentobox.border.listeners;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.github.yannicklamprecht.worldborder.api.BorderAPI;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.events.island.IslandEvent.IslandDeleteEvent;
import world.bentobox.bentobox.api.events.island.IslandEvent.IslandEnterEvent;
import world.bentobox.bentobox.api.events.island.IslandEvent.IslandExitEvent;

/**
 * @author tastybento
 *
 */
public class PlayerListener implements Listener {

    private final BentoBox plugin = BentoBox.getInstance();

    private void setBorder(Player player) {
        BorderAPI.getApi().resetWorldBorderToGlobal(player);
        plugin.getIslands().getIslandAt(player.getLocation()).ifPresent(island -> {
            Bukkit.getScheduler().runTask(plugin, () -> BorderAPI
                    .getApi()
                    .setBorder(
                            player, 
                            island.getProtectionRange() * 2, 
                            island.getCenter()));
        });
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerEnterWorld(PlayerJoinEvent e) {
        setBorder(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onIslandExitEvent(IslandExitEvent e) {
        BorderAPI.getApi().resetWorldBorderToGlobal(Bukkit.getPlayer(e.getPlayerUUID()));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onIslandEnterEvent(IslandEnterEvent e) {
        setBorder(Bukkit.getPlayer(e.getPlayerUUID()));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onIslandExitEvent(IslandDeleteEvent e) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(e.getOwner());
        if (p.isOnline()) {
            BorderAPI.getApi().resetWorldBorderToGlobal(p.getPlayer());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        setBorder(e.getPlayer());
    }
}
