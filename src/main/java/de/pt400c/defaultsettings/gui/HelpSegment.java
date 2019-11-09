package de.pt400c.defaultsettings.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import static de.pt400c.defaultsettings.FileUtil.MC;
import static de.pt400c.neptunefx.NEX.*;
import java.awt.Color;
import static de.pt400c.defaultsettings.DefaultSettings.fontRenderer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import de.pt400c.defaultsettings.DefaultSettings;
import de.pt400c.defaultsettings.FileUtil;
import de.pt400c.defaultsettings.GuiConfig;
import de.pt400c.neptunefx.NEX;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

@SideOnly(Side.CLIENT)
public class HelpSegment extends BakedSegment {
	
	private final Function<GuiConfig, Integer> posXF;
	public float timer = 0;
	protected boolean grabbed;
    private float processFactor;
    public float flickerTimer = 0;
	private final ResourceLocation icon = new ResourceLocation(DefaultSettings.MODID, "textures/gui/about.png");
	private boolean doIt;
	private boolean selected;
	
	public HelpSegment(GuiScreen gui, Function<GuiConfig, Integer> posX, float posY) {
		super(gui, 0, posX.apply((GuiConfig) gui), posY, 45, 25, 44, 44, 44, true, false);
		this.posXF = posX;
	}
	
	public void openPopup() {
		GuiConfig config = ((GuiConfig) this.gui);
		MenuScreen menu = config.menu;
		menu.getVariants().get(menu.index).selected = null;
		
		config.popup.setOpening(true);
		config.popup.getWindow().title = "Config Options";
		config.popup.getWindow().setPos(config.width / 2 - 210 / 2, config.height / 2 - 100 / 2);
		config.popupField = config.popup;
		config.popupField.getWindow().clearChildren();
		config.popupField.getWindow().addChild(new TextSegment(config, 5, 29, 0, 0, "Should local configs be persistent?", 0xffffffff, true));
		config.popupField.getWindow().addChild(new QuitButtonSegment(config, 190, 5, 14, 14, quitButton -> {

			config.popupField.setOpening(false);

			return true;
		}, 3F, true));
		
		List<String> actives = FileUtil.getActives();
		
		config.popupField.getWindow().addChild(new TextSegment(config, 35, 45, 0, 0, "Always", 0xffffffff, true));
		config.popupField.getWindow().addChild(new TextSegment(config, 35, 65, 0, 0, "Replaced once", 0xffffffff, true));
		config.popupField.getWindow().addChild(new TextSegment(config, 35, 85, 0, 0, "Never", 0xffffffff, true));
		config.popup.setVisible(true);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		
		if(resized != this.resized_mark) 
			posX = posXF.apply((GuiConfig) this.gui);
		
		setup();
		
		this.selected = this.isSelected(mouseX, mouseY);

		if (this.selected) {
			
			if (this.timer <= (Math.PI / 3)) {
				this.timer += 0.05;
				doIt = true;
			}

		} else {

			if (this.timer > 0) {
				this.timer -= 0.05;
				doIt = true;
			}
		}
		
		if(doIt) {
			compiled = false;
			doIt = false;
		}
		
		if(!compiled) {
			
			this.processFactor = (float) ((Math.sin(3 * this.timer - 3 * (Math.PI / 2)) + 1) / 2);
			
			preRender();

			glEnable(GL_BLEND);
	    	glDisable(GL_ALPHA_TEST);
	    	glDisable(GL_TEXTURE_2D);
	    	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			drawRectRoundedCorners(0, 0, this.width, this.height, calcAlpha(0xff696969, this.processFactor).getRGB(), 4);
	
			glEnable(GL_TEXTURE_2D);
	    	
			MC.getTextureManager().bindTexture(icon);
			glColor3f(1, 1, 1);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
			drawScaledTex(2, this.height / 2 - 16 / 2, (int) 16, (int) 16);
			glDisable(GL_BLEND);
			
			fontRenderer.drawString("Help", 21, 9, 0xffffffff, 0.8F, true);
			glEnable(GL_ALPHA_TEST);
    	
		postRender(1, false);
		}
		glPushMatrix();

		drawTexture(1);
		glPopMatrix();

	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			this.grabbed = true;
			((DefaultSettingsGUI) this.gui).resetSelected();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseDragged(int mouseX, int mouseY, int button) {
		if (!this.isSelected(mouseX, mouseY))
			this.grabbed = false;
		return super.mouseDragged(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int button) {
		if (this.grabbed) {
			if (this.isSelected(mouseX, mouseY))
				this.grabbed = false;

			
			this.clickSound();
			openPopup();

		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
}