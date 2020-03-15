package de.pt400c.defaultsettings.gui;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import de.pt400c.defaultsettings.FileUtil;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import static org.lwjgl.opengl.GL11.*;
import static de.pt400c.neptunefx.NEX.*;
import de.pt400c.defaultsettings.gui.MathUtil.Vec2f;

@SideOnly(Side.CLIENT)
public class ButtonCheckboxSegment extends Segment {
	
	protected final String name;
	public boolean active;
	protected boolean grabbed;
	public float timer = 0;
	private float offX;
	private float offY;
	private final ScrollableSegment parent;

	public ButtonCheckboxSegment(GuiScreen gui, float posX, float posY, float width, float height, String name, boolean popupSegment, ScrollableSegment parent, boolean active) {
		super(gui, posX, posY, width, height, popupSegment);
		this.name = name;
		this.active = active;
		this.parent = parent;
		timer = active ? MathUtil.PI / 3 : 0;
	}
	
	@Override
	public void customRender(int mouseX, int mouseY, float customPosX, float customPosY, float partialTicks) {

		if (active) {

			if (this.timer <= MathUtil.PI / 3)
				this.timer += 0.05;

		} else {

			if (this.timer > 0)
				this.timer -= 0.05;
		}
		
		float tempTimer = this.timer;
		if (this.timer > MathUtil.PI / 3)
			tempTimer = MathUtil.PI / 3;
		else if (this.timer < 0)
			tempTimer = 0;

		final float alphaRate = (float) ((Math.sin(3 * tempTimer - 3 * (MathUtil.PI / 2)) + 1) / 2);

		int color = ExportSegment.locked ? 0xff878787 : 0xffe6e6e6;
		this.offX = customPosX;
		this.offY = customPosY;
		customPosX += this.getPosX();
		customPosY += this.getPosY();

		glEnable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		float outRad = 3F;
		
		float inRad = 1F;
		
		drawRectRoundedCorners(customPosX - 2 - 3, customPosY - 2 - 3, (float) customPosX + this.width + 2 + 3, (float) customPosY + this.height + 2 + 3, color, outRad);
		
		float factor = 1F - ((outRad - inRad) / outRad);
		
		float innerRadius = outRad - (factor * outRad);

		if (this.timer <= MathUtil.PI / 3) {
			color = 0xff282828;
			drawRectRoundedCorners(customPosX - 2 - 3 + inRad, customPosY - 2 - 3 + inRad, (float) customPosX + this.width + 2 + 3 - inRad, (float) customPosY + this.height + 2 + 3 - inRad, color, innerRadius < 0 ? 0 : innerRadius);
		}

		color = 0xffff8518;
		int value = (int) ((((color >> 24 & 255) / 255.0F) - alphaRate) * 255F) ;
		
		color = ((value & 0x0ff) << 24) | (((color >> 16 & 255) & 0x0ff) << 16) | (((color >> 8 & 255) & 0x0ff) << 8) | ((color & 255) & 0x0ff);
		
		drawRectRoundedCorners(customPosX - 2 - 3 + inRad, customPosY - 2 - 3 + inRad, (float) customPosX + this.width + 2 + 3 - inRad, (float) customPosY + this.height + 2 + 3 - inRad, color, innerRadius < 0 ? 0 : innerRadius);
		if (this.timer > 0) {
			color = 0xff282828;

			float f3 = (float) (color >> 24 & 255) / 255.0F;
			float f = (float) (color >> 16 & 255) / 255.0F;
			float f1 = (float) (color >> 8 & 255) / 255.0F;
			float f2 = (float) (color & 255) / 255.0F;

			int scaleFactor = scaledresolution.getScaleFactor();

			drawLine2D_2(f, f1, f2, f3, scaleFactor, 3F, new Vec2f((float) customPosX - 2, (float) customPosY + 1.5F), new Vec2f((float) customPosX + 4 - 3, (float) customPosY + 4 + 1), new Vec2f((float) customPosX + 7 - 2.5F, (float) customPosY - 5 + 3F));
		}

		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);
	}
	
	@Override
	public boolean isSelected(int mouseX, int mouseY) {
		float tempX = this.getPosX() + this.offX + this.hitX; 
		float tempY = this.getPosY() + this.offY + this.hitY; 
		return (((GuiConfig) this.gui).popupField == null || this.getIsPopupSegment()) && mouseX >= tempX - 4 && mouseY >= tempY - 4 && mouseX < tempX + this.getWidth() + 4&& mouseY < tempY + this.getHeight() + 4;
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

			this.active = Boolean.logicalXor(this.active, true);
			FileUtil.switchActive(this.name);

			File fileDir = new File(FileUtil.mcDataDir, "config");
			FileFilter ff = null;
			String arg = this.parent.searchbar.query;
			if (arg != null) {
				ff = new FileFilter() {

					@Override
					public boolean accept(File file) {

						if (!file.getName().equals("defaultsettings") && !file.getName().equals("defaultsettings.json") && !file.getName().equals("ds_dont_export.json")
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
				try {
					boolean active = FileUtil.mainJson.activeConfigs.contains(files[i].getName());
					if (active)
						activeCount++;
				}catch(ArrayIndexOutOfBoundsException e) {
					activeCount = 0;
					break;
				}
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
	public void render(int mouseX, int mouseY, float partialTicks) {

	}
}