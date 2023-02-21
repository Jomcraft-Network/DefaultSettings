package net.jomcraft.defaultsettings;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.settings.KeyModifier;

public class KeyContainer {

	public final InputConstants.Key input;
	public final KeyModifier modifier;

	public KeyContainer(final InputConstants.Key input, final KeyModifier modifier) {
		this.input = input;
		this.modifier = modifier;
	}

}
