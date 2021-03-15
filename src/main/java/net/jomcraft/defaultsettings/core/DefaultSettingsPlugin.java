package net.jomcraft.defaultsettings.core;

import java.io.File;
import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.DependsOn;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;

@DependsOn("forge")
@SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions({ "net.jomcraft.defaultsettings.core" })
public class DefaultSettingsPlugin implements IFMLLoadingPlugin {

	public static File dataDir;
	
	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	@Override
	public String[] getASMTransformerClass() {

		return new String[] { "net.jomcraft.defaultsettings.core.DefaultSettingsClassTransformer" };
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
		dataDir = ((File) data.get("mcLocation"));
	}

}