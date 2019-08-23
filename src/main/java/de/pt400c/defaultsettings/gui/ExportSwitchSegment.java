package de.pt400c.defaultsettings.gui;

import static de.pt400c.defaultsettings.FileUtil.MC;
import java.awt.Color;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExportSwitchSegment extends Segment {
	
	private final String hoverMessage = "Switch Export Mode for the modpack";
    protected boolean grabbed;
    public float animTimer = 0;
    private float processFactor;
    public float flickerTimer = 0;

	public ExportSwitchSegment(Screen gui, float posX, float posY) {
		super(gui, posX, posY, 30, 15, false);
	}
	
	@Override
	public boolean isSelected(double mouseX, double mouseY) {
		return super.isSelected(mouseX, mouseY) || distanceBetweenPoints((float) this.getPosX() + (28F * (1 - processFactor)), (float) this.getPosY() + 7, (float) mouseX, (float) mouseY) <= 9.4F;
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		
		MC.fontRenderer.drawString("Export Mode:", (float) this.getPosX() - 77, (float) this.getPosY() + 3, 0xff5d5d5d);
		
		int on = 0xff08b306;
		
		final int off = 0xffd85755;
		float darken = 0;
		final byte exportActive = ((GuiConfig) this.gui).menu.exportActive.getByte();
		
		boolean inactive = false;
		
		if(exportActive == 2) {
			
			if(this.animTimer <= (Math.PI / 3))
				this.animTimer += 0.05;
			
		}else if(exportActive == 1){

			if(this.animTimer > 0)
				this.animTimer -= 0.05;
		}else {
			inactive = true;
			flickerTimer += 0.05;
			darken = (float) ((Math.sin(flickerTimer - Math.PI / 2) + 1) / 4 + 0.5);
			
		}
		
		final int getRed = getRed(on);
		final int getGreen = getGreen(on);
		final int getBlue = getBlue(on);
		
		this.processFactor = (float) ((Math.sin(3 * this.animTimer - (Math.PI / 2)) + 1) / 2);

		final float red = (getRed(off) - getRed) * (1 - processFactor);
		
		final float green = (getGreen(off) - getGreen) * (1 - processFactor);
		
		final float blue = (getBlue(off) - getBlue) * (1 - processFactor);
		
		on = new Color((int)(getRed + red), (int)(getGreen + green), (int) (getBlue + blue)).getRGB();
		
		if(inactive) 
			on = darkenColor(0xff5b5b5b, darken).getRGB();
		
		final float f3 = (float) (on >> 24 & 255) / 255.0F;
		final float f = (float) (on >> 16 & 255) / 255.0F;
		final float f1 = (float) (on >> 8 & 255) / 255.0F;
		final float f2 = (float) (on & 255) / 255.0F;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		float radius = 7;
		
		GlStateManager.color4f(f, f1, f2, f3);
		
		Segment.drawCircle((float) this.getPosX() + 7, (float) this.getPosY() + 7,  radius, 90, 50);
		
		Segment.drawCircle((float) this.getPosX() + 15 + 7, (float) this.getPosY() + 7, radius, 270, 50);
		
		Segment.drawRect(this.getPosX() + 7, (float) this.getPosY(), this.getPosX() + 7 + 15, this.getPosY() + 14, null, false, null, false);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		
		if(!inactive) {
		
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glScalef(0.65F, 0.65F, 0.65F);

			MC.fontRenderer.drawString("ON", (float) this.getPosX() + 110, (float) this.getPosY() + 11, new Color(255, 255, 255, (int) (MathHelper.clamp(255 * processFactor, 4, 255))).getRGB());

			MC.fontRenderer.drawString("OFF", (float) this.getPosX() + 95, (float) this.getPosY() + 11, new Color(255, 255, 255, (int) (MathHelper.clamp(255 * (1 - processFactor), 4, 255))).getRGB());
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		radius = 9.4F;
		
		GlStateManager.color3f(0.5F, 0.5F, 0.5F);

		Segment.drawCircle((float) this.getPosX() + (28F * (1 - processFactor)), (float) this.getPosY() + 7, radius, 0, 0);
		radius = 8.5F;
		GlStateManager.color3f(1, 1, 1);

		Segment.drawCircle((float) this.getPosX() + (28F * (1 - processFactor)), (float) this.getPosY() + 7, radius, 0, 0);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		
		
	}
	
	@Override
	public void hoverCheck(float mouseX, float mouseY) {
		if(this.isSelected(mouseX, mouseY) && this.hoverMessage != null) {
			
			final ArrayList<String> lines = new ArrayList<String>();
			
			int textWidth = (int) (mouseX + 14 + MC.fontRenderer.getStringWidth(this.hoverMessage));
			if(textWidth > this.gui.width) {
				lines.addAll(MC.fontRenderer.listFormattedStringToWidth(this.hoverMessage, (int) (this.gui.width - mouseX - 14)));
			}else {
				lines.add(this.hoverMessage);
			}
			textWidth = 0;
			for(String line : lines) 
				if(MC.fontRenderer.getStringWidth(line) > textWidth)
					textWidth = MC.fontRenderer.getStringWidth(line);
			
			Segment.drawButton(mouseX + 8, mouseY + 7, mouseX + 14 + textWidth, mouseY + 11 + 10 * lines.size(), 0xff3a3a3a, 0xffdcdcdc, 2);
			int offset = 0;
			
			for(String line : lines) {
				MC.fontRenderer.drawString(line, (float)(mouseX + 11), (float)(mouseY + 10 - offset), 0xff3a3a3a);
				offset -= 10;
			}
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			this.grabbed = true;
			((DefaultSettingsGUI) this.gui).resetSelected();

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button) {
		if (!this.isSelected(mouseX, mouseY))
			this.grabbed = false;
		return super.mouseDragged(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.grabbed) {
			if (this.isSelected(mouseX, mouseY))
				this.grabbed = false;

			this.clickSound();
			byte exportActive = ((GuiConfig) this.gui).menu.exportActive.getByte();
			if(exportActive == 2) 
				((GuiConfig) this.gui).copyConfigs();
			else if(exportActive == 1)
				((GuiConfig) this.gui).deleteConfigs();

		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

}