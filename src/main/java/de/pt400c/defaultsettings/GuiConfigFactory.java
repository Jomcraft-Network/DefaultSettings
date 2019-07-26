package de.pt400c.defaultsettings;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class GuiConfigFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}
	
	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return GuiConfig.class;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new GuiConfig(parentScreen);
	}
	
	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}
}