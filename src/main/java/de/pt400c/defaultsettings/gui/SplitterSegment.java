package de.pt400c.defaultsettings.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import static de.pt400c.neptunefx.NEX.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class SplitterSegment extends Segment {
	
	private final LeftMenu menu;
	private int bgDPLList = -1;
    private boolean compiled;
	
	public SplitterSegment(GuiScreen gui, float posX, float posY, int height, LeftMenu menu) {
		super(gui, posX, posY, 1, height, false);
		this.menu = menu;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		
		glPushMatrix();
		glTranslatef(-this.menu.offs, 0, 0);
		
		if (compiled)
			glCallList(this.bgDPLList);
		else {
			this.bgDPLList = GLAllocation.generateDisplayLists(1);
			glNewList(this.bgDPLList, GL_COMPILE);
			glDisable(GL_TEXTURE_2D);
			glEnable(GL_BLEND);
			glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
			glShadeModel(GL_SMOOTH);
			drawGradientCircle((float) this.getPosX(), (float) this.getPosY() + 4, 6, 270, 75, 0xffaaaaaa, 0x00ffffff);
			drawGradientCircle((float) this.getPosX(), (float) this.getPosY() + this.getHeight() - 4, 6, 0, 75, 0xffaaaaaa, 0x00ffffff);
			drawGradient(this.getPosX(), this.getPosY() + 4, this.getPosX() + 6, this.getPosY() + this.getHeight() - 4, 0xffaaaaaa, 0x00ffffff, 0);
			glShadeModel(GL_FLAT);
			glDisable(GL_BLEND);
			glEnable(GL_TEXTURE_2D);
			drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), 0xffbebebe, true, null, false);	
			glEndList();
			compiled = true;
			glCallList(this.bgDPLList);
		}
		glPopMatrix();
	}
}