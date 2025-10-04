package net.jomcraft.defaultsettings;

public class Core {

    public static ICoreHook instance = null;

    public static void setInstance(ICoreHook coreHook){
        instance = coreHook;
    }

    public static ICoreHook getInstance() {
        return instance;
    }

}
