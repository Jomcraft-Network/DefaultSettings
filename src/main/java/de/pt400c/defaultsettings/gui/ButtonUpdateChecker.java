package de.pt400c.defaultsettings.gui;

import static de.pt400c.defaultsettings.FileUtil.MC;
import java.util.ArrayList;
import java.util.Collections;
import de.pt400c.defaultsettings.DefaultSettings;
import de.pt400c.defaultsettings.GuiConfig;
import de.pt400c.defaultsettings.UpdateContainer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ButtonUpdateChecker extends ButtonSegment {

	public float timer = 0;

	public ButtonUpdateChecker(GuiScreen gui, float posX, float posY) {
		super(gui, posX, posY, null, null, 20, 20, 2);
		if(DefaultSettings.getUpdater().getStatus() == UpdateContainer.Status.ERROR || DefaultSettings.getUpdater().getStatus() == UpdateContainer.Status.UNKNOWN)
			DefaultSettings.getUpdater().update();
		
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		timer += 0.05;
		float darken = (float) ((Math.sin(timer - Math.PI / 2) + 1) / 4 + 0.5);
		Segment.drawButton(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), this.isSelected(mouseX, mouseY) ? darkenColor(this.color).getRGB() : this.color, statusToColor(DefaultSettings.getUpdater().getStatus(), darken), this.border);
	
	}

	@Override
	public void hoverCheck(float mouseX, float mouseY) {
		String text = statusToIdentifier(DefaultSettings.getUpdater().getStatus());
		if(this.isSelected(mouseX, mouseY) && text != null) {
			
			ArrayList<String> lines = new ArrayList<String>();
			
			int textWidth = 0;
			lines.addAll(MC.fontRenderer.listFormattedStringToWidth(text, (int) (this.gui.width - mouseX - 12)));
			for(String line : lines) {
				
				if(MC.fontRenderer.getStringWidth(line) > textWidth)
					textWidth = MC.fontRenderer.getStringWidth(line);
			}
			
			Segment.drawButton(mouseX + 6, mouseY - 7 - 10 * lines.size(), mouseX + 12 + textWidth, mouseY - 3, 0xff3a3a3a, 0xffdcdcdc, 2);
			int offset = 0;
			
			Collections.reverse(lines);
			
			for(String line : lines) {
			
				MC.fontRenderer.drawString(line, (float)(mouseX + 9), (float)(mouseY - 14 - offset), 0xff3a3a3a);
				offset += 10;
			}
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.isSelected(mouseX, mouseY)) {
			this.grabbed = true;
			MenuScreen menu = ((GuiConfig) this.gui).menu;
			menu.getVariants().get(menu.index).selected = null;

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
			return String.format("Your mod's version is outdated\nPlease update to %s", DefaultSettings.getUpdater().getOnlineVersion());
		case UP_TO_DATE: 
			return "Up to date";
		case ERROR: 

		default: 
			return "Something went wrong :(\nWe couldn't check if your\ninstallation is up-to-date";
		}
		
	}
	
	public static int statusToColor(UpdateContainer.Status status, float darken) {
		
		switch (status) {
		case CHECKING: 
			return darkenColor(0xffcfcfcf, darken).getRGB();
		case OUTDATED: 
			return 0xfff5ac21;
		case UP_TO_DATE: 
			return 0xff68f521;
		case ERROR: 

		default: 
			return 0xfff42310;
		}
		
	}

}