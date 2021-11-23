package world.bentobox.border.listeners;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.util.Util;
import world.bentobox.border.BorderAddon;

/**
 * @author tastybento
 */
public class PlayerListener implements Listener {

    private static final Vector XZ = new Vector(1,0,1);
    private final BorderAddon addon;
    private Set<UUID> inTeleport;
    private final BorderShower border;

    public PlayerListener(BorderAddon addon) {
        this.addon = addon;
        inTeleport = new HashSet<>();
        this.border = addon.getPlayerBorder().getBorder();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        border.clearUser(User.getInstance(e.getPlayer()));
        Bukkit.getScheduler().runTask(addon.getPlugin(), () -> addon.getIslands().getIslandAt(e.getPlayer().getLocation()).ifPresent(i ->
        border.enable(e.getPlayer(), i)));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent e) {
        border.clearUser(User.getInstance(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        border.clearUser(User.getInstance(e.getPlayer()));
        Bukkit.getScheduler().runTask(addon.getPlugin(), () -> addon.getIslands().getIslandAt(e.getPlayer().getLocation()).ifPresent(i ->
        border.enable(e.getPlayer(), i)));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        border.clearUser(User.getInstance(e.getPlayer()));
        // Check if border is on and if from is inside island and to location is outside of
        Bukkit.getScheduler().runTask(addon.getPlugin(), () -> addon.getIslands().getIslandAt(e.getPlayer().getLocation()).ifPresent(i ->
        border.enable(e.getPlayer(), i)));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLeaveIsland(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location from = e.getFrom();
        if (!addon.getSettings().isReturnTeleport() || !outsideCheck(e.getPlayer(), from, e.getTo())) {
            return;
        }
        // Move the player back inside the border
        if (addon.getIslands().getProtectedIslandAt(from).isPresent()) {
            e.setCancelled(true);
            inTeleport.add(p.getUniqueId());
            Util.teleportAsync(p, from).thenRun(() -> inTeleport.remove(p.getUniqueId()));
            return;
        }
        // Backtrack
        addon.getIslands().getIslandAt(p.getLocation()).ifPresent(i -> {
            Vector unitVector = i.getProtectionCenter().toVector().subtract(p.getLocation().toVector()).normalize()
                    .multiply(new Vector(1,0,1));
            RayTraceResult r = i.getProtectionBoundingBox().rayTrace(p.getLocation().toVector(), unitVector, i.getRange());
            if (r != null) {
                inTeleport.add(p.getUniqueId());
                Location targetPos = r.getHitPosition().toLocation(p.getWorld(), p.getLocation().getYaw(), p.getLocation().getPitch());
                if (!addon.getIslands().isSafeLocation(targetPos)) {
                    switch (targetPos.getWorld().getEnvironment()) {
                    case NETHER:
                        targetPos.getBlock().getRelative(BlockFace.DOWN).setType(Material.NETHERRACK);
                        break;
                    case THE_END:
                        targetPos.getBlock().getRelative(BlockFace.DOWN).setType(Material.END_STONE);
                        break;
                    default:
                        targetPos.getBlock().getRelative(BlockFace.DOWN).setType(Material.STONE);
                        break;
                    }
                }
                Util.teleportAsync(p, targetPos).thenRun(() -> inTeleport.remove(p.getUniqueId()));
            }
        });
    }

    /**
     * Check if the player is outside the island protection zone that they are supposed to be in.
     * @param player - player moving
     * @param from - from location
     * @param to - to location
     * @return true if outside the island protection zone
     */
    private boolean outsideCheck(Player player, Location from, Location to) {
        User user = Objects.requireNonNull(User.getInstance(player));

        if ((from.getWorld() != null && from.getWorld().equals(to.getWorld())
                && from.toVector().multiply(XZ).equals(to.toVector().multiply(XZ)))
                || !addon.inGameWorld(player.getWorld())
                || !addon.getIslands().getIslandAt(to).filter(i -> addon.getIslands().locationIsOnIsland(player, i.getCenter())).isPresent()
                || !user.getMetaData(BorderShower.BORDER_STATE_META_DATA).map(MetaDataValue::asBoolean).orElse(addon.getSettings().isShowByDefault())) {
            return false;
        }
        return addon.getIslands().getIslandAt(to).filter(i -> !i.onIsland(to)).isPresent();
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
