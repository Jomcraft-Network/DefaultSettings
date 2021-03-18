package de.pt400c.defaultsettings.gui;

import java.awt.Color;
import java.util.function.Function;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import static de.pt400c.neptunefx.NEX.*;

@SideOnly(Side.CLIENT)
public class ButtonSegment extends Segment {
	
	protected final Function<ButtonSegment, Boolean> function;
	private static final float BRIGHT_SCALE = 0.85f;
	public String title;
	public String hoverMessage = null;
	protected boolean grabbed;
	protected final int border;
	public int color = 0xffa4a4a4;
    private Function<Segment, Float> posXF;
	private Function<Segment, Float> posYF;
	private Segment parent;

	public ButtonSegment(GuiScreen gui, float posX, float posY, String title, Function<ButtonSegment, Boolean> function, int width, int height, int border, String hoverMessage, LeftMenu menu, boolean popupSegment) {
		super(gui, posX, posY, width, height, popupSegment);
		this.title = title;
		this.function = function;
		this.border = border;
		this.hoverMessage = hoverMessage;
	}
	
	public ButtonSegment(GuiScreen gui, float posX, float posY, String title, Function<ButtonSegment, Boolean> function, int width, int height, int border, String hoverMessage, boolean popupSegment) {
		this(gui, posX, posY, title, function, width, height, border, hoverMessage, null, popupSegment);
	}
	
	public ButtonSegment(GuiScreen gui, float posX, float posY, String title, Function<ButtonSegment, Boolean> function, int width, int height, int border, String hoverMessage) {
		this(gui, posX, posY, title, function, width, height, border, hoverMessage, null, false);
	}
	
	public ButtonSegment(GuiScreen gui, Segment parent, Function<Segment, Float> posX, Function<Segment, Float> posY, String title, Function<ButtonSegment, Boolean> function, int width, int height, int border, String hoverMessage) {
		this(gui, posX.apply(parent), posY.apply(parent), title, function, width, height, border, hoverMessage, null, false);
		this.parent = parent;
		this.posXF = posX;
		this.posYF = posY;
	}
	
	public ButtonSegment(GuiScreen gui, float posX, float posY, String title, Function<ButtonSegment, Boolean> function, int width, int height, int border) {
		this(gui, posX, posY, title, function, width, height, border, null);
	}
	
	public ButtonSegment(GuiScreen gui, float posX, float posY, String title, Function<ButtonSegment, Boolean> function, int width, int height, int border, boolean popupSegment) {
		this(gui, posX, posY, title, function, width, height, border, null, popupSegment);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {

		if(resized != this.resized_mark && posXF != null) {
			posX = posXF.apply(this.parent);
			posY = posYF.apply(this.parent);
			this.resized_mark = resized;
		}
	}
	
	protected static Color darkenColor(int color) {
		return new Color((int) (((color & RED_MASK) >> 16) * BRIGHT_SCALE), (int) (((color & GREEN_MASK) >> 8) * BRIGHT_SCALE), (int) ((color & BLUE_MASK) * BRIGHT_SCALE), 255);
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {
	
		if (this.isSelected(mouseX, mouseY)) {
			this.grabbed = true;
			((DefaultSettingsGUI) this.gui).resetSelected();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseDragged(int mouseX, int mouseY, int button) {
		if (!this.isSelected(mouseX, mouseY))
			this.grabbed = false;
		return super.mouseDragged(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int button) {
		if (this.grabbed) {
			if (this.isSelected(mouseX, mouseY))
				this.grabbed = false;

			if (this.function.apply(this)) 
				this.clickSound();

		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public Segment setPos(float x, float y) {
		return super.setPos(x, y);
	}
}