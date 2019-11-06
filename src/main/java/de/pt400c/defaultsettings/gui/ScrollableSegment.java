package de.pt400c.defaultsettings.gui;

import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static de.pt400c.defaultsettings.DefaultSettings.fontRenderer;
import org.lwjgl.input.Mouse;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import static de.pt400c.neptunefx.NEX.*;
import de.pt400c.defaultsettings.FileUtil;
import de.pt400c.defaultsettings.GuiConfig;
import de.pt400c.neptunefx.NEX;
import de.pt400c.defaultsettings.gui.MathUtil.Vec2f;
import net.minecraft.client.gui.GuiScreen;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
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

	public ScrollableSegment(GuiScreen gui, float posX, float posY, Function<GuiConfig, Integer> width, Function<GuiConfig, Integer> height, byte id) {	
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
	
	public List<RowItem> getRowList(final String[] arg) {
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
				rows.add(new RowItem(files[i].getName(), new ButtonCheckboxSegment(gui, 104, yOffTemp + 46.5F, 2.5F, 2.5F, files[i].getName(), false, this, i, active), new SettingsButtonSegment(gui, i, new Function<ScrollableSegment, Float>() {
					@Override
					public Float apply(ScrollableSegment i2) {return i2.getWidth() + i2.posX - 12;}
				}, yOffTemp + 44.5F, files[i].getName(), this, FileUtil.getOverrides().containsKey(files[i].getName()))));
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
	public boolean handleMouseInput() {
		final float scroll = Mouse.getEventDWheel();
		this.maxSize = row - 0.5F + row * (this.list.size() - 1);
		if(!this.invisible)
			this.velocity += scroll / 120F;
		return true;
	}
	
	@Override
	public void guiContentUpdate(final String... arg){
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

		final int scaleFactor = scaledresolution.getScaleFactor();

		glPushMatrix();
		glEnable(GL_SCISSOR_TEST);
		glScissor((int) (this.getPosX() * scaleFactor), (int) ( (float) (scaledresolution.getScaledHeight() - this.getPosY() - this.getHeight() - 1F) * scaleFactor), (int) (this.getWidth() * scaleFactor), (int) ((float) (this.getHeight() + 1F) * scaleFactor));

		for (int i = 0; i < this.list.size(); i++) {
			final float yOffTemp = row - 0.5F + row * i + add;

			if (yOffTemp < -3)
				continue;
			if (yOffTemp > this.height + row - 0.5F)
				break;
			
			final String text = this.list.get(i).displayString;
			final float dots = fontRenderer.getStringWidth("...", 1, false);

			final float widthString = fontRenderer.getStringWidth(text, 1, false);
			final SettingsButtonSegment button = (SettingsButtonSegment) this.list.get(i).childs[1];
			if (widthString >= (width - (button.mark ? 55 : 40))) {
				fontRenderer.drawString(fontRenderer.trimStringToWidth(text, (int) ((width - (button.mark ? 55 : 40)) - 1 - dots), false) + "...", (int) this.getPosX() + 23, (int) this.getPosY() + yOffTemp - 11.5F, 0xffe6e6e6, 1.0F, false);
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

class SettingsButtonSegment extends Segment {
	
	protected final int id;
	protected final String name;
	protected boolean grabbed;
	private float offX;
	private float offY;
	private final ScrollableSegment parent;
	private boolean active;
	public float timer = 0;
	private final Function<ScrollableSegment, Float> posXF;
	private static final float BRIGHT_SCALE = 0.85f;
	public boolean mark;
		
	public SettingsButtonSegment(GuiScreen gui, int id, Function<ScrollableSegment, Float> posX, float posY, String name, ScrollableSegment parent, boolean mark) {
		super(gui, posX.apply(parent), posY, 6, 6, false);
		this.id = id;
		this.name = name;
		this.parent = parent;
		this.mark = mark;
		this.posXF = posX;
	}
	
	@Override
	public boolean hoverCheck(int mouseX, int mouseY) {

		if(this.isSelectedMark(mouseX, mouseY) && this.mark) {
			ArrayList<String> lines = new ArrayList<String>();
			
			float textWidth = 0;
			lines.addAll(fontRenderer.listFormattedStringToWidth("Only replaced once on update", (int) (this.gui.width - mouseX - 12), true));
			for(String line : lines) {
				
				if(fontRenderer.getStringWidth(line, 0.8F, true) > textWidth)
					textWidth = fontRenderer.getStringWidth(line, 0.8F, true);
			}
			
			drawButton(mouseX + 5, mouseY - 7 - 10 * lines.size(), mouseX + 12 + textWidth, mouseY - 3, 0xff3a3a3a, 0xffdcdcdc, 2);
			int offset = 0;
			
			Collections.reverse(lines);
			
			for(String line : lines) {
				fontRenderer.drawString(line, (float)(mouseX + 9), (float)(mouseY - 14 - offset), 0xff3a3a3a, 0.8F, true);
				offset += 10;
			}
			
			return true;
			
		}else if(this.isSelected(mouseX, mouseY)) {
			this.active = true;
			return true;
		}
		return false;
	}
	
	@Override
	public void customRender(int mouseX, int mouseY, float customPosX, float customPosY, float partialTicks) {
		
		if(resized != this.resized_mark) {
			posX = posXF.apply(this.parent);
			this.resized_mark = resized;
		}
		
		if(this.active && this.timer <= Math.PI)
			timer += 0.3;
		else if(!this.active && this.timer > 0)
			timer -= 0.3;

		final float darken = (float) ((Math.sin(timer + Math.PI / 2) + 1) / 6 + 0.67);

		int color = NEX.darkenColor(0xff3c3c3c, darken).getRGB();
		this.offX = customPosX;
		this.offY = customPosY;
		customPosX += this.getPosX();
		customPosY += this.getPosY();
		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;

		glEnable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glColor4f(f, f1, f2, f3);

		drawCircle((float) customPosX, (float) customPosY + 3.3F, 6.5F, 0, 0);
		color = 0xffe6e6e6;

		f3 = (float) (color >> 24 & 255) / 255.0F;
		f = (float) (color >> 16 & 255) / 255.0F;
		f1 = (float) (color >> 8 & 255) / 255.0F;
		f2 = (float) (color & 255) / 255.0F;
		final int scaleFactor = scaledresolution.getScaleFactor();
		drawDots(f, f1, f2, f3, scaleFactor, new Vec2f((float) customPosX, (float) customPosY + 3.3F), new Vec2f((float) customPosX, (float) customPosY + 4 + 3.5F), new Vec2f((float) customPosX, (float) customPosY - 5 + 4F));

		if (this.mark) {
			color = 0xff2675b6;

			f3 = (float) (color >> 24 & 255) / 255.0F;
			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;
			glColor4f(f, f1, f2, f3);
			drawCircle((float) customPosX - 15, (float) customPosY + 3.3F, 6.5F, 0, 0);

			color = 0xffffffff;

			f3 = (float) (color >> 24 & 255) / 255.0F;
			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;
			drawLine2D_2(f, f1, f2, f3, scaleFactor, 3.0F, new Vec2f((float) customPosX - 15, (float) customPosY), new Vec2f((float) customPosX - 15, (float) customPosY + 4.5F));
			drawDot(f, f1, f2, f3, scaleFactor, 3, new Vec2f((float) customPosX - 15, (float) customPosY + 7.3F));
		}
		
		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);
		this.active = false;
	}
	
	protected static Color darkenColor(int color) {
		return new Color((int) (((color & RED_MASK) >> 16) * BRIGHT_SCALE), (int) (((color & GREEN_MASK) >> 8) * BRIGHT_SCALE),
		(int) ((color & BLUE_MASK) * BRIGHT_SCALE), 255);
	}
	
	@Override
	public boolean isSelected(int mouseX, int mouseY) {
		final float tempX = this.getPosX() + this.offX + this.hitX; 
		final float tempY = this.getPosY() + this.offY + this.hitY; 
		return (((GuiConfig) this.gui).popupField == null || this.getIsPopupSegment()) && mouseX >= tempX - 8 && mouseY >= tempY - 4 && mouseX < tempX + this.getWidth() + 2 && mouseY < tempY + this.getHeight() + 4;
	}
	
	public boolean isSelectedMark(int mouseX, int mouseY) {
		final float tempX = this.getPosX() + this.offX + this.hitX; 
		final float tempY = this.getPosY() + this.offY + this.hitY; 
		return (((GuiConfig) this.gui).popupField == null || this.getIsPopupSegment()) && mouseX >= tempX - 24 && mouseY >= tempY - 4 && mouseX < tempX + this.getWidth() - 14 && mouseY < tempY + this.getHeight() + 4;
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			this.grabbed = true;
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

			final GuiConfig config = ((GuiConfig) this.gui);
			MenuScreen menu = config.menu;
			this.clickSound();
			menu.getVariants().get(menu.index).selected = null;
			
			config.popup.setOpening(true);
			config.popup.getWindow().title = "Config Options";
			config.popup.getWindow().setPos(config.width / 2 - 210 / 2, config.height / 2 - 100 / 2);
			config.popupField = config.popup;
			config.popupField.getWindow().clearChildren();
			config.popupField.getWindow().addChild(new TextSegment(config, 5, 29, 0, 0, "Should local configs be persistent?", 0xffffffff, true));
			config.popupField.getWindow().addChild(new QuitButtonSegment(config, 190, 5, 14, 14, new Function<ButtonSegment, Boolean>() {
				@Override
				public Boolean apply(ButtonSegment quitButton) {

					config.popupField.setOpening(false);

					return true;
				}
			}, 3F, true));
			
			List<String> actives = FileUtil.getActives();
			boolean active = actives.contains(this.name);
			
			config.popupField.getWindow().addChild(new TextSegment(config, 35, 45, 0, 0, "Always", 0xffffffff, true));
			config.popupField.getWindow().addChild(new TextSegment(config, 35, 65, 0, 0, "Replaced once", 0xffffffff, true));
			config.popupField.getWindow().addChild(new TextSegment(config, 35, 85, 0, 0, "Never", 0xffffffff, true));
			config.popupField.getWindow().addChild(new PopupCheckboxSegment(config, this.id, 15, 45, this.name, this.parent, config.popupField, (byte) 0, active && !FileUtil.getOverrides().containsKey(this.name)));
			config.popupField.getWindow().addChild(new PopupCheckboxSegment(config, this.id, 15, 65, this.name, this.parent, config.popupField, (byte) 1, active && FileUtil.getOverrides().containsKey(this.name)));
			config.popupField.getWindow().addChild(new PopupCheckboxSegment(config, this.id, 15, 85, this.name, this.parent, config.popupField, (byte) 2, !active));

			config.popup.setVisible(true);

		}
		return super.mouseReleased(mouseX, mouseY, button);
		
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {

	}

}

class PopupCheckboxSegment extends Segment {
	
	protected final String name;
	public boolean active;
	private final byte type;
	protected boolean grabbed;
	public float timer = 0;
	private final int id;
	private final ScrollableSegment segment;
	private final PopupSegment parent;

	public PopupCheckboxSegment(GuiScreen gui, int id, float posX, float posY, String name, ScrollableSegment segment, PopupSegment popupField, byte type, boolean active) {
		super(gui, posX, posY, 6, 6, true);
		this.name = name;
		this.id = id;
		this.active = active;
		this.parent = popupField;
		this.type = type;
		this.segment = segment;
		timer = active ? (float) (Math.PI / 3) : 0;
	}
	
	@Override
	public boolean isSelected(int mouseX, int mouseY) {
		float tempX = this.getPosX() + this.hitX; 
		float tempY = this.getPosY() + this.hitY; 
		return (((GuiConfig) this.gui).popupField == null || this.getIsPopupSegment()) && mouseX >= tempX - 4 && mouseY >= tempY - 4 && mouseX < tempX + this.getWidth() + 4&& mouseY < tempY + this.getHeight() + 4;
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			this.grabbed = true;
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

		if (this.isSelected(mouseX, mouseY)) {
			this.grabbed = false;
			if (!this.active) {

				if (this.active ^ true) {
					for (Segment child : this.parent.getWindow().getChildren()) {
						if (child instanceof PopupCheckboxSegment) {
							PopupCheckboxSegment checkButton = (PopupCheckboxSegment) child;
							checkButton.active = false;
						}
					}
				}
				this.active = this.active ^ true;

				if (this.type == 0) {

					RowItem item = this.segment.list.get(this.id);
					ButtonCheckboxSegment but = (ButtonCheckboxSegment) item.childs[0];
					but.active = true;
					FileUtil.setActive(this.name, true);
					FileUtil.setOverride(this.name, false);
					SettingsButtonSegment set = (SettingsButtonSegment) item.childs[1];
					set.mark = false;
					File fileDir = new File(FileUtil.mcDataDir, "config");
					FileFilter ff = null;
					final String arg = this.segment.searchbar.query;
					if (arg != null) {
						ff = new FileFilter() {

							@Override
							public boolean accept(File file) {

								if (!file.getName().equals("defaultsettings")
										&& !file.getName().equals("defaultsettings.json")
										&& !file.getName().equals("ds_dont_export.json")
										&& !file.getName().equals("keys.txt") && !file.getName().equals("options.txt")
										&& !file.getName().equals("optionsof.txt")
										&& !file.getName().equals("servers.dat")
										&& file.getName().toLowerCase().startsWith(arg.toLowerCase()))
									return true;

								return false;
							}
						};
					} else {
						ff = FileUtil.fileFilter;
					}
					
					File[] files = fileDir.listFiles(ff);
					List<RowItem> rows = this.segment.list;
					int activeCount = 0;
					for (int i = 0; i < rows.size(); i++) {

						boolean active = FileUtil.getActives().contains(files[i].getName());
						if (active)
							activeCount++;

					}

					if (rows.size() != 0 && activeCount == rows.size())
						this.segment.cache_activity = 2;
					else if (activeCount > 0)
						this.segment.cache_activity = 1;
					else
						this.segment.cache_activity = 0;
				} else if (this.type == 1) {

					RowItem item = this.segment.list.get(this.id);
					ButtonCheckboxSegment but = (ButtonCheckboxSegment) item.childs[0];
					SettingsButtonSegment set = (SettingsButtonSegment) item.childs[1];
					set.mark = true;
					but.active = true;
					FileUtil.setActive(this.name, true);
					FileUtil.setOverride(this.name, true);
					File fileDir = new File(FileUtil.mcDataDir, "config");
					FileFilter ff = null;
					final String arg = this.segment.searchbar.query;
					if (arg != null) {
						ff = new FileFilter() {

							@Override
							public boolean accept(File file) {

								if (!file.getName().equals("defaultsettings")
										&& !file.getName().equals("defaultsettings.json")
										&& !file.getName().equals("ds_dont_export.json")
										&& !file.getName().equals("keys.txt") && !file.getName().equals("options.txt")
										&& !file.getName().equals("optionsof.txt")
										&& !file.getName().equals("servers.dat")
										&& file.getName().toLowerCase().startsWith(arg.toLowerCase()))
									return true;

								return false;
							}
						};
					} else {
						ff = FileUtil.fileFilter;
					}
					File[] files = fileDir.listFiles(ff);
					List<RowItem> rows = this.segment.list;
					int activeCount = 0;
					for (int i = 0; i < rows.size(); i++) {
						try {
							boolean active = FileUtil.getActives().contains(files[i].getName());
							if (active)
								activeCount++;
						} catch (ArrayIndexOutOfBoundsException e) {
							activeCount = 0;
							break;
						}
					}

					if (rows.size() != 0 && activeCount == rows.size())
						this.segment.cache_activity = 2;
					else if (activeCount > 0)
						this.segment.cache_activity = 1;
					else
						this.segment.cache_activity = 0;

				} else if (this.type == 2) {

					RowItem item = this.segment.list.get(this.id);
					ButtonCheckboxSegment but = (ButtonCheckboxSegment) item.childs[0];
					but.active = false;
					FileUtil.setActive(this.name, false);
					FileUtil.setOverride(this.name, false);
					SettingsButtonSegment set = (SettingsButtonSegment) item.childs[1];
					set.mark = false;
					File fileDir = new File(FileUtil.mcDataDir, "config");
					FileFilter ff = null;
					final String arg = this.segment.searchbar.query;
					if (arg != null) {
						ff = new FileFilter() {

							@Override
							public boolean accept(File file) {

								if (!file.getName().equals("defaultsettings")
										&& !file.getName().equals("defaultsettings.json")
										&& !file.getName().equals("ds_dont_export.json")
										&& !file.getName().equals("keys.txt") && !file.getName().equals("options.txt")
										&& !file.getName().equals("optionsof.txt")
										&& !file.getName().equals("servers.dat")
										&& file.getName().toLowerCase().startsWith(arg.toLowerCase()))
									return true;

								return false;
							}
						};
					} else {
						ff = FileUtil.fileFilter;
					}
					File[] files = fileDir.listFiles(ff);
					List<RowItem> rows = this.segment.list;
					int activeCount = 0;
					for (int i = 0; i < rows.size(); i++) {

						boolean active = FileUtil.getActives().contains(files[i].getName());
						if (active)
							activeCount++;

					}

					if (rows.size() != 0 && activeCount == rows.size()) {
						this.segment.cache_activity = 2;
					} else if (activeCount > 0)
						this.segment.cache_activity = 1;
					else
						this.segment.cache_activity = 0;

				}

			}

			this.clickSound();
			return true;
		}
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
	
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

		final float alphaRate = (float) ((Math.sin(3 * tempTimer - 3 * (Math.PI / 2)) + 1) / 2);

		int color = 0xffe6e6e6;
		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;

		glEnable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE);

		float outRad = 4.5F;
	       
        float inRad = 1F;

        drawRectRoundedCorners(this.getPosX() - 2 - 3, this.getPosY() - 2 - 3, (float) this.getPosX() + this.width + 2 + 3, (float) this.getPosY() + this.height + 2 + 3, color, outRad);

        float factor = 1F - ((outRad - inRad) / outRad);
        
        float innerRadius = outRad - (factor * outRad);
        
		if (this.timer <= (Math.PI / 3)) {
			color = 0xff282828;
			drawRectRoundedCorners(this.getPosX() - 2 - 3 + inRad, this.getPosY() - 2 - 3 + inRad, (float) this.getPosX() + this.width + 2 + 3 - inRad, (float) this.getPosY() + this.height + 2 + 3 - inRad, color, innerRadius < 0 ? 0 : innerRadius);
		}
		
		color = 0xfffe8518;

		int value = (int) ((((color >> 24 & 255) / 255.0F) - alphaRate) * 255F) ;
	       
        color = ((value & 0x0ff) << 24) | (((color >> 16 & 255) & 0x0ff) << 16) | (((color >> 8 & 255) & 0x0ff) << 8) | ((color & 255) & 0x0ff);
		
        drawRectRoundedCorners(this.getPosX() - 2 - 3 + inRad, this.getPosY() - 2 - 3 + inRad, (float) this.getPosX() + this.width + 2 + 3 - inRad, (float) this.getPosY() + this.height + 2 + 3 - inRad, color, innerRadius < 0 ? 0 : innerRadius);
        
		if (this.timer > 0) {
			color = 0xff2c2c2c;

			f3 = (float) (color >> 24 & 255) / 255.0F;
			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;

			final int scaleFactor = scaledresolution.getScaleFactor();

			drawLine2D_2(f, f1, f2, f3, scaleFactor, 4, new Vec2f((float) this.getPosX() - 1, (float) this.getPosY() + 3.5F), new Vec2f((float) this.getPosX() + 4 - 1, (float) this.getPosY() + 4 + 3.5F), new Vec2f((float) this.getPosX() + 7, (float) this.getPosY() - 5 + 3.5F));
		}

		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);
	}
}