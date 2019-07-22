package de.pt400c.defaultsettings.gui;

import static de.pt400c.defaultsettings.FileUtil.MC;
import java.io.File;
import java.io.FileFilter;
import java.util.List;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import de.pt400c.defaultsettings.FileUtil;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ButtonCheckboxSegment extends Segment {
	
	protected final String name;
	public boolean active;
	protected boolean grabbed;
	public float timer = 0;
	private float offX;
	private float offY;
	private final ScrollableSegment parent;

	public ButtonCheckboxSegment(Screen gui, float posX, float posY, int width, int height, String name, boolean popupSegment, ScrollableSegment parent, boolean active) {
		super(gui, posX, posY, width, height, popupSegment);
		this.name = name;
		this.active = active;
		this.parent = parent;
		timer = active ? (float) (Math.PI / 3) : 0;
	}

	@Override
	public void customRender(float mouseX, float mouseY, float customPosX, float customPosY, float partialTicks) {

		if (active) {

			if (this.timer <= (Math.PI / 3))
				this.timer += 0.05;

		} else {

			if (this.timer > 0)
				this.timer -= 0.05;

		}
		float tempTimer = this.timer;
		if (this.timer > (Math.PI / 3))
			tempTimer = (float) (Math.PI / 3);
		else if (this.timer < 0)
			tempTimer = 0;

		float alphaRate = (float) ((Math.sin(3 * tempTimer - 3 * (Math.PI / 2)) + 1) / 2);

		int color = 0xff000000;
		this.offX = customPosX;
		this.offY = customPosY;
		customPosX += this.getPosX();
		customPosY += this.getPosY();
		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GlStateManager.color4f(f, f1, f2, f3);

		Segment.drawCircle((float) customPosX - 2, (float) customPosY - 2, 3, 180, 75);

		Segment.drawCircle((float) customPosX - 2, (float) customPosY + this.height + 2, 3, 90, 75);

		Segment.drawCircle((float) customPosX + this.width + 2, (float) customPosY + this.height + 2, 3, 0, 75);

		Segment.drawCircle((float) customPosX + this.width + 2, (float) customPosY - 2, 3, 270, 75);

		Segment.drawRect(customPosX - 5, customPosY - 2, customPosX + this.width + 5, customPosY + this.height + 2, null, false, null, false);

		Segment.drawRect(customPosX - 2, customPosY - 5, customPosX + width + 2, customPosY + this.height + 5, null, false, null, false);

		if (this.timer <= (Math.PI / 3)) {
			color = 0xffffffff;

			f3 = (float) (color >> 24 & 255) / 255.0F;
			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;

			GlStateManager.color4f(f, f1, f2, f3);

			Segment.drawCircle((float) customPosX - 1, (float) customPosY - 1, 3, 180, 75);

			Segment.drawCircle((float) customPosX - 1, (float) customPosY + this.height + 1, 3, 90, 75);

			Segment.drawCircle((float) customPosX + this.width + 1, (float) customPosY + this.height + 1, 3, 0, 75);

			Segment.drawCircle((float) customPosX + this.width + 1, (float) customPosY - 1, 3, 270, 75);

			Segment.drawRect(customPosX - 1, customPosY - 4, customPosX + width + 1, customPosY + this.height + 4, null, false, null, false);

			Segment.drawRect(customPosX - 4, customPosY - 1, customPosX + width + 4, customPosY + this.height + 1, null, false, null, false);

		}

		color = 0xfffe8518;
		f3 = (float) (color >> 24 & 255) / 255.0F;
		f = (float) (color >> 16 & 255) / 255.0F;
		f1 = (float) (color >> 8 & 255) / 255.0F;
		f2 = (float) (color & 255) / 255.0F;

		GlStateManager.color4f(f, f1, f2, f3 - alphaRate);

		Segment.drawCircle((float) customPosX - 1, (float) customPosY - 1, 3, 180, 75);

		Segment.drawCircle((float) customPosX - 1, (float) customPosY + this.height + 1, 3, 90, 75);

		Segment.drawCircle((float) customPosX + this.width + 1, (float) customPosY + this.height + 1, 3, 0, 75);

		Segment.drawCircle((float) customPosX + this.width + 1, (float) customPosY - 1, 3, 270, 75);

		Segment.drawRect(customPosX - 1, customPosY - 4, customPosX + width + 1, customPosY - 1, null, false, null, false);

		Segment.drawRect(customPosX - 1, customPosY + this.height + 1, customPosX + width + 1, customPosY + this.height + 4, null, false, null, false);

		Segment.drawRect(customPosX - 4, customPosY - 1, customPosX + width + 4, customPosY + this.height + 1, null, false, null, false);

		if (this.timer > 0) {
			color = 0xffffffff;

			f3 = (float) (color >> 24 & 255) / 255.0F;
			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;

			int scaleFactor = (int) MC.mainWindow.getGuiScaleFactor();

			Segment.drawLine2D(f, f1, f2, f3, scaleFactor, new Vec2f((float) customPosX - 1, (float) customPosY + 3.5F), new Vec2f((float) customPosX + 4 - 1, (float) customPosY + 4 + 3.5F), new Vec2f((float) customPosX + 7 - 1, (float) customPosY - 5 + 3.5F));
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

	}
	
	@Override
	public boolean isSelected(double mouseX, double mouseY) {
		double tempX = this.getPosX() + this.offX; 
		double tempY = this.getPosY() + this.offY; 
		return (((GuiConfig) this.gui).popupField == null || this.getIsPopupSegment()) && mouseX >= tempX - 4 && mouseY >= tempY - 4 && mouseX < tempX + this.getWidth() + 4&& mouseY < tempY + this.getHeight() + 4;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			this.grabbed = true;
			MenuScreen menu = ((GuiConfig) this.gui).menu;
			menu.getVariants().get(menu.index).selected = null;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button) {
		if (!this.isSelected(mouseX, mouseY)) {
			this.grabbed = false;
			return false;
		}
		return true;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.grabbed) {
			if (this.isSelected(mouseX, mouseY))
				this.grabbed = false;

			this.active = Boolean.logicalXor(this.active, true);
			FileUtil.switchActive(this.name);

			File fileDir = new File(FileUtil.mcDataDir, "config");
			FileFilter ff = null;
			String arg = this.parent.searchbar.query;
			if (arg != null) {
				ff = new FileFilter() {

					@Override
					public boolean accept(File file) {

						if (!file.getName().equals("defaultsettings") && !file.getName().equals("defaultsettings.json")
								&& !file.getName().equals("keys.txt") && !file.getName().equals("options.txt")
								&& !file.getName().equals("optionsof.txt") && !file.getName().equals("servers.dat")
								&& file.getName().toLowerCase().startsWith(arg.toLowerCase()))
							return true;

						return false;
					}
				};
			} else {
				ff = FileUtil.fileFilter;
			}
			File[] files = fileDir.listFiles(ff);
			List<RowItem> rows = parent.list;
			int activeCount = 0;
			for (int i = 0; i < rows.size(); i++) {

				boolean active = FileUtil.getActives().contains(files[i].getName());
				if (active)
					activeCount++;

			}

			if (rows.size() != 0 && activeCount == rows.size())
				this.parent.cache_activity = 2;
			else if (activeCount > 0)
				this.parent.cache_activity = 1;
			else
				this.parent.cache_activity = 0;

			this.clickSound();
			return true;

		}
		return false;
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {

	}

}