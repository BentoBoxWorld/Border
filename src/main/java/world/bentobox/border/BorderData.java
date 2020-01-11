package world.bentobox.border;

import com.google.gson.annotations.Expose;
import org.bukkit.Color;
import world.bentobox.bentobox.database.objects.DataObject;

public class BorderData implements DataObject {

    @Expose
    private String uniqueId;
    @Expose
    private boolean enabled;
    @Expose
    private Color color;

    public BorderData() {
    }

    public BorderData(String uniqueId, boolean enabled, Color color) {
        this.uniqueId = uniqueId;
        this.enabled = enabled;
        this.color = color;
    }

    public BorderData(String uniqueId) {
        this(uniqueId, true, Color.BLUE);
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
