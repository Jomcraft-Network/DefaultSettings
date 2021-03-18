package de.pt400c.defaultsettings.gui;

import static de.pt400c.defaultsettings.FileUtil.MC;
import org.lwjgl.input.Mouse;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public abstract class Segment {
	
	protected final GuiScreen gui;
	protected float posX;
	protected float posY;
	protected float width;
	protected float height;
	protected float hitX;
	protected float hitY;
	protected final boolean isPopupSegment;
	public static ScaledResolution scaledresolution;
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
	
	public abstract void render(int mouseX, int mouseY, float partialTicks);
	
	public void customRender(int mouseX, int mouseY, float customPosX, float customPosY, float partialTicks) {};
	
	public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return this.isSelected(mouseX, mouseY);
	}
	
	public boolean handleMouseInput() {
    	int mouseX = (int) (Mouse.getEventX() * this.width / MC.displayWidth);
        int mouseY = (int) (this.height - Mouse.getEventY() * this.height / MC.displayHeight - 1);
        return this.isSelected(mouseX, mouseY);
    }
    
    protected boolean keyTyped(char typedChar, int keyCode) {
    	return false;
    }
    
    public void init() {};
    
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
	
	public float getWidth() {
        return this.width;
	}
	
	public float getHeight() {
        return this.height;
	}
	
	public boolean getIsPopupSegment() {
        return this.isPopupSegment;
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
		MC.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
    }
}