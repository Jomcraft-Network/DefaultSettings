package cpw.mods.fml.common;

import cpw.mods.fml.relauncher.Side;

public class FMLCommonHandler {

    private static final FMLCommonHandler INSTANCE = new FMLCommonHandler();
    public static FMLCommonHandler instance() {
        return INSTANCE;
    }

    public Side getEffectiveSide() {
        Thread thr = Thread.currentThread();
        if ((thr.getName().equals("Server thread"))) {
            return Side.SERVER;
        }

        return Side.CLIENT;
    }
}