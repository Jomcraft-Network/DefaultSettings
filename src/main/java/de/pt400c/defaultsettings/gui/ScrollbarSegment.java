package de.pt400c.defaultsettings.gui;

import org.lwjgl.opengl.GL11;

import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ScrollbarSegment extends ButtonSegment {
	
	protected boolean grabbed;
	
	private final ScrollableSegment superScrollable;
	
	private double distanceY = 0;

	public ScrollbarSegment(GuiScreen gui, float posX, float posY, int width, int height, ScrollableSegment segment) {
		super(gui, posX, posY, null, null, width, height, 0);
		this.superScrollable = segment;
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {

		if (this.grabbed) {
			int factor = (int) (mouseY - distanceY);

			this.posY = factor;

			if (this.posY < this.superScrollable.posY)
				this.posY = this.superScrollable.posY;

			if (this.posY > this.superScrollable.posY + this.superScrollable.height - height)
				this.posY = this.superScrollable.posY + this.superScrollable.height - height;

			double distance = Math.round(this.superScrollable.height - height);
			double pos = Math.round(this.posY - this.superScrollable.posY);

			float tempAdd = (int) (this.superScrollable.getPosY() + this.superScrollable.height - 1 - 20 * (this.superScrollable.list.size() - 1) - this.superScrollable.getPosY() - 18);

			this.superScrollable.add = (int) (tempAdd * (pos / distance));

		} else {
			distanceY = 0;
		}
		
		
		 GlStateManager.disableTexture2D();
	        GlStateManager.enableBlend();
	        GlStateManager.disableAlpha();
	        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	        GL11.glShadeModel(GL11.GL_SMOOTH);
		
	        Segment.drawGradientCircle((float) this.getPosX() + this.getWidth() - 2, (float) this.getPosY() + 7, 6, 180, 50, 0xff3a3a3a, 0x003a3a3a);
	        
		Segment.drawGradient(this.getPosX() + this.getWidth() - 2, this.getPosY() + 7, this.getPosX() - 2 + 6 + this.getWidth(), this.getPosY() + this.getHeight() - 2, 0xff3a3a3a, 0x003a3a3a);

		Segment.drawGradientCircle((float) this.getPosX() + this.getWidth() - 2, (float) this.getPosY() + this.getHeight() - 2, 6, 0, 50, 0xff3a3a3a, 0x003a3a3a);
		

       GL11.glShadeModel(GL11.GL_FLAT);
       GlStateManager.disableBlend();
       GlStateManager.enableAlpha();
       GlStateManager.enableTexture2D();

		
		Segment.drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), 0xffe0e0e0, true, null, false);

		Segment.drawRect(this.getPosX() + this.width / 2 - 2D, this.getPosY() + this.height / 2 - 3, this.getPosX() + this.width / 2 + 2D, this.getPosY() + this.height / 2 - 3 + 1, 0xff373737, true, null, false);
		
		Segment.drawRect(this.getPosX() + this.width / 2 - 2D, this.getPosY() + this.height / 2, this.getPosX() + this.width / 2 + 2D, this.getPosY() + this.height / 2 + 1, 0xff373737, true, null, false);

		Segment.drawRect(this.getPosX() + this.width / 2 - 2D, this.getPosY() + this.height / 2 + 3, this.getPosX() + this.width / 2 + 2D, this.getPosY() + this.height / 2 + 1 + 3, 0xff373737, true, null, false);
		
	
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			this.grabbed = true;
			MenuScreen menu = ((GuiConfig) this.gui).menu;
			menu.getVariants().get(menu.index).selected = null;
			this.distanceY += (int) (mouseY - this.posY);
			
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button) {
		return super.mouseDragged(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.grabbed) {
				this.grabbed = false;
				
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

}
