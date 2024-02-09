package net.jomcraft.defaultsettings;

import javax.annotation.Nullable;

public class Core {

    @Nullable
    public static ICoreHook instance = null;

    public static void setInstance(ICoreHook coreHook){
        instance = coreHook;
    }

    public static ICoreHook getInstance() {
        return instance;
    }

}
