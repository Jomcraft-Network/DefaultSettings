package de.pt400c.defaultsettings;

import static org.lwjgl.glfw.GLFW.*;
import static de.pt400c.defaultsettings.FileUtil.MC;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.client.gui.ModListScreen;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public class EventHandlers115 {

	//private static boolean bootedUp;
	
	@SubscribeEvent
	public void tickEvent(TickEvent.ClientTickEvent event) {
		
		if ((MC.currentScreen instanceof ModListScreen && MC.world == null) && InputMappings.isKeyDown(MC.func_228018_at_().getHandle(), GLFW_KEY_F7) && InputMappings.isKeyDown(MC.func_228018_at_().getHandle(), GLFW_KEY_G))
			
			MC.displayGuiScreen(new GuiConfig(MC.currentScreen));
	}

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {
		CommandDefaultSettings.register(event);
		
	}
	
	@SubscribeEvent
	//Broken in 1.14.2
	public void onGuiOpened(GuiOpenEvent event) {
	/*	System.out.println("ROF");
		if (!bootedUp) {
		
			if(event.getGui() instanceof MainMenuScreen && FileUtil.getMainJSON().initPopup) {
				bootedUp = true;
				event.setGui(new GuiDSMainMenu(new MainMenuScreen()));
			}
		}*/
	}
	
	protected static class NewModInfo extends ModInfo {
        public NewModInfo(ModInfo modInfo) {
            super(modInfo.getOwningFile(), modInfo.getModConfig());
        }
        
        @Override
        public boolean hasConfigUI() {
            return true;
        }
    }

}