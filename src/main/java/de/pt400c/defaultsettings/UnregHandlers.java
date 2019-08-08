package de.pt400c.defaultsettings;

import static de.pt400c.defaultsettings.FileUtil.MC;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class UnregHandlers {

	private static boolean bootedUp;

	@SubscribeEvent
	public void tickEvent(TickEvent.ClientTickEvent event) {
		if (!bootedUp) {

			if(MC.currentScreen instanceof MainMenuScreen) {
				bootedUp = true;

				if(FileUtil.getMainJSON().initPopup) {
			
				MC.displayGuiScreen(new GuiDSMainMenu(new MainMenuScreen()));
		
				}
				MinecraftForge.EVENT_BUS.unregister(this);
			}
		}
	}

}
