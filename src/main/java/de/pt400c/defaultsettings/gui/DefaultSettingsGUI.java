package de.pt400c.defaultsettings.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DefaultSettingsGUI extends GuiScreen {
	
	private List<Segment> segments = new ArrayList<>();
	
	public MenuScreen menu;
	
	public PopupSegment popupField = null;
	
	public void addSegment(Segment segment) {
		synchronized (this.segments) {
			this.segments.add(segment);
		}
	}
	
	public void clearSegments() {
		segments = new ArrayList<>();
	}
	
	@Override
	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
		synchronized (this.segments) {
			if (this.popupField == null) {
				for (Segment segment : segments) {
					if (segment.charTyped(p_charTyped_1_, p_charTyped_2_)) {
						return true;
					}
				}
			} else {

				return this.popupField.charTyped(p_charTyped_1_, p_charTyped_2_);
			}
		}
		return super.charTyped(p_charTyped_1_, p_charTyped_2_);
	}
	
	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		synchronized (this.segments) {
			if (this.popupField == null) {
				for (Segment segment : segments) {
					if (segment.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
						return true;
					}
				}
			} else {

				return this.popupField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
			}
		}
		return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
    public void render(int mouseX, int mouseY, float partialTicks) {

		synchronized (this.segments) {
				this.segments.forEach(segment -> segment.render(mouseX, mouseY, partialTicks));
				
				if(this.popupField == null) {
					for (Segment segment : segments) {
						if (segment.hoverCheck(mouseX, mouseY)) {
							break;
						}
					}
				
				}else {
					this.popupField.hoverCheck(mouseX, mouseY);
				}
		}
        super.render(mouseX, mouseY, partialTicks);
	}
	
	public void resetSelected() {
		final MenuScreen menu = this.menu;
		if(menu != null)
			menu.getVariants().get(menu.index).selected = null;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		synchronized (this.segments) {
			if (this.popupField == null) {
				for (Segment segment : segments) {
					if (segment.mouseClicked((int) mouseX, (int) mouseY, mouseButton)) {
						break;
					}
				}
			} else {

				this.popupField.mouseClicked((int) mouseX, (int) mouseY, mouseButton);
			}

		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
		synchronized (this.segments) {
			if (this.popupField == null) {
				for (Segment segment : this.segments) {
					if (segment.mouseDragged((int) p_mouseDragged_1_, (int) p_mouseDragged_3_, p_mouseDragged_5_)) {
						break;
					}

				}
			} else {
				this.popupField.mouseDragged((int) p_mouseDragged_1_, (int) p_mouseDragged_3_, p_mouseDragged_5_);
			}
		}
		return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_,
				p_mouseDragged_8_);
	}
	
	@Override
	public void initGui() {
		
		synchronized (this.segments) {
        	for(Segment segment : this.segments)
	        	segment.initSegment();

        }
		super.initGui();
	}
	
	@Override
	public boolean mouseScrolled(double p_mouseScrolled_1_) {
		synchronized (this.segments) {
			if (this.popupField == null) {
				for (Segment segment : this.segments) {
					if (segment.mouseScrolled((float) p_mouseScrolled_1_)) {
						return true;
					}

				}
			} else {
				return this.popupField.mouseScrolled((float) p_mouseScrolled_1_);
			}
		}
		return super.mouseScrolled(p_mouseScrolled_1_);
	}
	
	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
		synchronized (this.segments) {
			if (this.popupField == null) {
				for (Segment segment : this.segments) {
					if (segment.mouseReleased((int) p_mouseReleased_1_, (int) p_mouseReleased_3_, p_mouseReleased_5_)) {
						break;
					}

				}
			} else {
				this.popupField.mouseReleased((int) p_mouseReleased_1_, (int) p_mouseReleased_3_, p_mouseReleased_5_);
			}
		}
		return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}
}
