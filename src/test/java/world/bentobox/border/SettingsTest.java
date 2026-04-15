package world.bentobox.border;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author tastybento
 *
 */
class SettingsTest {

    private Settings settings;

    /**
     */
    @BeforeEach
    protected void setUp() {
        settings = new Settings();
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#setDisabledGameModes(java.util.Set)}.
     */
    @Test
    void testSetDisabledGameModes() {
        settings.setDisabledGameModes(Collections.singleton("test"));
        assertTrue(settings.getDisabledGameModes().contains("test"));
        assertEquals(1, settings.getDisabledGameModes().size());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#getDisabledGameModes()}.
     */
    @Test
    void testGetDisabledGameModes() {
        assertTrue(settings.getDisabledGameModes().isEmpty());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#isUseBarrierBlocks()}.
     */
    @Test
    void testIsUseBarrierBlocks() {
        assertTrue(settings.isUseBarrierBlocks());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#setUseBarrierBlocks(boolean)}.
     */
    @Test
    void testSetUseBarrierBlocks() {
        settings.setUseBarrierBlocks(false);
        assertFalse(settings.isUseBarrierBlocks());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#isShowByDefault()}.
     */
    @Test
    void testIsShowByDefault() {
        assertTrue(settings.isShowByDefault());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#setShowByDefault(boolean)}.
     */
    @Test
    void testSetShowByDefault() {
        settings.setShowByDefault(false);
        assertFalse(settings.isShowByDefault());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#isShowMaxBorder()}.
     */
    @Test
    void testIsShowMaxBorder() {
        assertTrue(settings.isShowMaxBorder());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#setShowMaxBorder(boolean)}.
     */
    @Test
    void testSetShowMaxBorder() {
        settings.setShowMaxBorder(false);
        assertFalse(settings.isShowMaxBorder());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#getType()}.
     */
    @Test
    void testGetType() {
        assertEquals(BorderType.VANILLA, settings.getType());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#setType(BorderType)}.
     */
    @Test
    void testSetType() {
        assertEquals(BorderType.VANILLA, settings.getType());
        settings.setType(BorderType.BARRIER);
        assertEquals(BorderType.BARRIER, settings.getType());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#isReturnTeleport()}.
     */
    @Test
    void testIsReturnTeleport() {
        assertTrue(settings.isReturnTeleport());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#setReturnTeleport(boolean)}.
     */
    @Test
    void testSetReturnTeleport() {
        settings.setReturnTeleport(false);
        assertFalse(settings.isReturnTeleport());
    }
    
    /**
     * Test method for {@link world.bentobox.border.Settings#getBarrierOffset()}.
     */
    @Test
    void testGetBarrierOffset() {
        assertEquals(0, settings.getBarrierOffset());
    }
    
    /**
     * Test method for {@link world.bentobox.border.Settings#setBarrierOffset(int)}.
     */
    @Test
    void testsetBarrierOffset() {
        assertEquals(0, settings.getBarrierOffset());
        settings.setBarrierOffset(-234);
        assertEquals(0, settings.getBarrierOffset());
        settings.setBarrierOffset(123);
        assertEquals(123, settings.getBarrierOffset());
    }

}
