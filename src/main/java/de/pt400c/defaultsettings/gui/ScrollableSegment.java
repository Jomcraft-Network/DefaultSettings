package de.pt400c.defaultsettings.gui;

import static de.pt400c.defaultsettings.FileUtil.MC;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.pt400c.defaultsettings.FileUtil;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

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
		this.list = getRowList(null);
		this.bulkAction = new ButtonBulkActionSegment(this.gui, posX + 82, posY + 5, 6, 6, this);
		this.searchbar = new SearchbarSegment(gui, posX + 112, posY - 1, 45, 18, false, this);
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

						if (!file.getName().equals("defaultsettings") && !file.getName().equals("defaultsettings.json") && !file.getName().equals("keys.txt") && !file.getName().equals("options.txt") && !file.getName().equals("optionsof.txt") && !file.getName().equals("servers.dat") && file.getName().toLowerCase().startsWith(arg[0].toLowerCase()))
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
				rows.add(new RowItem(files[i].getName(), new ButtonCheckboxSegment(gui, 132, yOffTemp + 43, 6, 6, files[i].getName(), false, this, active)));
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

		GL11.glColor4f(f, f1, f2, f3);

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

		ScaledResolution scaledResolution = new ScaledResolution(MC, MC.displayWidth, MC.displayHeight);
		int scaleFactor = scaledResolution.getScaleFactor();

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor((int) (this.getPosX() * scaleFactor), (int) ((scaledResolution.getScaledHeight() - this.getPosY() - this.getHeight()) * scaleFactor), (int) (this.getWidth() * scaleFactor), (int) (this.getHeight() * scaleFactor));

		for (int i = 0; i < this.list.size(); i++) {
			int yOffTemp = 18 + 20 * i + add;

			if (yOffTemp < -3)
				continue;
			if (yOffTemp > this.height + 18)
				break;
			
			String text = this.list.get(i).displayString;
			int dots = MC.fontRenderer.getStringWidth("...");

			int widthString = MC.fontRenderer.getStringWidth(text);

			if (widthString >= (width - 35)) {

				MC.fontRenderer.drawString(MC.fontRenderer.trimStringToWidth(text, (int) ((width - 35) - 1 - dots)) + "...", (int) this.getPosX() + 23, (int) this.getPosY() + yOffTemp - 13, 0x0, false);

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
			MenuScreen lel = ((GuiConfig) this.gui).menu;
			lel.getVariants().get(lel.index).selected = null;
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

			for (Segment segment : this.list.get(i).childs) {
				if (segment.mouseReleased(mouseX, mouseY, button))
					return true;

			}

		}
		return this.scrollBar.mouseReleased(mouseX, mouseY, button) ? true : this.bulkAction.mouseReleased(mouseX, mouseY, button) ? true : this.searchbar.mouseReleased(mouseX, mouseY, button);
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