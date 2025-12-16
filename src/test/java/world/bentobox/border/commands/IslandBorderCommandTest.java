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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.managers.CommandsManager;
import world.bentobox.bentobox.managers.PlayersManager;
import world.bentobox.bentobox.util.Util;
import world.bentobox.border.Border;
import world.bentobox.border.BorderType;
import world.bentobox.border.CommonTestSetup;
import world.bentobox.border.Settings;
import world.bentobox.border.listeners.BorderShower;

/**
 * @author tastybento
 *
 */
public class IslandBorderCommandTest extends CommonTestSetup {

    @Mock
    private CompositeCommand ac;
    @Mock
    private User user;
    @Mock
    private Border addon;

    private final Set<BorderType> availableBorderTypes = EnumSet.of(BorderType.VANILLA, BorderType.BARRIER);

    private IslandBorderCommand ic;
    @Mock
    private BorderShower bs;
    @Mock
    private PlayersManager pm;

    /**
     * @throws java.lang.Exception
     */
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        // Command manager
        CommandsManager cm = mock(CommandsManager.class);
        when(plugin.getCommandsManager()).thenReturn(cm);

        // Player
        Player p = mock(Player.class);
        // Sometimes use Mockito.withSettings().verboseLogging()
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

        // List of types
        when(addon.getAvailableBorderTypesView()).thenReturn(availableBorderTypes);

        // Util
         // Return what was put into the method
        mockedUtil.when(() -> Util.getWorld(any())).thenAnswer(invocation -> invocation.getArgument(0, World.class));

        // Islands
        when(addon.getIslands()).thenReturn(im);
        when(im.getIsland(world, user)).thenReturn(island);
        when(im.getIslandAt(any())).thenReturn(Optional.of(island));

        // IWM
        when(iwm.getPermissionPrefix(any())).thenReturn("bskyblock.");

        // Shower
        when(addon.getBorderShower()).thenReturn(bs);

        // Settings
        Settings settings = new Settings();
        when(addon.getSettings()).thenReturn(settings);

        // Players Manager
        when(addon.getPlayers()).thenReturn(pm);

        ic = new IslandBorderCommand(addon, ac, "");
    }

    /**
     * @throws java.lang.Exception
     */
    @Override
    @AfterEach
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link world.bentobox.border.commands.IslandBorderCommand#setup()}.
     */
    @Test
    public void testSetup() {
        assertEquals("border.toggle", ic.getPermission());
        assertEquals("border.toggle.description", ic.getDescription());
        assertTrue(ic.isOnlyPlayer());
        assertTrue(ic.isConfigurableRankCommand());
        // Help and the type command
        assertEquals(2,ic.getSubCommands().size());
    }

    /**
     * Test method for {@link world.bentobox.border.commands.BorderTypeCommand#canExecute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testCanExecuteWrongWorld() {
        when(user.getWorld()).thenReturn(mock(World.class));
        assertFalse(ic.canExecute(user, "", Collections.emptyList()));
        verify(user).sendMessage("general.errors.wrong-world");
    }

    /**
     * Test method for {@link world.bentobox.border.commands.BorderTypeCommand#canExecute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testCanExecuteNoIsland() {
        when(im.getIslandAt(any())).thenReturn(Optional.empty());
        assertFalse(ic.canExecute(user, "", Collections.emptyList()));
        verify(user, never()).sendMessage("general.errors.wrong-world");
    }

    /**
     * Test method for {@link world.bentobox.border.commands.BorderTypeCommand#canExecute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testCanExecuteOk() {
        assertTrue(ic.canExecute(user, "", Collections.emptyList()));
        verify(user, never()).sendMessage("general.errors.wrong-world");
    }

    /**
     * Test method for {@link world.bentobox.border.commands.BorderTypeCommand#execute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testExecuteUserStringListOfStringDefault() {
        // Uses the default in settings
        assertTrue(ic.execute(user, "", Collections.emptyList()));
        verify(user).getMetaData("Border_state");
        verify(user).sendMessage("border.toggle.border-off");
        verify(user).putMetaData(eq("Border_state"), any());
    }
    /**
     * Test method for {@link world.bentobox.border.commands.BorderTypeCommand#execute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testExecuteUserStringListOfStringToggle() {
        when(user.getMetaData(BorderShower.BORDER_STATE_META_DATA)).thenReturn(Optional.of(new MetaDataValue(true)));
        assertTrue(ic.execute(user, "", Collections.emptyList()));
        verify(user).getMetaData("Border_state");
        verify(user).sendMessage("border.toggle.border-off");
        verify(user).putMetaData(eq("Border_state"), any());
        // Toggle
        when(user.getMetaData(BorderShower.BORDER_STATE_META_DATA)).thenReturn(Optional.of(new MetaDataValue(false)));
        assertTrue(ic.execute(user, "", Collections.emptyList()));
        verify(user).sendMessage("border.toggle.border-on");
    }


}
