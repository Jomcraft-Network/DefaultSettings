package de.pt400c.defaultsettings.gui;

import net.minecraft.client.audio.SimpleSound;
import static de.pt400c.defaultsettings.FileUtil.MC;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Segment {
	
	protected final GuiScreen gui;
	protected float posX;
	protected float posY;
	protected float width;
	protected float height;
	protected float hitX;
	protected float hitY;
	protected final boolean isPopupSegment;
	public static double scaledFactor;
	public static int resized = 0;
	protected int resized_mark = 0;
	
	public Segment(GuiScreen gui, float posX, float posY, float width, float height, boolean popupSegment) {
		this.gui = gui;
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		this.isPopupSegment = popupSegment;
	}
	
	public void init() {};
	
	public abstract void render(int mouseX, int mouseY, float partialTicks);
	
	public void customRender(int mouseX, int mouseY, float customPosX, float customPosY, float partialTicks) {};
	
	public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return this.isSelected(mouseX, mouseY);
	}
	
	public boolean mouseScrolled(float p_mouseScrolled_1_) {
		int mouseX = (int) (MC.mouseHelper.getMouseX() * MC.mainWindow.getScaledWidth() / (double) MC.mainWindow.getWidth());
        int mouseY = (int) (MC.mouseHelper.getMouseY() * MC.mainWindow.getScaledHeight() / (double) MC.mainWindow.getHeight());
        return this.isSelected(mouseX, mouseY);
    }
    
	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
    	return false;
    }
	
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
    	return false;
    }
    
    public void guiContentUpdate(String... arg) {};
	
	public boolean hoverCheck(int mouseX, int mouseY) { return false; }

    public boolean mouseReleased(int mouseX, int mouseY, int button) {
        return this.isSelected(mouseX, mouseY);
    }
    
    public boolean mouseDragged(int mouseX, int mouseY, int button) {
        return this.isSelected(mouseX, mouseY);
    }
	
	public boolean isSelected(int mouseX, int mouseY) {
		return (((DefaultSettingsGUI) this.gui).popupField == null || this.getIsPopupSegment()) && mouseX >= this.getPosX() + this.hitX && mouseY >= this.getPosY() + this.hitY && mouseX < this.getPosX() + this.hitX + this.getWidth() && mouseY < this.getPosY() + this.hitY + this.getHeight();
	}
	
	public float getPosX() {
        return this.posX;
	}
	
	public float getPosY() {
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
	
	public void initSegment() {

	}
	
	public Segment setPos(float x, float y) {
		this.posX = x;
		this.posY = y;
		return this;
	}
	

	public Segment setPosHit(float x, float y, float x2, float y2) {
		this.posX = x;
		this.posY = y;
		this.hitX = x2;
		this.hitY = y2;
		return this;
	}
	
	public void clickSound() {
        MC.getSoundHandler().play(SimpleSound.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}