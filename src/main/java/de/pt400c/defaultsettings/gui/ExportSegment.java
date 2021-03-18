package de.pt400c.defaultsettings.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import static net.jomcraft.neptunefx.NeptuneFX.*;
import static net.jomcraft.neptunefx.NEX.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.pt400c.defaultsettings.DefaultSettings;
import de.pt400c.defaultsettings.FileUtil;
import de.pt400c.defaultsettings.GuiConfig;
import net.jomcraft.neptunefx.NEX;
import net.jomcraft.neptunefx.gui.MathUtil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

@SideOnly(Side.CLIENT)
public class ExportSegment extends BakedSegment {
	
	private final String hoverMessage = "Switch Export Mode for the modpack";
	private final LeftMenu menu;
	private final Function<GuiConfig, Integer> posYF;
	public float animTimer = 0;
	protected boolean grabbed;
    private float processFactor;
    public float flickerTimer = 0;
	private float prevOff;
	static boolean locked;
	private static final ResourceLocation icon = new ResourceLocation(DefaultSettings.MODID, "textures/gui/export_mode.png");
	private boolean doIt;
	
	public ExportSegment(GuiScreen gui, float posX, Function<GuiConfig, Integer> posY, float width, float height, LeftMenu menu) {
		super(gui, 0, posX, posY.apply((GuiConfig) gui), width, height, 44, 44, 44, true, false);
		this.posYF = posY;
		this.menu = menu;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {

		locked = ((GuiConfig) this.gui).menu.exportActive.getByte() == 2 && FileUtil.exportMode() && FileUtil.mainJson.activeConfigs.size() != 0;
		
		if (resized != this.resized_mark)
			posY = posYF.apply((GuiConfig) this.gui);

		setup();

		boolean inactive = false;
		final byte exportActive = ((GuiConfig) this.gui).menu.exportActive.getByte();
		float darken = 0;
		if (exportActive == 2) {

			if (this.animTimer <= MathUtil.PI / 3) {
				this.animTimer += 0.05;
				doIt = true;
			}

		} else if (exportActive == 1) {

			if (this.animTimer > 0) {
				this.animTimer -= 0.05;
				doIt = true;
			}
		} else {
			inactive = true;
			flickerTimer += 0.05;
			darken = (float) ((Math.sin(flickerTimer - MathUtil.PI / 2) + 1) / 4 + 0.5);
			doIt = true;

		}

		if (this.menu.offs != this.prevOff || doIt) {
			compiled = false;
			doIt = false;
		}

		if (!compiled) {
			preRender();

			glEnable(GL_BLEND);
			glDisable(GL_ALPHA_TEST);
			glDisable(GL_TEXTURE_2D);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			glShadeModel(GL_SMOOTH);

			drawGradient(0, 1, 72 - this.menu.offs, 2, 0xffaaaaaa, 0x00e6e6e6, 1);

			drawGradient(0, 41, 72 - this.menu.offs, 42, 0xffaaaaaa, 0x00e6e6e6, 1);
			glShadeModel(GL_FLAT);

			NEX.drawRect(0, 0, 72 - this.menu.offs, 1, 0xffe6e6e6, false, null, false);

			NEX.drawRect(0, 40, 72 - this.menu.offs, 41, 0xffe6e6e6, false, null, false);

			int on = 0xff08b306;

			final int off = 0xffd85755;

			float offset = this.menu.offs / 20;

			final int getRed = getRed(on);
			final int getGreen = getGreen(on);
			final int getBlue = getBlue(on);

			this.processFactor = (float) ((Math.sin(3 * this.animTimer - (MathUtil.PI / 2)) + 1) / 2);

			final float red = (getRed(off) - getRed) * (1 - processFactor);

			final float green = (getGreen(off) - getGreen) * (1 - processFactor);

			final float blue = (getBlue(off) - getBlue) * (1 - processFactor);

			on = new Color((int) (getRed + red), (int) (getGreen + green), (int) (getBlue + blue)).getRGB();

			if (inactive)
				on = darkenColor(0xff5b5b5b, darken).getRGB();

			float xPos = (72 - this.menu.offs) / 2 - 29F / 2F;

			drawRectRoundedCorners(xPos + offset * 2 - 0.5F, 20 + offset - 1, xPos + 29 - offset * 2 + 0.5F, 14 + 20 - offset - 1F, darkenColor(on, 0.5F).getRGB(), 7 - offset);

			drawRectRoundedCorners(xPos + offset * 2, 20 + offset, xPos + 29 - offset * 2, 14 + 20 - offset, on, 7 - offset);

			glEnable(GL_TEXTURE_2D);
			glDisable(GL_BLEND);
			glEnable(GL_ALPHA_TEST);

			fontRenderer.drawString("I", xPos + offset * -1F + 11, 23.5F, new Color(255, 255, 255, (int) (MathUtil.clamp(255 * processFactor, 4, 255))).getRGB(), 0.8F, true);

			fontRenderer.drawString("0", xPos + 29 + offset * 0.7F - 14, 23.5F, new Color(255, 255, 255, (int) (MathUtil.clamp(255 * (1 - processFactor), 4, 255))).getRGB(), 0.8F, true);

			glEnable(GL_BLEND);
			glDisable(GL_ALPHA_TEST);
			glDisable(GL_TEXTURE_2D);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			float radius = 9.4F;

			int color = 0xffe6e6e6;

			float f = (float) (color >> 16 & 255) / 255.0F;
			float f1 = (float) (color >> 8 & 255) / 255.0F;
			float f2 = (float) (color & 255) / 255.0F;

			glColor3f(f, f1, f2);

			drawCircle((float) ((28F - offset * 4) * processFactor) + xPos + offset * 2, (float) 7 + 20, radius - offset, 0, 0);
			radius = 8.5F;

			color = 0xff3c3c3c;

			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;

			glColor3f(f, f1, f2);

			drawCircle((float) ((28F - offset * 4) * processFactor) + xPos + offset * 2, (float) 7 + 20, radius - offset, 0, 0);

			final int scaleFactor = scaledresolution.getScaleFactor();

			glEnable(GL_TEXTURE_2D);
			glDisable(GL_BLEND);
			glEnable(GL_ALPHA_TEST);
			final float percent = MathUtil.clamp(menu.offsetTick / menu.maxOffTick, 0, 0.95F);
			glPushMatrix();
			glEnable(GL_BLEND);
			glBlendFuncSeparate(770, 771, 1, 0);
			MC.getTextureManager().bindTexture(icon);
			glColor4f(1, 1, 1, percent);
			glTranslatef(5 + 7 * percent, 2, 0);
			drawScaledTex(0, 0, 19, 19);
			glDisable(GL_BLEND);
			glPopMatrix();

			glEnable(GL_SCISSOR_TEST);

			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			glScissor((int) ((this.menu.offs * 1.2F) / 2 * (scaleFactor)), (int) (5 * (scaleFactor)), (int) ((72 - this.menu.offs * 1.6F) * (scaleFactor)) - (int) ((this.menu.offs * 1.2F) / 2 * (scaleFactor)), (int) (35 * (scaleFactor)));

			glDisable(GL_BLEND);

			if (!(percent >= 0.8F))
				fontRenderer.drawString("Export Mode:", 2F + (72 - this.menu.offs) / 2 - fontRenderer.getStringWidth("Export Mode:", 0.9F, true) / 2, 6, calcAlpha(0xffffffff, percent).getRGB(), 0.9F, true);

			glDisable(GL_SCISSOR_TEST);

			postRender(1, false);
		}
		glPushMatrix();

		drawTexture(1);
		glPopMatrix();

		this.prevOff = this.menu.offs;
	}
	
	@Override
	public boolean isSelected(int mouseX, int mouseY) {
		float offset = this.menu.offs / 20;
		float xPos = (72 - this.menu.offs) / 2 - 29F / 2F;
		return (mouseX >= this.getPosX() + xPos + offset * 2 && mouseY >= this.getPosY() + 20 + offset && mouseX < this.getPosX() + xPos + 29 - offset * 2 && mouseY < this.getPosY() + 14 + 20 - offset) || (distanceBetweenPoints((float) ((28F - offset * 4) * processFactor) + xPos + offset * 2, this.posY + 7 + 20, (float) mouseX, (float) mouseY) <= 9.4F - offset);
	}
	
	@Override
	public boolean hoverCheck(int mouseX, int mouseY) {
		if(this.isSelected(mouseX, mouseY) && this.hoverMessage != null) {
			final ArrayList<String> lines = new ArrayList<String>();
			float textWidth = (int) (mouseX + 12 + fontRenderer.getStringWidth(this.hoverMessage, 0.8F, true));
			if(textWidth > this.gui.width) {
				lines.addAll(fontRenderer.listFormattedStringToWidth(this.hoverMessage, (int) (this.gui.width - mouseX - 12), true));
			}else {
				lines.add(this.hoverMessage);
			}
			textWidth = 0;
			for(String line : lines) {
				
				if(fontRenderer.getStringWidth(line, 0.8F, true) > textWidth)
					textWidth = fontRenderer.getStringWidth(line, 0.8F, true);
			}
			
			drawButton(mouseX + 5, mouseY - 7 - 10 * lines.size(), mouseX + 15 + textWidth, mouseY - 3, 0xff3a3a3a, 0xffdcdcdc, 2);
			int offset = 0;
			
			Collections.reverse(lines);
			
			for(String line : lines) {
			
				fontRenderer.drawString(line, (float)(mouseX + 9), (float)(mouseY - 14 - offset), 0xff3a3a3a, 0.8F, true);
				offset += 10;
			}
			return true;
		}
		return false;
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
			byte exportActive = ((GuiConfig) this.gui).menu.exportActive.getByte();
			if(exportActive == 2) 
				((GuiConfig) this.gui).copyConfigs();
			else if(exportActive == 1) 
				((GuiConfig) this.gui).deleteConfigs();

		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
}