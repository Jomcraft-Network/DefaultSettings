package de.pt400c.defaultsettings.gui;

import static de.pt400c.defaultsettings.FileUtil.MC;
import java.io.File;
import java.io.FileFilter;
import java.util.List;
import static org.lwjgl.opengl.GL11.*;
import de.pt400c.defaultsettings.FileUtil;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ButtonBulkActionSegment extends Segment {
	
	//STATE: 
	//0 : Nothing selected, select all
	//1 : Particially select, deselect all
	//2 : All selected, deselect all
	
	protected boolean grabbed;
	public float timer = 0;
	private final ScrollableSegment parent;

	public ButtonBulkActionSegment(Screen gui, float posX, float posY, int width, int height, ScrollableSegment parent) {
		super(gui, posX, posY, width, height, false);
		this.parent = parent;
		if(parent.cache_activity == 1 || parent.cache_activity == 2)
			this.timer = (float) (Math.PI / 3);
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {

		if (parent.cache_activity == 1 || parent.cache_activity == 2) {

			if (this.timer <= (Math.PI / 3))
				this.timer += 0.05;

		} else {

			if (this.timer > 0)
				this.timer -= 0.05;

		}

		final float alphaRate = (float) ((Math.sin(3 * timer - 3 * (Math.PI / 2)) + 1) / 2);

		int color = 0xff000000;
		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;

		glEnable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glColor4f(f, f1, f2, f3);

		Segment.drawCircle((float) this.getPosX() - 2, (float) this.getPosY() - 2, 3, 180, 75);

		Segment.drawCircle((float) this.getPosX() - 2, (float) this.getPosY() + this.height + 2, 3, 90, 75);

		Segment.drawCircle((float) this.getPosX() + this.width + 2, (float) this.getPosY() + this.height + 2, 3, 0, 75);

		Segment.drawCircle((float) this.getPosX() + this.width + 2, (float) this.getPosY() - 2, 3, 270, 75);

		Segment.drawRect(this.getPosX() - 5, this.getPosY() - 2, this.getPosX() + this.width + 5, this.getPosY() + this.height + 2, null, false, null, false);

		Segment.drawRect(this.getPosX() - 2, this.getPosY() - 5, this.getPosX() + width + 2, this.getPosY() + this.height + 5, null, false, null, false);

		if (this.timer <= (Math.PI / 3)) {
			color = 0xffffffff;

			f3 = (float) (color >> 24 & 255) / 255.0F;
			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;

			glColor4f(f, f1, f2, f3);

			Segment.drawCircle((float) this.getPosX() - 1, (float) this.getPosY() - 1, 3, 180, 75);

			Segment.drawCircle((float) this.getPosX() - 1, (float) this.getPosY() + this.height + 1, 3, 90, 75);

			Segment.drawCircle((float) this.getPosX() + this.width + 1, (float) this.getPosY() + this.height + 1, 3, 0, 75);

			Segment.drawCircle((float) this.getPosX() + this.width + 1, (float) this.getPosY() - 1, 3, 270, 75);

			Segment.drawRect(this.getPosX() - 1, this.getPosY() - 4, this.getPosX() + width + 1, this.getPosY() + this.height + 4, null, false, null, false);

			Segment.drawRect(this.getPosX() - 4, this.getPosY() - 1, this.getPosX() + width + 4, this.getPosY() + this.height + 1, null, false, null, false);

		}

		color = 0xfffe8518;
		f3 = (float) (color >> 24 & 255) / 255.0F;
		f = (float) (color >> 16 & 255) / 255.0F;
		f1 = (float) (color >> 8 & 255) / 255.0F;
		f2 = (float) (color & 255) / 255.0F;

		glColor4f(f, f1, f2, f3 - alphaRate);

		Segment.drawCircle((float) this.getPosX() - 1, (float) this.getPosY() - 1, 3, 180, 75);

		Segment.drawCircle((float) this.getPosX() - 1, (float) this.getPosY() + this.height + 1, 3, 90, 75);

		Segment.drawCircle((float) this.getPosX() + this.width + 1, (float) this.getPosY() + this.height + 1, 3, 0, 75);

		Segment.drawCircle((float) this.getPosX() + this.width + 1, (float) this.getPosY() - 1, 3, 270, 75);

		Segment.drawRect(this.getPosX() - 1, this.getPosY() - 4, this.getPosX() + width + 1, this.getPosY() - 1, null, false, null, false);

		Segment.drawRect(this.getPosX() - 1, this.getPosY() + this.height + 1, this.getPosX() + width + 1, this.getPosY() + this.height + 4, null, false, null, false);

		Segment.drawRect(this.getPosX() - 4, this.getPosY() - 1, this.getPosX() + width + 4, this.getPosY() + this.height + 1, null, false, null, false);

		if (this.parent.cache_activity == 1) {
			color = 0xffffffff;

			f3 = (float) (color >> 24 & 255) / 255.0F;
			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;

			int scaleFactor = (int) MC.mainWindow.getGuiScaleFactor();

			Segment.drawLine2D(f, f1, f2, f3, scaleFactor, new Vec2f((float) this.getPosX(), (float) this.getPosY() + 3.5F), new Vec2f((float) this.getPosX() + 5.5F, (float) this.getPosY() + 3.5F));

		}

		if (this.parent.cache_activity == 2) {
			color = 0xffffffff;

			f3 = (float) (color >> 24 & 255) / 255.0F;
			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;

			int scaleFactor = (int) MC.mainWindow.getGuiScaleFactor();

			Segment.drawLine2D(f, f1, f2, f3, scaleFactor, new Vec2f((float) this.getPosX() - 1, (float) this.getPosY() + 3.5F), new Vec2f((float) this.getPosX() + 4 - 1, (float) this.getPosY() + 4 + 3.5F), new Vec2f((float) this.getPosX() + 7 - 1, (float) this.getPosY() - 5 + 3.5F));

		}

		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);

	}
	
	@Override
	public boolean isSelected(double mouseX, double mouseY) {
		return (((GuiConfig) this.gui).popupField == null || this.getIsPopupSegment()) && mouseX >= this.getPosX() - 4 && mouseY >= this.getPosY() - 4 && mouseX < this.getPosX() + this.getWidth() + 4&& mouseY < this.getPosY() + this.getHeight() + 4;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			this.grabbed = true;
			((DefaultSettingsGUI) this.gui).resetSelected();
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

			FileUtil.switchState(this.parent.cache_activity, this.parent.searchbar.query);

			if (this.parent.cache_activity == 1) {
				File fileDir = new File(FileUtil.mcDataDir, "config");
				FileFilter ff = null;
				String arg = this.parent.searchbar.query;
				if (arg != null) {
					ff = new FileFilter() {

						@Override
						public boolean accept(File file) {

							if (!file.getName().equals("defaultsettings")
									&& !file.getName().equals("defaultsettings.json")
									&& !file.getName().equals("keys.txt") && !file.getName().equals("options.txt") && !file.getName().equals("ds_dont_export.json")
									/*&& !file.getName().equals("optionsof.txt")*/ && !file.getName().equals("servers.dat")
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
				float timer = 0;

				for (int i = 0; i < this.parent.list.size(); i++) {
					for (Segment child : this.parent.list.get(i).childs) {
						if (child instanceof ButtonCheckboxSegment) {
							ButtonCheckboxSegment checkbox = (ButtonCheckboxSegment) child;
							if (checkbox.active) {
								checkbox.active = false;

								int yOffTemp = 18 + 20 * i + this.parent.add;
								boolean invalid = false;
								if (yOffTemp < -3)
									invalid = true;
								if (yOffTemp > this.parent.height + 18)
									invalid = true;

								if (invalid)
									checkbox.timer = 0;
								else {
									if (checkbox.timer >= (float) (Math.PI / 3))
										checkbox.timer = (float) (Math.PI / 3) + timer;
									timer += 0.2F;
								}

							}

						} else if (child instanceof SettingsButtonSegment) {
							SettingsButtonSegment set = (SettingsButtonSegment) child;
							set.mark = false;
						}
					}

				}

			} else if (this.parent.cache_activity == 0 && parent.list.size() != 0) {

				this.parent.cache_activity = 2;
				float timer = 0;

				for (int i = 0; i < this.parent.list.size(); i++) {
					for (Segment child : this.parent.list.get(i).childs) {
						if (child instanceof ButtonCheckboxSegment) {
							ButtonCheckboxSegment checkbox = (ButtonCheckboxSegment) child;
							if (!checkbox.active) {
								checkbox.active = true;

								int yOffTemp = 18 + 20 * i + this.parent.add;
								boolean invalid = false;
								if (yOffTemp < -3)
									invalid = true;
								if (yOffTemp > this.parent.height + 18)
									invalid = true;

								if (invalid)
									checkbox.timer = (float) (Math.PI / 3);
								else {
									if (checkbox.timer <= 0)
										checkbox.timer = 0 - timer;
									timer += 0.2F;
								}

							}

						}
					}

				}

			} else if (this.parent.cache_activity == 2) {

				this.parent.cache_activity = 0;
				float timer = 0;

				for (int i = 0; i < this.parent.list.size(); i++) {
					for (Segment child : this.parent.list.get(i).childs) {
						if (child instanceof ButtonCheckboxSegment) {
							ButtonCheckboxSegment checkbox = (ButtonCheckboxSegment) child;
							if (checkbox.active) {
								checkbox.active = false;

								int yOffTemp = 18 + 20 * i + this.parent.add;
								boolean invalid = false;
								if (yOffTemp < -3)
									invalid = true;
								if (yOffTemp > this.parent.height + 18)
									invalid = true;

								if (invalid) {
									checkbox.timer = 0;
								} else {
									if (checkbox.timer >= (float) (Math.PI / 3))
										checkbox.timer = (float) (Math.PI / 3) + timer;
									timer += 0.2F;
								}

							}

						} else if (child instanceof SettingsButtonSegment) {
							SettingsButtonSegment set = (SettingsButtonSegment) child;
							set.mark = false;
						}
					}

				}

			}
			this.clickSound();
			return true;

		}
		return false;
	}

}