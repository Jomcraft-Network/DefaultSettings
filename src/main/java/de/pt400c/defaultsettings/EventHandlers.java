package de.pt400c.defaultsettings;

import static org.lwjgl.glfw.GLFW.*;
import static de.pt400c.defaultsettings.FileUtil.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiModList;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public class EventHandlers {

	@SubscribeEvent
	public void tickEvent(TickEvent.ClientTickEvent event) {
		
		if ((MC.field_71462_r instanceof GuiModList || MC.field_71462_r == null) && InputMappings.func_216506_a(Minecraft.getInstance().mainWindow.getHandle(), GLFW_KEY_F7) && InputMappings.func_216506_a(Minecraft.getInstance().mainWindow.getHandle(), GLFW_KEY_G))
			
			MC.displayGuiScreen(new GuiConfig(Minecraft.getInstance().field_71462_r));
	}

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {
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
