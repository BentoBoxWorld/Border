package world.bentobox.border;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

/**
 * @author tastybento
 *
 */
public class SettingsTest {

    private Settings settings;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        settings = new Settings();
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#setDisabledGameModes(java.util.Set)}.
     */
    @Test
    public void testSetDisabledGameModes() {
        settings.setDisabledGameModes(Collections.singleton("test"));
        assertTrue(settings.getDisabledGameModes().contains("test"));
        assertEquals(1, settings.getDisabledGameModes().size());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#getDisabledGameModes()}.
     */
    @Test
    public void testGetDisabledGameModes() {
        assertTrue(settings.getDisabledGameModes().isEmpty());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#isUseBarrierBlocks()}.
     */
    @Test
    public void testIsUseBarrierBlocks() {
        assertTrue(settings.isUseBarrierBlocks());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#setUseBarrierBlocks(boolean)}.
     */
    @Test
    public void testSetUseBarrierBlocks() {
        settings.setUseBarrierBlocks(false);
        assertFalse(settings.isUseBarrierBlocks());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#isShowByDefault()}.
     */
    @Test
    public void testIsShowByDefault() {
        assertTrue(settings.isShowByDefault());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#setShowByDefault(boolean)}.
     */
    @Test
    public void testSetShowByDefault() {
        settings.setShowByDefault(false);
        assertFalse(settings.isShowByDefault());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#isShowMaxBorder()}.
     */
    @Test
    public void testIsShowMaxBorder() {
        assertTrue(settings.isShowMaxBorder());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#setShowMaxBorder(boolean)}.
     */
    @Test
    public void testSetShowMaxBorder() {
        settings.setShowMaxBorder(false);
        assertFalse(settings.isShowMaxBorder());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#getType()}.
     */
    @Test
    public void testGetType() {
        assertEquals(BorderType.VANILLA, settings.getType());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#setType(BorderType)}.
     */
    @Test
    public void testSetType() {
        assertEquals(BorderType.VANILLA, settings.getType());
        settings.setType(BorderType.BARRIER);
        assertEquals(BorderType.BARRIER, settings.getType());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#isReturnTeleport()}.
     */
    @Test
    public void testIsReturnTeleport() {
        assertTrue(settings.isReturnTeleport());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#setReturnTeleport(boolean)}.
     */
    @Test
    public void testSetReturnTeleport() {
        settings.setReturnTeleport(false);
        assertFalse(settings.isReturnTeleport());
    }
    
    /**
     * Test method for {@link world.bentobox.border.Settings#getBarrierOffset()}.
     */
    @Test
    public void testGetBarrierOffset() {
        assertEquals(0, settings.getBarrierOffset());
    }
    
    /**
     * Test method for {@link world.bentobox.border.Settings#setBarrierOffset(int)}.
     */
    @Test
    public void testsetBarrierOffset() {
        assertEquals(0, settings.getBarrierOffset());
        settings.setBarrierOffset(-234);
        assertEquals(0, settings.getBarrierOffset());
        settings.setBarrierOffset(123);
        assertEquals(123, settings.getBarrierOffset());
    }

}
