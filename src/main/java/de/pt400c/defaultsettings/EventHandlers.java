package de.pt400c.defaultsettings;

import static org.lwjgl.glfw.GLFW.*;
import java.lang.reflect.Field;
import static de.pt400c.defaultsettings.FileUtil.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

@Mod.EventBusSubscriber(modid = DefaultSettings.MODID, value = Dist.CLIENT)
public class EventHandlers {

	@SubscribeEvent
	public void tickEvent(TickEvent.ClientTickEvent event) {
		if ((MC.currentScreen instanceof GuiModList || MC.currentScreen == null) && InputMappings.isKeyDown(GLFW_KEY_F7) && InputMappings.isKeyDown(GLFW_KEY_G))
			
			MC.displayGuiScreen(new GuiConfig(Minecraft.getInstance().currentScreen));
	}

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {
		CommandDefaultSettings.register(event);
	}
	
	@SubscribeEvent
	public static void onGuiRender(GuiScreenEvent.DrawScreenEvent event) {

		if (event.getGui() instanceof GuiModList) {
			try {
				GuiModList modList = (GuiModList) event.getGui();
				Field modField = GuiModList.class.getDeclaredField("selectedMod");
				modField.setAccessible(true);
				ModInfo selectedMod = (ModInfo) modField.get(modList);
				if (selectedMod != null && selectedMod.getModId().equals(DefaultSettings.MODID)) {
					Field buttonField = GuiModList.class.getDeclaredField("configButton");
					buttonField.setAccessible(true);
					GuiButton button = (GuiButton) buttonField.get(modList);
					button.enabled = true;
				}
				
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				//Forget about everything
			}

		}
	}
}
