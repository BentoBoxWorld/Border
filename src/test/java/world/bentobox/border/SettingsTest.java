/**
 *
 */
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
     * Test method for {@link world.bentobox.border.Settings#isUseWbapi()}.
     */
    @Test
    public void testIsUseWbapi() {
        assertFalse(settings.isUseWbapi());
    }

    /**
     * Test method for {@link world.bentobox.border.Settings#setUseWbapi(boolean)}.
     */
    @Test
    public void testSetUseWbapi() {
        settings.setUseWbapi(true);
        assertTrue(settings.isUseWbapi());
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

}
