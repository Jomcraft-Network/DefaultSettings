package cpw.mods.fml.common.event;

import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.event.FMLStateEvent;

public class FMLLoadCompleteEvent extends FMLStateEvent {

    public FMLLoadCompleteEvent(Object... data) {
        super(data);
    }

    @Override
    public LoaderState.ModState getModState() {
        return null;
    }

}