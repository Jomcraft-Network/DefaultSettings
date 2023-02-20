package net.jomcraft.defaultsettings;

import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyModifier;

public class KeyContainer {

	public final InputMappings.Input input;
	public final KeyModifier modifier;

	public KeyContainer(final InputMappings.Input input, final KeyModifier modifier) {
		this.input = input;
		this.modifier = modifier;
	}

}
