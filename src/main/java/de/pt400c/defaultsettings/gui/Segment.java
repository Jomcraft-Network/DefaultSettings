package de.pt400c.defaultsettings.gui;

import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import static de.pt400c.defaultsettings.FileUtil.MC;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Segment {
	
	protected final Screen gui;
	protected double posX;
	protected double posY;
	protected float width;
	protected float height;
	protected final boolean isPopupSegment;
	
	public Segment(Screen gui, float posX, float posY, float width, float height, boolean popupSegment) {
		this.gui = gui;
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		this.isPopupSegment = popupSegment;
	}
	
	public void init() {};
	
	public abstract void render(float mouseX, float mouseY, float partialTicks);
	
	public void customRender(float mouseX, float mouseY, float customPosX, float customPosY, float partialTicks) {};
	
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.isSelected(mouseX, mouseY);
	}
	
	public boolean mouseScrolled(double p_mouseScrolled_1_) {
		double mouseX = MC.mouseHelper.getMouseX() * (double) MC.mainWindow.getScaledWidth() / (double) MC.mainWindow.getWidth();
        double mouseY = MC.mouseHelper.getMouseY() * (double) MC.mainWindow.getScaledHeight() / (double) MC.mainWindow.getHeight();
        return this.isSelected(mouseX, mouseY);
    }
    
	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
    	return false;
    }
	
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
    	return false;
    }
    
    public void guiContentUpdate(String... arg) {};
	
	public void hoverCheck(float mouseX, float mouseY) {}

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.isSelected(mouseX, mouseY);
    }
    
    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        return this.isSelected(mouseX, mouseY);
    }
	
	public boolean isSelected(double mouseX, double mouseY) {
		return (((DefaultSettingsGUI) this.gui).popupField == null || this.getIsPopupSegment()) && mouseX >= this.getPosX() && mouseY >= this.getPosY() && mouseX < this.getPosX() + this.getWidth() && mouseY < this.getPosY() + this.getHeight();
	}
	
	public double getPosX() {
        return this.posX;
	}
	
	public double getPosY() {
        return this.posY;
	}
	
	public boolean getIsPopupSegment() {
        return this.isPopupSegment;
	}
	
	public float getWidth() {
        return this.width;
	}
	
	public float getHeight() {
        return this.height;
	}
	
	public Segment setPos(double x, double y) {
		this.posX = x;
		this.posY = y;
		return this;
	}
	
	public void clickSound() {
		MC.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}