package de.pt400c.defaultsettings;

import static de.pt400c.defaultsettings.FileUtil.MC;
import org.lwjgl.input.Keyboard;
import com.google.common.base.Joiner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.pt400c.defaultsettings.gui.ButtonRoundSegment;
import de.pt400c.defaultsettings.gui.DefaultSettingsGUI;
import de.pt400c.defaultsettings.gui.Function;
import de.pt400c.defaultsettings.gui.Segment;
import de.pt400c.defaultsettings.gui.TextSegment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class GuiDSMainMenu extends DefaultSettingsGUI {
	
    public final GuiScreen parentScreen;
    public FramebufferObject framebufferMc;

    public GuiDSMainMenu(GuiScreen parentScreen) {
        this.mc = MC;
        this.parentScreen = parentScreen;
    }
    
    @Override
    public void initGui() {

    	Segment.resized++;

		Segment.scaledresolution = new ScaledResolution(MC.gameSettings, MC.displayWidth, MC.displayHeight);
    	
    	new FileUtil.RegistryChecker();
			
    	if(this.framebufferMc != null) 
    		this.framebufferMc.resize(MC.displayWidth, MC.displayHeight);
 
        Keyboard.enableRepeatEvents(true);

        this.framebufferMc = new FramebufferObject(MC.displayWidth, MC.displayHeight);

        Keyboard.enableRepeatEvents(true);
        this.clearSegments();
        
        this.addSegment(new TextSegment(this, this.width / 2 - DefaultSettings.fontRenderer.getStringWidth("- DefaultSettings -", 1, true) / 2, 7, 0, 0, "- DefaultSettings -", 0xfffafafa, false, 1.2F));
        String text = "This is the first bootup of \u00a7lDefaultSettings\u00a7r. In order to assure proper functionality, you should consider the following:"
        		+ " Automatically this mod ships all mod configs and doesn't replace them when you as the modpack's creator update the configs. Also neither the default keybindings, options nor the default servers are shipped by default. For the most cases that is not optional. Please customise DS by opening the management GUI (F7 + G in the mods list or click on the 'Config' button when selecting DS in that list).\n\u00a7c\u00a7lImportant\u00a7r: Once you finished configuring the modpack, you have to activate the 'Export Mode' in that GUI!";

        ArrayList<String> lines = new ArrayList<String>();
		
		final float textWidth = DefaultSettings.fontRenderer.getStringWidth(text, 1, true);
		if(textWidth > this.width - 5) 
			lines.addAll(DefaultSettings.fontRenderer.listFormattedStringToWidth(text, this.width - 5, true));
		else 
			lines.add(text);
		
        this.addSegment(new TextSegment(this, 10, 35, 0, 0, Joiner.on("\n").join(lines), 0xffe6e6e6, 13, false, 0.9F));
    	
    	this.addSegment(new ButtonRoundSegment(this, 70, this.height - 50, 80, 25, "Later", null, new Function<ButtonRoundSegment, Boolean>() {
			@Override
			public Boolean apply(ButtonRoundSegment button) {
				FileUtil.setPopup(true);
				GuiDSMainMenu.this.mc.displayGuiScreen(GuiDSMainMenu.this.parentScreen);
				return true;}
		}, 1F, false));
    																									
    	this.addSegment(new ButtonRoundSegment(this, this.width - 80 - 70, this.height - 50, 80, 25, "Ignore", null, new Function<ButtonRoundSegment, Boolean>() {
			@Override
			public Boolean apply(ButtonRoundSegment button) {
				FileUtil.setPopup(false);
				GuiDSMainMenu.this.mc.displayGuiScreen(GuiDSMainMenu.this.parentScreen);
				return true;}
		}, 1F, false));
    	
    	super.initGui();
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	
    	glPushMatrix();
		glClear(16640);
		this.framebufferMc.bindFramebuffer(true);
		glEnable(GL_TEXTURE_2D);

		glClear(256);
		glMatrixMode(5889);
		glLoadIdentity();
		glOrtho(0.0D, Segment.scaledresolution.getScaledWidth_double(), Segment.scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
		glMatrixMode(5888);
		glLoadIdentity();
		glTranslatef(0.0F, 0.0F, -2000.0F);

		glClear(256);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_ALPHA_TEST);

		Gui.drawRect(0, 0, this.width, this.height, 0xff2c2c2c);
		glDisable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glDisable(GL_ALPHA_TEST);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
		glShadeModel(GL_SMOOTH);
		glShadeModel(GL_FLAT);
		glDisable(GL_BLEND);
		glDisable(GL_ALPHA_TEST);
		glEnable(GL_TEXTURE_2D);
    	Gui.drawRect(0, 0, width, 25, 0xff505050);
    	Gui.drawRect(0, 25, width, 26, 0xff161616);
    	super.drawScreen(mouseX, mouseY, partialTicks);

		glPushMatrix();

		glPopMatrix();
		this.framebufferMc.unbindFramebuffer();
		glPopMatrix();

		glPushMatrix();

		glBindFramebuffer(GL_READ_FRAMEBUFFER, framebufferMc.framebufferObject);
		glBlitFramebuffer(0, 0, MC.displayWidth, MC.displayHeight, 0, 0, MC.displayWidth, MC.displayHeight, GL_COLOR_BUFFER_BIT, GL_NEAREST);

		glPopMatrix();
		
    }
    
    @Override
    public void onGuiClosed() {
    	Keyboard.enableRepeatEvents(false);

    	BakeryRegistry.clearAll();
    	if(framebufferMc != null)
    		framebufferMc.deleteFramebuffer();
    }
}