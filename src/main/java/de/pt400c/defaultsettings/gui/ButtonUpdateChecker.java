package de.pt400c.defaultsettings.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;

import com.mojang.blaze3d.platform.GlStateManager;

import de.pt400c.defaultsettings.DefaultSettings;
import de.pt400c.defaultsettings.GuiConfig;
import de.pt400c.defaultsettings.UpdateContainer;
import de.pt400c.defaultsettings.UpdateContainer.Status;
import de.pt400c.neptunefx.NEX;
import static de.pt400c.neptunefx.NEX.*;
import static de.pt400c.defaultsettings.DefaultSettings.fontRenderer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glScissor;

import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ButtonUpdateChecker extends ButtonSegment {

	public float timer = 0;
	private final LeftMenu menu;
	private final Function<GuiConfig, Float> posYF;

	public ButtonUpdateChecker(Screen gui, Function<GuiConfig, Float> posY, LeftMenu menu) {
		super(gui, 0, posY.apply((GuiConfig) gui), null, null, 60, 24, 2);
		this.posYF = posY;
		this.menu = menu;
		if(DefaultSettings.getUpdater().getStatus() == UpdateContainer.Status.ERROR || DefaultSettings.getUpdater().getStatus() == UpdateContainer.Status.UNKNOWN)
			DefaultSettings.getUpdater().update();
		
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		if(resized != this.resized_mark) {
			posY = posYF.apply((GuiConfig) this.gui);
			this.resized_mark = resized;
		}
		
		this.timer += 0.05;
		
		this.width = 60 - this.menu.offs;
		final float right = this.menu.width - this.menu.offs + this.width - 35 + this.menu.offs;
		this.posX = right / 2 - this.width / 2;
				
		final float darken = (float) ((Math.sin(this.timer - MathUtil.PI / 2) + 1) / 4 + 0.5);

		float inRad = 1.5F;

		GlStateManager.enableBlend();
    	GlStateManager.disableAlphaTest();
    	GlStateManager.disableTexture();
    	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		drawRectRoundedCorners(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), NEX.darkenColor(statusToColor(DefaultSettings.getUpdater().getStatus(), darken), 0.5F).getRGB(), 5);
	
		float factor = 1F - ((5 - inRad) / 5);
	       
	    float innerRadius = 5 - (factor * 5);

		drawRectRoundedCorners(this.getPosX() + inRad, this.getPosY() + inRad, (float) this.getPosX() + this.width - inRad, (float) this.getPosY() + this.height - inRad, statusToColor(DefaultSettings.getUpdater().getStatus(), darken), innerRadius < 0 ? 0 : innerRadius);

    	GlStateManager.disableBlend();
    	GlStateManager.enableAlphaTest();

    	GlStateManager.enableBlend();
 
    	GlStateManager.disableTexture();
    	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
 
    	glEnable(GL_SCISSOR_TEST);

		glScissor((6 + (int) (this.menu.offs / 2.8F)) * (int) scaledFactor, (int) (5) * (int) scaledFactor, (int)((54 - this.menu.offs / 0.6F) * (int) scaledFactor), 25 * (int) scaledFactor);

		final float percent = MathUtil.clamp(menu.offsetTick / menu.maxOffTick, 0, 1);
		GlStateManager.enableTexture();
		fontRenderer.drawString(statusToStr(DefaultSettings.getUpdater().getStatus()), posX + width / 2F - fontRenderer.getStringWidth(statusToStr(DefaultSettings.getUpdater().getStatus()), 0.8F, false) / 2 - 3, this.getPosY() + 9, calcAlpha(DefaultSettings.getUpdater().getStatus() != Status.OUTDATED ? 0xffffffff : 0xff6e6e6e, percent).getRGB(), 0.8F, true);
	
	    glDisable(GL_SCISSOR_TEST);
	    	
	    GlStateManager.disableBlend();
	}

	@Override
	public boolean hoverCheck(int mouseX, int mouseY) {
		final String text = statusToIdentifier(DefaultSettings.getUpdater().getStatus());
		if(this.isSelected(mouseX, mouseY) && text != null) {
			
			ArrayList<String> lines = new ArrayList<String>();
			
			float textWidth = 0;
			lines.addAll(fontRenderer.listFormattedStringToWidth(text, (int) (this.gui.field_230708_k_ - mouseX - 12), true));
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
			if (this.isSelected(mouseX, mouseY)) {
				this.grabbed = false;

				DefaultSettings.getUpdater().update();
				this.clickSound();
		}

		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	public static String statusToIdentifier(UpdateContainer.Status status) {
		switch (status) {
		case CHECKING:
			return "Checking ...";
		case OUTDATED:
			return String.format("Your mod version is outdated\nPlease update to %s", DefaultSettings.getUpdater().getOnlineVersion());
		case AHEAD_OF_TIME:
			return "Heck, you're ahead of reality?!";
		case UP_TO_DATE:
			return "Up to date";
		case ERROR:

		default:
			return "Something went wrong :(\nWe couldn't check if your\ninstallation is up-to-date";
		}
	}
	
	public static String statusToStr(UpdateContainer.Status status) {
		switch (status) {
		case CHECKING:
			return "Checking";
		case OUTDATED:
			return "Outdated";
		case AHEAD_OF_TIME:
			return "Beta";
		case UP_TO_DATE:
			return "Latest";
		case ERROR:

		default:
			return "Error";
		}
	}
	
	public static int statusToColor(UpdateContainer.Status status, float darken) {
		
		switch (status) {
		case CHECKING: 
			return NEX.darkenColor(0xffcfcfcf, darken).getRGB();
		case OUTDATED: 
			return 0xfff5ac21;
		case AHEAD_OF_TIME: 
			return 0xff0884b6;
		case UP_TO_DATE: 
			return 0xff2ca220;
		case ERROR: 
		default: 
			return 0xfff42310;
		}
	}
}