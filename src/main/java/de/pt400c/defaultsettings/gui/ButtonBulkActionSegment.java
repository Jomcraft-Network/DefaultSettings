package de.pt400c.defaultsettings.gui;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import de.pt400c.defaultsettings.FileUtil;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static de.pt400c.neptunefx.NEX.*;
import static org.lwjgl.opengl.GL11.*;
import de.pt400c.defaultsettings.gui.MathUtil.Vec2f;
import de.pt400c.defaultsettings.gui.MenuScreen.MutableByte;

@OnlyIn(Dist.CLIENT)
public class ButtonBulkActionSegment extends Segment {
	
	//STATE: 
	//0 : Nothing selected, select all
	//1 : Particially select, deselect all
	//2 : All selected, deselect all
	protected boolean grabbed;
	public float timer = 0;
	private boolean prevActive = false;
	private final ScrollableSegment parent;
	

	public ButtonBulkActionSegment(Screen gui, float posX, float posY, int width, int height, ScrollableSegment parent) {
		super(gui, posX, posY, width, height, false);
		this.parent = parent;
		if(parent.cache_activity == 1 || parent.cache_activity == 2)
			this.timer = MathUtil.PI / 3;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {

		if(!((parent.cache_activity == 1 || parent.cache_activity == 2) == prevActive)) {
			new Thread(new ThreadRunnable(((GuiConfig) gui).menu.exportActive) {
				
				@Override
				public void run() {
					if(FileUtil.exportMode())
						this.supply.setByte((byte) 2);
					else
						this.supply.setByte((byte) 1);
				}
			}).start();
		}
		
		if (parent.cache_activity == 1 || parent.cache_activity == 2) {
			prevActive = true;
			if (this.timer <= MathUtil.PI / 3)
				this.timer += 0.05;

		} else {
			prevActive = false;
			if (this.timer > 0)
				this.timer -= 0.05;
		}

		final float alphaRate = (float) ((Math.sin(3 * timer - 3 * (MathUtil.PI / 2)) + 1) / 2);

		int color = ExportSegment.locked ? 0xff878787 : 0xffe6e6e6;
		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;

		glEnable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		float outRad = 4.5F;
	       
        float inRad = 1F;
       
        drawRectRoundedCorners(this.getPosX() - 2 - 3, this.getPosY() - 2 - 3, (float) this.getPosX() + this.width + 2 + 3, (float) this.getPosY() + this.height + 2 + 3, color, outRad);
       
        float factor = 1F - ((outRad - inRad) / outRad);
       
        float innerRadius = outRad - (factor * outRad);
		
		if (this.timer <= MathUtil.PI / 3) {
			color = 0xff282828;
			drawRectRoundedCorners(this.getPosX() - 2 - 3 + inRad, this.getPosY() - 2 - 3 + inRad, (float) this.getPosX() + this.width + 2 + 3 - inRad, (float) this.getPosY() + this.height + 2 + 3 - inRad, color, innerRadius < 0 ? 0 : innerRadius);
		}

		color = 0xfffe8518;
		
		int value = (int) ((((color >> 24 & 255) / 255.0F) - alphaRate) * 255F) ;
	       
        color = ((value & 0x0ff) << 24) | (((color >> 16 & 255) & 0x0ff) << 16) | (((color >> 8 & 255) & 0x0ff) << 8) | ((color & 255) & 0x0ff);
		
        drawRectRoundedCorners(this.getPosX() - 2 - 3 + inRad, this.getPosY() - 2 - 3 + inRad, (float) this.getPosX() + this.width + 2 + 3 - inRad, (float) this.getPosY() + this.height + 2 + 3 - inRad, color, innerRadius < 0 ? 0 : innerRadius);

        int scaleFactor = (int) scaledFactor;
        
		if (this.parent.cache_activity == 1) {
			color = 0xff2c2c2c;

			f3 = (float) (color >> 24 & 255) / 255.0F;
			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;

			drawLine2D_2(f, f1, f2, f3, scaleFactor, 4, new Vec2f((float) this.getPosX(), (float) this.getPosY() + 3.5F), new Vec2f((float) this.getPosX() + 5.5F, (float) this.getPosY() + 3.5F));
		}

		if (this.parent.cache_activity == 2) {
			color = 0xff2c2c2c;

			f3 = (float) (color >> 24 & 255) / 255.0F;
			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;

			drawLine2D_2(f, f1, f2, f3, scaleFactor, 4, new Vec2f((float) this.getPosX() - 1, (float) this.getPosY() + 3.5F), new Vec2f((float) this.getPosX() + 4 - 1, (float) this.getPosY() + 4 + 3.5F), new Vec2f((float) this.getPosX() + 7, (float) this.getPosY() - 5 + 3.5F));
		}

		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);
	}
	
	@Override
	public boolean isSelected(int mouseX, int mouseY) {
		return (((GuiConfig) this.gui).popupField == null || this.getIsPopupSegment()) && mouseX >= this.getPosX() + this.hitX - 4 && mouseY >= this.getPosY() + this.hitY - 4 && mouseX < this.getPosX() + this.hitX + this.getWidth() + 4 && mouseY < this.getPosY() + this.hitY + this.getHeight() + 4;
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
		if (!this.isSelected(mouseX, mouseY)) {
			this.grabbed = false;
			return false;
		}
		return true;
	}

	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int button) {
		if (this.grabbed) {
			if (this.isSelected(mouseX, mouseY))
				this.grabbed = false;

			if(ExportSegment.locked)
				return false;

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
									&& !file.getName().equals("ds_dont_export.json")
									&& !file.getName().equals("keys.txt") && !file.getName().equals("options.txt")
									&& !file.getName().equals("optionsof.txt") && !file.getName().equals("servers.dat")
									&& !new File(FileUtil.getMainFolder(), "sharedConfigs/" + file.getName()).exists()
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

					boolean active = FileUtil.mainJson.activeConfigs.contains(files[i].getName());
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

								float yOffTemp = ScrollableSegment.row - 0.5F + ScrollableSegment.row * i + this.parent.add;
								boolean invalid = false;
								if (yOffTemp < -3)
									invalid = true;
								if (yOffTemp > this.parent.height + ScrollableSegment.row - 0.5F)
									invalid = true;

								if (invalid)
									checkbox.timer = 0;
								else {
									if (checkbox.timer >= MathUtil.PI/ 3)
										checkbox.timer = MathUtil.PI / 3 + timer;
									timer += 0.2F;
								}

							}
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

								float yOffTemp = ScrollableSegment.row - 0.5F + ScrollableSegment.row * i + this.parent.add;
								boolean invalid = false;
								if (yOffTemp < -3)
									invalid = true;
								if (yOffTemp > this.parent.height + ScrollableSegment.row - 0.5F)
									invalid = true;

								if (invalid)
									checkbox.timer = MathUtil.PI / 3;
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
	
								float yOffTemp = ScrollableSegment.row - 0.5F + ScrollableSegment.row/*18 + 20*/ * i + this.parent.add;
								boolean invalid = false;
								if (yOffTemp < -3)
									invalid = true;
								if (yOffTemp > this.parent.height + ScrollableSegment.row - 0.5F/*+ 18*/)
									invalid = true;

								if (invalid) {
									checkbox.timer = 0;
								} else {
									if (checkbox.timer >= MathUtil.PI / 3)
										checkbox.timer = MathUtil.PI / 3 + timer;
									timer += 0.2F;
								}

							}
						}
					}

				}

			}
			this.clickSound();
			return true;

		}
		return false;
	}

	abstract private class ThreadRunnable implements Runnable {
	 	   
        final MutableByte supply;
        ThreadRunnable(MutableByte supply) {
            this.supply = supply;
        }
    }	
}