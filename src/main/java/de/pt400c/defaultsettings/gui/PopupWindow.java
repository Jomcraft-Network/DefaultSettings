package de.pt400c.defaultsettings.gui;

import static de.pt400c.defaultsettings.FileUtil.MC;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.GuiScreen;

public class PopupWindow extends Segment {
	
	private List<Segment> children = new ArrayList<>();
	
	public String title;
	
	public float alphaRate;
	
	@Deprecated
	private List<Segment> persistent = new ArrayList<>();
	
	private boolean dragging;
	private double distanceX = 0;
	private double distanceY = 0;

	public PopupWindow(GuiScreen gui, float posX, float posY, float width, float height, String title) {
		super(gui, posX, posY, width, height, true);
		this.title = title;
	}
	
	@Override
	public void render(final float mouseX, final float mouseY, final float partialTicks) {
		
		this.alphaRate = ((GuiConfig) this.gui).popupField == null ? 1 : (float) ((Math.sin(3 * ((GuiConfig) this.gui).popupField.windowTimer - 3 * (Math.PI / 2)) + 1) / 2);

		Segment.drawRectRoundedUpper((float) this.posX, (float) this.posY, (float) (this.posX + width), (float) (this.posY + 24), 0xff8b8b8b, this.alphaRate);
		Segment.drawRectRoundedLower((float) this.posX, (float) this.posY + 24, (float) (this.posX + width), (float) (this.posY + height), 0xfffbfbfb, this.alphaRate);
		
		GL11.glPushMatrix();
     	GL11.glEnable(GL11.GL_BLEND);
     	GL14.glBlendFuncSeparate(770, 771, 1, 0);
		this.drawString(this.title, (float) (this.getPosX() + this.getWidth() / 2 + 1 - MC.fontRenderer.getStringWidth(this.title) / 2), (float) (this.getPosY() + 9), calcAlpha(0xff1b1b1b, this.alphaRate).getRGB(), false);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		
		synchronized (this.children) {
			for(Segment child : this.children)
				child.render(mouseX, mouseY, partialTicks);

			for(Segment child : this.children)
				child.hoverCheck(mouseX, mouseY);
		}
		
		/*
		synchronized (this.persistent) {
			this.persistent.forEach(segment -> segment.render(mouseX, mouseY, partialTicks));

			this.persistent.forEach(segment -> segment.hoverCheck(mouseX, mouseY));
		}*/

		if (this.dragging) {

			final double origX = this.posX;
			final double origY = this.posY;
			
			this.posX = mouseX - distanceX;
			this.posY = mouseY - distanceY;
			
			for(Segment child : this.children)
				child.setPos(child.posX + (PopupWindow.this.posX - origX), child.posY + (PopupWindow.this.posY - origY));

			//this.persistent.forEach(segment -> segment.setPos(segment.posX + (this.posX - origX), segment.posY + (this.posY - origY)));
		}


	}
	
	@Override
	public boolean isSelected(double mouseX, double mouseY) {
		return (mouseX >= this.getPosX() && mouseY >= this.getPosY() + 10 && mouseX < this.getPosX() + this.getWidth() && mouseY < this.getPosY() + 24) || (mouseX >= this.getPosX() + 10 && mouseY >= this.getPosY() && mouseX < this.getPosX() + this.getWidth() - 10 && mouseY < this.getPosY() + 10) || (distanceBetweenPoints((float) this.getPosX() + 10F, (float) this.getPosY() + 10F, (float) mouseX, (float) mouseY) <= 10) || (distanceBetweenPoints((float) this.getPosX() + this.getWidth() - 10F, (float) this.getPosY() + 10F, (float) mouseX, (float) mouseY) <= 10);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
			synchronized (this.children) {
				for (Segment segment : children) {
					if (segment.mouseClicked(mouseX, mouseY, mouseButton)) {
						return true;
					}
				}
				
			}
			/*
			synchronized (this.persistent) {
				for (Segment segment : persistent) {
					if (segment.mouseClicked(mouseX, mouseY, mouseButton)) {
						return true;
					}
				}
				
			}
			*/
			if (this.isSelected(mouseX, mouseY)) {
				this.dragging = true;
				distanceX = (mouseX - this.posX);
				distanceY = (mouseY - this.posY);
				
				return true;
			} else {
				return false;
			}

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
			/*
			synchronized (this.persistent) {
				for (Segment segment : this.persistent) {
					if (segment.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_)) {
						break;
					}

				}
			}*/
		return false;
	}
	
	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
			synchronized (this.children) {
				for (Segment segment : this.children) {
					if (segment.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
						return true;
					}

				}
			}
			/*
			synchronized (this.persistent) {
				for (Segment segment : this.persistent) {
					if (segment.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
						return true;
					}

				}
			}
			*/
			this.dragging = false;
			
		return false;
	}

	public PopupWindow addChild(Segment segment) {
		synchronized (this.children) {
			this.children.add(segment.setPos(this.posX + segment.posX, this.posY + segment.posY));
		}
		return this;
	}
	
	@Deprecated
	public PopupWindow addPersistent(Segment segment) {
		synchronized (this.persistent) {
			this.persistent.add(segment.setPos(this.posX + segment.posX, this.posY + segment.posY));
		}
		return this;
	}

	public void clearChildren() {
		synchronized (this.children) {
			this.children.clear();
		}
	}

}
