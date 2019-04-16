package de.pt400c.defaultsettings.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;

public class DefaultSettingsGUI extends GuiScreen {
	
	private List<Segment> segments = new ArrayList<Segment>();
	
	public boolean dragging = false;
	
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
            for(Segment segment : this.segments)
            	segment.render(mouseX, mouseY, partialTicks);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
	}
	

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		this.dragging = true;
		synchronized (this.segments) {
			for (Segment segment : segments) {
				if (segment.mouseClicked(mouseX, mouseY, mouseButton)) {
					break;
				}

			}
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void mouseMovedOrUp(int p_mouseReleased_1_, int p_mouseReleased_3_, int p_mouseReleased_5_) {
		this.dragging = false;
		synchronized (this.segments) {
			for (Segment segment : this.segments) {
				if (segment.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
					break;
				}

			}
		}
		super.mouseMovedOrUp(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}
}
