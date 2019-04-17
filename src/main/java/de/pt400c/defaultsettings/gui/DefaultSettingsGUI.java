package de.pt400c.defaultsettings.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;

public class DefaultSettingsGUI extends GuiScreen {
	
	private List<Segment> segments = new ArrayList<>();
	
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
    public void render(int mouseX, int mouseY, float partialTicks) {

        synchronized (this.segments) {
            this.segments.forEach(segment -> segment.render(mouseX, mouseY, partialTicks));
            this.segments.forEach(segment -> segment.hoverCheck(mouseX, mouseY));
        }
        super.render(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		synchronized (this.segments) {
			for (Segment segment : segments) {
				if (segment.mouseClicked(mouseX, mouseY, mouseButton)) {
					break;
				}

			}
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
		synchronized (this.segments) {
			for (Segment segment : this.segments) {
				if (segment.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_)) {
					break;
				}

			}
		}
		return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_,
				p_mouseDragged_8_);
	}
	
	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
		synchronized (this.segments) {
			for (Segment segment : this.segments) {
				if (segment.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
					break;
				}

			}
		}
		return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}
}
