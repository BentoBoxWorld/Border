package world.bentobox.border;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import java.util.Optional;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.border.listeners.BorderShower;

/**
 * Tests for {@link PerPlayerBorderProxy} border selection logic.
 */
class PerPlayerBorderProxyTest {

    @Mock
    private Border addon;
    @Mock
    private BorderShower customBorder;
    @Mock
    private BorderShower vanillaBorder;
    @Mock
    private User user;
    @Mock
    private Player player;
    @Mock
    private Entity entity;
    @Mock
    private Island island;

    private Settings settings;
    private MockedStatic<User> mockedUser;
    private AutoCloseable closeable;

    @BeforeEach
    protected void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        settings = new Settings();
        when(addon.getSettings()).thenReturn(settings);
        when(addon.getAvailableBorderTypesView()).thenReturn(EnumSet.of(BorderType.VANILLA, BorderType.BARRIER));

        mockedUser = Mockito.mockStatic(User.class, Mockito.RETURNS_MOCKS);
        mockedUser.when(() -> User.getInstance(any(Player.class))).thenReturn(user);
        mockedUser.when(() -> User.getInstance(any(Entity.class))).thenReturn(user);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        mockedUser.closeOnDemand();
        closeable.close();
    }

    @Test
    void testShowBorderUsesDefaultWhenNoMetadata() {
        when(user.getMetaData(PerPlayerBorderProxy.BORDER_BORDERTYPE_META_DATA)).thenReturn(Optional.empty());
        PerPlayerBorderProxy proxy = new PerPlayerBorderProxy(addon, customBorder, vanillaBorder);

        proxy.showBorder(player, island);

        verify(vanillaBorder).showBorder(player, island);
        verify(customBorder, never()).showBorder(player, island);
    }

    @Test
    void testShowBorderUsesCustomWhenMetadataBarrier() {
        MetaDataValue metaDataValue = mock(MetaDataValue.class);
        when(metaDataValue.asByte()).thenReturn(BorderType.BARRIER.getId());
        when(user.getMetaData(PerPlayerBorderProxy.BORDER_BORDERTYPE_META_DATA)).thenReturn(Optional.of(metaDataValue));
        PerPlayerBorderProxy proxy = new PerPlayerBorderProxy(addon, customBorder, vanillaBorder);

        proxy.showBorder(player, island);

        verify(customBorder).showBorder(player, island);
        verify(vanillaBorder, never()).showBorder(player, island);
    }

    @Test
    void testHideBorderFallsBackWhenTypeUnavailable() {
        MetaDataValue metaDataValue = mock(MetaDataValue.class);
        when(metaDataValue.asByte()).thenReturn(BorderType.BARRIER.getId());
        when(user.getMetaData(PerPlayerBorderProxy.BORDER_BORDERTYPE_META_DATA)).thenReturn(Optional.of(metaDataValue));
        when(addon.getAvailableBorderTypesView()).thenReturn(EnumSet.of(BorderType.VANILLA));
        PerPlayerBorderProxy proxy = new PerPlayerBorderProxy(addon, customBorder, vanillaBorder);

        proxy.hideBorder(user);

        verify(vanillaBorder).hideBorder(user);
        verify(customBorder, never()).hideBorder(user);
    }

    @Test
    void testTeleportEntityUsesCustomWhenBarrier() {
        MetaDataValue metaDataValue = mock(MetaDataValue.class);
        when(metaDataValue.asByte()).thenReturn(BorderType.BARRIER.getId());
        when(user.getMetaData(PerPlayerBorderProxy.BORDER_BORDERTYPE_META_DATA)).thenReturn(Optional.of(metaDataValue));
        PerPlayerBorderProxy proxy = new PerPlayerBorderProxy(addon, customBorder, vanillaBorder);

        proxy.teleportEntity(addon, entity);

        verify(customBorder).teleportEntity(addon, entity);
        verify(vanillaBorder, never()).teleportEntity(addon, entity);
    }
}

