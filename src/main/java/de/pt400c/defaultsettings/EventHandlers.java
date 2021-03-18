package de.pt400c.defaultsettings;

import static de.pt400c.defaultsettings.FileUtil.MC;
import org.lwjgl.input.Keyboard;
import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;

public class EventHandlers {

	private static boolean bootedUp;
	
	@SubscribeEvent
	public void tickEvent(TickEvent.ClientTickEvent event) {
		if((MC.currentScreen instanceof GuiModList && MC.theWorld == null) && Keyboard.isKeyDown(Keyboard.KEY_F7) && Keyboard.isKeyDown(Keyboard.KEY_G))
			MC.displayGuiScreen(new GuiConfig(MC.currentScreen));
		
	}
	
	public static int getLimitFramerate() {
		return MC.currentScreen instanceof GuiConfig ? 60 : MC.theWorld == null && MC.currentScreen != null ? 30 : MC.gameSettings.limitFramerate;
	}
	
	@SubscribeEvent
	public void onGuiOpened(GuiOpenEvent event) {
		if (!bootedUp) {
			if(event.gui instanceof GuiMainMenu && FileUtil.mainJson.initPopup) {
				bootedUp = true;
				event.gui = new GuiDSMainMenu(new GuiMainMenu());
			}
		}
	}
	
}
