package net.jomcraft.defaultsettings;

import net.minecraftforge.client.settings.KeyModifier;

public class KeyContainer {
	
	public final int input;
	public final KeyModifier modifier;
	
	public KeyContainer(final int input, final KeyModifier modifier) {
		this.input = input;
		this.modifier = modifier;
	}
}