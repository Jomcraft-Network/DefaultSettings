package de.pt400c.defaultsettings.gui;

import static de.pt400c.defaultsettings.FileUtil.MC;
import java.util.ArrayList;
import java.util.Collections;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.pt400c.defaultsettings.DefaultSettings;
import de.pt400c.defaultsettings.UpdateContainer;
import de.pt400c.neptunefx.NEX;
import net.minecraft.client.gui.GuiScreen;
import static de.pt400c.neptunefx.NEX.*;

@SideOnly(Side.CLIENT)
public class ButtonUpdateChecker extends ButtonSegment {

	public float timer = 0;
	private final LeftMenu menu;

	public ButtonUpdateChecker(GuiScreen gui, float posY, LeftMenu menu) {
		super(gui, 0, posY, null, null, 20, 20, 2);
		
		this.menu = menu;
		if(DefaultSettings.getUpdater().getStatus() == UpdateContainer.Status.ERROR || DefaultSettings.getUpdater().getStatus() == UpdateContainer.Status.UNKNOWN)
			DefaultSettings.getUpdater().update();
		
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		this.timer += 0.05;
		final float right = this.menu.width - this.menu.offs + this.width + 6;
		this.posX = right / 2 - this.width / 2;
				
		final float darken = (float) ((Math.sin(this.timer - Math.PI / 2) + 1) / 4 + 0.5);
		drawButton(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), this.isSelected(mouseX, mouseY) ? darkenColor(this.color).getRGB() : this.color, statusToColor(DefaultSettings.getUpdater().getStatus(), darken), this.border);
	
	}

	@SuppressWarnings("unchecked")
	@Override
	public void hoverCheck(float mouseX, float mouseY) {
		final String text = statusToIdentifier(DefaultSettings.getUpdater().getStatus());
		if(this.isSelected(mouseX, mouseY) && text != null) {
			
			ArrayList<String> lines = new ArrayList<String>();
			
			int textWidth = 0;
			lines.addAll(MC.fontRenderer.listFormattedStringToWidth(text, (int) (this.gui.width - mouseX - 12)));
			for(String line : lines) {
				
				if(MC.fontRenderer.getStringWidth(line) > textWidth)
					textWidth = MC.fontRenderer.getStringWidth(line);
			}
			
			drawButton(mouseX + 6, mouseY - 7 - 10 * lines.size(), mouseX + 12 + textWidth, mouseY - 3, 0xff3a3a3a, 0xffdcdcdc, 2);
			int offset = 0;
			
			Collections.reverse(lines);
			
			for(String line : lines) {
			
				this.drawString(line, (float)(mouseX + 9), (float)(mouseY - 14 - offset), 0xff3a3a3a, false);
				offset += 10;
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
		case AHEAD_OF_TIME:
			return "Heck, you're ahead of reality?!";
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
			return NEX.darkenColor(0xffcfcfcf, darken).getRGB();
		case OUTDATED: 
			return 0xfff5ac21;
		case AHEAD_OF_TIME: 
			return 0xff0884b6;
		case UP_TO_DATE: 
			return 0xff68f521;
		case ERROR: 
		default: 
			return 0xfff42310;
		}
	}
}