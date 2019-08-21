package de.pt400c.defaultsettings.gui;

import static de.pt400c.defaultsettings.FileUtil.MC;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;

@SideOnly(Side.CLIENT)
public class PopupWindow extends Segment {
	
	private List<Segment> children = new ArrayList<>();
	public String title;
	private boolean dragging;
	private double distanceX = 0;
	private double distanceY = 0;

	public PopupWindow(GuiScreen gui, float posX, float posY, float width, float height, String title) {
		super(gui, posX, posY, width, height, true);
		this.title = title;
	}
	
	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {

		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
	    GL11.glDisable(GL11.GL_ALPHA_TEST);
	 	GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		Segment.drawGradient(this.getPosX() + this.width - 10, this.getPosY() + 10, this.getPosX() + this.width + 5, this.getPosY() + this.height - 10, 0xff000000, 0x00101010, 0);
		
		Segment.drawGradient(this.getPosX() - 5, this.getPosY() + 10, this.getPosX() + 10, this.getPosY() + this.height - 10, 0xff000000, 0x00101010, 2);
		
		Segment.drawGradient(this.getPosX() + 10, this.getPosY() - 5, this.getPosX() + this.width - 10, this.getPosY() + 10, 0xff000000, 0x00101010, 3);
		
		Segment.drawGradient(this.getPosX() + 10, this.getPosY() + this.height - 10, this.getPosX() + this.width - 10, this.getPosY() + this.height + 5, 0xff000000, 0x00101010, 1);
		
		Segment.drawGradientCircle((float) this.getPosX() + 10, (float) this.getPosY() + 10, 15, 180, 75, 0xff000000, 0x00101010);
		
		Segment.drawGradientCircle((float) this.getPosX() + this.width - 10, (float) this.getPosY() + 10, 15, 270, 75, 0xff000000, 0x00101010);
		
		Segment.drawGradientCircle((float) this.getPosX() + this.width - 10, (float) this.getPosY() + this.height - 10, 15, 0, 75, 0xff000000, 0x00101010);
		
		Segment.drawGradientCircle((float) this.getPosX() + 10, (float) this.getPosY() + this.height - 10, 15, 90, 75, 0xff000000, 0x00101010);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		
		Segment.drawRectRoundedUpper((float) this.posX, (float) this.posY, (float) (this.posX + width), (float) (this.posY + 24), 0xff8b8b8b, 0);
		Segment.drawRectRoundedLower((float) this.posX, (float) this.posY + 24, (float) (this.posX + width), (float) (this.posY + height), 0xfffbfbfb, 0);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    GL11.glDisable(GL11.GL_ALPHA_TEST);
	 	GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		Segment.drawGradient(this.getPosX(), this.getPosY() + 24, this.getPosX() + this.width, this.getPosY() + 24 + 5, 0xff606060, 0x00404040, 1);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		
		this.drawString(this.title, (float) (this.getPosX() + this.getWidth() / 2 + 1 - MC.fontRenderer.getStringWidth(this.title) / 2), (float) (this.getPosY() + 9), 0xff1b1b1b, false);
		
		synchronized (this.children) {
			this.children.forEach(segment -> segment.render(mouseX, mouseY, partialTicks));
			this.children.forEach(segment -> segment.hoverCheck(mouseX, mouseY));
		}
		
		if (this.dragging) {

			final double origX = this.posX;
			final double origY = this.posY;
			
			this.posX = mouseX - distanceX;
			this.posY = mouseY - distanceY;
			
			this.children.forEach(segment -> segment.setPos(segment.posX + (this.posX - origX), segment.posY + (this.posY - origY)));
		}
	}
	
	@Override
	public boolean isSelected(double mouseX, double mouseY) {
		return (mouseX >= this.getPosX() && mouseY >= this.getPosY() + 10 && mouseX < this.getPosX() + this.getWidth() && mouseY < this.getPosY() + 24) || (mouseX >= this.getPosX() + 10 && mouseY >= this.getPosY() && mouseX < this.getPosX() + this.getWidth() - 10 && mouseY < this.getPosY() + 10) || (distanceBetweenPoints((float) this.getPosX() + 10F, (float) this.getPosY() + 10F, (float) mouseX, (float) mouseY) <= 10) || (distanceBetweenPoints((float) this.getPosX() + this.getWidth() - 10F, (float) this.getPosY() + 10F, (float) mouseX, (float) mouseY) <= 10);
	}
	
	public boolean isSelectedLower(double mouseX, double mouseY) {
		return (mouseX >= this.getPosX() && mouseY >= this.getPosY() + 10 && mouseX < this.getPosX() + this.getWidth() && mouseY < this.getPosY() + height) || (mouseX >= this.getPosX() + 10 && mouseY >= this.getPosY() && mouseX < this.getPosX() + this.getWidth() - 10 && mouseY < this.getPosY() + 10) || (distanceBetweenPoints((float) this.getPosX() + 10F, (float) this.getPosY() + 10F, (float) mouseX, (float) mouseY) <= 10) || (distanceBetweenPoints((float) this.getPosX() + this.getWidth() - 10F, (float) this.getPosY() + 10F, (float) mouseX, (float) mouseY) <= 10);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if(!this.isSelectedLower(mouseX, mouseY))
			((GuiConfig) this.gui).popupField.setOpening(false);

			synchronized (this.children) {
				for (Segment segment : children) 
					if (segment.mouseClicked(mouseX, mouseY, mouseButton)) 
						return true;

			}

			if (this.isSelected(mouseX, mouseY)) {
				this.dragging = true;
				this.distanceX = (mouseX - this.posX);
				this.distanceY = (mouseY - this.posY);
				
				return true;
			} else {
				return false;
			}
	}
	
	@Override
	public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_) {
			synchronized (this.children) {
				for (Segment segment : this.children) 
					if (segment.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_)) 
						break;

			}
		return false;
	}
	
	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
			synchronized (this.children) {
				for (Segment segment : this.children) 
					if (segment.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) 
						return true;
				
			}
			this.dragging = false;
			
		return false;
	}

	public PopupWindow addChild(Segment segment) {
		synchronized (this.children) {
			this.children.add(segment.setPos(this.posX + segment.posX, this.posY + segment.posY));
		}
		return this;
	}

	public void clearChildren() {
		synchronized (this.children) {
			this.children.clear();
		}
	}
	
	public List<Segment> getChildren() {
		return this.children;
	}
}