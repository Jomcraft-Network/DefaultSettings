package cpw.mods.fml.common.event;

import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.event.FMLStateEvent;

public class FMLConstructionEvent extends FMLStateEvent {

    @SuppressWarnings("unchecked")
    public FMLConstructionEvent(Object... eventData) {

    }

    @Override
    public LoaderState.ModState getModState() {
        return null;
    }
}