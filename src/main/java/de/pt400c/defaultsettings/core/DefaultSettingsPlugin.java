package de.pt400c.defaultsettings.core;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.DependsOn;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@DependsOn("forge")
@SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions({ "de.pt400c.defaultsettings.core" })
public class DefaultSettingsPlugin implements IFMLLoadingPlugin {


	@Override
	public String getAccessTransformerClass() {
		return null;
	}

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

}