package net.jomcraft.defaultsettings;

import com.mojang.blaze3d.platform.InputConstants;

public class KeyContainer {

    public final InputConstants.Key input;
    public final Object modifier;

    public KeyContainer(final InputConstants.Key input, final Object modifier) {
        this.input = input;
        this.modifier = modifier;
    }

}