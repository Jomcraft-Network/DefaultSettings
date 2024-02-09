package net.jomcraft.defaultsettings;

import javax.annotation.Nullable;

public class Core {

    @Nullable
    public static MCInstancer instance = null;

    public static MCInstancer getInstance() {
        return instance;
    }

}
