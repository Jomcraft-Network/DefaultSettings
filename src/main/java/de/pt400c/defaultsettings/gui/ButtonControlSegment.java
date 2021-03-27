package de.pt400c.defaultsettings.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static de.pt400c.defaultsettings.FileUtil.MC;
import static de.pt400c.neptunefx.NEX.*;
import java.util.ArrayList;
import static de.pt400c.defaultsettings.DefaultSettings.fontRenderer;
import java.util.Collections;
import java.util.function.Function;

import com.mojang.blaze3d.platform.GlStateManager;

import de.pt400c.defaultsettings.DefaultSettings;
import de.pt400c.defaultsettings.FileUtil;
import static org.lwjgl.opengl.GL11.*;

@OnlyIn(Dist.CLIENT)
public class ButtonControlSegment extends BakedSegment {
	
	private final Function<Segment, Float> posXF;
	private final Function<Segment, Float> posYF;
	protected boolean grabbed;
	protected boolean grabbed_prev;
	private Segment parent;
	final int id;
	public float animTimer = 0;
	public float animTimerRight = 0;
	private float timer;
	private static final ResourceLocation icon = new ResourceLocation(DefaultSettings.MODID, "textures/gui/trash.png");
    private float processFactor;
    private final String hoverMsg;
    private boolean doIt;
    private boolean init;
    private final Function<ButtonControlSegment, Boolean> function;
	private boolean selected_prev;
	private boolean deleting;
	private boolean left;
	public int color;
	public final String title;
	public int color_prev;
	
	public ButtonControlSegment(Screen gui, Function<Segment, Float> posX, Function<Segment, Float> posY, float width, float height, Segment parent, int id, String title, String hoverMsg, Function<ButtonControlSegment, Boolean> function) {
		super(gui, 0, posX.apply(parent), posY.apply(parent), width, height, 44, 44, 44, true, false);
		this.posYF = posY;
		this.id = id;
		this.posXF = posX;
		this.function = function;
		this.hoverMsg = hoverMsg;
		this.title = title;
		this.parent = parent;
	}
	
	@Override
	public boolean isSelected(int mouseX, int mouseY) {
		boolean general = (((DefaultSettingsGUI) this.gui).popupField == null || this.getIsPopupSegment()) && ((distanceBetweenPoints(this.getPosX() + this.getHeight() / 2F, this.getPosY() + this.getHeight() / 2F, mouseX, mouseY) <= this.getHeight() / 2F) || (distanceBetweenPoints(this.getPosX() + this.getWidth() - this.getHeight() / 2F, this.getPosY() + this.getHeight() / 2F, mouseX, mouseY) <= this.getHeight() / 2F) || (mouseX >= this.getPosX() + (height / 2F) && mouseY >= this.getPosY() && mouseX < this.getPosX() + this.getWidth() - (height / 2F) && mouseY < this.getPosY() + this.getHeight()));
		this.left = mouseX < this.getPosX() + this.getWidth() - (deleting ? 20 : 0);	
		return general;
	}
	
	@Override
	public boolean hoverCheck(int mouseX, int mouseY) {
		if(this.isSelected(mouseX, mouseY)) {
			String message;
			if(!this.left)
				message = "Delete this saved entry";
			else 
				message = this.hoverMsg;
			final ArrayList<String> lines = new ArrayList<String>();
			
			float textWidth = (int) (mouseX + 12 + fontRenderer.getStringWidth(message, 0.8F, true));
			if(textWidth > this.gui.field_230708_k_) {
				lines.addAll(fontRenderer.listFormattedStringToWidth(message, (int) (this.gui.field_230708_k_ - mouseX - 12), true));
			}else {
				lines.add(message);
			}
			textWidth = 0;
			for(String line : lines) {
				
				if(fontRenderer.getStringWidth(line, 0.8F, true) > textWidth)
					textWidth = fontRenderer.getStringWidth(line, 0.8F, true);
			}
			
			drawButton(mouseX + 5, mouseY - 7 - 10 * lines.size(), mouseX + 15 + textWidth, mouseY - 3, 0xff3a3a3a, 0xffdcdcdc, 2);
			int offset = 0;
			
			Collections.reverse(lines);
			
			for(String line : lines) {
			
				fontRenderer.drawString(line, (float)(mouseX + 9), (float)(mouseY - 14 - offset), 0xff3a3a3a, 0.8F, true);
				offset += 10;
			}
			return true;
		}
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		if(resized != this.resized_mark) {
			posY = posYF.apply(this.parent);
			posX = posXF.apply(this.parent);
		}
		
		setup();
		
		this.deleting = true;

		if(!init) {
			this.timer = (float) (1.05F);
			if(this.id == 0 && !FileUtil.keys_exists) {
				this.deleting = false;
				this.timer = 0;
			}
			if(this.id == 1 && !FileUtil.options_exists) {
				this.deleting = false;
				this.timer = 0;
			}
			if(this.id == 2 && !FileUtil.servers_exists) {
				this.deleting = false;
				this.timer = 0;
			}
			init = true;
		}
		
		if(this.id == 0 && !FileUtil.keys_exists)
			this.deleting = false;
		if(this.id == 1 && !FileUtil.options_exists)
			this.deleting = false;
		if(this.id == 2 && !FileUtil.servers_exists)
			this.deleting = false;
		
		final boolean selected = this.isSelected(mouseX, mouseY);
		
		if(selected && this.left) {
			
			if(this.animTimer <= MathUtil.PI / 2) {
				this.animTimer += 0.15;
				doIt = true;
			}
			
		}else{

			if(this.animTimer > 0) {
				this.animTimer -= 0.15;
				doIt = true;
			}

		}

		if(selected && !this.left) {
			
			if(this.animTimerRight <= MathUtil.PI / 2) {
				this.animTimerRight += 0.15;
				doIt = true;
			}
			
		}else{

			if(this.animTimerRight > 0) {
				this.animTimerRight -= 0.15;
				doIt = true;
			}

		}
		
		if (this.deleting) {
			
			if (this.timer <= MathUtil.PI / 3) {
				this.timer += 0.05;
				doIt = true;
			}

		} else {

			if (this.timer > 0) {
				this.timer -= 0.05;
				doIt = true;
			}
		}
		
		float alpha = (float) ((Math.sin(3 * this.timer - 3 * (MathUtil.PI / 2)) + 1) / 2);

		if(this.selected_prev != selected || this.grabbed != this.grabbed_prev || this.color != this.color_prev || doIt) {
			this.compiled = false;
			doIt = false;
		}
		
		if(!compiled) {
			preRender();
			glPushMatrix();
			GlStateManager.enableBlend();
	    	GlStateManager.disableAlphaTest();
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    	GlStateManager.disableTexture();

			drawRectRoundedCorners(0, 0, width, height, this.color, Integer.MAX_VALUE);
			
			float diff = this.grabbed ? 1.7F : 1.3F;

			final int off = 0xff3c3c3c;

			this.processFactor = (float) Math.sin(this.animTimer * 2 - (MathUtil.PI / 2)) / 2 + 0.5F;
			
			int red = (int) ((getRed(off)) + 35 * (processFactor));
			
			int green = (int) ((getGreen(off)) + 35 * (processFactor));
				
			int blue = (int) ((getBlue(off)) + 35 * (processFactor));

			drawRectRoundedCorners(diff, diff, width - diff, height - diff, ((255 & 0x0ff) << 24) | ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff), Integer.MAX_VALUE);
			
			GlStateManager.enableTexture();
			GlStateManager.enableAlphaTest();
			GlStateManager.disableBlend();
			final String txt = this.title;
			glEnable(GL_SCISSOR_TEST);
			
			glScissor(4 * (int) scaledFactor, (int) (5 * (int) scaledFactor), (int) ((this.getWidth() - 8) * (int) scaledFactor), (int) (20 * (int) scaledFactor));
			
			fontRenderer.drawString(txt, 1 + (this.getWidth() - 19 * (1 - alpha)) / 2 - fontRenderer.getStringWidth(txt, 1 - 0.2F * (1 - alpha), true) / 2, 11 + 1 * (1 - alpha), 0xffffffff, 1 - 0.2F * (1 - alpha), true);
	    	
	    	glScissor((int) ((this.width - 20 * (1 - alpha)) * (int) scaledFactor), (int) (0 * (int) scaledFactor), (int) 20 * (int) scaledFactor, (int) (30 * (int) scaledFactor));
	    		
	    	GlStateManager.enableBlend();
	    	GlStateManager.disableAlphaTest();
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    	GlStateManager.disableTexture();
	    	
	    	int color = 0xffbf3f3a;

	    	float testFactor = (float) Math.sin(this.animTimerRight * 2 - (MathUtil.PI / 2)) / 2 + 0.5F;
			
			red = (int) ((getRed(color)) + 30 * (testFactor));
			
			green = (int) ((getGreen(color)) + 30 * (testFactor));
				
			blue = (int) ((getBlue(color)) + 30 * (testFactor));

			drawRectRoundedCorners(diff, diff, width - diff, height - diff, ((255 & 0x0ff) << 24) | ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff)/*on*//*selected ? 0xff606060 : 0xff3c3c3c*/, Integer.MAX_VALUE);
	    	
			color = ((255 & 0x0ff) << 24) | ((red & 0x0ff) << 16) | ((green & 0x0ff) << 8) | (blue & 0x0ff);

			float f = (float) (color >> 16 & 255) / 255.0F;
			float f1 = (float) (color >> 8 & 255) / 255.0F;
			float f2 = (float) (color & 255) / 255.0F;
	    	
	    	glColor3d(f, f1, f2);

	    	drawCircle(width - diff - (height - diff * 2) / 2, (height) / 2, (height - diff * 2) / 2, 270, 50);
	    	
	    	drawRect(width - diff - (height - diff * 2) / 2 - 4, diff, width - diff - (height - diff * 2) / 2, height - diff, color, false, null, false);
	    	
	    	drawRect(width - diff - (height - diff * 2) / 2 - 5, diff, width - diff - (height - diff * 2) / 2 - 4, height - diff, darkenColor(color, 0.8F).getRGB(), false, null, false);
	    	
	    	GlStateManager.enableTexture();

	    	GlStateManager.disableBlend();
	    	
	    	GlStateManager.enableAlphaTest();
	    	GlStateManager.enableBlend();
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			
			GlStateManager.color4f(1, 1, 1, 1 -alpha);
			MC.getTextureManager().bindTexture(icon);
			drawScaledTex((float) this.width - 20, (float) 6, (int) (18 - alpha), (int) (18 - alpha));
			
			GlStateManager.disableBlend();
			glDisable(GL_SCISSOR_TEST);
	    	
	    	glPopMatrix();
	    	postRender(1, false);
	    	
		}
		
		glPushMatrix();
		drawTexture(1);
		glPopMatrix();
		this.selected_prev = selected;
		this.color_prev = this.color;
		this.grabbed_prev = this.grabbed;
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
		if (!this.isSelected(mouseX, mouseY))
			this.grabbed = false;
		return super.mouseDragged(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int button) {
		if (this.grabbed) {
			if (this.isSelected(mouseX, mouseY))
				this.grabbed = false;
			if(!this.left) {
			if (this.id == 0) 
				FileUtil.deleteKeys();
			else if (this.id == 1)
				FileUtil.deleteOptions();
			else if (this.id == 2)
				FileUtil.deleteServers();
			}else 
				this.function.apply(this);
			
				this.clickSound();

		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
}