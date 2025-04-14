package world.bentobox.border;


import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.Pladdon;


public class BorderPladdon extends Pladdon {

    private Border addon;

    @Override
    public Addon getAddon() {
        if (addon == null) {
            addon = new Border();
        }
        return addon;
    }

}
