package de.pt400c.defaultsettings.gui;

import static de.pt400c.defaultsettings.FileUtil.MC;
import org.lwjgl.input.Mouse;
import de.pt400c.defaultsettings.DefaultSettings;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class Segment {
	
	protected final GuiScreen gui;
	protected float posX;
	protected float posY;
	protected float width;
	protected float height;
	protected final boolean isPopupSegment;
	
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
	
	public boolean hoverCheck(int mouseX, int mouseY) { return false; }

    public boolean mouseReleased(int mouseX, int mouseY, int button) {
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
    
    public boolean mouseDragged(int mouseX, int mouseY, int button) {
        return this.isSelected(mouseX, mouseY);
    }
	
	public boolean isSelected(int mouseX, int mouseY) {
        return (((DefaultSettingsGUI) this.gui).popupField == null || this.getIsPopupSegment()) && mouseX >= this.getPosX() && mouseY >= this.getPosY() && mouseX < this.getPosX() + this.getWidth() && mouseY < this.getPosY() + this.getHeight();
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
	
	public void init() {};
	
	public void guiContentUpdate(String... arg) {};
	
	public Segment setPos(float x, float y) {
		this.posX = x;
		this.posY = y;
		return this;
	}
	
	public void clickSound() {
		if(DefaultSettings.mcVersion.startsWith("1.8"))
			MC.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
		else
			MC.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}