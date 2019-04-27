package de.pt400c.defaultsettings.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;

public class DefaultSettingsGUI extends GuiScreen {
	
	private List<Segment> segments = new ArrayList<>();
	
	public PopupSegment popupField = null;
	
	public void addSegment(Segment segment) {
		synchronized (this.segments) {
			this.segments.add(segment);
		}
	}
	
	public void clearSegments() {
		synchronized (this.segments) {
			this.segments.clear();
		}
	}

	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		synchronized (this.segments) {
				this.segments.forEach(segment -> segment.render(mouseX, mouseY, partialTicks));
				
				if(this.popupField == null) {
				
				this.segments.forEach(segment -> segment.hoverCheck(mouseX, mouseY));
				
				}else {
					this.popupField.hoverCheck(mouseX, mouseY);
				}
		}
        super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		synchronized (this.segments) {
			if (this.popupField == null) {
				for (Segment segment : segments) {
					if (segment.mouseClicked(mouseX, mouseY, mouseButton)) {
						break;
					}
				}
			} else {

				this.popupField.mouseClicked(mouseX, mouseY, mouseButton);
			}
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	public void mouseClickMove(int p_mouseDragged_1_, int p_mouseDragged_3_, int p_mouseDragged_5_, long p_mouseDragged_8_) {
		synchronized (this.segments) {
			if (this.popupField == null) {
				for (Segment segment : this.segments) {
					if (segment.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_)) {
						break;
					}

				}
			} else {
				this.popupField.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_);
			}
		}
		super.mouseClickMove(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_8_);
	}
	
	public void mouseReleased(int p_mouseReleased_1_, int p_mouseReleased_3_, int p_mouseReleased_5_) {
		synchronized (this.segments) {
			if (this.popupField == null) {
				for (Segment segment : this.segments) {
					if (segment.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
						break;
					}

				}
			} else {
				this.popupField.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
			}
		}
		super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}
}
