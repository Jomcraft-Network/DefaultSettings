package de.pt400c.defaultsettings.gui;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.GuiScreen;

@SideOnly(Side.CLIENT)
public class MenuArea extends Segment {
	
	private List<Segment> children = new ArrayList<>();
	
	public Segment selected = null;

	public MenuArea(GuiScreen gui, float posX, float posY) {
		super(gui, posX, posY, gui.width - posX, gui.height - posY, false);
	}
	
	@Override
    public void render(int mouseX, int mouseY, float partialTicks) {

        synchronized (this.children) {
            this.children.forEach(segment -> segment.render(mouseX, mouseY, partialTicks));

            if(((GuiConfig) this.gui).popupField == null)
            	this.children.forEach(segment -> segment.hoverCheck(mouseX, mouseY));
        }
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		synchronized (this.children) {
			for (Segment segment : children)
				if (segment.mouseClicked(mouseX, mouseY, mouseButton)) 
					return true;

		}
		this.selected = null;
		return false;
	}
	
	@Override
	public boolean mouseDragged(int p_mouseDragged_1_, int p_mouseDragged_3_, int p_mouseDragged_5_) {
		synchronized (this.children) {
			for (Segment segment : this.children) 
				if (segment.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_)) 
					break;

		}
		return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_);
	}
	
	@Override
	protected boolean keyTyped(char typedChar, int keyCode) {
		return this.selected != null ? this.selected.keyTyped(typedChar, keyCode) : false;
	}
	
	@Override
	public boolean handleMouseInput() {
		synchronized (this.children) {
			for (Segment segment : this.children) 
				if (segment.handleMouseInput()) 
					break;

		}
		return super.handleMouseInput();
	}
	
	@Override
	public boolean mouseReleased(int p_mouseReleased_1_, int p_mouseReleased_3_, int p_mouseReleased_5_) {
		synchronized (this.children) {
			for (Segment segment : this.children) 
				if (segment.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) 
					break;

		}
		return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}

	public MenuArea addChild(Segment segment) {
		synchronized (this.children) {
			this.children.add(segment.setPos(this.posX + segment.posX, this.posY + segment.posY));
			segment.init();
		}
		return this;
	}
	
	public List<Segment> getChildren() {
		return this.children;
	}
}