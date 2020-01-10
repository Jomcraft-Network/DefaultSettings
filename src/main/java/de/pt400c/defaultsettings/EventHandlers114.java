package de.pt400c.defaultsettings;

import static org.lwjgl.glfw.GLFW.*;
import static de.pt400c.defaultsettings.FileUtil.MC;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.ModListScreen;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public class EventHandlers114 {

	@SubscribeEvent
	public void tickEvent(TickEvent.ClientTickEvent event) {
		
		if ((MC.currentScreen instanceof ModListScreen && MC.world == null) && InputMappings.isKeyDown(MC.func_228018_at_().getHandle(), GLFW_KEY_F7) && InputMappings.isKeyDown(MC.func_228018_at_().getHandle(), GLFW_KEY_G))
			MC.displayGuiScreen(new GuiConfig(MC, MC.currentScreen));
	}

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {
		CommandSwitchProfile.register(event);
		CommandDefaultSettings.register(event);
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
