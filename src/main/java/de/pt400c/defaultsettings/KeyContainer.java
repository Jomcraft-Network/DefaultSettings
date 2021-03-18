package de.pt400c.defaultsettings;

import net.minecraft.client.util.InputMappings.Input;
import net.minecraftforge.client.settings.KeyModifier;

public class KeyContainer {
	
	public final Input input;
	public final KeyModifier modifier;
	
	public KeyContainer(final Input input, final KeyModifier modifier) {
		this.input = input;
		this.modifier = modifier;
	}
	
}
