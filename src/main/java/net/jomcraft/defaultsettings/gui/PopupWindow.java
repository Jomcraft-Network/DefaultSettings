package net.jomcraft.defaultsettings.gui;

import java.util.ArrayList;
import java.util.List;

import net.jomcraft.defaultsettings.GuiConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static net.jomcraft.neptunefx.NeptuneFX.*;
import static net.jomcraft.neptunefx.NEX.*;

@OnlyIn(Dist.CLIENT)
public class PopupWindow extends Segment {
	
	List<Segment> children = new ArrayList<>();
	public String title;

	public PopupWindow(Screen gui, float posX, float posY, float width, float height, String title) {
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
	
	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		synchronized (this.children) {
			for (Segment segment : this.children) {
				if (segment.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_))
					break;

			}
		}
		return false;
	}
	
	@Override
	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
		synchronized (this.children) {
			for (Segment segment : this.children) {
				if (segment.charTyped(p_charTyped_1_, p_charTyped_2_))
					break;

			}
		}
		return false;
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