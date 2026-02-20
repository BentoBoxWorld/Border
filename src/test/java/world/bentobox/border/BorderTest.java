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

import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.util.Util;

/**
 * Tests for {@link Border} behavior that does not require a full Bukkit runtime.
 */
public class BorderTest extends CommonTestSetup {

    private Border border;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        border = new Border();
        mockedUtil.when(() -> Util.getWorld(world)).thenReturn(world);
        setField(border, "settings", new Settings());
    }

    @Test
    public void testInGameWorldReturnsTrueWhenAnyGameModeMatches() throws Exception {
        GameModeAddon matching = mock(GameModeAddon.class);
        when(matching.inWorld(world)).thenReturn(true);
        getGameModes().add(matching);

        assertTrue(border.inGameWorld(world));
    }

    @Test
    public void testInGameWorldReturnsFalseWhenNoGameModeMatches() throws Exception {
        GameModeAddon nonMatching = mock(GameModeAddon.class);
        when(nonMatching.inWorld(world)).thenReturn(false);
        getGameModes().add(nonMatching);

        assertFalse(border.inGameWorld(world));
    }

    @Test
    public void testGetAvailableBorderTypesViewIsUnmodifiable() {
        Set<BorderType> view = border.getAvailableBorderTypesView();
        assertTrue(view.contains(BorderType.VANILLA));
        assertTrue(view.contains(BorderType.BARRIER));
        assertThrows(UnsupportedOperationException.class, () -> view.add(BorderType.VANILLA));
    }

    @Test
    public void testGetSettingsReturnsConfiguredInstance() {
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

