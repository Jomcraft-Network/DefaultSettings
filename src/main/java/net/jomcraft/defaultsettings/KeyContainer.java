package net.jomcraft.defaultsettings;

import com.mojang.blaze3d.platform.InputConstants;

public class KeyContainer {

	public final InputConstants.Key input;
	public final String modifier;

	public KeyContainer(final InputConstants.Key input, final String modifier) {
		this.input = input;
		this.modifier = modifier;
	}

}
