package de.pt400c.defaultsettings.gui;

import de.pt400c.defaultsettings.DefaultSettings;
import de.pt400c.defaultsettings.GuiConfig;
import static de.pt400c.defaultsettings.FileUtil.MC;
import static org.lwjgl.glfw.GLFW.*;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static de.pt400c.defaultsettings.DefaultSettings.fontRenderer;
import static de.pt400c.neptunefx.NEX.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL11.*;

@OnlyIn(Dist.CLIENT)
public class SearchbarSegment extends Segment {
	
	protected boolean grabbed;
	public String query = "";
	protected boolean focused = false;
	private int cursorTimer = 0;
	boolean activated;
	private float flashingTimer = 0;
	private final ResourceLocation icon;
	protected final ScrollableSegment parent;
	private static final String chars = "@^\"$%&/()=?`\\#+*'-}][{-_~";

	public SearchbarSegment(Screen gui, float posX, float posY, int width, int height, boolean popupSegment, ScrollableSegment parent) {
		super(gui, posX, posY, width, height, popupSegment);
		this.parent = parent;
		this.icon = new ResourceLocation(DefaultSettings.MODID, "textures/gui/glass.png");
	}
	
	public static boolean isAllowedCharacter(char character)
    {
        return character != 167 && character >= ' ' && character != 127 && chars.indexOf(character) == -1;
    }
	
	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (p_keyPressed_1_ == GLFW_KEY_BACKSPACE) {
			if (this.query.length() > 0)
				this.query = this.query.substring(0, this.query.length() - 1);
			this.activated = false;
			return true;
		} else if (p_keyPressed_1_ == GLFW_KEY_ENTER || p_keyPressed_1_ == GLFW_KEY_KP_ENTER) {
			if (!this.query.isEmpty()) {
				this.activated = true;

			}
			this.sendQuery();

			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
		if (isAllowedCharacter(p_charTyped_1_)) {
			final String s1 = SharedConstants.filterAllowedCharacters(Character.toString(p_charTyped_1_));
			if (this.query.isEmpty() && s1.equals(" "))
				return true;

			this.query += s1;
			this.activated = false;
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
		final float darken = (float) ((Math.sin(flashingTimer - Math.PI / 2) + 1) / 4 + 0.5);

		int color = 0;

		this.width = MathUtil.clamp(fontRenderer.getStringWidth(this.query, 1, false) + 15, 40, this.gui.width - 180);

		String text = this.query;
		float dots = fontRenderer.getStringWidth("...", 1, false);

		float widthString = fontRenderer.getStringWidth(text, 1, false);

		if (widthString >= this.gui.width - 190) 
			text = fontRenderer.trimStringToWidth(text, (int) (this.gui.width - 190 - 1 - dots), false) + "...";
		
		MenuScreen menu = ((GuiConfig) this.gui).menu;

		if (menu.getVariants().get(menu.index).selected == this)
			this.focused = true;
		else
			this.focused = false;

		float diff = this.focused ? 1.5F : 1;

		drawRectRoundedCorners(this.getPosX() - diff, this.getPosY() - diff, this.getPosX() + this.getWidth() + diff, this.getPosY() + this.getHeight() + diff, 0xffe6e6e6, Integer.MAX_VALUE);
		
		drawRectRoundedCorners(this.getPosX(), this.getPosY(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeight(), 0xff3c3c3c, Integer.MAX_VALUE);

		glEnable(GL_TEXTURE_2D);
		
		MC.getTextureManager().bindTexture(icon);
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

			drawRect(this.getPosX() + 5 + fontRenderer.getStringWidth(text, 1, false), this.getPosY() + 4, this.getPosX() + 5.5F + fontRenderer.getStringWidth(text, 1, false), this.getPosY() + this.getHeight() - 4, null, false, null, false);
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