package world.bentobox.border.listeners;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.events.island.IslandProtectionRangeChangeEvent;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.IslandsManager;
import world.bentobox.bentobox.util.Util;
import world.bentobox.border.Border;
import world.bentobox.border.PerPlayerBorderProxy;
import world.bentobox.border.Settings;

/**
 * @author tastybento
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, BentoBox.class, User.class, Util.class })
public class PlayerListenerTest {
    
    @Mock
    private BentoBox plugin;
    @Mock
    private Border addon;
    @Mock
    private Player player;
    @Mock
    private User user;
    
    private PlayerListener pl;
    @Mock
    private BorderShower show;
    @Mock
    private Location from;
    @Mock
    private Location to;
    private Settings settings;
    @Mock
    private World world;
    @Mock
    private IslandsManager im;
    @Mock
    private Island island;
    @Mock
    private Vehicle vehicle;


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        
        PowerMockito.mockStatic(Bukkit.class, Mockito.RETURNS_MOCKS);
        
        PowerMockito.mockStatic(User.class, Mockito.RETURNS_MOCKS);
        when(User.getInstance(any(Player.class))).thenReturn(user);
        
        // Border Shower
        when(addon.getBorderShower()).thenReturn(show);
        
        // Settings
        settings = new Settings();
        when(addon.getSettings()).thenReturn(settings);
        
        // Locations
        when(to.getWorld()).thenReturn(world);
        when(from.getWorld()).thenReturn(world);
        when(from.toVector()).thenReturn(new Vector(1,2,3));
        when(to.toVector()).thenReturn(new Vector(6,7,8));
        
        // In game world
        when(addon.inGameWorld(any())).thenReturn(true);
        
        // User
        when(user.getPlayer()).thenReturn(player);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.getWorld()).thenReturn(world);
        when(user.getMetaData(anyString())).thenReturn(Optional.empty()); // No meta data
        when(player.getLocation()).thenReturn(to);
        
        // Islands
        when(island.onIsland(any())).thenReturn(true); // Default on island
        when(im.getIslandAt(any())).thenReturn(Optional.of(island));
        when(addon.getIslands()).thenReturn(im);
        when(im.getProtectedIslandAt(any())).thenReturn(Optional.of(island));
        when(island.getProtectionCenter()).thenReturn(from);
        when(island.getProtectionBoundingBox()).thenReturn(BoundingBox.of(new Vector(0,0,0), new Vector(50,50,50)));
        when(island.getRange()).thenReturn(3);
        when(im.isSafeLocation(any())).thenReturn(true); // safe for now
        when(island.getPlayersOnIsland()).thenReturn(List.of(player));
        
        // Vehicle
        when(vehicle.getPassengers()).thenReturn(List.of(player));
        
        // Util
        PowerMockito.mockStatic(Util.class, Mockito.RETURNS_MOCKS);
        
        pl = new PlayerListener(addon);
        
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#PlayerListener(world.bentobox.border.Border)}.
     */
    @Test
    public void testPlayerListener() {
        assertNotNull(pl);
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#processEvent(PlayerJoinEvent)}.
     */
    @Test
    public void testOnPlayerJoinNoPerms() {
        PlayerJoinEvent event = new PlayerJoinEvent(player, "");
        pl.processEvent(event);
        verify(user).putMetaData(eq(BorderShower.BORDER_STATE_META_DATA), any());
        verify(user).putMetaData(eq(PerPlayerBorderProxy.BORDER_BORDERTYPE_META_DATA), any());
        PowerMockito.verifyStatic(Bukkit.class);
        Bukkit.getScheduler();
        verify(show).hideBorder(user);
        verify(player).setWorldBorder(null);
        
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent)}.
     */
    @Test
    public void testOnPlayerQuit() {
        PlayerQuitEvent event = new PlayerQuitEvent(player, "");
        pl.onPlayerQuit(event);
        verify(show).clearUser(user);
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#onPlayerRespawn(org.bukkit.event.player.PlayerRespawnEvent)}.
     */
    @Test
    public void testOnPlayerRespawn() {
        PlayerRespawnEvent event = new PlayerRespawnEvent(player, null, false, false);
        pl.onPlayerRespawn(event);
        PowerMockito.verifyStatic(Bukkit.class);
        Bukkit.getScheduler();
        verify(show).clearUser(user);
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#onPlayerTeleport(org.bukkit.event.player.PlayerTeleportEvent)}.
     */
    @Test
    public void testOnPlayerTeleportNotInGameWorld() {
        when(addon.inGameWorld(any())).thenReturn(false);
        PlayerTeleportEvent event = new PlayerTeleportEvent(player, from, to, TeleportCause.NETHER_PORTAL);
        pl.onPlayerTeleport(event);
        verify(show).clearUser(user);
        PowerMockito.verifyStatic(Bukkit.class, never());
        Bukkit.getScheduler();
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#onPlayerTeleport(org.bukkit.event.player.PlayerTeleportEvent)}.
     */
    @Test
    public void testOnPlayerTeleportInGameWorld() {
        when(addon.inGameWorld(any())).thenReturn(true);
        PlayerTeleportEvent event = new PlayerTeleportEvent(player, from, to, TeleportCause.NETHER_PORTAL);
        pl.onPlayerTeleport(event);
        verify(show).clearUser(user);
        PowerMockito.verifyStatic(Bukkit.class);
        Bukkit.getScheduler();
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#onPlayerLeaveIsland(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerLeaveIslandNoReturnTeleport() {
        settings.setReturnTeleport(false);
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        pl.onPlayerLeaveIsland(event);
        verify(addon, never()).getIslands();
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#onPlayerLeaveIsland(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerLeaveIslandReturnTeleportOutsideCheckSameXZ() {
        
        when(to.toVector()).thenReturn(new Vector(1,2,3)); // Same as from
        settings.setReturnTeleport(true);
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        pl.onPlayerLeaveIsland(event);
        verify(addon, never()).getIslands();
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#onPlayerLeaveIsland(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerLeaveIslandReturnTeleportOutsideCheckNotInGameWorld() {
        when(addon.inGameWorld(any())).thenReturn(false);
        settings.setReturnTeleport(true);
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        pl.onPlayerLeaveIsland(event);
        verify(addon, never()).getIslands();
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#onPlayerLeaveIsland(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerLeaveIslandReturnTeleportOutsideSpectator() {
        when(player.getGameMode()).thenReturn(GameMode.SPECTATOR);
        settings.setReturnTeleport(true);
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        pl.onPlayerLeaveIsland(event);
        verify(addon, never()).getIslands();
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#onPlayerLeaveIsland(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerLeaveIslandReturnTeleportOutsideShowByDefaultFalse() {
        settings.setShowByDefault(false);
        settings.setReturnTeleport(true);
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        pl.onPlayerLeaveIsland(event);
        verify(addon, never()).getIslands();
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#onPlayerLeaveIsland(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerLeaveIslandReturnTeleporOnIsland() {
        when(island.onIsland(any())).thenReturn(true);
        settings.setReturnTeleport(true);
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        pl.onPlayerLeaveIsland(event);
        verify(addon).getIslands(); // One time
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#onPlayerLeaveIsland(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerLeaveIslandReturnTeleportOutsideIsland() {
        when(island.onIsland(any())).thenReturn(false);
        settings.setReturnTeleport(true);
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        pl.onPlayerLeaveIsland(event);
        verify(addon, times(2)).getIslands();
        assertTrue(event.isCancelled());
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#onPlayerLeaveIsland(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerLeaveIslandReturnTeleportWaaayOutsideIsland() {
        // Need to backtrack to nearest island
        when(im.getProtectedIslandAt(any())).thenReturn(Optional.empty());
        when(island.onIsland(any())).thenReturn(false);
        settings.setReturnTeleport(true);
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        pl.onPlayerLeaveIsland(event);
        verify(addon, times(4)).getIslands();
        assertFalse(event.isCancelled());
        PowerMockito.verifyStatic(Util.class);
        Util.teleportAsync(any(), any());
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMove() {
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        pl.onPlayerMove(event);
        verify(show).refreshView(any(),any());
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)}.
     */
    @Test
    public void testOnPlayerMoveHeadOnly() {
        when(to.toVector()).thenReturn(new Vector(1,2,3));// Same as from
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        pl.onPlayerMove(event);
        verify(show, never()).refreshView(any(),any());
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#onVehicleMove(org.bukkit.event.vehicle.VehicleMoveEvent)}.
     */
    @Test
    public void testOnVehicleMove() {
        VehicleMoveEvent event = new VehicleMoveEvent(vehicle, from, to);
        pl.onVehicleMove(event);
        verify(show).refreshView(any(),any());
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#onVehicleMove(org.bukkit.event.vehicle.VehicleMoveEvent)}.
     */
    @Test
    public void testOnVehicleMoveHeadOnly() {
        when(to.toVector()).thenReturn(new Vector(1,2,3));// Same as from
        VehicleMoveEvent event = new VehicleMoveEvent(vehicle, from, to);
        pl.onVehicleMove(event);
        verify(show, never()).refreshView(any(),any());
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.PlayerListener#onProtectionRangeChange(world.bentobox.bentobox.api.events.island.IslandProtectionRangeChangeEvent)}.
     */
    @Test
    public void testOnProtectionRangeChange() {
        UUID uuid = UUID.randomUUID();
        IslandProtectionRangeChangeEvent event = new IslandProtectionRangeChangeEvent(island, uuid, false, from, 0, 0);
        pl.onProtectionRangeChange(event);
        verify(show).hideBorder(user);
        verify(show).showBorder(player, island);
    }

}
