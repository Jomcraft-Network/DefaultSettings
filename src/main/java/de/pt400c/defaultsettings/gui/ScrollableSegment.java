package de.pt400c.defaultsettings.gui;

import static de.pt400c.defaultsettings.FileUtil.MC;

import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import de.pt400c.defaultsettings.FileUtil;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ScrollableSegment extends Segment {

	protected boolean grabbed;
	protected int add = 0;
	//0 : Nothing selected
	//1 : Something is selected
	//2 : All is selected
	protected byte cache_activity;
	public List<RowItem> list = new ArrayList<RowItem>();
	private boolean invisible;
	private final ScrollbarSegment scrollBar;
	private final ButtonBulkActionSegment bulkAction;
	public final SearchbarSegment searchbar;
	private double distanceY = 0;
	private int maxSize = 0;
	private final byte id;
	private float velocity = 0;

	public ScrollableSegment(GuiScreen gui, float posX, float posY, int width, int height, byte id) {	
		super(gui, posX, posY, width, height, false);
		this.scrollBar = new ScrollbarSegment(this.gui, (float) (posX + width), (float) posY, 10, 20, this);
		this.id = id;
		this.bulkAction = new ButtonBulkActionSegment(this.gui, posX + 82, posY + 5, 6, 6, this);
		this.searchbar = new SearchbarSegment(gui, posX + 112, posY - 1, 45, 18, false, this);
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

				int yOffTemp = 18 + 20 * i + add;
				boolean active = FileUtil.getActives().contains(files[i].getName());
				if (active)
					activeCount++;
				rows.add(new RowItem(files[i].getName(), new ButtonCheckboxSegment(gui, 132, yOffTemp + 43, 6, 6, files[i].getName(), false, this, i, active), new SettingsButtonSegment(gui, i, (float) (this.getWidth() + this.posX - 12), yOffTemp + 43, files[i].getName(), this, FileUtil.getOverrides().containsKey(files[i].getName()))));
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
		float scroll = Mouse.getEventDWheel();
		this.maxSize = 18 + 20 * (this.list.size() - 1);
		if(!this.invisible)
			this.velocity += scroll / 120F;
		return true;
	}
	
	@Override
	public void guiContentUpdate(String... arg){
		this.list = getRowList(arg);
	}
	
	@Override
	public void hoverCheck(float mouseX, float mouseY) {
		
		float offX = (float) (this.getWidth() + 88);
		float offY = 53;
		float tempHeight = this.getHeight() + 2;
		float tempWidth = 35;
		
		if(mouseX >= offX && mouseY >= offY && mouseX < offX + tempWidth && mouseY < offY + tempHeight) {
			for (int i = 0; i < this.list.size(); i++) {
				int yOffTemp = 18 + 20 * i + add;

				if (yOffTemp < -3)
					continue;
				if (yOffTemp > this.height + 18)
					break;

				for (Segment segment : this.list.get(i).childs) {
					segment.hoverCheck(mouseX, mouseY);
				}

			}
		}
			
	}

	@Override
	public void render(float mouseX, float mouseY, float partialTicks) {
		this.add += (int) (this.velocity);
		
		if (this.add > 0) {
			this.add = 0;
			this.velocity = 0;
		}

		if (this.add < 0) {
			int yOff = maxSize + this.add;
			int movable = (int) (this.getPosY() + yOff);
			int fix = (int) (this.getPosY() + height - 1);

			if (movable < fix) {

				int tempAdd = (int) (this.getPosY() + height - 1 - 20 * (this.list.size() - 1) - this.getPosY() - 18);
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

		this.maxSize = 18 + 20 * (this.list.size() - 1);
		int color = 0xff818181;

		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GlStateManager.color(f, f1, f2, f3);

		Segment.drawCircle((float) this.getPosX(), (float) this.getPosY(), 5, 180, 75);

		Segment.drawCircle((float) this.getPosX(), (float) this.getPosY() + this.height, 5, 90, 75);

		Segment.drawCircle((float) this.getPosX() + (this.invisible ? this.width : this.width + this.scrollBar.getWidth()), (float) this.getPosY() + this.height, 5, 0, 75);

		Segment.drawCircle((float) this.getPosX() + (this.invisible ? this.width : this.width + this.scrollBar.getWidth()), (float) this.getPosY(), 5, 270, 75);

		Segment.drawRect(this.getPosX(), this.getPosY() - 5, this.getPosX() + this.width + (!this.invisible ? this.scrollBar.getWidth() : 0), this.getPosY() + this.height + 5, null, false, null, false);

		Segment.drawRect(this.getPosX() + this.width + (!this.invisible ? this.scrollBar.getWidth() : 0), this.getPosY(), this.getPosX() + this.width + 5 + (!this.invisible ? this.scrollBar.getWidth() : 0), this.getPosY() + this.height, null, false, null, false);

		Segment.drawRect(this.getPosX() - 5, this.getPosY(), this.getPosX(), this.getPosY() + this.height, null, false, null, false);

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		Segment.drawRect(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), 0xffe0e0e0, true, null, false);

		if (this.grabbed) {
			if (this.invisible)
				distanceY = 0;
			else {
				int factor = (int) (mouseY - distanceY);

				if (factor > 0)
					factor = 0;

				if (factor < 0) {
					int yOff = maxSize + factor;
					int movable = (int) (this.getPosY() + yOff);
					int fix = (int) (this.getPosY() + height - 1);

					if (movable < fix) {

						int tempAdd = (int) (this.getPosY() + height - 1 - 20 * (this.list.size() - 1) - this.getPosY() - 18);

						factor = tempAdd;

					}
				}
				this.add = factor;
			}

		} else {
			distanceY = 0;
		}

		if (maxSize <= height)
			this.invisible = true;
		else
			this.invisible = false;

		ScaledResolution scaledResolution = new ScaledResolution(MC);
		int scaleFactor = scaledResolution.getScaleFactor();

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor((int) (this.getPosX() * scaleFactor), (int) ( (float) (scaledResolution.getScaledHeight() - this.getPosY() - this.getHeight() - 0.5F) * scaleFactor), (int) (this.getWidth() * scaleFactor), (int) (this.getHeight() * scaleFactor));

		for (int i = 0; i < this.list.size(); i++) {
			int yOffTemp = 18 + 20 * i + add;

			if (yOffTemp < -3)
				continue;
			if (yOffTemp > this.height + 18)
				break;
			
			String text = this.list.get(i).displayString;
			int dots = MC.fontRenderer.getStringWidth("...");

			int widthString = MC.fontRenderer.getStringWidth(text);
			SettingsButtonSegment button = (SettingsButtonSegment) this.list.get(i).childs[1];
			if (widthString >= (width - (button.mark ? 55 : 40))) {

				MC.fontRenderer.drawString(MC.fontRenderer.trimStringToWidth(text, (int) ((width - (button.mark ? 55 : 40)) - 1 - dots)) + "...", (int) this.getPosX() + 23, (int) this.getPosY() + yOffTemp - 13, 0x0, false);

			} else {
				MC.fontRenderer.drawString(text, (int) this.getPosX() + 23, (int) this.getPosY() + yOffTemp - 13, 0x0, false);
			}

			for (Segment segment : this.list.get(i).childs) {

				segment.customRender(mouseX, mouseY, 0, this.add, partialTicks);

			}

			if (!(i == this.list.size() - 1))
				Segment.drawRect(this.getPosX(), this.getPosY() + yOffTemp, this.getPosX() + this.getWidth(), this.getPosY() + yOffTemp + 2, 0xff373737, true, null, false);
			else
				Segment.drawRect(this.getPosX(), this.getPosY() + (maxSize + this.add), this.getPosX() + this.getWidth(), this.getPosY() + (maxSize + this.add) + 1, 0xff373737, true, null, false);

		}

		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GL11.glPopMatrix();

		float percentHeight = height / this.maxSize;

		this.scrollBar.height = clamp(height * percentHeight, 5, Integer.MAX_VALUE);

		int first = (int) (this.maxSize - height + 1);
		int second = -this.add;
		boolean active = true;

		if (first > second)
			active = false;

		float posPercentage = -(float) (this.add) / (float) (this.maxSize - height + (active ? 1 : -1));
		this.scrollBar.setPos((float) (posX + width), (float) posY + (height - this.scrollBar.height) * posPercentage);

		Segment.drawRect(this.getPosX() + this.width - 3, this.getPosY(), this.getPosX() + this.width, this.getPosY() + this.getHeight(), 0xff818181, true, null, false);

		if (!this.invisible)
			this.scrollBar.render(mouseX, mouseY, partialTicks);

		this.bulkAction.render(mouseX, mouseY, partialTicks);

		this.searchbar.render(mouseX, mouseY, partialTicks);

	}
	
	public static float clamp(float num, float min, float max)
    {
        if (num < min)
        {
            return min;
        }
        else
        {
            return num > max ? max : num;
        }
    }
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.isSelected(mouseX, mouseY)) {
			MenuScreen menu = ((GuiConfig) this.gui).menu;
			menu.getVariants().get(menu.index).selected = null;
			for (int i = 0; i < this.list.size(); i++) {
				int yOffTemp = 18 + 20 * i + add;

				if (yOffTemp < -3)
					continue;
				if (yOffTemp > this.height + 18)
					break;

				for (Segment segment : this.list.get(i).childs) {
					if (segment.mouseClicked(mouseX, mouseY, button))
						return true;

				}

			}

			this.grabbed = true;
			this.distanceY += (int) (mouseY - this.add);

			return true;
		} else {
			return this.scrollBar.mouseClicked(mouseX, mouseY, button) ? true : this.bulkAction.mouseClicked(mouseX, mouseY, button) ? true : this.searchbar.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		this.grabbed = false;

		for (int i = 0; i < this.list.size(); i++) {
			int yOffTemp = 18 + 20 * i + add;

			if (yOffTemp < -3)
				continue;
			if (yOffTemp > this.height + 18)
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
	public boolean mouseDragged(double mouseX, double mouseY, int button) {

		for (int i = 0; i < this.list.size(); i++) {
			int yOffTemp = 18 + 20 * i + add;

			if (yOffTemp < -3)
				continue;
			if (yOffTemp > this.height + 18)
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
	private static final float BRIGHT_SCALE = 0.85f;
	public boolean mark;
		
	public SettingsButtonSegment(GuiScreen gui, int id, float posX, float posY, String name, ScrollableSegment parent, boolean mark) {
		super(gui, posX, posY, 6, 6, false);
		this.id = id;
		this.name = name;
		this.parent = parent;
		this.mark = mark;
	}
	
	@Override
	public void hoverCheck(float mouseX, float mouseY) {

		if(this.isSelectedMark(mouseX, mouseY) && this.mark) {
			ArrayList<String> lines = new ArrayList<String>();
			
			int textWidth = 0;
			lines.addAll(MC.fontRenderer.listFormattedStringToWidth("Only replaced once on update", (int) (this.gui.width - mouseX - 12)));
			for(String line : lines) {
				
				if(MC.fontRenderer.getStringWidth(line) > textWidth)
					textWidth = MC.fontRenderer.getStringWidth(line);
			}
			
			Segment.drawButton(mouseX + 6, mouseY - 7 - 10 * lines.size(), mouseX + 12 + textWidth, mouseY - 3, 0xff3a3a3a, 0xffdcdcdc, 2);
			int offset = 0;
			
			Collections.reverse(lines);
			
			for(String line : lines) {
			
				MC.fontRenderer.drawString(line, (float)(mouseX + 9), (float)(mouseY - 14 - offset), 0xff3a3a3a, false);
				offset += 10;
			}
			
			
			
		}else if(this.isSelected(mouseX, mouseY)) {
			this.active = true;
		}
	}
	
	@Override
	public void customRender(float mouseX, float mouseY, float customPosX, float customPosY, float partialTicks) {
		if(this.active && this.timer <= Math.PI)
			timer += 0.3;
		else if(!this.active && this.timer > 0)
			timer -= 0.3;

		float darken = (float) ((Math.sin(timer + Math.PI / 2) + 1) / 6 + 0.67);

		int color = darkenColor(0xffe0e0e0, darken).getRGB();
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
		GlStateManager.color(f, f1, f2, f3);

		Segment.drawCircle((float) customPosX, (float) customPosY + 3.3F, 7, 0, 0);
		color = 0xff202020;

		f3 = (float) (color >> 24 & 255) / 255.0F;
		f = (float) (color >> 16 & 255) / 255.0F;
		f1 = (float) (color >> 8 & 255) / 255.0F;
		f2 = (float) (color & 255) / 255.0F;
		ScaledResolution scaledResolution = new ScaledResolution(MC);
		int scaleFactor = scaledResolution.getScaleFactor();
		Segment.drawDots(f, f1, f2, f3, scaleFactor, new Vec2f((float) customPosX, (float) customPosY + 3.3F), new Vec2f((float) customPosX, (float) customPosY + 4 + 3.5F), new Vec2f((float) customPosX, (float) customPosY - 5 + 4F));

		if (this.mark) {
			color = 0xff3d9d20;

			f3 = (float) (color >> 24 & 255) / 255.0F;
			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;
			GlStateManager.color(f, f1, f2, f3);
			Segment.drawCircle((float) customPosX - 15, (float) customPosY + 3.3F, 7, 0, 0);

			color = 0xffffffff;

			f3 = (float) (color >> 24 & 255) / 255.0F;
			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;
			Segment.drawLine2D_2(f, f1, f2, f3, scaleFactor, new Vec2f((float) customPosX - 15, (float) customPosY), new Vec2f((float) customPosX - 15, (float) customPosY + 4.5F));
			Segment.drawDot(f, f1, f2, f3, scaleFactor, 3, new Vec2f((float) customPosX - 15, (float) customPosY + 7.3F));
		}
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		this.active = false;
	}
	
	protected static Color darkenColor(int color) {
		return new Color((int) (((color & RED_MASK) >> 16) * BRIGHT_SCALE), (int) (((color & GREEN_MASK) >> 8) * BRIGHT_SCALE),
		(int) ((color & BLUE_MASK) * BRIGHT_SCALE), 255);
	}
	
	@Override
	public boolean isSelected(double mouseX, double mouseY) {
		double tempX = this.getPosX() + this.offX; 
		double tempY = this.getPosY() + this.offY; 
		return (((GuiConfig) this.gui).popupField == null || this.getIsPopupSegment()) && mouseX >= tempX - 8 && mouseY >= tempY - 4 && mouseX < tempX + this.getWidth() + 2 && mouseY < tempY + this.getHeight() + 4;
	}
	
	public boolean isSelectedMark(double mouseX, double mouseY) {
		double tempX = this.getPosX() + this.offX; 
		double tempY = this.getPosY() + this.offY; 
		return (((GuiConfig) this.gui).popupField == null || this.getIsPopupSegment()) && mouseX >= tempX - 24 && mouseY >= tempY - 4 && mouseX < tempX + this.getWidth() - 14 && mouseY < tempY + this.getHeight() + 4;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			this.grabbed = true;
			GuiConfig config = ((GuiConfig) this.gui);
			MenuScreen menu = config.menu;
			this.clickSound();
			menu.getVariants().get(menu.index).selected = null;
			
			config.popup.setOpening(true);
			config.popup.getWindow().title = "Config Options";
			config.popup.getWindow().setPos(config.width / 2 - 210 / 2, config.height / 2 - 100 / 2);
			config.popupField = config.popup;
			config.popupField.getWindow().clearChildren();
			config.popupField.getWindow().addChild(new TextSegment(config, 5, 30, 0, 0, "Should local configs be persistent?", 0, true));
			config.popupField.getWindow().addChild(new QuitButtonSegment(config, 190, 5, 14, 14, quitButton -> {

				config.popupField.setOpening(false);

				return true;
			}, true));
			
			List<String> actives = FileUtil.getActives();
			boolean active = actives.contains(this.name);
			
			config.popupField.getWindow().addChild(new PopupCheckboxSegment(config, this.id, 15, 45, this.name, this.parent, config.popupField, (byte) 0, active && !FileUtil.getOverrides().containsKey(this.name)));
			config.popupField.getWindow().addChild(new TextSegment(config, 35, 45, 0, 0, "Always", 0, true));
			config.popupField.getWindow().addChild(new PopupCheckboxSegment(config, this.id, 15, 65, this.name, this.parent, config.popupField, (byte) 1, active && FileUtil.getOverrides().containsKey(this.name)));
			config.popupField.getWindow().addChild(new TextSegment(config, 35, 65, 0, 0, "Replaced once", 0, true));
			config.popupField.getWindow().addChild(new PopupCheckboxSegment(config, this.id, 15, 85, this.name, this.parent, config.popupField, (byte) 2, !active));
			config.popupField.getWindow().addChild(new TextSegment(config, 35, 85, 0, 0, "Never", 0, true));
			
			config.popup.isVisible = true;
			
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
	public void render(float mouseX, float mouseY, float partialTicks) {

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
	public boolean isSelected(double mouseX, double mouseY) {
		double tempX = this.getPosX(); 
		double tempY = this.getPosY(); 
		return (((GuiConfig) this.gui).popupField == null || this.getIsPopupSegment()) && mouseX >= tempX - 4 && mouseY >= tempY - 4 && mouseX < tempX + this.getWidth() + 4&& mouseY < tempY + this.getHeight() + 4;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {

		if (this.isSelected(mouseX, mouseY)) {
			this.grabbed = true;
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

			if (this.isSelected(mouseX, mouseY)) {
				this.grabbed = false;
			if(!this.active) {
				
				
			if(Boolean.logicalXor(this.active, true)) {
					for (Segment child : this.parent.getWindow().getChildren()) {
						if(child instanceof PopupCheckboxSegment) {
							PopupCheckboxSegment checkButton = (PopupCheckboxSegment) child;
							checkButton.active = false;
						}
					}
			}
			this.active = Boolean.logicalXor(this.active, true);
			
			
			if(this.type == 0) {

				RowItem item = this.segment.list.get(this.id);
				ButtonCheckboxSegment but = (ButtonCheckboxSegment) item.childs[0];
				but.active = true;
				FileUtil.setActive(this.name, true);
				FileUtil.setOverride(this.name, false);
				SettingsButtonSegment set = (SettingsButtonSegment) item.childs[1];
				set.mark = false;
				File fileDir = new File(FileUtil.mcDataDir, "config");
				FileFilter ff = null;
				String arg = this.segment.searchbar.query;
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
			}else if(this.type == 1) {
				
				RowItem item = this.segment.list.get(this.id);
				ButtonCheckboxSegment but = (ButtonCheckboxSegment) item.childs[0];
				SettingsButtonSegment set = (SettingsButtonSegment) item.childs[1];
				set.mark = true;
				but.active = true;
				FileUtil.setActive(this.name, true);
				FileUtil.setOverride(this.name, true);
				File fileDir = new File(FileUtil.mcDataDir, "config");
				FileFilter ff = null;
				String arg = this.segment.searchbar.query;
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
				List<RowItem> rows = this.segment.list;
				int activeCount = 0;
				for (int i = 0; i < rows.size(); i++) {
					try {
						boolean active = FileUtil.getActives().contains(files[i].getName());
						if (active)
							activeCount++;
					}catch(ArrayIndexOutOfBoundsException e) {
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
				
			}else if(this.type == 2) {

				RowItem item = this.segment.list.get(this.id);
				ButtonCheckboxSegment but = (ButtonCheckboxSegment) item.childs[0];
				but.active = false;
				FileUtil.setActive(this.name, false);
				FileUtil.setOverride(this.name, false);
				SettingsButtonSegment set = (SettingsButtonSegment) item.childs[1];
				set.mark = false;
				File fileDir = new File(FileUtil.mcDataDir, "config");
				FileFilter ff = null;
				String arg = this.segment.searchbar.query;
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
				List<RowItem> rows = this.segment.list;
				int activeCount = 0;
				for (int i = 0; i < rows.size(); i++) {

					boolean active = FileUtil.getActives().contains(files[i].getName());
					if (active)
						activeCount++;

				}

				if (rows.size() != 0 && activeCount == rows.size()) {
					this.segment.cache_activity = 2;
				}else if (activeCount > 0)
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
	public void render(float mouseX, float mouseY, float partialTicks) {
		float alpha = ((GuiConfig) this.gui).popupField == null ? 1 : ((GuiConfig) this.gui).popupField.getWindow().alphaRate;
	
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

		int color = calcAlpha(0xff000000, alpha).getRGB();
		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GlStateManager.color(f, f1, f2, f3);

		Segment.drawCircle((float) this.getPosX() - 2, (float) this.getPosY() - 2, 3, 180, 75);

		Segment.drawCircle((float) this.getPosX() - 2, (float) this.getPosY() + this.height + 2, 3, 90, 75);

		Segment.drawCircle((float) this.getPosX() + this.width + 2, (float) this.getPosY() + this.height + 2, 3, 0, 75);

		Segment.drawCircle((float) this.getPosX() + this.width + 2, (float) this.getPosY() - 2, 3, 270, 75);

		Segment.drawRect(this.getPosX() - 5, this.getPosY() - 2, this.getPosX() + this.width + 5, this.getPosY() + this.height + 2, null, false, null, false);
		
		Segment.drawRect(this.getPosX() - 2, this.getPosY() + this.height + 2, this.getPosX() + this.width + 2, this.getPosY() + this.height + 5, null, false, null, false);
		
		Segment.drawRect(this.getPosX() - 2, this.getPosY() - 5, this.getPosX() + this.width + 2, this.getPosY() - 2, null, false, null, false);

		if (this.timer <= (Math.PI / 3)) {
			color = calcAlpha(0xffffffff, alpha).getRGB();
			f3 = (float) (color >> 24 & 255) / 255.0F;
			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;

			GlStateManager.color(f, f1, f2, f3);

			Segment.drawCircle((float) this.getPosX() - 1, (float) this.getPosY() - 1, 3, 180, 75);

			Segment.drawCircle((float) this.getPosX() - 1, (float) this.getPosY() + this.height + 1, 3, 90, 75);

			Segment.drawCircle((float) this.getPosX() + this.width + 1, (float) this.getPosY() + this.height + 1, 3, 0, 75);

			Segment.drawCircle((float) this.getPosX() + this.width + 1, (float) this.getPosY() - 1, 3, 270, 75);

			Segment.drawRect(this.getPosX() - 4, this.getPosY() - 1, this.getPosX() + width + 4, this.getPosY() + this.height + 1, null, false, null, false);
			
			Segment.drawRect(this.getPosX() - 1, this.getPosY() + this.height + 1, this.getPosX() + width + 1, this.getPosY() + this.height + 4, null, false, null, false);
			
			Segment.drawRect(this.getPosX() - 1, this.getPosY() - 4, this.getPosX() + width + 1, this.getPosY() - 1, null, false, null, false);

		}
		
		color = calcAlpha(0xfffe8518, alpha).getRGB();

		f3 = (float) (color >> 24 & 255) / 255.0F;
		f = (float) (color >> 16 & 255) / 255.0F;
		f1 = (float) (color >> 8 & 255) / 255.0F;
		f2 = (float) (color & 255) / 255.0F;

		GlStateManager.color(f, f1, f2, f3 - alphaRate);

		Segment.drawCircle((float) this.getPosX() - 1, (float) this.getPosY() - 1, 3, 180, 75);

		Segment.drawCircle((float) this.getPosX() - 1, (float) this.getPosY() + this.height + 1, 3, 90, 75);

		Segment.drawCircle((float) this.getPosX() + this.width + 1, (float) this.getPosY() + this.height + 1, 3, 0, 75);

		Segment.drawCircle((float) this.getPosX() + this.width + 1, (float) this.getPosY() - 1, 3, 270, 75);

		Segment.drawRect(this.getPosX() - 1, this.getPosY() - 4, this.getPosX() + width + 1, this.getPosY() - 1, null, false, null, false);

		Segment.drawRect(this.getPosX() - 1, this.getPosY() + this.height + 1, this.getPosX() + width + 1, this.getPosY() + this.height + 4, null, false, null, false);

		Segment.drawRect(this.getPosX() - 4, this.getPosY() - 1, this.getPosX() + width + 4, this.getPosY() + this.height + 1, null, false, null, false);

		if (this.timer > 0) {
			color = calcAlpha(0xffffffff, alpha).getRGB();

			f3 = (float) (color >> 24 & 255) / 255.0F;
			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;

			ScaledResolution scaledResolution = new ScaledResolution(MC);
			int scaleFactor = scaledResolution.getScaleFactor();

			Segment.drawLine2D(f, f1, f2, f3, scaleFactor, new Vec2f((float) this.getPosX() - 1, (float) this.getPosY() + 3.5F), new Vec2f((float) this.getPosX() + 4 - 1, (float) this.getPosY() + 4 + 3.5F), new Vec2f((float) this.getPosX() + 7 - 1, (float) this.getPosY() - 5 + 3.5F));
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
	}

}