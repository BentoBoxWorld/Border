package world.bentobox.border;

import org.bukkit.entity.Player;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.border.listeners.BorderShower;

public final class PerPlayerBorderProxy implements BorderShower {

    public static final String BORDER_BORDERTYPE_META_DATA = "Border_bordertype";

    private final BorderShower customBorder;
    private final BorderShower wbapiBorder;

    public PerPlayerBorderProxy(BorderShower customBorder, BorderShower wbapiBorder) {
        this.customBorder = customBorder;
        this.wbapiBorder = wbapiBorder;
    }

    @Override
    public void showBorder(Player player, Island island) {
        var user = User.getInstance(player);
        var border = getBorder(user);
        border.showBorder(player, island);
    }

    @Override
    public void hideBorder(User user) {
        var border = getBorder(user);
        border.hideBorder(user);
    }

    @Override
    public void clearUser(User user) {
        var border = getBorder(user);
        border.clearUser(user);
    }

    @Override
    public void refreshView(User user, Island island) {
        var border = getBorder(user);
        border.clearUser(user);
    }

    private BorderShower getBorder(User user) {
        BorderType borderType = getBorderSettingOrDefault(user);
        return switch (borderType) {
            case Barrier -> customBorder;
            case Vanilla -> wbapiBorder;
        };
    }

    private BorderType getBorderSettingOrDefault(User user) {
        Byte typeId = user.getMetaData(BORDER_BORDERTYPE_META_DATA)
                .map(MetaDataValue::asByte)
                .orElse(getDefaultBorderTypeId());

        return BorderType.fromId(typeId).get();
    }

    private static byte getDefaultBorderTypeId(){
        return BorderType.Barrier.getId();
    }
}
