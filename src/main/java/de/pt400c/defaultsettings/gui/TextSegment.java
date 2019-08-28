package de.pt400c.defaultsettings.gui;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import static de.pt400c.neptunefx.DrawString.*;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextSegment extends Segment {

	public final int color;
	public final String text;
	private final int offset;
	
	public TextSegment(Screen gui, float posX, float posY, int width, int height, String text, int color, boolean popup) {
		this(gui, posX, posY, width, height, text, color, 9, popup);
	}
	
	public TextSegment(Screen gui, float posX, float posY, int width, int height, String text, int color, int offset, boolean popup) {
		super(gui, posX, posY, width, height, popup);
		
		this.color = color;
		this.text = text;
		this.offset = offset;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		
		glPushMatrix();
     	glEnable(GL_BLEND);
     	glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
     	int offsetY = 0;
     	for(String line : this.text.split("\n")) {
     		drawString(line, (float) this.getPosX(), (float) this.getPosY() + offsetY, this.color);
     		offsetY += this.offset;
     	}
		glDisable(GL_BLEND);
		glPopMatrix();
	}
}