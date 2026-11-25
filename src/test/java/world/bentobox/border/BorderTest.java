package world.bentobox.border;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.bukkit.World.Environment;

import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.util.Util;

/**
 * Tests for {@link Border} behavior that does not require a full Bukkit runtime.
 */
class BorderTest extends CommonTestSetup {

    private Border border;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        border = new Border();
        mockedUtil.when(() -> Util.getWorld(world)).thenReturn(world);
        setField(border, "settings", new Settings());
    }

    @Test
    void testInGameWorldReturnsTrueWhenAnyGameModeMatches() throws Exception {
        GameModeAddon matching = mock(GameModeAddon.class);
        when(matching.inWorld(world)).thenReturn(true);
        getGameModes().add(matching);

        assertTrue(border.inGameWorld(world));
    }

    @Test
    void testInGameWorldReturnsFalseWhenNoGameModeMatches() throws Exception {
        GameModeAddon nonMatching = mock(GameModeAddon.class);
        when(nonMatching.inWorld(world)).thenReturn(false);
        getGameModes().add(nonMatching);

        assertFalse(border.inGameWorld(world));
    }

    @Test
    void testInGameWorldReturnsFalseForVanillaNether() {
        when(world.getEnvironment()).thenReturn(Environment.NETHER);
        when(iwm.isIslandNether(world)).thenReturn(false);

        assertFalse(border.inGameWorld(world));
    }

    @Test
    void testInGameWorldReturnsFalseForVanillaEnd() {
        when(world.getEnvironment()).thenReturn(Environment.THE_END);
        when(iwm.isIslandEnd(world)).thenReturn(false);

        assertFalse(border.inGameWorld(world));
    }

    @Test
    void testInGameWorldDelegatesToGameModeForIslandNether() throws Exception {
        when(world.getEnvironment()).thenReturn(Environment.NETHER);
        when(iwm.isIslandNether(world)).thenReturn(true);
        GameModeAddon matching = mock(GameModeAddon.class);
        when(matching.inWorld(world)).thenReturn(true);
        getGameModes().add(matching);

        assertTrue(border.inGameWorld(world));
    }

    @Test
    void testInGameWorldDelegatesToGameModeForIslandEnd() throws Exception {
        when(world.getEnvironment()).thenReturn(Environment.THE_END);
        when(iwm.isIslandEnd(world)).thenReturn(true);
        GameModeAddon matching = mock(GameModeAddon.class);
        when(matching.inWorld(world)).thenReturn(true);
        getGameModes().add(matching);

        assertTrue(border.inGameWorld(world));
    }

    @Test
    void testGetAvailableBorderTypesViewIsUnmodifiable() {
        Set<BorderType> view = border.getAvailableBorderTypesView();
        assertTrue(view.contains(BorderType.VANILLA));
        assertTrue(view.contains(BorderType.BARRIER));
        assertThrows(UnsupportedOperationException.class, () -> view.add(BorderType.VANILLA));
    }

    /**
     * A game mode's request applies in its own worlds only and leaves the config alone.
     */
    @Test
    void testGameModeRequest() {
        Settings settings = new Settings();
        settings.setReturnTeleport(false);
        setField(border, "settings", settings);
        border.setDescription(new world.bentobox.bentobox.api.addons.AddonDescription.Builder("main", "Border", "1").build());
        GameModeAddon gm = mock(GameModeAddon.class);
        when(gm.getDescription())
                .thenReturn(new world.bentobox.bentobox.api.addons.AddonDescription.Builder("main", "Boxed", "1").build());
        when(gm.inWorld(world)).thenReturn(true);
        // The border type is each player's choice and cannot be asked for
        border.requester = (g, n) -> java.util.Map.of("return-teleport", true, "type", "BARRIER");

        border.loadRequests(gm);

        assertTrue(border.getSettings(world).isReturnTeleport());
        assertSame(BorderType.VANILLA, border.getSettings(world).getType());
        assertFalse(border.getSettings().isReturnTeleport());
        assertFalse(border.getSettings(mock(org.bukkit.World.class)).isReturnTeleport());
    }

    /**
     * The admin can ignore requests.
     */
    @Test
    void testGameModeRequestIgnored() {
        Settings settings = new Settings();
        settings.setReturnTeleport(false);
        settings.setIgnoreRequests(Set.of("boxed"));
        setField(border, "settings", settings);
        GameModeAddon gm = mock(GameModeAddon.class);
        when(gm.getDescription())
                .thenReturn(new world.bentobox.bentobox.api.addons.AddonDescription.Builder("main", "Boxed", "1").build());
        when(gm.inWorld(world)).thenReturn(true);
        border.requester = (g, n) -> java.util.Map.of("return-teleport", true);

        border.loadRequests(gm);

        assertFalse(border.getSettings(world).isReturnTeleport());
    }

    @Test
    void testGetSettingsReturnsConfiguredInstance() {
        Settings settings = new Settings();
        setField(border, "settings", settings);
        assertSame(settings, border.getSettings());
    }

    @SuppressWarnings("unchecked")
    private List<GameModeAddon> getGameModes() throws Exception {
        Field field = Border.class.getDeclaredField("gameModes");
        field.setAccessible(true);
        return (List<GameModeAddon>) field.get(border);
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set field '" + fieldName + "'", e);
        }
    }
}

