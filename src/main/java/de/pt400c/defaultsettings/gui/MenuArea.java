package de.pt400c.defaultsettings.gui;

import java.util.ArrayList;
import java.util.List;

import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.GuiScreen;

public class MenuArea extends Segment {
	
	private List<Segment> children = new ArrayList<>();

	public MenuArea(GuiScreen gui, float posX, float posY) {
		super(gui, posX, posY, gui.width - posX, gui.height - posY, false);
	}
	
	@Override
    public void render(float mouseX, float mouseY, float partialTicks) {

        synchronized (this.children) {
            this.children.forEach(segment -> segment.render(mouseX, mouseY, partialTicks));

            if(((GuiConfig) this.gui).popupField == null)
            	this.children.forEach(segment -> segment.hoverCheck(mouseX, mouseY));
        }

	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		synchronized (this.children) {
			for (Segment segment : children) {
				if (segment.mouseClicked(mouseX, mouseY, mouseButton)) {
					break;
				}
			}
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_) {
		synchronized (this.children) {
			for (Segment segment : this.children) {
				if (segment.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_)) {
					break;
				}

			}
		}
		return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_);
	}
	
	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
		synchronized (this.children) {
			for (Segment segment : this.children) {
				if (segment.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
					break;
				}

			}
		}
		return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}

	public MenuArea addChild(Segment segment) {
		synchronized (this.children) {
			this.children.add(segment.setPos(this.posX + segment.posX, this.posY + segment.posY));
		}
		return this;
	}

}
