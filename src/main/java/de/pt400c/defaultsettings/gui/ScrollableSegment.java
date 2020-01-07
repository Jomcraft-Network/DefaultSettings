package de.pt400c.defaultsettings.gui;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import static de.pt400c.defaultsettings.FileUtil.MC;
import java.util.List;
import static de.pt400c.defaultsettings.DefaultSettings.fontRenderer;
import java.util.function.Function;
import static de.pt400c.neptunefx.NEX.*;
import de.pt400c.defaultsettings.FileUtil;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static org.lwjgl.opengl.GL11.*;

@OnlyIn(Dist.CLIENT)
public class ScrollableSegment extends Segment {

	protected boolean grabbed;
	protected float add = 0;
	//0 : Nothing selected
	//1 : Something is selected
	//2 : All is selected
	protected byte cache_activity;
	public List<RowItem> list = new ArrayList<RowItem>();
	private boolean invisible;
	private final ScrollbarSegment scrollBar;
	private final ButtonBulkActionSegment bulkAction;
	public final SearchbarSegment searchbar;
	private float distanceY = 0;
	private float maxSize = 0;
	private final byte id;
	private float velocity = 0;
	private final Function<GuiConfig, Integer> widthF;
	private final Function<GuiConfig, Integer> heightF;
	static final int row = 15;
	private String prevTerm = "";

	public ScrollableSegment(Screen gui, float posX, float posY, Function<GuiConfig, Integer> width, Function<GuiConfig, Integer> height, byte id) {	
		super(gui, posX, posY, width.apply((GuiConfig) gui), height.apply((GuiConfig) gui), false);
		this.scrollBar = new ScrollbarSegment(this.gui, (float) (this.posX + this.width), (float) posY, 6, 20, this);
		this.id = id;
		this.list = getRowList(null);
		this.bulkAction = new ButtonBulkActionSegment(this.gui, posX + 82, posY + 5, 6, 6, this);
		this.searchbar = new SearchbarSegment(gui, posX + 122, posY - 1, 40, 18, false, this);
		this.widthF = width;
		this.heightF = height;
	}
	
	@Override
	public void init() {
		this.list = getRowList(null);
	}
	
	public List<RowItem> getRowList(String[] arg) {
		this.add = 0;
		switch (id) {
		
		case 0: {

			List<RowItem> rows = new ArrayList<RowItem>();
			File fileDir = new File(FileUtil.mcDataDir, "config");
			FileFilter ff = null;
			if (arg != null && arg.length != 0) {
				ff = new FileFilter() {

					@Override
					public boolean accept(File file) {

						if (!file.getName().equals("defaultsettings") && !file.getName().equals("defaultsettings.json") && !file.getName().equals("ds_dont_export.json") && !file.getName().equals("keys.txt") && !file.getName().equals("options.txt") && !file.getName().equals("optionsof.txt") && !file.getName().equals("servers.dat") && file.getName().toLowerCase().startsWith(arg[0].toLowerCase()))
							return true;

						return false;
					}
				};
			} else {
				ff = FileUtil.fileFilter;
			}
			File[] files = fileDir.listFiles(ff);
			int activeCount = 0;
			for (int i = 0; i < files.length; i++) {

				float yOffTemp = row - 0.5F + row * i + add;
				boolean active = FileUtil.getActives().contains(files[i].getName());
				if (active)
					activeCount++;
				rows.add(new RowItem(files[i].getName(), new ButtonCheckboxSegment(gui, 104, yOffTemp + 46.5F, 2.5F, 2.5F, files[i].getName(), false, this, active)));
			}

			if (rows.size() != 0 && activeCount == files.length)
				this.cache_activity = 2;
			else if (activeCount > 0)
				this.cache_activity = 1;
			else
				this.cache_activity = 0;

			return rows;
		}

		case 1: {
			//Nothing yet
		}

		default:
			return new ArrayList<RowItem>();
		}

	}
	
	@Override
	public boolean mouseScrolled(float p_mouseScrolled_1_) {
		this.maxSize = row - 0.5F + row * (this.list.size() - 1);
		if(!this.invisible)
			this.velocity += p_mouseScrolled_1_;
		return true;
	}

	@Override
	public void guiContentUpdate(String... arg){
		FileFilter ff = null;

		File fileDir = new File(FileUtil.mcDataDir, "config");
		if (arg != null && arg.length != 0) {
			
			if(!this.searchbar.activated && !arg[0].equals("")) 
				arg[0] = this.prevTerm;
			else if(this.searchbar.activated || arg[0].equals(""))
				this.prevTerm = arg[0];

			ff = new FileFilter() {

				@Override
				public boolean accept(File file) {

					if (!file.getName().equals("defaultsettings") && !file.getName().equals("defaultsettings.json") && !file.getName().equals("ds_dont_export.json") && !file.getName().equals("keys.txt") && !file.getName().equals("options.txt") && !file.getName().equals("optionsof.txt") && !file.getName().equals("servers.dat") && file.getName().toLowerCase().startsWith(arg[0].toLowerCase()))
						return true;

					return false;
				}
			};
		} else {
			ff = FileUtil.fileFilter;
		}
		File[] files = fileDir.listFiles(ff);
		if(files.length != this.list.size()) 
			this.list = getRowList(arg);
	}
	
	@Override
	public boolean hoverCheck(int mouseX, int mouseY) {
		
		final float offX = (float) (this.getWidth() + 58);
		final float offY = 53;
		final float tempHeight = this.getHeight() + 2;
		final float tempWidth = 35;
		
		if(mouseX >= offX && mouseY >= offY && mouseX < offX + tempWidth && mouseY < offY + tempHeight) {
			for (int i = 0; i < this.list.size(); i++) {
				float yOffTemp = row - 0.5F + row * i + add;

				if (yOffTemp < -3)
					continue;
				if (yOffTemp > this.height + row - 0.5F)
					break;

				for (Segment segment : this.list.get(i).childs) {
					if(segment.hoverCheck(mouseX, mouseY))
						return true;
				}

			}
		}
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		
		if(resized != this.resized_mark) {
			width = widthF.apply((GuiConfig) this.gui);
			height = heightF.apply((GuiConfig) this.gui);
			this.resized_mark = resized;
		}
		
		this.add += this.velocity;
		
		if (this.add > 0) {
			this.add = 0;
			this.velocity = 0;
		}

		if (this.add < 0) {
			final float yOff = maxSize + this.add;
			final float movable = this.getPosY() + yOff;
			final float fix = this.getPosY() + height - 1;

			if (movable < fix) {

				float tempAdd = this.getPosY() + height - 1 - row * (this.list.size() - 1) - this.getPosY() - row - 0.5F;
				this.velocity = 0;
				this.add = tempAdd;

			}
		}
		
		if(this.velocity > 0) {
			this.velocity -= 1;
			if(this.velocity - 1 == 0)
				this.velocity -= 1;
		}
		else if(this.velocity < 0) {
			this.velocity += 1;
			if(this.velocity + 1 == 0)
				this.velocity += 1;
		}

		this.maxSize = row - 0.5F + row * (this.list.size() - 1);
		final int color = 0xffe6e6e6;

		glEnable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		if (maxSize <= height)
			this.invisible = true;
		else
			this.invisible = false;

		drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.width + (!this.invisible ? this.scrollBar.getWidth() + 2 : 0), this.getPosY() + this.height, 0xff3c3c3c, false, null, false);

		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);

		drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), 0xff3c3c3c, true, null, false);

		if (this.grabbed) {
			if (this.invisible)
				distanceY = 0;
			else {
				float factor = mouseY - distanceY;

				if (factor > 0)
					factor = 0;

				if (factor < 0) {
					final float yOff = maxSize + factor;
					final float movable = this.getPosY() + yOff;
					final float fix = this.getPosY() + height - 1;

					if (movable < fix) {

						float tempAdd = this.getPosY() + height - 1 - row * (this.list.size() - 1) - this.getPosY() - row - 0.5F;

						factor = tempAdd;

					}
				}
				this.add = factor;
			}

		} else {
			distanceY = 0;
		}

		final int scaleFactor = (int) scaledFactor;

		glPushMatrix();
		glEnable(GL_SCISSOR_TEST);
		glScissor((int) (this.getPosX() * scaleFactor), (int) ( (float) (MC.mainWindow.getScaledHeight() - this.getPosY() - this.getHeight() - 1F) * scaleFactor), (int) (this.getWidth() * scaleFactor), (int) ((float) (this.getHeight() + 1F) * scaleFactor));

		for (int i = 0; i < this.list.size(); i++) {
			final float yOffTemp = row - 0.5F + row * i + add;

			if (yOffTemp < -3)
				continue;
			if (yOffTemp > this.height + row - 0.5F)
				break;
			
			final String text = this.list.get(i).displayString;
			final float dots = fontRenderer.getStringWidth("...", 1, false);

			final float widthString = fontRenderer.getStringWidth(text, 1, false);

			if (widthString >= (width - 40)) {
				fontRenderer.drawString(fontRenderer.trimStringToWidth(text, (int) ((width - 40) - 1 - dots), false) + "...", (int) this.getPosX() + 23, (int) this.getPosY() + yOffTemp - 11.5F, 0xffe6e6e6, 1.0F, false);
			} else {
				fontRenderer.drawString(text, (int) this.getPosX() + 23, (int) this.getPosY() + yOffTemp - 11.5F, 0xffe6e6e6, 1.0F, false);
			}

			for (Segment segment : this.list.get(i).childs) 
				segment.customRender(mouseX, mouseY, 0, this.add, partialTicks);

			if (!(i == this.list.size() - 1))
				drawRect(this.getPosX(), this.getPosY() + yOffTemp, this.getPosX() + this.getWidth() - 3, this.getPosY() + yOffTemp + 0.5F, 0xffa0a0a0, true, null, false);

		}
	
		glDisable(GL_SCISSOR_TEST);
		glPopMatrix();

		final float percentHeight = height / this.maxSize;

		this.scrollBar.height = MathUtil.clamp(height * percentHeight, 20, Integer.MAX_VALUE);

		final float first = this.maxSize - height + 1;
		final float second = -this.add;
		boolean active = true;

		if (first > second)
			active = false;

		final float posPercentage = -(float) (this.add) / (float) (this.maxSize - height + (active ? 1 : -1));
		this.scrollBar.setPos((float) (posX + width), (float) posY + (height - this.scrollBar.height) * posPercentage);

		if(!this.invisible)
			drawRect(this.getPosX() + this.width - 3, this.getPosY(), this.getPosX() + this.width - 2, this.getPosY() + this.getHeight(), 0xffe6e6e6, true, null, false);

		glEnable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		drawRectRoundedCornersHollow(this.getPosX() - 4, this.getPosY() - 4, (float) this.getPosX() + (this.invisible ? this.width : this.width + this.scrollBar.getWidth() + 2) + 4, this.getPosY() + this.height + 4, color, 10, 6);

		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);

		if (!this.invisible)
			this.scrollBar.render(mouseX, mouseY, partialTicks);

		this.bulkAction.render(mouseX, mouseY, partialTicks);

		this.searchbar.render(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {
		if (this.isSelected(mouseX, mouseY)) {
			((DefaultSettingsGUI) this.gui).resetSelected();
			for (int i = 0; i < this.list.size(); i++) {
				float yOffTemp = row - 0.5F + row * i + add;

				if (yOffTemp < -3)
					continue;
				if (yOffTemp > this.height + row - 0.5F)
					break;

				for (Segment segment : this.list.get(i).childs) {
					if (segment.mouseClicked(mouseX, mouseY, button))
						return true;

				}

			}

			this.grabbed = true;
			this.distanceY += mouseY - this.add;

			return true;
		} else {
			return this.scrollBar.mouseClicked(mouseX, mouseY, button) ? true : this.bulkAction.mouseClicked(mouseX, mouseY, button) ? true : this.searchbar.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int button) {
		this.grabbed = false;

		for (int i = 0; i < this.list.size(); i++) {
			float yOffTemp = row - 0.5F + row * i + add;

			if (yOffTemp < -3)
				continue;
			if (yOffTemp > this.height + row - 0.5F)
				break;
			
			if(this.scrollBar.mouseReleased(mouseX, mouseY, button))
				return true;
			
			for (Segment segment : this.list.get(i).childs) {
				if (segment.mouseReleased(mouseX, mouseY, button))
					return true;

			}

		}
		return this.bulkAction.mouseReleased(mouseX, mouseY, button) ? true : this.searchbar.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseDragged(int mouseX, int mouseY, int button) {

		for (int i = 0; i < this.list.size(); i++) {
			float yOffTemp = row - 0.5F + row * i + add;

			if (yOffTemp < -3)
				continue;
			if (yOffTemp > this.height + row - 0.5F)
				break;

			for (Segment segment : this.list.get(i).childs) {

				if (segment.mouseDragged(mouseX, mouseY, button))
					return true;
			}

		}

		if (!this.isSelected(mouseX, mouseY))
			this.grabbed = false;
		return this.scrollBar.mouseDragged(mouseX, mouseY, button) ? true : this.bulkAction.mouseDragged(mouseX, mouseY, button) ? true : this.searchbar.mouseDragged(mouseX, mouseY, button);
	}
	
}

class RowItem {
	
	public final String displayString;
	public final Segment[] childs;
	
	public RowItem(String name, Segment... childs) {
		this.displayString = name;
		this.childs = childs;
	}
	
}