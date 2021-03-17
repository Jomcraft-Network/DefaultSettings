package net.jomcraft.defaultsettings;

import java.util.ArrayList;

import net.jomcraft.defaultsettings.gui.ButtonRoundSegment;
import net.jomcraft.defaultsettings.gui.DefaultSettingsGUI;
import net.jomcraft.defaultsettings.gui.Segment;
import net.jomcraft.defaultsettings.gui.TextSegment;
import net.minecraft.client.gui.AbstractGui;

import static net.jomcraft.defaultsettings.FileUtil.MC;
import static net.jomcraft.neptunefx.NeptuneFX.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL11.*;

@OnlyIn(Dist.CLIENT)
public class GuiDSMainMenu extends DefaultSettingsGUI {
	
    public final Screen parentScreen;

    public GuiDSMainMenu(Screen parentScreen) {
    	super(new TranslationTextComponent("defaultsettings.popup.title"));
        this.minecraft = MC;
        this.parentScreen = parentScreen;
    }
    
    @Override
    public void init() {
    	
    	Segment.scaledFactor = MC.mainWindow.getGuiScaleFactor();
    	
    	MC.keyboardListener.enableRepeatEvents(true);
        this.clearSegments();
        
        this.addSegment(new TextSegment(this, this.width / 2 - fontRenderer.getStringWidth("- DefaultSettings -", 1, true) / 2, 7, 0, 0, "- DefaultSettings -", 0xfffafafa, false, 1.2F));
        String text = "This is the first bootup of \u00a7lDefaultSettings\u00a7r. In order to assure proper functionality, you should consider the following:"
        		+ " Automatically this mod ships all mod configs and doesn't replace them when you as the modpack's creator update the configs. Also neither the default keybindings, options nor the default servers are shipped by default. For the most cases that is not optional. Please customise DS by opening the management GUI (F7 + G in the mods list or click on the 'Config' button when selecting DS in that list).\n\u00a7c\u00a7lImportant\u00a7r: Once you finished configuring the modpack, you have to activate the 'Export Mode' in that GUI!";
        
        ArrayList<String> lines = new ArrayList<String>();
		
		final float textWidth = fontRenderer.getStringWidth(text, 1, true);
		if(textWidth > this.width - 5) 
			lines.addAll(fontRenderer.listFormattedStringToWidth(text, this.width - 5, true));
		else 
			lines.add(text);
		
        this.addSegment(new TextSegment(this, 10, 35, 0, 0, String.join("\n", lines), 0xffe6e6e6, 13, false, 0.9F));
    	
    	this.addSegment(new ButtonRoundSegment(this, 70, this.height - 50, 80, 25, "Later", null, button -> {
    		FileUtil.setPopup(true);
    		GuiDSMainMenu.this.minecraft.displayGuiScreen(GuiDSMainMenu.this.parentScreen);
    		return true;}, 1F, false));
    																									
    	this.addSegment(new ButtonRoundSegment(this, this.width - 80 - 70, this.height - 50, 80, 25, "Ignore", null, button -> {
    		FileUtil.setPopup(false);
    		GuiDSMainMenu.this.minecraft.displayGuiScreen(GuiDSMainMenu.this.parentScreen);
    		return true;}, 1F, false));
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
		AbstractGui.fill(0, 0, this.width, this.height, 0xff2c2c2c);
		glDisable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glDisable(GL_ALPHA_TEST);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
		glShadeModel(GL_SMOOTH);
		glShadeModel(GL_FLAT);
		glDisable(GL_BLEND);
		glDisable(GL_ALPHA_TEST);
		glEnable(GL_TEXTURE_2D);
		AbstractGui.fill(0, 0, width, 25, 0xff505050);
		AbstractGui.fill(0, 25, width, 26, 0xff161616);
    	super.render(mouseX, mouseY, partialTicks);
    }
}