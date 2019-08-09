package de.pt400c.defaultsettings;

import static de.pt400c.defaultsettings.FileUtil.MC;
import java.awt.Color;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import de.pt400c.defaultsettings.gui.ButtonSegment;
import de.pt400c.defaultsettings.gui.DefaultSettingsGUI;
import de.pt400c.defaultsettings.gui.Segment;
import de.pt400c.defaultsettings.gui.TextSegment;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.text.TextFormatting;

public class GuiDSMainMenu extends DefaultSettingsGUI {
	
    public final GuiScreen parentScreen;

    public GuiDSMainMenu(GuiScreen parentScreen)
    {
        this.mc = MC;
        this.parentScreen = parentScreen;
    }
    
    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.clearSegments();
        
        this.addSegment(new TextSegment(this, this.width / 2 - MC.fontRenderer.getStringWidth("- DefaultSettings -") / 2, 9, 0, 0, "- DefaultSettings -", 0x0, false));
        String text;
        if(DefaultSettings.is18)
        
        	text = "This is the first bootup of " + EnumChatFormatting.BOLD + "DefaultSettings" + EnumChatFormatting.RESET + ". In order to assure proper functionality, you should consider the following:"
        		+ " Automatically this mod ships all mod configs and doesn't replace them when you as the modpack's creator update the configs. Also neither the default keybindings, options nor the default servers are shipped by default. For the most cases that is not optional. Please customise DS by opening the management GUI (F7 + G in the mods list or click on the 'Config' button when selecting DS in that list).\n" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "Important" + EnumChatFormatting.RESET + ": Once you finished configuring the modpack, you have to activate the 'Export Mode' in that GUI!";
        else
        	text = "This is the first bootup of " + TextFormatting.BOLD + "DefaultSettings" + TextFormatting.RESET + ". In order to assure proper functionality, you should consider the following:"
            		+ " Automatically this mod ships all mod configs and doesn't replace them when you as the modpack's creator update the configs. Also neither the default keybindings, options nor the default servers are shipped by default. For the most cases that is not optional. Please customise DS by opening the management GUI (F7 + G in the mods list or click on the 'Config' button when selecting DS in that list).\n" + TextFormatting.RED + TextFormatting.BOLD + "Important" + TextFormatting.RESET + ": Once you finished configuring the modpack, you have to activate the 'Export Mode' in that GUI!";
        
        ArrayList<String> lines = new ArrayList<String>();
		
		int textWidth = MC.fontRenderer.getStringWidth(text);
		if(textWidth > this.width - 20) 
			lines.addAll(MC.fontRenderer.listFormattedStringToWidth(text, this.width - 20));
		else 
			lines.add(text);
		
        this.addSegment(new TextSegment(this, 10, 35, 0, 0, String.join("\n", lines), 0x0, 14, false));
    	
    	this.addSegment(new ButtonSegment(this, 70, this.height - 50, "Later", button -> {
    		FileUtil.setPopup(true);
    		GuiDSMainMenu.this.mc.displayGuiScreen(GuiDSMainMenu.this.parentScreen);
    		return true;}, 80, 25, 3));
    	
    	this.addSegment(new ButtonSegment(this, this.width - 80 - 70, this.height - 50, "Dismiss", button -> {
    		FileUtil.setPopup(false);
    		GuiDSMainMenu.this.mc.displayGuiScreen(GuiDSMainMenu.this.parentScreen);
    		return true;}, 80, 25, 3));
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GuiConfig.drawRect(0, 0, this.width, this.height, Color.WHITE.getRGB());
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		Segment.drawGradientFromTop(0, 25, width, 30, 0xffb3b3b3, 0x00ffffff);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
    	GuiConfig.drawRect(0, 0, width, 25, 0xffe0e0e0);
   
    	super.drawScreen(mouseX, mouseY, partialTicks);
    }
	
}