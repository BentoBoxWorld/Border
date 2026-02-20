package world.bentobox.border.listeners;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.WorldBorder;
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
import world.bentobox.border.Border;
import world.bentobox.border.CommonTestSetup;
import world.bentobox.border.Settings;

/**
 * @author tastybento
 *
 */
public class ShowWorldBorderTest extends CommonTestSetup {
    @Mock
    private Border addon;
    private Settings settings;
    private ShowWorldBorder svwb;
    @Mock
    private @NonNull User user;
    @Mock
    private WorldBorder wb;



    /**
     * @throws java.lang.Exception - exception
     */
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        settings = new Settings();
        when(addon.getSettings()).thenReturn(settings);
        
        // Island
        when(island.getRange()).thenReturn(400);
        when(island.getProtectionRange()).thenReturn(100);
        when(island.getProtectionCenter()).thenReturn(location);
                
        // User
        MockedStatic<User> mockedUser = Mockito.mockStatic(User.class, Mockito.RETURNS_MOCKS);
        mockedUser.when(() -> User.getInstance(any(Player.class))).thenReturn(user);
        when(user.getPlayer()).thenReturn(mockPlayer);
        when(location.toVector()).thenReturn(new Vector(0,0,0));
        when(mockPlayer.getLocation()).thenReturn(location);
        when(mockPlayer.getWorld()).thenReturn(world);
                
        // Bukkit
        mockedBukkit.when(Bukkit::createWorldBorder).thenReturn(wb);
        
        svwb = new ShowWorldBorder(addon);
    }
    
    @Override
    @AfterEach
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testShowVirtualWorldBorder() {
        assertNotNull(svwb);
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.ShowWorldBorder#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testShowBorder() {
        svwb.showBorder(mockPlayer, island);
        verify(wb).setSize(200.0D);
        
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.ShowWorldBorder#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testShowBorderWithOffset() {
        settings.setBarrierOffset(10);
        svwb.showBorder(mockPlayer, island);
        verify(wb).setSize(220.0D);
        
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.ShowWorldBorder#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     */
    @Test
    public void testShowBorderWithLargeOffset() {
        settings.setBarrierOffset(10000);
        svwb.showBorder(mockPlayer, island);
        verify(wb).setSize(800.0D); // Max size
        
    }

    /**
     * Test method for {@link world.bentobox.border.listeners.ShowWorldBorder#hideBorder(world.bentobox.bentobox.api.user.User)}.
     */
    @Test
    public void testHideBorder() {
        // Nothing to hide
        svwb.hideBorder(user);
        verify(mockPlayer).setWorldBorder(null);
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.ShowWorldBorder#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     * Tests that border is shown when player is in an island nether world.
     */
    @Test
    public void testShowBorderInIslandNetherWorld() {
        // Setup: Player is in a nether environment that IS an island nether world
        when(world.getEnvironment()).thenReturn(Environment.NETHER);
        when(iwm.isIslandNether(world)).thenReturn(true);
        when(addon.getPlugin()).thenReturn(plugin);
        
        svwb.showBorder(mockPlayer, island);
        
        // Verify that the border was set (border should show in island nether worlds)
        verify(mockPlayer).setWorldBorder(wb);
        verify(wb).setSize(200.0D);
    }
    
    /**
     * Test method for {@link world.bentobox.border.listeners.ShowWorldBorder#showBorder(org.bukkit.entity.Player, world.bentobox.bentobox.database.objects.Island)}.
     * Tests that border is NOT shown when player is in a non-island nether world.
     */
    @Test
    public void testShowBorderInNonIslandNetherWorld() {
        // Setup: Player is in a nether environment that is NOT an island nether world
        when(world.getEnvironment()).thenReturn(Environment.NETHER);
        when(iwm.isIslandNether(world)).thenReturn(false);
        when(addon.getPlugin()).thenReturn(plugin);
        
        svwb.showBorder(mockPlayer, island);
        
        // Verify that the border was NOT set (border should not show in non-island nether worlds)
        verify(mockPlayer, never()).setWorldBorder(any());
    }
    
}
