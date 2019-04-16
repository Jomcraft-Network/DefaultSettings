package de.pt400c.defaultsettings.core;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.5.2")
@IFMLLoadingPlugin.TransformerExclusions({ "de.pt400c.defaultsettings.core" })
public class DefaultSettingsPlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {

		return new String[] { "de.pt400c.defaultsettings.core.DefaultSettingsClassTransformer" };
	}

	@Override
	public String getModContainerClass() {
		return null;
	
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

	}

	@Override
	public String[] getLibraryRequestClass() {
		return null;
	}

}