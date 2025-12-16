package world.bentobox.border.listeners;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.eclipse.jdt.annotation.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.util.Util;
import world.bentobox.border.Border;
import world.bentobox.border.CommonTestSetup;
import world.bentobox.border.Settings;

/**
 * @author tastybento
 *
 */
public class ShowBarrierTest extends CommonTestSetup {
    @Mock
    private Border addon;
    @Mock
    private Player player;
    
    private ShowBarrier sb;
    private Settings settings;
    @Mock
    private @NonNull User user;
    @Mock
    private @NonNull Location center;
    
    private MockedStatic<User> mockedUser;
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        settings = new Settings();
        settings.setShowMaxBorder(false);
        when(addon.getSettings()).thenReturn(settings);
        
        // Island
        when(island.getGameMode()).thenReturn("bskyblock");
        when(island.getMinX()).thenReturn(-400);
        when(island.getMinZ()).thenReturn(-400);
        when(island.getMaxX()).thenReturn(400);
        when(island.getMaxZ()).thenReturn(400);
        when(island.getMinProtectedX()).thenReturn(-100);
        when(island.getMinProtectedZ()).thenReturn(-100);
        when(island.getMaxProtectedX()).thenReturn(100);
        when(island.getMaxProtectedZ()).thenReturn(100);
        when(center.toVector()).thenReturn(new Vector(0,0,0));
        when(center.clone()).thenReturn(center);
        when(island.getCenter()).thenReturn(center);
        
        // Island Manager
        when(addon.getIslands()).thenReturn(im);
        when(im.getIslandAt(any(Location.class))).thenReturn(Optional.of(island));
        
        // User
        mockedUser = Mockito.mockStatic(User.class, Mockito.RETURNS_MOCKS);
        mockedUser.when(() -> User.getInstance(any(Player.class))).thenReturn(user);
        when(user.getMetaData(anyString())).thenReturn(Optional.empty());
        when(user.getPlayer()).thenReturn(player);
        when(location.getBlockX()).thenReturn(99);
        when(location.getBlockZ()).thenReturn(99);
        when(location.toVector()).thenReturn(new Vector(99,99,0));
        when(player.getLocation()).thenReturn(location);
        when(player.getWorld()).thenReturn(world);
        
        // Util
        Chunk chunk = mock(Chunk.class);
        CompletableFuture<Chunk> future = new CompletableFuture<>();
        future.complete(chunk);
        mockedUtil.when(() -> Util.getChunkAtAsync(any())).thenReturn(future);
        sb = new ShowBarrier(addon);
    }
    
    @Override
    @AfterEach
    public void tearDown() throws Exception {
        if (mockedUser != null) {
            mockedUser.close();
        }
        super.tearDown();
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.ShowBarrier#ShowBarrier(world.bentobox.border.Border)}.
     */
    @Test
    public void testShowBarrier() {
        assertNotNull(sb);
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.ShowBarrier#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testShowBorderNearBorder() {
        sb.showBorder(player, island);
        verify(player, times(131)).getLocation();        
        mockedUtil.verify(() ->  Util.getChunkAtAsync(any(Location.class)), times(120));
    }
        
    /**
     * Test method for {@link world.bentobox.border.listeners.ShowBarrier#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testShowBorderNearBorderNoBarrierBlocks() {
        settings.setUseBarrierBlocks(false);
        sb.showBorder(player, island);
        verify(player).getLocation();        
        mockedUtil.verify(() ->  Util.getChunkAtAsync(any(Location.class)), times(120));
        
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.ShowBarrier#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testShowBorderFarBorderWithOffset() {
        // Large offset means that the player never sees the border at this position
        settings.setBarrierOffset(50);
        sb.showBorder(player, island);
        verify(player).getLocation();        
        mockedUtil.verify(() ->  Util.getChunkAtAsync(any(Location.class)), never());
        
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.ShowBarrier#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testShowBorderNearBorderWithOffset() {
        // Large offset means that the player never sees the border at this position
        settings.setBarrierOffset(2);
        sb.showBorder(player, island);
        verify(player, times(171)).getLocation();        
        mockedUtil.verify(() ->  Util.getChunkAtAsync(any(Location.class)), times(160));
        
    }
    /**
     * Test method for {@link world.bentobox.border.listeners.ShowBarrier#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testShowBorderNotNearMaxBorder() {
        settings.setShowMaxBorder(true);
        // Not close to the max border, so the times will be the same as above
        sb.showBorder(player, island);
        verify(player, times(131)).getLocation();        
        mockedUtil.verify(() ->  Util.getChunkAtAsync(any(Location.class)), times(120));
        
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.ShowBarrier#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testShowBorderNearBorderShowMaxBorder() {
        settings.setShowMaxBorder(true);
        // Shrink max min
        when(island.getMinX()).thenReturn(-100);
        when(island.getMinZ()).thenReturn(-100);
        when(island.getMaxX()).thenReturn(100);
        when(island.getMaxZ()).thenReturn(100);
        sb.showBorder(player, island);
        // Number of times should be double
        verify(player, times(261)).getLocation();        
        mockedUtil.verify(() ->  Util.getChunkAtAsync(any(Location.class)), times(240));
        
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.ShowBarrier#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testShowBorderNearBorder2() {
        when(location.getBlockX()).thenReturn(0);
        when(location.getBlockZ()).thenReturn(99);
        when(location.toVector()).thenReturn(new Vector(0,0,99));
        sb.showBorder(player, island);
        verify(player, times(111)).getLocation();        
        mockedUtil.verify(() ->  Util.getChunkAtAsync(any(Location.class)), times(100));
        
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.ShowBarrier#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testShowBorderNearBorder3() {
        when(location.getBlockX()).thenReturn(99);
        when(location.getBlockZ()).thenReturn(0);
        when(location.toVector()).thenReturn(new Vector(99,0,0));
        sb.showBorder(player, island);
        verify(player, times(101)).getLocation();        
        mockedUtil.verify(() ->  Util.getChunkAtAsync(any(Location.class)), times(100));
        
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.ShowBarrier#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testShowBorderNearBorder4() {
        when(location.getBlockX()).thenReturn(0);
        when(location.getBlockZ()).thenReturn(-99);
        when(location.toVector()).thenReturn(new Vector(0,0,-99));
        sb.showBorder(player, island);
        verify(player, times(111)).getLocation();        
        mockedUtil.verify(() ->  Util.getChunkAtAsync(any(Location.class)), times(100));
        
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.ShowBarrier#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testShowBorderNearBorder5() {
        when(location.getBlockX()).thenReturn(-99);
        when(location.getBlockZ()).thenReturn(0);
        when(location.toVector()).thenReturn(new Vector(-99,0,0));
        sb.showBorder(player, island);
        verify(player, times(101)).getLocation();        
        mockedUtil.verify(() ->  Util.getChunkAtAsync(any(Location.class)), times(100));
        
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.ShowBarrier#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testShowBorderAwayFromBorder() {
        when(location.getBlockX()).thenReturn(0);
        when(location.getBlockZ()).thenReturn(0);
        when(location.toVector()).thenReturn(new Vector(0,0,0));
        sb.showBorder(player, island);
        verify(player).getLocation();        
        mockedUtil.verify(() ->  Util.getChunkAtAsync(any(Location.class)), never());
        
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.ShowBarrier#hideBorder(world.bentobox.bentobox.api.user.User)}.
     */
    @Test
    public void testHideBorder() {
        // Nothing to hide
        sb.hideBorder(user);
        verify(user).getUniqueId();
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.ShowBarrier#clearUser(world.bentobox.bentobox.api.user.User)}.
     */
    @Test
    public void testClearUser() {
        sb.clearUser(user);
        verify(user).getUniqueId();
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.ShowBarrier#refreshView(world.bentobox.bentobox.api.user.User, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testRefreshView() {
        sb.refreshView(user, island);
        verify(user).getPlayer();
    }

}
