package world.bentobox.border.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.CommandsManager;
import world.bentobox.bentobox.managers.IslandWorldManager;
import world.bentobox.bentobox.managers.IslandsManager;
import world.bentobox.bentobox.managers.LocalesManager;
import world.bentobox.bentobox.util.Util;
import world.bentobox.border.Border;
import world.bentobox.border.BorderType;
import world.bentobox.border.Settings;
import world.bentobox.border.listeners.BorderShower;

/**
 * @author tastybento
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, BentoBox.class, User.class, Util.class })
public class BorderTypeCommandTest {
    @Mock
    private BentoBox plugin;
    @Mock
    private CompositeCommand ac;
    @Mock
    private User user;
    @Mock
    private LocalesManager lm;
    @Mock
    private Border addon;

    private final Set<BorderType> availableBorderTypes = EnumSet.of(BorderType.VANILLA, BorderType.BARRIER);



    private BorderTypeCommand ic;
    private UUID uuid;
    @Mock
    private World world;
    @Mock
    private IslandsManager im;
    @Mock
    private @Nullable Island island;
    @Mock
    private IslandWorldManager iwm;
    @Mock
    private BorderShower bs;


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        // Set up plugin
        BentoBox plugin = mock(BentoBox.class);
        Whitebox.setInternalState(BentoBox.class, "instance", plugin);

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
        PowerMockito.mockStatic(Util.class, Mockito.RETURNS_MOCKS);
        // Return what was put into the method
        when(Util.getWorld(any())).thenAnswer(invocation -> invocation.getArgument(0, World.class));

        // Islands
        when(plugin.getIslands()).thenReturn(im);
        when(im.getIsland(world, user)).thenReturn(island);

        // IWM
        when(plugin.getIWM()).thenReturn(iwm);
        when(iwm.getPermissionPrefix(any())).thenReturn("bskyblock.");

        // Shower
        when(addon.getBorderShower()).thenReturn(bs);
        
        
        // Settings
        Settings settings = new Settings();
        when(addon.getSettings()).thenReturn(settings);


        ic = new BorderTypeCommand(addon, ac, "type");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link world.bentobox.border.commands.BorderTypeCommand#setup()}.
     */
    @Test
    public void testSetup() {
        assertEquals("border.type", ic.getPermission());
        assertEquals("border.set-type.description", ic.getDescription());
        assertTrue(ic.isOnlyPlayer());
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
        when(im.getIsland(world, user)).thenReturn(null);
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
    public void testExecuteUserStringListOfStringShowHelp() {
        assertFalse(ic.execute(user, "", List.of("arg", "two")));
        verify(user).sendMessage("commands.help.header","[label]",null);
    }

    /**
     * Test method for {@link world.bentobox.border.commands.BorderTypeCommand#execute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testExecuteUserStringListOfStringWrongType() {
        assertFalse(ic.execute(user, "", List.of("unknown")));
        verify(user).sendMessage("border.set-type.error-unavailable-type");
    }

    /**
     * Test method for {@link world.bentobox.border.commands.BorderTypeCommand#execute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testExecuteUserStringListOfStringToggle() {
        assertTrue(ic.execute(user, "", Collections.emptyList()));
        verify(user).sendMessage("border.set-type.changed","[type]","barrier");
    }

    /**
     * Test method for {@link world.bentobox.border.commands.BorderTypeCommand#execute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testExecuteUserStringListOfStringSetExplicitly() {
        assertTrue(ic.execute(user, "", List.of("barrier")));
        verify(user).sendMessage("border.set-type.changed","[type]","barrier");
        assertTrue(ic.execute(user, "", List.of("vanilla")));
        verify(user).sendMessage("border.set-type.changed","[type]","vanilla");

    }

    /**
     * Test method for {@link world.bentobox.border.commands.BorderTypeCommand#tabComplete(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testTabCompleteUserStringListOfString() {
        Optional<List<String>> l = ic.tabComplete(user, "", Collections.emptyList());
        assertTrue(l.isPresent());
        List<String> ll = l.get();
        assertEquals("barrier", ll.get(0));
        assertEquals("vanilla", ll.get(1));
    }

}
