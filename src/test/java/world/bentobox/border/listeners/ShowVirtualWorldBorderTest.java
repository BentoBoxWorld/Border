package world.bentobox.border.listeners;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.eclipse.jdt.annotation.NonNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.border.Border;
import world.bentobox.border.Settings;

/**
 * @author tastybento
 *
 */
@PrepareForTest({Bukkit.class, User.class})
@RunWith(PowerMockRunner.class)
public class ShowVirtualWorldBorderTest {
    @Mock
    private Border addon;
    private Settings settings;
    private ShowVirtualWorldBorder svwb;
    @Mock
    private Player player;
    @Mock
    private Island island;
    @Mock
    private @NonNull User user;
    @Mock
    private Location location;
    @Mock
    private World world;
    @Mock
    private WorldBorder wb;



    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        settings = new Settings();
        when(addon.getSettings()).thenReturn(settings);
        
        // Island
        when(island.getRange()).thenReturn(400);
        when(island.getProtectionRange()).thenReturn(100);
                
        // User
        PowerMockito.mockStatic(User.class, Mockito.RETURNS_MOCKS);
        when(User.getInstance(any(Player.class))).thenReturn(user);
        when(user.getPlayer()).thenReturn(player);
        when(player.getLocation()).thenReturn(location);
        when(player.getWorld()).thenReturn(world);
                
        // Bukkit
        PowerMockito.mockStatic(Bukkit.class, Mockito.RETURNS_MOCKS);
        when(Bukkit.createWorldBorder()).thenReturn(wb);
        
        svwb = new ShowVirtualWorldBorder(addon);
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.ShowVirtualWorldBorder#ShowVirtualWorldBorder(world.bentobox.border.Border)}.
     */
    @Test
    public void testShowVirtualWorldBorder() {
        assertNotNull(svwb);
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.ShowVirtualWorldBorder#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testShowBorder() {
        svwb.showBorder(player, island);
        verify(wb).setSize(200.0D);
        
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.ShowVirtualWorldBorder#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testShowBorderWithOffset() {
        settings.setBarrierOffset(10);
        svwb.showBorder(player, island);
        verify(wb).setSize(220.0D);
        
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.ShowVirtualWorldBorder#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testShowBorderWithLargeOffset() {
        settings.setBarrierOffset(10000);
        svwb.showBorder(player, island);
        verify(wb).setSize(800.0D); // Max size
        
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.ShowVirtualWorldBorder#hideBorder(world.bentobox.bentobox.api.user.User)}.
     */
    @Test
    public void testHideBorder() {
        // Nothing to hide
        svwb.hideBorder(user);
        verify(player).setWorldBorder(null);
    }
    
}
