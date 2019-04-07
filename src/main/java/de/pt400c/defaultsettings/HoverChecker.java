package de.pt400c.defaultsettings;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.gui.GuiButton;

public class HoverChecker {
	private int top, bottom, left, right, threshold;
	private GuiButton button;
	private long hoverStart;

	public HoverChecker(int top, int bottom, int left, int right, int threshold) {
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
		this.threshold = threshold;
		this.hoverStart = -1;
	}

	public HoverChecker(GuiButton button, int threshold) {
		this.button = button;
		this.threshold = threshold;
	}

	public void updateBounds(int top, int bottom, int left, int right) {
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}

	public boolean checkHover(int mouseX, int mouseY) {
		if (this.button != null) {
			int b_width = ObfuscationReflectionHelper.getPrivateValue(GuiButton.class, this.button, "width");
			int b_height = ObfuscationReflectionHelper.getPrivateValue(GuiButton.class, this.button, "height");
			this.top = button.yPosition;
			this.bottom = button.yPosition + b_height;
			this.left = button.xPosition;
			this.right = button.xPosition + b_width;
		}

		if (hoverStart == -1 && mouseY >= top && mouseY <= bottom && mouseX >= left && mouseX <= right)
			hoverStart = System.currentTimeMillis();
		else if (mouseY < top || mouseY > bottom || mouseX < left || mouseX > right)
			resetHoverTimer();

		return hoverStart != -1 && System.currentTimeMillis() - hoverStart >= threshold;
	}

	public void resetHoverTimer() {
		hoverStart = -1;
	}
}