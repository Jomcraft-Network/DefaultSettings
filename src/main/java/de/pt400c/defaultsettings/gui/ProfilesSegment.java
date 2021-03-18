package de.pt400c.defaultsettings.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static de.pt400c.defaultsettings.DefaultSettings.fontRenderer;
import java.util.function.Function;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import static de.pt400c.neptunefx.NEX.*;
import de.pt400c.defaultsettings.DefaultSettings;
import de.pt400c.defaultsettings.FileUtil;
import de.pt400c.defaultsettings.GuiConfig;
import de.pt400c.defaultsettings.gui.MathUtil.Vec2f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

@SideOnly(Side.CLIENT)
public class ProfilesSegment extends Segment {

	protected boolean grabbed;
	protected float add = 0;
	public List<RowItem> list = new ArrayList<RowItem>();
	private boolean invisible;
	private final ScrollbarSegment scrollBar;
	public final SearchbarSegment searchbar;
	private float distanceY = 0;
	private float maxSize = 0;
	private float velocity = 0;
	private final Function<GuiConfig, Integer> widthF;
	private final Function<GuiConfig, Integer> heightF;
	static final int row = 15;
	public ArrayListCaseless profiles = new ArrayListCaseless();
	public final ContextMenuSegment context;
	public String selectedName = FileUtil.privateJson.currentProfile;

	public ProfilesSegment(GuiScreen gui, float posX, float posY, Function<GuiConfig, Integer> width, Function<GuiConfig, Integer> height) {	
		super(gui, posX, posY, width.apply((GuiConfig) gui), height.apply((GuiConfig) gui), false);
		this.scrollBar = new ScrollbarSegment(this.gui, (float) (this.posX + this.width), (float) posY, 6, 20, this);
		this.list = getRowList(null);
		
		this.context = new ContextMenuSegment(this.gui, this, 0, 0, 87, 62);
		this.searchbar = new SearchbarSegment(gui, posX + 96, posY - 1, 40, 18, false, this);
		this.widthF = width;
		this.heightF = height;
	}
	
	@Override
	public void init() {
		this.list = getRowList(null);
	}
	
	public List<RowItem> getRowList(String[] arg) {

		List<RowItem> rows = new ArrayList<RowItem>();
		File fileDir = FileUtil.getMainFolder();

		int i = 0;
		this.profiles.clear();
		for (File file : fileDir.listFiles()) {
			if (!file.isDirectory() || (arg != null && arg.length != 0 && !file.getName().toLowerCase().startsWith(arg[0].toLowerCase())) || file.getName().equals("sharedConfigs"))
				continue;
			float yOffTemp = row - 0.5F + row * i;
			i++;
			this.profiles.add(file.getName());
			rows.add(new RowItem(file.getName(), new RadioButtonSegment(gui, 104, yOffTemp + 46.5F, 2.5F, 2.5F, file.getName(), false, this)));
		}

		return rows;
	}
	
	@Override
	public boolean handleMouseInput() {
		if(this.context.openContext)
			return false;
		
		final float scroll = Mouse.getEventDWheel();
		this.maxSize = row - 0.5F + row * (this.list.size() - 1);
		if(!this.invisible)
			this.velocity += scroll / 120F;
		return true;
	}
	
	@Override
	public void guiContentUpdate(String... arg){
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

			}
		}
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {

		if (resized != this.resized_mark) {
			width = widthF.apply((GuiConfig) this.gui);
			height = heightF.apply((GuiConfig) this.gui);
			this.resized_mark = resized;
			this.context.openContext = false;
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

				float tempAdd = this.getPosY() + height - 1 - row * (this.list.size() - 1) - this.getPosY() - row
						- 0.5F;
				this.velocity = 0;
				this.add = tempAdd;

			}
		}

		if (this.velocity > 0) {
			this.velocity -= 1;
			if (this.velocity - 1 == 0)
				this.velocity -= 1;
		} else if (this.velocity < 0) {
			this.velocity += 1;
			if (this.velocity + 1 == 0)
				this.velocity += 1;
		}

		this.maxSize = row - 0.5F + row * (this.list.size() - 1);
		int color = 0xffe6e6e6;

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

		if (this.grabbed && !this.context.openContext) {
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
		glScissor((int) (this.getPosX() * scaleFactor), (int) ((float) (scaledresolution.getScaledHeight() - this.getPosY() - this.getHeight() - 1F) * scaleFactor), (int) (this.getWidth() * scaleFactor), (int) ((float) (this.getHeight() + 1F) * scaleFactor));
		boolean hover = false;
		for (int i = 0; i < this.list.size(); i++) {
			final float yOffTemp = row - 0.5F + row * i + add;

			if (yOffTemp < -3)
				continue;
			if (yOffTemp > this.height + row - 0.5F)
				break;

			String text = this.list.get(i).displayString;
			if(FileUtil.deleted.contains(text))
				text = "\u00A7c"+text;

			final float dots = fontRenderer.getStringWidth("...", 1, false);

			final float widthString = fontRenderer.getStringWidth(text, 1, false);
			glEnable(GL_BLEND);
			glDisable(GL_ALPHA_TEST);
			glDisable(GL_TEXTURE_2D);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			
			if (this.profiles.get(i).equals(this.context.id)) 
				drawRectRoundedCorners(this.getPosX() + 19, this.getPosY() + yOffTemp - 13.5F, this.getPosX() + this.getWidth() - 5, this.getPosY() + yOffTemp - 1F, calcAlpha(0xff8b8b8b, 1 - this.context.alpha).getRGB(), 2);

			if (this.profiles.get(i).equals(FileUtil.mainJson.mainProfile)) {

				color = 0xff2185d7;

				float f = (float) (color >> 16 & 255) / 255.0F;
				float f1 = (float) (color >> 8 & 255) / 255.0F;
				float f2 = (float) (color & 255) / 255.0F;

				glColor3f(f, f1, f2);
				color = 0xffe6e6e6;
				drawCircle(this.getPosX(), this.getPosY() + yOffTemp - 7.25F, 2, 270, 50);

				if (distanceBetweenPoints(this.getPosX(), this.getPosY() + yOffTemp - 7.25F, mouseX, mouseY) <= 3.5F)
					hover = true;

			}
			glEnable(GL_TEXTURE_2D);
			glDisable(GL_BLEND);
			glEnable(GL_ALPHA_TEST);

			if (widthString >= (width - 40)) 
				fontRenderer.drawString( fontRenderer.trimStringToWidth(text, (int) ((width - 40) - 1 - dots), false) + "...", (int) this.getPosX() + 23, (int) this.getPosY() + yOffTemp - 11.5F, 0xffe6e6e6, 1.0F, false);
			else 
				fontRenderer.drawString(text, (int) this.getPosX() + 23, (int) this.getPosY() + yOffTemp - 11.5F, 0xffe6e6e6, 1.0F, false);

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

		if (!this.invisible)
			drawRect(this.getPosX() + this.width - 3, this.getPosY(), this.getPosX() + this.width - 2,
					this.getPosY() + this.getHeight(), 0xffe6e6e6, true, null, false);

		glEnable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		drawRectRoundedCornersHollow(this.getPosX() - 4, this.getPosY() - 4,
				(float) this.getPosX() + (this.invisible ? this.width : this.width + this.scrollBar.getWidth() + 2) + 4,
				this.getPosY() + this.height + 4, color, 10, 6);

		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);

		if (!this.invisible)
			this.scrollBar.render(mouseX, mouseY, partialTicks);

		this.searchbar.render(mouseX, mouseY, partialTicks);

		this.context.render(mouseX, mouseY, partialTicks);

		final ArrayList<String> lines = new ArrayList<String>();

		if (hover) {
			String txt = "Main Profile, default to be used";
			float textWidth = (int) (mouseX + 12 + fontRenderer.getStringWidth(txt, 0.8F, true));
			if (textWidth > this.gui.width) {
				lines.addAll(fontRenderer.listFormattedStringToWidth(txt, (int) (this.gui.width - mouseX - 12), true));
			} else {
				lines.add(txt);
			}
			textWidth = 0;
			for (String line : lines) {

				if (fontRenderer.getStringWidth(line, 0.8F, true) > textWidth)
					textWidth = fontRenderer.getStringWidth(line, 0.8F, true);
			}

			drawButton(mouseX + 5, mouseY - 7 - 10 * lines.size(), mouseX + 15 + textWidth, mouseY - 3, 0xff3a3a3a, 0xffdcdcdc, 2);
			int offset = 0;

			Collections.reverse(lines);

			for (String line : lines) {

				fontRenderer.drawString(line, (float) (mouseX + 9), (float) (mouseY - 14 - offset), 0xff3a3a3a, 0.8F, true);
				offset += 10;
			}
		}

	}
	
	
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {
		if (this.isSelected(mouseX, mouseY)) {
			
			if(!this.context.isSelected(mouseX, mouseY))
				this.context.openContext = false;

			((DefaultSettingsGUI) this.gui).setSelected(this);

			for (int i = 0; i < this.list.size(); i++) {
				float yOffTemp = row * i + row - 0.5F + add;

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
			return this.scrollBar.mouseClicked(mouseX, mouseY, button) ? true : this.searchbar.mouseClicked(mouseX, mouseY, button);
		}
	}
	
	public void openContext(int mouseX, int mouseY, String id) {
		this.context.setPos(mouseX, mouseY);
		this.context.id = id;
		this.context.openContext = true;
	}

	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int button) {
		this.grabbed = false;

		if(button == 1 && !this.context.openContext) {

			int clickedID = (int) Math.ceil(-(add-(mouseY - getPosY() + 0.5F)) / row - 1F);
			
			int posX = mouseX <= this.posX + this.getWidth() - 80 ? mouseX : mouseX - 85;
			int posY = mouseY <= 21 + this.posY + this.getHeight() - 85 ? mouseY : mouseY - 60;
			
			if(this.profiles.size() - 1 >= clickedID)
				this.openContext(posX, posY, this.profiles.get(clickedID));
		}
		
		if(this.context.mouseReleased(mouseX, mouseY, button))
			return true;
	
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
		return this.searchbar.mouseReleased(mouseX, mouseY, button);
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
		return this.scrollBar.mouseDragged(mouseX, mouseY, button) ? true : this.searchbar.mouseDragged(mouseX, mouseY, button);
	}
	
	@SideOnly(Side.CLIENT)
	public class ScrollbarSegment extends ButtonSegment {
		
		protected boolean grabbed;
		
		private final ProfilesSegment superScrollable;
		
		private double distanceY = 0;

		public ScrollbarSegment(GuiScreen gui, float posX, float posY, int width, int height, ProfilesSegment segment) {
			super(gui, posX, posY, null, null, width, height, 0);
			this.superScrollable = segment;
		}
		
		@Override
		public boolean isSelected(int mouseX, int mouseY) {
			return mouseX >= this.getPosX() && mouseY >= this.getPosY() + 3 && mouseX < this.getPosX() + this.getWidth() && mouseY < this.getPosY() + this.getHeight() - 3;
		}

		@Override
		public void render(int mouseX, int mouseY, float partialTicks) {

			if (this.grabbed) {
				final int factor = (int) (mouseY - distanceY);

				this.posY = factor;

				if (this.posY < this.superScrollable.posY)
					this.posY = this.superScrollable.posY;

				if (this.posY > this.superScrollable.posY + this.superScrollable.height - height)
					this.posY = this.superScrollable.posY + this.superScrollable.height - height;

				final float distance = Math.round(this.superScrollable.height - height);
				final float pos = Math.round(this.posY - this.superScrollable.posY);

				final float tempAdd = (int) (this.superScrollable.getPosY() + this.superScrollable.height - 1 - ScrollableSegment.row * (this.superScrollable.list.size() - 1) - this.superScrollable.getPosY() - ScrollableSegment.row - 0.5F);

				this.superScrollable.add = (int) (tempAdd * (pos / distance));

			} else {
				distanceY = 0;
			}

			glDisable(GL_TEXTURE_2D);
			glEnable(GL_BLEND);
			glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
			
			drawRectRoundedCorners(this.getPosX(), this.getPosY() + 3, this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight() - 3, 0xffe0e0e0, 800);
			drawLine2D_2(0.2F, 0.2F, 0.2F, 1, scaledresolution.getScaleFactor(), 2, new Vec2f(this.getPosX() + this.width / 2 - 2F + 0.5F, this.getPosY() + this.height / 2 - 3), new Vec2f(this.getPosX() + this.width / 2 + 2F - 0.5F, this.getPosY() + this.height / 2 - 3));
			
			drawLine2D_2(0.2F, 0.2F, 0.2F, 1, scaledresolution.getScaleFactor(), 2, new Vec2f(this.getPosX() + this.width / 2 - 2F + 0.5F, this.getPosY() + this.height / 2), new Vec2f(this.getPosX() + this.width / 2 + 2F - 0.5F, this.getPosY() + this.height / 2));
			
			drawLine2D_2(0.2F, 0.2F, 0.2F, 1, scaledresolution.getScaleFactor(), 2, new Vec2f(this.getPosX() + this.width / 2 - 2F + 0.5F, this.getPosY() + this.height / 2 + 3), new Vec2f(this.getPosX() + this.width / 2 + 2F - 0.5F, this.getPosY() + this.height / 2 + 3));
			glDisable(GL_BLEND);
			glEnable(GL_TEXTURE_2D);
		
		}
		
		@Override
		public boolean mouseClicked(int mouseX, int mouseY, int button) {

			if (this.isSelected(mouseX, mouseY)) {
				this.grabbed = true;
				((DefaultSettingsGUI) this.gui).resetSelected();
				this.distanceY += (int) (mouseY - this.posY);
				
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean mouseDragged(int mouseX, int mouseY, int button) {
			return super.mouseDragged(mouseX, mouseY, button);
		}

		@Override
		public boolean mouseReleased(int mouseX, int mouseY, int button) {
			if (this.grabbed) 
					this.grabbed = false;
					
			return super.mouseReleased(mouseX, mouseY, button);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public class SearchbarSegment extends Segment {
		
		protected boolean grabbed;
		public String query = "";
		protected boolean focused = false;
		private int cursorTimer = 0;
		boolean activated;
		private float flashingTimer = 0;
		private final ResourceLocation icon;
		int cursorPosition;
		protected final ProfilesSegment parent;
		private static final String chars = "@^°\"§$%&/()=?`´\\#+*'-}][{-_~";

		public SearchbarSegment(GuiScreen gui, float posX, float posY, int width, int height, boolean popupSegment, ProfilesSegment parent) {
			super(gui, posX, posY, width, height, popupSegment);
			this.parent = parent;
			this.icon = new ResourceLocation(DefaultSettings.MODID, "textures/gui/glass.png");
		}
		
		public boolean isAllowedCharacter(char character)
	    {
	        return character != 167 && character >= ' ' && character != 127 && chars.indexOf(character) == -1;
	    }
		
		@Override
		protected boolean keyTyped(char typedChar, int keyCode) {
			if (isAllowedCharacter(typedChar)) {
				final String s1 = ChatAllowedCharacters.filerAllowedCharacters(Character.toString(typedChar));
				if (this.query.isEmpty() && s1.equals(" "))
					return true;

				if(this.query.length() > 30)
					return false;
				
				String left = this.query.substring(0, cursorPosition);
				String right = this.query.substring(cursorPosition);
				
				++this.cursorPosition;
				
				this.query = left + s1 + right;

				this.activated = false;
				return true;

			} else if (keyCode == Keyboard.KEY_BACK) {
				if(this.query.length() < 1 || this.cursorPosition < 1)
					return false;
				
				String left = this.query.substring(0, cursorPosition);
				String right = this.query.substring(cursorPosition);
				
				left = left.substring(0, left.length() - 1);
				
				if(this.cursorPosition > 0)
					this.cursorPosition -= 1;
				
				this.query = left + right;

				this.activated = false;
				return true;
			} else if (keyCode == Keyboard.KEY_LEFT) {
				this.cursorPosition = MathUtil.clamp(this.cursorPosition - 1, 0, this.query.length());
				return true;
			} else if (keyCode == Keyboard.KEY_RIGHT) {
				this.cursorPosition = MathUtil.clamp(this.cursorPosition + 1, 0, this.query.length());
				return true;
			} else if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
				if (!this.query.isEmpty()) {
					this.activated = true;

				}
				this.sendQuery();

				return true;
			} else {
				return false;
			}
		}

		private void sendQuery() {
			parent.add = 0;
			parent.guiContentUpdate(this.query);
		}

		@Override
		public void render(int mouseX, int mouseY, float partialTicks) {
			glEnable(GL_BLEND);
			glDisable(GL_TEXTURE_2D);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			flashingTimer += 0.07;
			final float darken = (float) ((Math.sin(flashingTimer - MathUtil.PI / 2) + 1) / 4 + 0.5);

			int color = 0;

			this.width = MathUtil.clamp(fontRenderer.getStringWidth(this.query, 1, false) + 15, 40, this.gui.width - 240);

			String text = this.query;
			float dots = fontRenderer.getStringWidth("...", 1, false);

			float widthString = fontRenderer.getStringWidth(text, 1, false);

			if (widthString >= this.gui.width - 250) 
				text = fontRenderer.trimStringToWidth(text, (int) (this.gui.width - 250 - 1 - dots), false) + "...";
			
			MenuScreen menu = ((GuiConfig) this.gui).menu;

			if (menu.getVariants().get(menu.index).selected == this)
				this.focused = true;
			else
				this.focused = false;

			float diff = this.focused ? 1.5F : 1;

			drawRectRoundedCorners(this.getPosX() - diff, this.getPosY() - diff, this.getPosX() + this.getWidth() + diff, this.getPosY() + this.getHeight() + diff, 0xffe6e6e6, Integer.MAX_VALUE);
			
			drawRectRoundedCorners(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), 0xff3c3c3c, Integer.MAX_VALUE);

			glEnable(GL_TEXTURE_2D);
			
			Minecraft.getMinecraft().getTextureManager().bindTexture(icon);
			glColor3f(1, 1, 1);
			drawScaledTex(this.getPosX() - 18, this.getPosY() + 2, 15, 15);
			
			glDisable(GL_TEXTURE_2D);

			this.cursorTimer++;
			if (this.cursorTimer > 80)
				this.cursorTimer = 0;

			if (this.cursorTimer <= 40 && this.focused) {

				color = 0xffa0a0a0;

				float f3 = (float) (color >> 24 & 255) / 255.0F;
				float f = (float) (color >> 16 & 255) / 255.0F;
				float f1 = (float) (color >> 8 & 255) / 255.0F;
				float f2 = (float) (color & 255) / 255.0F;

				glColor4f(f, f1, f2, f3);

				drawRect(this.getPosX() + 5 + fontRenderer.getStringWidth(text.substring(0, this.cursorPosition), 1, false), this.getPosY() + 4, this.getPosX() + 5.5F + fontRenderer.getStringWidth(text.substring(0, this.cursorPosition), 1, false), this.getPosY() + this.getHeight() - 4, null, false, null, false);
			}
			
			glDisable(GL_BLEND);
			glEnable(GL_TEXTURE_2D);
			glPushMatrix();
			glEnable(GL_BLEND);
			glBlendFuncSeparate(770, 771, 1, 0);

			if (this.query.isEmpty())
				fontRenderer.drawString("Query", (float) (this.getPosX() + 5), (float) (this.getPosY() + 5), this.focused && !this.activated ? darkenColor(0xffe6e6e6, darken).getRGB() : 0xffe6e6e6, 1, false);
			else
				fontRenderer.drawString(text, (float) (this.getPosX() + 5), (float) (this.getPosY() + 5), 0xffe6e6e6, 1, false);
			glDisable(GL_BLEND);
			glPopMatrix();
		}

		@Override
		public boolean mouseClicked(int mouseX, int mouseY, int button) {

			if (this.isSelected(mouseX, mouseY)) {
				MenuScreen menu = ((GuiConfig) this.gui).menu;
				menu.getVariants().get(menu.index).selected = this;
				this.grabbed = true;

				if(this.query.isEmpty())
					return true;
				
				this.cursorPosition = fontRenderer.trimStringToWidth(this.query, (int)(mouseX - (this.getPosX() + this.hitX + 5)), false).length();
				
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean mouseDragged(int mouseX, int mouseY, int button) {
			if (!this.isSelected(mouseX, mouseY))
				this.grabbed = false;
			
			return super.mouseDragged(mouseX, mouseY, button);
		}

		@Override
		public boolean mouseReleased(int mouseX, int mouseY, int button) {
			if (this.grabbed) {
				if (this.isSelected(mouseX, mouseY)) 
					this.grabbed = false;

			}
			return super.mouseReleased(mouseX, mouseY, button);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public class RadioButtonSegment extends Segment {
		
		protected final String name;
		protected boolean grabbed;
		public float timer = 0;
		private float offX;
		private float offY;
		private final ProfilesSegment parent;

		public RadioButtonSegment(GuiScreen gui, float posX, float posY, float width, float height, String name, boolean popupSegment, ProfilesSegment parent) {
			super(gui, posX, posY, width, height, popupSegment);
			this.name = name;
			this.parent = parent;
			timer = this.parent.selectedName.equals(name) ? MathUtil.PI / 3 : 0;
		}
		
		@Override
		public void customRender(int mouseX, int mouseY, float customPosX, float customPosY, float partialTicks) {

			if (this.parent.selectedName.equals(this.name)) {

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
			
			float f = (float) (color >> 16 & 255) / 255.0F;
			float f1 = (float) (color >> 8 & 255) / 255.0F;
			float f2 = (float) (color & 255) / 255.0F;
			
			glColor3f(f, f1, f2);
			
			drawCircle(customPosX + width / 2, customPosY + width / 2, 6F, 0, 0);

			color = 0xff282828;
			
			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;
			
			glColor3f(f, f1, f2);
			
			drawCircle(customPosX + width / 2, customPosY + width / 2, 5F, 0, 0);

			if (this.timer <= MathUtil.PI / 3) 
				color = 0xff282828;

			color = 0xfffe8518;
			int value = (int) ((((color >> 24 & 255) / 255.0F) - alphaRate) * 255F) ;
			
			color = ((value & 0x0ff) << 24) | (((color >> 16 & 255) & 0x0ff) << 16) | (((color >> 8 & 255) & 0x0ff) << 8) | ((color & 255) & 0x0ff);
			
			f = (float) (color >> 16 & 255) / 255.0F;
			f1 = (float) (color >> 8 & 255) / 255.0F;
			f2 = (float) (color & 255) / 255.0F;
			float f3 = (float) (color >> 24 & 255) / 255.0F;
			
			glColor4f(f, f1, f2, f3);
			
			drawCircle(customPosX + width / 2, customPosY + width / 2, 3F, 0, 0);

			glDisable(GL_BLEND);
			glEnable(GL_TEXTURE_2D);
		}
		
		@Override
		public boolean isSelected(int mouseX, int mouseY) {
			float tempX = this.getPosX() + this.offX + this.hitX; 
			float tempY = this.getPosY() + this.offY + this.hitY; 
			return (((GuiConfig) this.gui).popupField == null || this.getIsPopupSegment()) && mouseX >= tempX - 4 && mouseY >= tempY - 4 && mouseX < tempX + this.getWidth() + 4 && mouseY < tempY + this.getHeight() + 4;
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

				this.parent.selectedName = this.name;
				
				if(!FileUtil.privateJson.currentProfile.equals(this.name)) {
					
					FileUtil.privateJson.targetProfile = this.name;
					GuiConfig gui = ((GuiConfig) this.gui);
					gui.popup.setOpening(true);
					gui.popup.getWindow().title = "Profiles Warning";
					gui.popup.getWindow().setPos(this.width / 2 - 210 / 2, this.height / 2 - 100 / 2);
					gui.popupField = gui.popup;
					gui.popupField.getWindow().clearChildren();
					
					gui.popupField.getWindow().addChild(new TextSegment(gui, 5, 30, 20, 20, "The new profile will be loaded by\nnext game's startup\n\n\u00a7cThus editing this profile requires\n\u00a7ca restart", 0xffffffff, true));
					gui.popupField.getWindow().addChild(new QuitButtonSegment(gui, 190, 5, 14, 14, button2 -> {

						gui.popupField.setOpening(false);

						return true;
					}, 3F, true));

					gui.popupField.getWindow().addChild(new ButtonRoundSegment(gui, 105 - 30, 75, 60, 20, "Okay", null, button2 -> {

						gui.popupField.setOpening(false);

						return true;
					}, 0.8F, true));

					gui.popup.setVisible(true);
					
					FileUtil.privateJson.save();
					
				}
				
				this.clickSound();
				return true;

			}
			return false;
		}

		@Override
		public void render(int mouseX, int mouseY, float partialTicks) {

		}
	}
	
	public static class AddSegment extends ButtonSegment {

		private final Function<GuiConfig, Integer> posXF;
		private RenameSegment nameField;
		
		public AddSegment(GuiScreen gui, Function<GuiConfig, Integer> posX, float posY, int width, int height, boolean popup) {
			super(gui, posX.apply((GuiConfig) gui), posY, null, null, width, height, 0, popup);
			this.posXF = posX;
		}

		@Override
		public void render(int mouseX, int mouseY, float partialTicks) {

			if(resized != this.resized_mark && posXF != null) {
				posX = posXF.apply((GuiConfig) this.gui);
				this.resized_mark = resized;
			}
			
			glPushMatrix();
			glEnable(GL_BLEND);
			glDisable(GL_TEXTURE_2D);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			
			int color = 0xff47b832;
			
			if(isSelected(mouseX, mouseY))
				color = 0xff3c9d2b;
			
			float f = (float) (color >> 16 & 255) / 255.0F;
			float f1 = (float) (color >> 8 & 255) / 255.0F;
			float f2 = (float) (color & 255) / 255.0F;
			
			glColor3f(f, f1, f2);
			drawCircle(this.posX + this.width / 2, this.posY + this.height / 2, this.width / 2, 0, 0);

			final int scaleFactor = scaledresolution.getScaleFactor();
	     	drawLine2D_2(1, 1, 1, 1, scaleFactor, 3.0F, new Vec2f(posX + width / 2, posY + 3), new Vec2f(posX + width / 2, posY + height - 3));
	     	drawLine2D_2(1, 1, 1, 1, scaleFactor, 3.0F, new Vec2f(posX + 3, posY + height / 2), new Vec2f(posX + width -3, posY + height / 2));
	    
			glDisable(GL_BLEND);
			glEnable(GL_TEXTURE_2D);
			glPopMatrix();
			
		}
		
		@Override
		public boolean mouseReleased(int mouseX, int mouseY, int button) {
			if (this.grabbed) {
				if (this.isSelected(mouseX, mouseY))
					this.grabbed = false;

					this.clickSound();
					
					GuiConfig gui = ((GuiConfig) this.gui);
					gui.popup.setOpening(true);
					gui.popup.getWindow().title = "New Profile";
					gui.popup.getWindow().setPos(this.width / 2 - 210 / 2, this.height / 2 - 100 / 2);
					gui.popupField = gui.popup;
					gui.popupField.getWindow().clearChildren();
					
					gui.popupField.getWindow().addChild(this.nameField = new RenameSegment(gui, "", 10, 35, 190, 18, true));
					gui.popupField.getWindow().addChild(new QuitButtonSegment(gui, 190, 5, 14, 14, button2 -> {

						gui.popupField.setOpening(false);

						return true;
					}, 3F, true));
					
					MenuScreen menu = ((GuiConfig) this.gui).menu;
					menu.getVariants().get(menu.index).selected = this.nameField;

					gui.popupField.getWindow().addChild(new ButtonRoundSegment(gui, 105 - 30, 75, 60, 20, "Okay", null, button2-> {
							if (!gui.scrollableProfiles.profiles.contains(this.nameField.query.toLowerCase())) {

								gui.scrollableProfiles.context.backgroundTimer = 2.5F * (MathUtil.PI / 3);
								gui.scrollableProfiles.context.setPos(-100, -100);

								File fileDir = new File(FileUtil.getMainFolder(), this.nameField.query);
								fileDir.mkdir();
								gui.scrollableProfiles.guiContentUpdate(gui.scrollableProfiles.searchbar.query);

								gui.scrollableProfiles.context.id = this.nameField.query;
								gui.popupField.setOpening(false);

							} else {
								this.nameField.denied = true;
							}
					
						return true;
					}, 0.8F, true));

					gui.popup.setVisible(true);

			}
			return super.mouseReleased(mouseX, mouseY, button);
		}
		
	}
	
	public static class ArrayListCaseless extends ArrayList<String> {

		private static final long serialVersionUID = 1366320704613211176L;

		@Override
	    public boolean contains(Object o) {
	        String str = (String) o;
	        for (String s : this) {
	            if (str.equalsIgnoreCase(s)) return true;
	        }
	        return false;
	    }
	}
}