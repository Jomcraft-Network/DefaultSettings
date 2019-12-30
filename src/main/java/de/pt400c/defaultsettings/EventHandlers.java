package de.pt400c.defaultsettings;

import static de.pt400c.defaultsettings.FileUtil.MC;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EventHandlers {
	
	private static boolean bootedUp;

	@SubscribeEvent
	public void tickEvent(TickEvent.ClientTickEvent event) {
		if((MC.currentScreen instanceof GuiModList && MC.world == null) && Keyboard.isKeyDown(Keyboard.KEY_F7) && Keyboard.isKeyDown(Keyboard.KEY_G))
			MC.displayGuiScreen(new GuiConfig(MC.currentScreen));
		
	}
	
	public static int getLimitFramerate() {
		return MC.currentScreen instanceof GuiConfig ? 60 : MC.world == null && MC.currentScreen != null ? 30 : MC.gameSettings.limitFramerate;
	}
	
	@SubscribeEvent
	public void onGuiOpened(GuiOpenEvent event) {
		if (!bootedUp) {
			if(DefaultSettings.mcVersion.startsWith("1.8")) {
				if(event.gui instanceof GuiMainMenu && FileUtil.getMainJSON().initPopup) {
					bootedUp = true;
					event.setCanceled(true);
					MC.displayGuiScreen(new GuiDSMainMenu(new GuiMainMenu()));
				}
			}else {
				if(event.getGui() instanceof GuiMainMenu && FileUtil.getMainJSON().initPopup) {
					bootedUp = true;
					event.setGui(new GuiDSMainMenu(new GuiMainMenu()));
				}
			}	
		}
	}
}