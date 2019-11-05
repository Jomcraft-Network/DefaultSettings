package de.pt400c.defaultsettings.gui;

import java.util.ArrayList;
import java.util.List;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import static de.pt400c.defaultsettings.DefaultSettings.fontRenderer;
import static de.pt400c.neptunefx.NEX.*;

@SideOnly(Side.CLIENT)
public class PopupWindow extends Segment {
	
	List<Segment> children = new ArrayList<>();
	public String title;

	public PopupWindow(GuiScreen gui, float posX, float posY, float width, float height, String title) {
		super(gui, posX, posY, width, height, true);
		this.title = title;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		
		posX = 5;
		posY = 5;
		drawRectRoundedUpper((float) this.posX, (float) this.posY, (float) (this.posX + width), (float) (this.posY + 24), 0xff505050);
		drawRectRoundedLower((float) this.posX, (float) this.posY + 24, (float) (this.posX + width), (float) (this.posY + height), 0xff2c2c2c);
		drawRect((float) this.posX, (float) this.posY + 24, (float) (this.posX + width), this.posY + 25, 0xff3b3b3b, true, null, false);
	}
	
	public void renderContents(int mouseX, int mouseY, float partialTicks) {
		fontRenderer.drawString(this.title, (float) (this.getPosX() + this.getWidth() / 2 + 1 - fontRenderer.getStringWidth(this.title, 1.1F, true) / 2), (float) (this.getPosY() + 8), 0xfffafafa, 1.1F, true);
		synchronized (this.children) {
			this.children.forEach(segment -> segment.render(mouseX, mouseY, partialTicks));
		}
	}
	
	public void hoverChecks(int mouseX, int mouseY) {
		synchronized (this.children) {
			this.children.forEach(segment -> segment.hoverCheck(mouseX, mouseY));
		}
	}

	@Override
	public boolean isSelected(int mouseX, int mouseY) {
		return (mouseX >= this.getPosX() && mouseY >= this.getPosY() + 10 && mouseX < this.getPosX() + this.getWidth() && mouseY < this.getPosY() + 24) || (mouseX >= this.getPosX() + 10 && mouseY >= this.getPosY() && mouseX < this.getPosX() + this.getWidth() - 10 && mouseY < this.getPosY() + 10) || (distanceBetweenPoints((float) this.getPosX() + 10F, (float) this.getPosY() + 10F, (float) mouseX, (float) mouseY) <= 10) || (distanceBetweenPoints((float) this.getPosX() + this.getWidth() - 10F, (float) this.getPosY() + 10F, (float) mouseX, (float) mouseY) <= 10);
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {

			synchronized (this.children) {
				for (Segment segment : children) {
					if (segment.mouseClicked(mouseX, mouseY, mouseButton)) 
						return true;

				}
				
			}
			return false;
	}
	
	@Override
	public boolean mouseDragged(int p_mouseDragged_1_, int p_mouseDragged_3_, int p_mouseDragged_5_) {
			synchronized (this.children) {
				for (Segment segment : this.children) {
					if (segment.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_))
						break;

				}
			}
		return false;
	}
	
	@Override
	public boolean mouseReleased(int p_mouseReleased_1_, int p_mouseReleased_3_, int p_mouseReleased_5_) {
			synchronized (this.children) {
				for (Segment segment : this.children) {
					if (segment.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_))
						return true;

				}
			}
			
		return false;
	}

	public PopupWindow addChild(Segment segment) {
		synchronized (this.children) {
			float offX = ((GuiConfig) this.gui).popupField == null ? 0 : ((GuiConfig) this.gui).popupField.popX;
			float offY = ((GuiConfig) this.gui).popupField == null ? 0 : ((GuiConfig) this.gui).popupField.popY;
			
			this.children.add(segment.setPosHit(5 + segment.posX, 5 + segment.posY, offX, offY));
		}
		return this;
	}

	public void clearChildren() {
		((GuiConfig) this.gui).popupField.reset();
		synchronized (this.children) {
			this.children.clear();
		}
	}

	public List<Segment> getChildren() {
		return this.children;
	}
}