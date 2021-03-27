package de.pt400c.defaultsettings.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import static de.pt400c.neptunefx.NEX.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import java.util.function.Function;
import de.pt400c.defaultsettings.GuiConfig;
import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class SplitterSegment extends BakedSegment {
	
	private final LeftMenu menu;
	private final Function<GuiConfig, Integer> heightF;
	
	public SplitterSegment(GuiScreen gui, float posX, float posY, Function<GuiConfig, Integer> height, LeftMenu menu) {
		super(gui, 0, posX, posY, 3F, height.apply((GuiConfig) gui), 255, 255, 255, true, false);
		this.heightF = height;
		this.menu = menu;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		
		if(resized != this.resized_mark) 
			height = heightF.apply((GuiConfig) this.gui);
		
		setup();

		if(!compiled) {
			preRender();
	
			glPushMatrix();

			GlStateManager.disableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
			GlStateManager.disableAlpha();
			glShadeModel(GL_SMOOTH);
			drawGradientCircle(0, 2.2F, 2F, 270, 75, 0xffaaaaaa, 0x00e6e6e6);
			drawGradientCircle(0, this.getHeight() - 2.2F, 2F, 0, 75, 0xffaaaaaa, 0x00e6e6e6);
			drawGradient(0, 2.2F, 2F, this.getHeight() - 2.2F, 0xffaaaaaa, 0x00e6e6e6, 0);
			glShadeModel(GL_FLAT);

			glEnable(GL_POINT_SMOOTH);

			glPointSize(1.25F * (scaledresolution.getScaleFactor() / 2F));

			glBegin(GL_POINTS);

			glVertex3f(0.5F, 1, 0.0f);

			glEnd();
			glDisable(GL_POINT_SMOOTH);

			glEnable(GL_POINT_SMOOTH);

			glPointSize(1.25F * (scaledresolution.getScaleFactor() / 2F));

			glBegin(GL_POINTS);

			glVertex3f(0.5F, this.getHeight() - 1, 0.0f);

			glEnd();
			glDisable(GL_POINT_SMOOTH);

			GlStateManager.disableBlend();
			GlStateManager.enableTexture2D();

			drawRect(0, 1, 1, this.getHeight() - 1, 0xffe6e6e6, true, null, false);

			glPopMatrix();

			postRender(1, false);
			
		}
		
		glPushMatrix();
		glTranslatef(-this.menu.offs, 0, 0);
		drawTexture(1);
		glPopMatrix();
	}
}