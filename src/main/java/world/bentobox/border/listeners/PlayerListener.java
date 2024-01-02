package world.bentobox.border.listeners;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import world.bentobox.bentobox.api.events.island.IslandProtectionRangeChangeEvent;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.util.Util;
import world.bentobox.border.Border;
import world.bentobox.border.PerPlayerBorderProxy;
import world.bentobox.border.commands.BorderTypeCommand;
import world.bentobox.border.commands.IslandBorderCommand;

/**
 * @author tastybento
 */
public class PlayerListener implements Listener {

    private static final Vector XZ = new Vector(1,0,1);
    private final Border addon;
    private Set<UUID> inTeleport;
    private final BorderShower show;

    public PlayerListener(Border addon) {
        this.addon = addon;
        inTeleport = new HashSet<>();
        this.show = addon.getBorderShower();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        // Run one-tick after joining because meta data cannot be set otherwise
        Bukkit.getScheduler().runTask(addon.getPlugin(), () -> processEvent(e));
    }

    protected void processEvent(PlayerJoinEvent e) {
        User user = User.getInstance(e.getPlayer());

        show.hideBorder(user);
        // Just for sure, disable world Border 
        user.getPlayer().setWorldBorder(null);

        // Check player perms and return to defaults if players don't have them
        if (!e.getPlayer().hasPermission(addon.getPermissionPrefix() + IslandBorderCommand.BORDER_COMMAND_PERM)) {
            // Restore barrier on/off to default
            user.putMetaData(BorderShower.BORDER_STATE_META_DATA, new MetaDataValue(addon.getSettings().isShowByDefault()));

            if (!e.getPlayer().hasPermission(addon.getPermissionPrefix() + BorderTypeCommand.BORDER_TYPE_COMMAND_PERM)) {
                // Restore default barrier type to player
                MetaDataValue metaDataValue = new MetaDataValue(addon.getSettings().getType().getId());
                user.putMetaData(PerPlayerBorderProxy.BORDER_BORDERTYPE_META_DATA, metaDataValue);                
            }
        }

        // Show the border if required one tick after   
        Bukkit.getScheduler().runTask(addon.getPlugin(), () -> addon.getIslands().getIslandAt(e.getPlayer().getLocation()).ifPresent(i -> 
        show.showBorder(e.getPlayer(), i)));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent e) {
        show.clearUser(User.getInstance(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        show.clearUser(User.getInstance(e.getPlayer()));
        Bukkit.getScheduler().runTask(addon.getPlugin(), () -> addon.getIslands().getIslandAt(e.getPlayer().getLocation()).ifPresent(i ->
        show.showBorder(e.getPlayer(), i)));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        Location to = e.getTo();

        show.clearUser(User.getInstance(player));

        if (!addon.inGameWorld(to.getWorld())) {
            return;
        }

        TeleportCause cause = e.getCause();
        boolean isBlacklistedCause = cause == TeleportCause.ENDER_PEARL || cause == TeleportCause.CHORUS_FRUIT;

        Bukkit.getScheduler().runTask(addon.getPlugin(), () ->
        addon.getIslands().getIslandAt(to).ifPresentOrElse(i -> {
            Optional<Flag> boxedEnderPearlFlag = i.getPlugin().getFlagsManager().getFlag("ALLOW_MOVE_BOX");

            if (isBlacklistedCause
                    && (!i.getProtectionBoundingBox().contains(to.toVector())
                            || !i.onIsland(player.getLocation()))) {
                e.setCancelled(true);
            }

            if (boxedEnderPearlFlag.isPresent()
                    && boxedEnderPearlFlag.get().isSetForWorld(to.getWorld())
                    && cause == TeleportCause.ENDER_PEARL) {
                e.setCancelled(false);
            }

            show.showBorder(player, i);
        }, () -> {
            if (isBlacklistedCause) {
                e.setCancelled(true);
                return;
            }
        })
                );
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
                if (!e.getPlayer().isFlying() && addon.getSettings().isReturnTeleportBlock()
                        && !addon.getIslands().isSafeLocation(targetPos)) {
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
                || user.getPlayer().getGameMode() == GameMode.SPECTATOR
                // || !addon.getIslands().getIslandAt(to).filter(i -> addon.getIslands().locationIsOnIsland(player, i.getProtectionCenter())).isPresent()
                || !user.getMetaData(BorderShower.BORDER_STATE_META_DATA).map(MetaDataValue::asBoolean).orElse(addon.getSettings().isShowByDefault())) {
            return false;
        }
        return addon.getIslands().getIslandAt(to).filter(i -> !i.onIsland(to)).isPresent();
    }

    /**
     * Refreshes the barrier view when the player moves (more than just moving their head)
     * @param e event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent e) {
        // Remove head movement
        if (!e.getFrom().toVector().equals(e.getTo().toVector())) {
            addon.getIslands()
            .getIslandAt(e.getPlayer().getLocation())
            .ifPresent(i -> show.refreshView(User.getInstance(e.getPlayer()), i));
        }
    }

    /**
     * Refresh the view when riding in a vehicle
     * @param e event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onVehicleMove(VehicleMoveEvent e) {
        // Remove head movement
        if (!e.getFrom().toVector().equals(e.getTo().toVector())) {
            e.getVehicle().getPassengers().stream()
            .filter(Player.class::isInstance)
            .map(Player.class::cast)
            .forEach(p -> addon
                    .getIslands()
                    .getIslandAt(p.getLocation())
                    .ifPresent(i -> show.refreshView(User.getInstance(p), i)));
        }
    }

    /**
     * Hide and then show the border to react to the change in protection area
     * @param e
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onProtectionRangeChange(IslandProtectionRangeChangeEvent e) {
        // Hide and show again
        e.getIsland().getPlayersOnIsland().forEach(player -> {
            show.hideBorder(User.getInstance(player));
            show.showBorder(player, e.getIsland());
        });
    }
}
