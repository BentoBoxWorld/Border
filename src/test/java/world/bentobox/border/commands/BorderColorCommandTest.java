package world.bentobox.border.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.managers.CommandsManager;
import world.bentobox.bentobox.util.Util;
import world.bentobox.border.Border;
import world.bentobox.border.CommonTestSetup;
import world.bentobox.border.PerPlayerBorderProxy;
import world.bentobox.border.Settings;
import world.bentobox.border.listeners.BorderShower;

/**
 * Tests for {@link BorderColorCommand}
 */
public class BorderColorCommandTest extends CommonTestSetup {

    @Mock
    private CompositeCommand ac;
    @Mock
    private User user;
    @Mock
    private Border addon;
    @Mock
    private BorderShower bs;

    private BorderColorCommand ic;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        // Command manager
        CommandsManager cm = mock(CommandsManager.class);
        when(plugin.getCommandsManager()).thenReturn(cm);

        // Player
        Player p = mock(Player.class);
        when(user.isOp()).thenReturn(false);
        when(user.getPermissionValue(anyString(), anyInt())).thenReturn(4);
        when(user.getWorld()).thenReturn(world);
        uuid = UUID.randomUUID();
        when(user.getUniqueId()).thenReturn(uuid);
        when(user.getPlayer()).thenReturn(p);
        when(user.getName()).thenReturn("tastybento");
        when(user.getTranslation(any())).thenAnswer(invocation -> invocation.getArgument(0, String.class));
        User.setPlugin(plugin);

        // Parent command has no aliases
        when(ac.getSubCommandAliases()).thenReturn(new HashMap<>());
        when(ac.getWorld()).thenReturn(world);

        // Util - return what was put into the method
        mockedUtil.when(() -> Util.getWorld(any())).thenAnswer(invocation -> invocation.getArgument(0, World.class));

        // Islands
        when(im.getIsland(world, user)).thenReturn(island);

        // IWM
        when(iwm.getPermissionPrefix(any())).thenReturn("bskyblock.");

        // Shower
        when(addon.getBorderShower()).thenReturn(bs);

        // Settings
        Settings settings = new Settings();
        when(addon.getSettings()).thenReturn(settings);

        ic = new BorderColorCommand(addon, ac, "color");
        // Pre-populate the island field (canExecute sets it; execute depends on it)
        ic.canExecute(user, "", Collections.emptyList());
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link BorderColorCommand#setup()}.
     */
    @Test
    public void testSetup() {
        assertEquals("border.color", ic.getPermission());
        assertEquals("border.set-color.description", ic.getDescription());
        assertTrue(ic.isOnlyPlayer());
    }

    /**
     * Test method for {@link BorderColorCommand#canExecute(User, String, List)}.
     */
    @Test
    public void testCanExecuteWrongWorld() {
        when(user.getWorld()).thenReturn(mock(World.class));
        assertFalse(ic.canExecute(user, "", Collections.emptyList()));
        verify(user).sendMessage("general.errors.wrong-world");
    }

    /**
     * Test method for {@link BorderColorCommand#canExecute(User, String, List)}.
     */
    @Test
    public void testCanExecuteNoIsland() {
        when(im.getIsland(world, user)).thenReturn(null);
        assertFalse(ic.canExecute(user, "", Collections.emptyList()));
        verify(user, never()).sendMessage("general.errors.wrong-world");
    }

    /**
     * Test method for {@link BorderColorCommand#canExecute(User, String, List)}.
     */
    @Test
    public void testCanExecuteOk() {
        assertTrue(ic.canExecute(user, "", Collections.emptyList()));
        verify(user, never()).sendMessage("general.errors.wrong-world");
    }

    /**
     * Test method for {@link BorderColorCommand#execute(User, String, List)}.
     * No arguments should show help.
     */
    @Test
    public void testExecuteNoArgs() {
        assertFalse(ic.execute(user, "", Collections.emptyList()));
        verify(user).sendMessage("commands.help.header", "[label]", "BSkyBlock");
    }

    /**
     * Test method for {@link BorderColorCommand#execute(User, String, List)}.
     * Too many arguments should show help.
     */
    @Test
    public void testExecuteTooManyArgs() {
        assertFalse(ic.execute(user, "", List.of("red", "extra")));
        verify(user).sendMessage("commands.help.header", "[label]", "BSkyBlock");
    }

    /**
     * Test method for {@link BorderColorCommand#execute(User, String, List)}.
     * Unknown color should send error message.
     */
    @Test
    public void testExecuteInvalidColor() {
        assertFalse(ic.execute(user, "", List.of("purple")));
        verify(user).sendMessage("border.set-color.error-invalid-color");
    }

    /**
     * Test method for {@link BorderColorCommand#execute(User, String, List)}.
     * No permission for the specific color should deny.
     */
    @Test
    public void testExecuteNoPermission() {
        when(user.hasPermission("bskyblock.border.color.red")).thenReturn(false);
        assertFalse(ic.execute(user, "", List.of("red")));
        verify(user).sendMessage(eq("general.errors.no-permission"), anyString(), anyString());
    }

    /**
     * Test method for {@link BorderColorCommand#execute(User, String, List)}.
     * Successfully set red.
     */
    @Test
    public void testExecuteSetRed() {
        when(user.hasPermission("bskyblock.border.color.red")).thenReturn(true);
        assertTrue(ic.execute(user, "", List.of("red")));
        verify(user).putMetaData(eq(PerPlayerBorderProxy.BORDER_COLOR_META_DATA), any());
        verify(bs).hideBorder(user);
        verify(bs).showBorder(any(Player.class), eq(island));
        verify(user).sendMessage("border.set-color.changed", "[color]", "red");
    }

    /**
     * Test method for {@link BorderColorCommand#execute(User, String, List)}.
     * Successfully set green.
     */
    @Test
    public void testExecuteSetGreen() {
        when(user.hasPermission("bskyblock.border.color.green")).thenReturn(true);
        assertTrue(ic.execute(user, "", List.of("green")));
        verify(user).putMetaData(eq(PerPlayerBorderProxy.BORDER_COLOR_META_DATA), any());
        verify(user).sendMessage("border.set-color.changed", "[color]", "green");
    }

    /**
     * Test method for {@link BorderColorCommand#execute(User, String, List)}.
     * Successfully set blue.
     */
    @Test
    public void testExecuteSetBlue() {
        when(user.hasPermission("bskyblock.border.color.blue")).thenReturn(true);
        assertTrue(ic.execute(user, "", List.of("blue")));
        verify(user).putMetaData(eq(PerPlayerBorderProxy.BORDER_COLOR_META_DATA), any());
        verify(user).sendMessage("border.set-color.changed", "[color]", "blue");
    }

    /**
     * Test method for {@link BorderColorCommand#execute(User, String, List)}.
     * Color argument should be case-insensitive.
     */
    @Test
    public void testExecuteCaseInsensitive() {
        when(user.hasPermission("bskyblock.border.color.red")).thenReturn(true);
        assertTrue(ic.execute(user, "", List.of("RED")));
        verify(user).sendMessage("border.set-color.changed", "[color]", "red");
    }

    /**
     * Test method for {@link BorderColorCommand#tabComplete(User, String, List)}.
     * All colors returned when user has all permissions.
     */
    @Test
    public void testTabCompleteAllPermissions() {
        when(user.hasPermission(anyString())).thenReturn(true);
        Optional<List<String>> result = ic.tabComplete(user, "", Collections.emptyList());
        assertTrue(result.isPresent());
        List<String> colors = result.get();
        assertTrue(colors.contains("red"));
        assertTrue(colors.contains("green"));
        assertTrue(colors.contains("blue"));
        assertEquals(3, colors.size());
    }

    /**
     * Test method for {@link BorderColorCommand#tabComplete(User, String, List)}.
     * No colors returned when user has no permissions.
     */
    @Test
    public void testTabCompleteNoPermissions() {
        when(user.hasPermission(anyString())).thenReturn(false);
        Optional<List<String>> result = ic.tabComplete(user, "", Collections.emptyList());
        assertTrue(result.isPresent());
        assertTrue(result.get().isEmpty());
    }

    /**
     * Test method for {@link BorderColorCommand#tabComplete(User, String, List)}.
     * Only permitted colors are returned.
     */
    @Test
    public void testTabCompletePartialPermissions() {
        when(user.hasPermission(anyString())).thenReturn(false);
        when(user.hasPermission("bskyblock.border.color.red")).thenReturn(true);
        Optional<List<String>> result = ic.tabComplete(user, "", Collections.emptyList());
        assertTrue(result.isPresent());
        assertEquals(List.of("red"), result.get());
    }
}
