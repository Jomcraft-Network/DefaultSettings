package de.pt400c.defaultsettings.gui;

import net.minecraft.client.audio.SimpleSound;
import static de.pt400c.defaultsettings.FileUtil.MC;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Segment {
	
	protected final GuiScreen gui;
	protected double posX;
	protected double posY;
	protected float width;
	protected float height;
	protected static final int RED_MASK = 255 << 16;
	protected static final int GREEN_MASK = 255 << 8;
	protected static final int BLUE_MASK = 255;
	protected final boolean isPopupSegment;
	private static int[] buffer = new int[0x10000];
    private static int bufferIndex = 0;
    private static ByteBuffer byteBuffer = GLAllocation.createDirectByteBuffer(0x200000 * 4);
    private static IntBuffer intBuffer = byteBuffer.asIntBuffer();
    private static FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
    private static final float circleTheta = (float) (2 * 3.1415926 / 100F); 
    private static final float tangetialFactor = (float) Math.tan(circleTheta);
	private static final float radialFactor = (float) Math.cos(circleTheta);
	
	public Segment(GuiScreen gui, float posX, float posY, float width, float height, boolean popupSegment) {
		this.gui = gui;
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		this.isPopupSegment = popupSegment;
	}
	
	public void init() {};
	
	public abstract void render(float mouseX, float mouseY, float partialTicks);
	
	public void customRender(float mouseX, float mouseY, float customPosX, float customPosY, float partialTicks) {};
	
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.isSelected(mouseX, mouseY);
	}
	
	public boolean mouseScrolled(double p_mouseScrolled_1_) {
		double mouseX = MC.mouseHelper.getMouseX() * (double) MC.mainWindow.getScaledWidth() / (double) MC.mainWindow.getWidth();
        double mouseY = MC.mouseHelper.getMouseY() * (double) MC.mainWindow.getScaledHeight() / (double) MC.mainWindow.getHeight();
        return this.isSelected(mouseX, mouseY);
    }
    
	public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
    	return false;
    }
	
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
    	return false;
    }
    
    public void guiContentUpdate(String... arg) {};
	
	public void hoverCheck(float mouseX, float mouseY) {}

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.isSelected(mouseX, mouseY);
    }
    
    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        return this.isSelected(mouseX, mouseY);
    }
	
	public boolean isSelected(double mouseX, double mouseY) {
		return (((GuiConfig) this.gui).popupField == null || this.getIsPopupSegment()) && mouseX >= this.getPosX() && mouseY >= this.getPosY() && mouseX < this.getPosX() + this.getWidth() && mouseY < this.getPosY() + this.getHeight();
	}
	
	public double getPosX() {
        return this.posX;
	}
	
	public double getPosY() {
        return this.posY;
	}
	
	public boolean getIsPopupSegment() {
        return this.isPopupSegment;
	}
	
	public float getWidth() {
        return this.width;
	}
	
	public float getHeight() {
        return this.height;
	}
	
	public Segment setPos(double x, double y) {
		this.posX = x;
		this.posY = y;
		return this;
	}
	
	public void clickSound() {
        MC.getSoundHandler().play(SimpleSound.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
	
	protected static void drawDot(float red, float green, float blue, float alpha, float scaleFactor, float size, Vec2f vector) {
		GL11.glColor4f(red, green, blue, alpha);

		GL11.glEnable(GL11.GL_POINT_SMOOTH);

		GL11.glPointSize(size * (scaleFactor / 2F));

		GL11.glBegin(GL11.GL_POINTS);

		GL11.glVertex3f(vector.x, vector.y, 0.0f);
		

		GL11.glEnd();
		GL11.glDisable(GL11.GL_POINT_SMOOTH);
		
	}
	
	protected static void drawDots(float red, float green, float blue, float alpha, float scaleFactor, Vec2f... vectors) {
		GL11.glColor4f(red, green, blue, alpha);

		GL11.glEnable(GL11.GL_POINT_SMOOTH);

		GL11.glPointSize(6.5F * (scaleFactor / 2F));

		GL11.glBegin(GL11.GL_POINTS);
		
		for(Vec2f vector : vectors) {
			GL11.glVertex3f(vector.x, vector.y, 0.0f);
		}

		GL11.glEnd();
		GL11.glDisable(GL11.GL_POINT_SMOOTH);
		
	}
	
	protected static void drawLine2D_2(float red, float green, float blue, float alpha, int factor, Vec2f... vectors) {
		GL11.glEnable(GL11.GL_LINE_SMOOTH);

		GL11.glLineWidth(3.0F * (factor / 2F));

		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glColor4f(red, green, blue, alpha);
		
		for(Vec2f vector : vectors) {
			GL11.glVertex3f(vector.x, vector.y, 0.0f);
		}
		
		GL11.glEnd();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		
		
		GL11.glEnable(GL11.GL_POINT_SMOOTH);
	
		GL11.glPointSize(3.0F * (factor / 2F));

		GL11.glBegin(GL11.GL_POINTS);
		
		for(Vec2f vector : vectors) {
			GL11.glVertex3f(vector.x, vector.y, 0.0f);
		}

		GL11.glEnd();
		GL11.glDisable(GL11.GL_POINT_SMOOTH);

	}
	
	protected static void drawLine2D(float red, float green, float blue, float alpha, int factor, Vec2f... vectors) {
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		if(!(factor == 1))
			GL11.glLineWidth(3.0F * (factor - 1));
		else
			GL11.glLineWidth(1F);

		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glColor4f(red, green, blue, alpha);
		
		for(Vec2f vector : vectors) {
			GL11.glVertex3f(vector.x, vector.y, 0.0f);
		}
		
		GL11.glEnd();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		
		
		GL11.glEnable(GL11.GL_POINT_SMOOTH);
		if(!(factor == 1))
			GL11.glPointSize(3.0F * (factor - 1));
		else
			GL11.glPointSize(1F);
		GL11.glBegin(GL11.GL_POINTS);
		
		for(Vec2f vector : vectors) {
			GL11.glVertex3f(vector.x, vector.y, 0.0f);
		}

		GL11.glEnd();
		GL11.glDisable(GL11.GL_POINT_SMOOTH);

		
	}
	
	public static void drawRect(double x1, double y1, double x2, double y2, Integer color, boolean blending, Float alpha, boolean multiply)
    {
		double j1;

        if (x1 < x2)
        {
            j1 = x1;
            x1 = x2;
            x2 = j1;
        }

        if (y1 < y2)
        {
            j1 = y1;
            y1 = y2;
            y2 = j1;
        }
        
        if(blending) {
        	GL11.glEnable(GL11.GL_BLEND);
        	GL11.glDisable(GL11.GL_TEXTURE_2D);
        	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }

        if(color != null) {
        	float f3 = (float)(color >> 24 & 255) / 255.0F;
            float f = (float)(color >> 16 & 255) / 255.0F;
            float f1 = (float)(color >> 8 & 255) / 255.0F;
            float f2 = (float)(color & 255) / 255.0F;
            if(alpha == null)
            	GlStateManager.color4f(f, f1, f2, f3);
            else if(multiply)
            	GlStateManager.color4f(f, f1, f2, f3 * alpha);
            else
            	GlStateManager.color4f(f, f1, f2, f3 - alpha);
        }

        addVertex((float) x1, (float) y2, 0);
        addVertex((float) x2, (float) y2, 0);
        addVertex((float) x2, (float) y1, 0);
        addVertex((float) x1, (float) y1, 0);

		draw(false);

		if(blending) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
        	GL11.glDisable(GL11.GL_BLEND);
		}
    }
	
	public static void drawRectRoundedUpper(float x1, float y1, float x2, float y2, int color, float alpha)
    {

        float f = (float)(color >> 24 & 255) / 255.0F;
        float f1 = (float)(color >> 16 & 255) / 255.0F;
        float f2 = (float)(color >> 8 & 255) / 255.0F;
        float f3 = (float)(color & 255) / 255.0F;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f - alpha);
        
        drawCircle(x1 + 10, y1 + 10, 10, 180F, 75);

        drawCircle(x2 - 10, y1 + 10, 10, 270F, 75);

        drawRect(x1 + 10, y1, x2 - 10, y1 + 10, null, false, null, false);
        
        drawRect(x1, y1 + 10, x2, y2, null, false, null, false);
        

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
       
    }
	
	protected static Color darkenColor(int color, float darken) {
		return new Color((int) (((color & RED_MASK) >> 16) * darken), (int) (((color & GREEN_MASK) >> 8) * darken), (int) ((color & BLUE_MASK) * darken), 255);
	}
	
	protected float distanceBetweenPoints(float posX, float posY, float mouseX, float mouseY) {
		return (float) Math.sqrt(((float) posX - mouseX) *  ((float) posX - mouseX) + ((float) posY - mouseY) *  ((float) posY - mouseY));
	}
	
	public static int getRed(int value) {
        return (value >> 16) & 0xFF;
    }
	
	public static int getGreen(int value) {
        return (value >> 8) & 0xFF;
    }
	
	public static int getBlue(int value) {
        return value & 0xFF;
    }
	
	public static int getAlpha(int value) {
        return (value >> 24) & 0xff;
    }
	
	protected static Color calcAlpha(int color, float alpha) {
		return new Color(getRed(color), getGreen(color), getBlue(color), GuiConfig.clamp((int) ((1 - alpha) * 255F), 4, 255));
	}
	
	public static void drawRectRoundedLower(float x1, float y1, float x2, float y2, int color, float alpha)
    {

        float f = (float)(color >> 24 & 255) / 255.0F;
        float f1 = (float)(color >> 16 & 255) / 255.0F;
        float f2 = (float)(color >> 8 & 255) / 255.0F;
        float f3 = (float)(color & 255) / 255.0F;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f - alpha);
        
        drawCircle(x1 + 10, y2 - 10, 10, 90F, 75);
        
        drawCircle(x2 - 10, y2 - 10, 10, 0F, 75);
        
        drawRect(x1, y1, x2, y2 - 10, null, false, null, false);
        
        drawRect(x1 + 10, y2 - 10, x2 - 10, y2, null, false, null, false);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
       
    }
	
	public static void drawCircle(float cx, float cy, float r, float rotation, int percentage) {

		float x = r;

		float y = 0;

		float posX1 = 0;
		float posY1 = 0;

		for (int l = 0; l < Math.round(100F / 360F * rotation); l++) {

			float tx = -y;
			float ty = x;

			x += tx * tangetialFactor;
			y += ty * tangetialFactor;

			x *= radialFactor;
			y *= radialFactor;
		}

		float posX2 = cx;
		float posY2 = cy;

		for (int i = 0; i < (100 + 1 - percentage); i++) {
			posX1 = posX2;
			posY1 = posY2;

			posX2 = x + cx;
			posY2 = y + cy;

			addVertex((float) posX1, (float) posY1, 0);
			addVertex((float) cx, (float) cy, 0);
			addVertex((float) posX2, (float) posY2, 0);

			draw(true);

			float tx = -y;
			float ty = x;

			x += tx * tangetialFactor;
			y += ty * tangetialFactor;

			x *= radialFactor;
			y *= radialFactor;

		}

	}

	public static void addVertex(float x, float y, float z) {
		buffer[bufferIndex + 0] = Float.floatToRawIntBits(x);
		buffer[bufferIndex + 1] = Float.floatToRawIntBits(y);
		buffer[bufferIndex + 2] = Float.floatToRawIntBits(z);
		bufferIndex += 8;
	}

	public static void draw(boolean triangle) {
		if (!triangle) {
			intBuffer.clear();
			intBuffer.put(buffer, 0, 32);
			byteBuffer.position(0);
			GL11.glVertexPointer(3, GL11.GL_FLOAT, 32, floatBuffer);
			GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
			GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		} else {
			intBuffer.clear();
			intBuffer.put(buffer, 0, 24);
			byteBuffer.position(0);
			GL11.glVertexPointer(3, GL11.GL_FLOAT, 32, floatBuffer);
			GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
			GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		}

		reset();
	}

	private static void reset() {
		byteBuffer.clear();
		bufferIndex = 0;
	}

	public static void drawButton(double left, double top, double right, double bottom, int color, int color2, int border) {
		drawRect(left, top, right, bottom, color, true, null, false);
		drawRect(left + border, top + border, right - border, bottom - border, color2, true, null, false);
	}
}
