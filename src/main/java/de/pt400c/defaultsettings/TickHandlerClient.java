package de.pt400c.defaultsettings;

import static de.pt400c.defaultsettings.FileUtil.MC;
import java.util.EnumSet;
import org.lwjgl.input.Keyboard;
import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.EntityRenderer;

public class TickHandlerClient implements ITickHandler {
	
	private static boolean bootedUp;
	
	@SuppressWarnings("rawtypes")
	@Override
	public void tickStart(EnumSet type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.CLIENT))) {
			if((MC.currentScreen instanceof GuiModList && MC.theWorld == null) && isPressed(Keyboard.KEY_F7) && isPressed(Keyboard.KEY_G))
				MC.displayGuiScreen(new GuiConfig(MC.currentScreen));
			
			if (!bootedUp) {

				if(MC.currentScreen instanceof GuiMainMenu) {
					bootedUp = true;

					if(FileUtil.getMainJSON().initPopup) 
						MC.displayGuiScreen(new GuiDSMainMenu(new GuiMainMenu()));

				}
			}

		}
	}
	
	@SuppressWarnings({"rawtypes"})
	@Override
	public void tickEnd(EnumSet type, Object... tickData) {}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public EnumSet ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return "TickHandlerDefaultSettings";
	}

	public static boolean isPressed(int key) {
		return Keyboard.isKeyDown(key);
	}
	
	public static int getLimitFramerate() {
		int main = getLimitFramerateMain();
		if(!(main > 0))
			return 0;
		if(main == 25)
			return 60;
		
		return EntityRenderer.performanceToFps(main);
	}
	
	private static int getLimitFramerateMain()
    {
        return MC.currentScreen != null && MC.currentScreen instanceof GuiMainMenu ? 2 : MC.currentScreen instanceof GuiConfig ? 25 : MC.gameSettings.limitFramerate;
    }
}