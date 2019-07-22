package de.pt400c.defaultsettings.gui;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;

@SideOnly(Side.CLIENT)
public class DefaultSettingsGUI extends GuiScreen {
	
	private List<Segment> segments = new ArrayList<Segment>();
	
	public boolean dragging = false;
	
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
	protected void keyTyped(char typedChar, int keyCode) {
		boolean positive = false;
		synchronized (this.segments) {
			if (this.popupField == null) {
				for (Segment segment : segments) {
					if (segment.keyTyped(typedChar, keyCode)) {
						positive = true;
						break;
					}
				}
			} else {

				positive = this.popupField.keyTyped(typedChar, keyCode);
			}
		}
		if(!positive)
			super.keyTyped(typedChar, keyCode);
	}

	@Override
	public void handleMouseInput() {
		synchronized (this.segments) {
			if (this.popupField == null) {
				for (Segment segment : this.segments) {
					if (segment.handleMouseInput()) {
						break;
					}

				}
			} else {
				this.popupField.handleMouseInput();
			}
		}
		super.handleMouseInput();
	}

	@Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {

        synchronized (this.segments) {
        	for(Segment segment : this.segments)
	        	segment.render(mouseX, mouseY, partialTicks);
			
			if(this.popupField == null) {
				
			for(Segment segment : this.segments)
				segment.hoverCheck(mouseX, mouseY);
			
			}else {
				this.popupField.hoverCheck(mouseX, mouseY);
			}
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
	}
	

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
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
	
	@Override
	public void mouseClickMove(int p_mouseDragged_1_, int p_mouseDragged_3_, int p_mouseDragged_5_, long p_mouseDragged_8_) {
		this.dragging = true;
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
	
	@Override
	public void mouseMovedOrUp(int p_mouseReleased_1_, int p_mouseReleased_3_, int p_mouseReleased_5_) {
		this.dragging = false;
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
		super.mouseMovedOrUp(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}
}
