package world.bentobox.border;

import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.ConfigObject;
import world.bentobox.bentobox.api.configuration.StoreAt;

import java.util.HashSet;
import java.util.Set;

@StoreAt(filename = "config.yml", path = "addons/Border")
public class Settings implements ConfigObject {

    @ConfigComment("Border addon configuration file")
    @ConfigComment("See the documentation at https://docs.bentobox.world/en/latest/addons/Border/")
    @ConfigComment("")
    @ConfigEntry(path = "disabled-gamemodes")
    private Set<String> disabledGameModes = new HashSet<>();

    @ConfigEntry(path = "use-wbapi")
    private boolean useWbapi = true;

    @ConfigEntry(path = "return-teleport")
    private boolean returnTeleport = true;

    @ConfigEntry(path = "use-barrier-blocks")
    private boolean useBarrierBlocks = true;

    @ConfigEntry(path = "show-by-default")
    private boolean showByDefault = true;

    @ConfigEntry(path = "show-max-border")
    private boolean showMaxBorder = true;

    @ConfigEntry(path = "show-particles")
    private boolean showParticles = true;

    public void setDisabledGameModes(Set<String> disabledGameModes) {
        this.disabledGameModes = disabledGameModes;
    }

    public Set<String> getDisabledGameModes() {
        return this.disabledGameModes;
    }

    public boolean isUseWbapi() {
        return useWbapi;
    }

    public void setUseWbapi(boolean useWbapi) {
        this.useWbapi = useWbapi;
    }

    public boolean isReturnTeleport() {
        return returnTeleport;
    }

    public void setReturnTeleport(boolean returnTeleport) {
        this.returnTeleport = returnTeleport;
    }

    public boolean isUseBarrierBlocks() {
        return useBarrierBlocks;
    }

    public void setUseBarrierBlocks(boolean useBarrierBlocks) {
        this.useBarrierBlocks = useBarrierBlocks;
    }

    public boolean isShowByDefault() {
        return showByDefault;
    }

    public void setShowByDefault(boolean showByDefault) {
        this.showByDefault = showByDefault;
    }

    public boolean isShowMaxBorder() {
        return showMaxBorder;
    }

    public void setShowMaxBorder(boolean showMaxBorder) {
        this.showMaxBorder = showMaxBorder;
    }

    public boolean isShowParticles() {
        return showParticles;
    }

    public void setShowParticles(boolean showParticles) {
        this.showParticles = showParticles;
    }
}
