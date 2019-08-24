package de.pt400c.defaultsettings.gui;

import static de.pt400c.defaultsettings.FileUtil.MC;
import static de.pt400c.defaultsettings.FileUtil.devEnv;
import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.Bidi;
import java.util.logging.Level;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.input.Mouse;
import de.pt400c.defaultsettings.DefaultSettings;
import de.pt400c.defaultsettings.GuiConfig;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;

public abstract class Segment {
	
	protected final GuiScreen gui;
	private static final String posXFr = devEnv ? "posX" : "field_78295_j";
	private static final String posYFr = devEnv ? "posY" : "field_78296_k";
	private static final String redFr = devEnv ? "red" : "field_78291_n";
	private static final String greenFr = devEnv ? "green" : "field_78306_p";
	private static final String blueFr = devEnv ? "blue" : "field_78292_o";
	private static final String alphaFr = devEnv ? "alpha" : "field_78305_q";
	private static final String randomStyleFr = devEnv ? "randomStyle" : "field_78303_s";
	private static final String boldStyleFr = devEnv ? "boldStyle" : "field_78302_t";
	private static final String italicStyleFr = devEnv ? "italicStyle" : "field_78301_u";
	private static final String underlineStyleFr = devEnv ? "underlineStyle" : "field_78300_v";
	private static final String strikethroughStyleFr = devEnv ? "strikethroughStyle" : "field_78299_w";
	private static final String textColorFr = devEnv ? "textColor" : "field_78304_r";
	private static final String colorCodeFr = devEnv ? "colorCode" : "field_78285_g";
	private static final String charWidthFr = devEnv ? "charWidth" : "field_78286_d";
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
	private static boolean hasColor;
	private static int color;
	
	public Segment(GuiScreen gui, float posX, float posY, float width, float height, boolean popupSegment) {
		this.gui = gui;
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		this.isPopupSegment = popupSegment;
	}
	
	public abstract void render(float mouseX, float mouseY, float partialTicks);
	
	public void init() {};
	
	public void customRender(float mouseX, float mouseY, float customPosX, float customPosY, float partialTicks) {};
	
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.isSelected(mouseX, mouseY);
	}
	
	public boolean handleMouseInput() {
    	float mouseX = Mouse.getEventX() * this.width / MC.displayWidth;
        float mouseY = this.height - Mouse.getEventY() * this.height / MC.displayHeight - 1;
        return this.isSelected(mouseX, mouseY);
    }
    
    protected boolean keyTyped(char typedChar, int keyCode) {
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
		return (((DefaultSettingsGUI) this.gui).popupField == null || this.getIsPopupSegment()) && mouseX >= this.getPosX() && mouseY >= this.getPosY() && mouseX < this.getPosX() + this.getWidth() && mouseY < this.getPosY() + this.getHeight();
	}
	
	public double getPosX() {
        return this.posX;
	}
	
	public double getPosY() {
        return this.posY;
	}
	
	public float getWidth() {
        return this.width;
	}
	
	public float getHeight() {
        return this.height;
	}
	
	public boolean getIsPopupSegment() {
        return this.isPopupSegment;
	}
	
	public Segment setPos(double x, double y) {
		this.posX = x;
		this.posY = y;
		return this;
	}
	
	public void clickSound() {
        MC.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
    }
	
	/**
	 * 
	 * @param x1 First x coord
	 * @param y1 First y coord
	 * @param x2 Second x coord
	 * @param y2 Second y coord
	 * @param color1 Start color of the gradient
	 * @param color2 End color of the gradient
	 * @param rotation 0 = left-right, 1 = top-down, 2 = right-left, 3 = down-top
	 */
	public static void drawGradient(double x1, double y1, double x2, double y2, int color1, int color2, final int rotation) {
		double j1;

		if (x1 < x2) {
			j1 = x1;
			x1 = x2;
			x2 = j1;
		}

		if (y1 < y2) {
			j1 = y1;
			y1 = y2;
			y2 = j1;
		}

		int f3 = (int) (color1 >> 24 & 255);
		int f = (int) (color1 >> 16 & 255);
		int f1 = (int) (color1 >> 8 & 255);
		int f2 = (int) (color1 & 255);

		setColor(f, f1, f2, f3);
		
		if(rotation == 1) {
			addVertex((float) x1, (float) y2, 0);
	        addVertex((float) x2, (float) y2, 0);
		}else if(rotation == 2) {
			addVertex((float) x1, (float) y1, 0);
			addVertex((float) x1, (float) y2, 0);
		}else if(rotation == 3) {
			addVertex((float) x2, (float) y1, 0);
	        addVertex((float) x1, (float) y1, 0);
		}else if(rotation == 0) {
			addVertex((float) x2, (float) y2, 0);
			addVertex((float) x2, (float) y1, 0);
		}
			
		f3 = (int) (color2 >> 24 & 255);
		f = (int) (color2 >> 16 & 255);
		f1 = (int) (color2 >> 8 & 255);
		f2 = (int) (color2 & 255);

		setColor(f, f1, f2, f3);
	
		if(rotation == 1) {
			addVertex((float) x2, (float) y1, 0);
	        addVertex((float) x1, (float) y1, 0);
		}else if(rotation == 2) {
			addVertex((float) x2, (float) y2, 0);
			addVertex((float) x2, (float) y1, 0);
		}else if(rotation == 3) {
			addVertex((float) x1, (float) y2, 0);
	        addVertex((float) x2, (float) y2, 0);
		}else if(rotation == 0) {
			addVertex((float) x1, (float) y1, 0);
			addVertex((float) x1, (float) y2, 0);
		}

		draw(false);
	}
	
	public static void drawGradientCircle(float cx, float cy, float r, float rotation, int percentage, int color1, int color2) {

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
			
			final int f3 = (int)(color2 >> 24 & 255);
			final int f = (int)(color2 >> 16 & 255);
			final int f1 = (int)(color2 >> 8 & 255);
			final int f2 = (int)(color2 & 255);
	        
	        setColor(f, f1, f2, f3);

			addVertex((float) posX1, (float) posY1, 0);
			
			final int f13 = (int)(color1 >> 24 & 255);
			final int f0 = (int)(color1 >> 16 & 255);
			final int f11 = (int)(color1 >> 8 & 255);
			final int f12 = (int)(color1 & 255);
			
	        setColor(f0, f11, f12, f13);
			addVertex((float) cx, (float) cy, 0);
			setColor(f, f1, f2, f3);
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
        	glEnable(GL_BLEND);
        	glDisable(GL_ALPHA_TEST);
        	glDisable(GL_TEXTURE_2D);
        	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }
        
        if(color != null) {
        	final float f3 = (float)(color >> 24 & 255) / 255.0F;
        	final float f = (float)(color >> 16 & 255) / 255.0F;
        	final float f1 = (float)(color >> 8 & 255) / 255.0F;
        	final float f2 = (float)(color & 255) / 255.0F;
            if(alpha == null)
            	glColor4f(f, f1, f2, f3);
            else if(multiply)
            	glColor4f(f, f1, f2, f3 * alpha);
            else
            	glColor4f(f, f1, f2, f3 - alpha);
        }
        
        addVertex((float) x1, (float) y2, 0);
        addVertex((float) x2, (float) y2, 0);
        addVertex((float) x2, (float) y1, 0);
        addVertex((float) x1, (float) y1, 0);

		draw(false);
		if(blending) {
			glEnable(GL_TEXTURE_2D);
			glEnable(GL_ALPHA_TEST);
        	glDisable(GL_BLEND);
		}
    }

	public static void drawRectRoundedUpper(float x1, float y1, float x2, float y2, int color, float alpha)
    {

        float f = (float)(color >> 24 & 255) / 255.0F;
        float f1 = (float)(color >> 16 & 255) / 255.0F;
        float f2 = (float)(color >> 8 & 255) / 255.0F;
        float f3 = (float)(color & 255) / 255.0F;

        glEnable(GL_BLEND);
        glDisable(GL_ALPHA_TEST);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(f1, f2, f3, f - alpha);
        
        drawCircle(x1 + 10, y1 + 10, 10, 180F, 75);

        drawCircle(x2 - 10, y1 + 10, 10, 270F, 75);

        drawRect(x1 + 10, y1, x2 - 10, y1 + 10, null, false, null, false);
        
        drawRect(x1, y1 + 10, x2, y2, null, false, null, false);
        
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_ALPHA_TEST);
        glDisable(GL_BLEND);
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

		final float f = (float)(color >> 24 & 255) / 255.0F;
        final float f1 = (float)(color >> 16 & 255) / 255.0F;
        final float f2 = (float)(color >> 8 & 255) / 255.0F;
        final float f3 = (float)(color & 255) / 255.0F;

        glEnable(GL_BLEND);
        glDisable(GL_ALPHA_TEST);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(f1, f2, f3, f - alpha);
        
        drawCircle(x1 + 10, y2 - 10, 10, 90F, 75);
        
        drawCircle(x2 - 10, y2 - 10, 10, 0F, 75);
        
        drawRect(x1, y1, x2, y2 - 10, null, false, null, false);
        
        drawRect(x1 + 10, y2 - 10, x2 - 10, y2, null, false, null, false);

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_ALPHA_TEST);
        glDisable(GL_BLEND);
       
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

	public static void setColor(int par1, int par2, int par3, int par4) {
            if (par1 > 255)
            {
                par1 = 255;
            }

            if (par2 > 255)
            {
                par2 = 255;
            }

            if (par3 > 255)
            {
                par3 = 255;
            }

            if (par4 > 255)
            {
                par4 = 255;
            }

            if (par1 < 0)
            {
                par1 = 0;
            }

            if (par2 < 0)
            {
                par2 = 0;
            }

            if (par3 < 0)
            {
                par3 = 0;
            }

            if (par4 < 0)
            {
                par4 = 0;
            }

            hasColor = true;

            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
            {
                color = par4 << 24 | par3 << 16 | par2 << 8 | par1;
            }
            else
            {
                color = par1 << 24 | par2 << 16 | par3 << 8 | par4;
            }
        
    }
	
	public static void addVertex(float x, float y, float z) {
		if (hasColor)
        {
			buffer[bufferIndex + 5] = color;
        }
		
		buffer[bufferIndex + 0] = Float.floatToRawIntBits(x);
		buffer[bufferIndex + 1] = Float.floatToRawIntBits(y);
		buffer[bufferIndex + 2] = Float.floatToRawIntBits(z);
		bufferIndex += 8;
	}

	public static void draw(boolean triangle) {
		if (!triangle) {
			intBuffer.clear();
			intBuffer.put(buffer, 0, 32);

			if (hasColor) {
				byteBuffer.position(20);
				glColorPointer(4, true, 32, byteBuffer);
				glEnableClientState(GL_COLOR_ARRAY);
			}
			
			byteBuffer.position(0);
			glVertexPointer(3, 32, floatBuffer);
			glEnableClientState(GL_VERTEX_ARRAY);
			glDrawArrays(GL_QUADS, 0, 4);
			glDisableClientState(GL_VERTEX_ARRAY);
			
			if (hasColor)
            {
                glDisableClientState(GL_COLOR_ARRAY);
            }
			
		} else {
			intBuffer.clear();
			intBuffer.put(buffer, 0, 24);
			
			if (hasColor) {
				byteBuffer.position(20);
				glColorPointer(4, true, 32, byteBuffer);
				glEnableClientState(GL_COLOR_ARRAY);
			}
			
			byteBuffer.position(0);
			glVertexPointer(3, 32, floatBuffer);
			glEnableClientState(GL_VERTEX_ARRAY);
			glDrawArrays(GL_TRIANGLES, 0, 3);
			glDisableClientState(GL_VERTEX_ARRAY);

			if (hasColor) {
				glDisableClientState(GL_COLOR_ARRAY);
			}
		}
		hasColor = false;
		reset();
	}
	
	protected static void drawDot(float red, float green, float blue, float alpha, float scaleFactor, float size, Vec2f vector) {
		glColor4f(red, green, blue, alpha);

		glEnable(GL_POINT_SMOOTH);

		glPointSize(size * (scaleFactor / 2F));

		glBegin(GL_POINTS);

		glVertex3d(vector.x, vector.y, 0.0f);
		
		glEnd();
		glDisable(GL_POINT_SMOOTH);
		
	}
	
	protected static void drawDots(float red, float green, float blue, float alpha, float scaleFactor, Vec2f... vectors) {
		glColor4f(red, green, blue, alpha);

		glEnable(GL_POINT_SMOOTH);

		glPointSize(6.5F * (scaleFactor / 2F));

		glBegin(GL_POINTS);
		
		for(Vec2f vector : vectors) {
			glVertex3d(vector.x, vector.y, 0.0f);
		}

		glEnd();
		glDisable(GL_POINT_SMOOTH);
		
	}
	
	protected static void drawLine2D_2(float red, float green, float blue, float alpha, int factor, Vec2f... vectors) {
		glEnable(GL_LINE_SMOOTH);

		glLineWidth(3.0F * (factor / 2F));

		glBegin(GL_LINE_STRIP);
		glColor4f(red, green, blue, alpha);
		
		for(Vec2f vector : vectors) {
			glVertex3d(vector.x, vector.y, 0.0f);
		}
		
		glEnd();
		glDisable(GL_LINE_SMOOTH);
		
		glEnable(GL_POINT_SMOOTH);
	
		glPointSize(3.0F * (factor / 2F));

		glBegin(GL_POINTS);
		
		for(Vec2f vector : vectors) {
			glVertex3d(vector.x, vector.y, 0.0f);
		}

		glEnd();
		glDisable(GL_POINT_SMOOTH);

	}
	
	protected static void drawLine2D(float red, float green, float blue, float alpha, int factor, Vec2f... vectors) {
		glEnable(GL_LINE_SMOOTH);
		if(!(factor == 1))
			glLineWidth(3.0F * (factor - 1));
		else
			glLineWidth(1F);

		glBegin(GL_LINE_STRIP);
		glColor4f(red, green, blue, alpha);
		
		for(Vec2f vector : vectors) {
			glVertex3d(vector.x, vector.y, 0.0f);
		}
		
		glEnd();
		glDisable(GL_LINE_SMOOTH);
		
		glEnable(GL_POINT_SMOOTH);
		if(!(factor == 1))
			glPointSize(3.0F * (factor - 1));
		else
			glPointSize(1F);
		glBegin(GL_POINTS);
		
		for(Vec2f vector : vectors) {
			glVertex3d(vector.x, vector.y, 0.0f);
		}

		glEnd();
		glDisable(GL_POINT_SMOOTH);
	}

	private static void reset() {
		byteBuffer.clear();
		bufferIndex = 0;
	}
		
	public static void drawButton(double left, double top, double right, double bottom, int color, int color2, int border) {
		drawRect(left, top, right, bottom, color, true, null, false);
		drawRect(left + border, top + border, right - border, bottom - border, color2, true, null, false);
	}

	public int drawString(String p_85187_1_, double p_85187_2_, double p_85187_3_, int p_85187_4_, boolean p_85187_5_) {
		glEnable(GL_ALPHA_TEST);
		
        this.resetStyles();
        int l = this.renderString(p_85187_1_, p_85187_2_, p_85187_3_, p_85187_4_, false);
        return l;
    }
	
	public void resetStyles() {
		try {
			Method resetStyles = FontRenderer.class.getDeclaredMethod(devEnv ? "resetStyles" : "func_78265_b", new Class<?>[0]);
			resetStyles.setAccessible(true);
			resetStyles.invoke(MC.fontRenderer, new Object[0]);
		} catch (IllegalAccessException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (IllegalArgumentException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (InvocationTargetException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (NoSuchMethodException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (SecurityException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		}
	}
	
	public int renderString(String p_78258_1_, double p_78258_2_, double p_78258_3_, int p_78258_4_, boolean p_78258_5_) {
		
		if (p_78258_1_ == null)
        {
            return 0;
        }
        else
        {
            if (MC.fontRenderer.getBidiFlag())
            {
                p_78258_1_ = this.bidiReorder(p_78258_1_);
            }

            if ((p_78258_4_ & -67108864) == 0)
            {
                p_78258_4_ |= -16777216;
            }

            if (p_78258_5_)
            {
                p_78258_4_ = (p_78258_4_ & 16579836) >> 2 | p_78258_4_ & -16777216;
            }

            setField(redFr, (float)(p_78258_4_ >> 16 & 255) / 255.0F, MC.fontRenderer);
            setField(blueFr, (float)(p_78258_4_ >> 8 & 255) / 255.0F, MC.fontRenderer);
            setField(greenFr, (float)(p_78258_4_ & 255) / 255.0F, MC.fontRenderer);
            setField(alphaFr, (float)(p_78258_4_ >> 24 & 255) / 255.0F, MC.fontRenderer);
            glColor4f((float) getField(redFr, MC.fontRenderer), (float) getField(blueFr, MC.fontRenderer), (float) getField(greenFr, MC.fontRenderer), (float) getField(alphaFr, MC.fontRenderer));
            setField(posXFr, (float)p_78258_2_, MC.fontRenderer);
            setField(posYFr, (float)p_78258_3_, MC.fontRenderer);
            this.renderStringAtPos(p_78258_1_, p_78258_5_);
            return (int) (float) getField(posXFr, MC.fontRenderer);
        }
		
	}
	
	private String bidiReorder(String par1Str)
    {
        if (par1Str != null && Bidi.requiresBidi(par1Str.toCharArray(), 0, par1Str.length()))
        {
            Bidi bidi = new Bidi(par1Str, -2);
            byte[] abyte = new byte[bidi.getRunCount()];
            String[] astring = new String[abyte.length];
            int i;

            for (int j = 0; j < abyte.length; ++j)
            {
                int k = bidi.getRunStart(j);
                i = bidi.getRunLimit(j);
                int l = bidi.getRunLevel(j);
                String s1 = par1Str.substring(k, i);
                abyte[j] = (byte)l;
                astring[j] = s1;
            }

            String[] astring1 = (String[])astring.clone();
            Bidi.reorderVisually(abyte, 0, astring, 0, abyte.length);
            StringBuilder stringbuilder = new StringBuilder();
            i = 0;

            while (i < astring.length)
            {
                byte b0 = abyte[i];
                int i1 = 0;

                while (true)
                {
                    if (i1 < astring1.length)
                    {
                        if (!astring1[i1].equals(astring[i]))
                        {
                            ++i1;
                            continue;
                        }

                        b0 = abyte[i1];
                    }

                    if ((b0 & 1) == 0)
                    {
                        stringbuilder.append(astring[i]);
                    }
                    else
                    {
                        for (i1 = astring[i].length() - 1; i1 >= 0; --i1)
                        {
                            char c0 = astring[i].charAt(i1);

                            if (c0 == 40)
                            {
                                c0 = 41;
                            }
                            else if (c0 == 41)
                            {
                                c0 = 40;
                            }

                            stringbuilder.append(c0);
                        }
                    }

                    ++i;
                    break;
                }
            }

            return stringbuilder.toString();
        }
        else
        {
            return par1Str;
        }
    }
	
	private void renderStringAtPos(String p_78255_1_, boolean p_78255_2_) {
        for (int i = 0; i < p_78255_1_.length(); ++i)
        {
            char c0 = p_78255_1_.charAt(i);
            int j;
            int k;

            if (c0 == 167 && i + 1 < p_78255_1_.length())
            {
                j = "0123456789abcdefklmnor".indexOf(p_78255_1_.toLowerCase().charAt(i + 1));

                if (j < 16)
                {
                	setField(randomStyleFr, false, MC.fontRenderer);
                	setField(boldStyleFr, false, MC.fontRenderer);
                	setField(strikethroughStyleFr, false, MC.fontRenderer);
                	setField(underlineStyleFr, false, MC.fontRenderer);
                	setField(italicStyleFr, false, MC.fontRenderer);

                    if (j < 0 || j > 15)
                    {
                        j = 15;
                    }

                    if (p_78255_2_)
                    {
                        j += 16;
                    }

                    k = ((int[])(getField(colorCodeFr, MC.fontRenderer)))[j];
                    setField(textColorFr, k, MC.fontRenderer);
                    glColor4f((float)(k >> 16) / 255.0F, (float)(k >> 8 & 255) / 255.0F, (float)(k & 255) / 255.0F, (float) getField(alphaFr, MC.fontRenderer));
                }
                else if (j == 16)
                {
                	setField(randomStyleFr, true, MC.fontRenderer);
                }
                else if (j == 17)
                {
                	setField(boldStyleFr, true, MC.fontRenderer);
                }
                else if (j == 18)
                {
                	setField(strikethroughStyleFr, true, MC.fontRenderer);
                }
                else if (j == 19)
                {
                	setField(underlineStyleFr, true, MC.fontRenderer);
                }
                else if (j == 20)
                {
                	setField(italicStyleFr, true, MC.fontRenderer);
                }
                else if (j == 21)
                {
                	setField(randomStyleFr, false, MC.fontRenderer);
                	setField(boldStyleFr, false, MC.fontRenderer);
                	setField(strikethroughStyleFr, false, MC.fontRenderer);
                	setField(underlineStyleFr, false, MC.fontRenderer);
                	setField(italicStyleFr, false, MC.fontRenderer);
                	
                    glColor4f((float) getField(redFr, MC.fontRenderer), (float) getField(blueFr, MC.fontRenderer), (float) getField(greenFr, MC.fontRenderer), (float) getField(alphaFr, MC.fontRenderer));
                }

                ++i;
            }
            else
            {
                j = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(c0);

                if ((boolean) getField(randomStyleFr, MC.fontRenderer) && j != -1)
                {
                    do
                    {
                        k = MC.fontRenderer.fontRandom.nextInt(((int[])(getField(charWidthFr, MC.fontRenderer))).length);
                    }

                    while (((int[])(getField(charWidthFr, MC.fontRenderer)))[j] != ((int[])(getField(charWidthFr, MC.fontRenderer)))[k]);

                    j = k;
                }

                float f1 = MC.fontRenderer.getUnicodeFlag() ? 0.5F : 1.0F;
                boolean flag1 = (c0 == 0 || j == -1 || MC.fontRenderer.getUnicodeFlag()) && p_78255_2_;

                if (flag1)
                {
                	setField(posXFr, (float) getField(posXFr, MC.fontRenderer) - f1, MC.fontRenderer);
                    setField(posYFr, (float) getField(posYFr, MC.fontRenderer) - f1, MC.fontRenderer);
                }

                float f = this.renderCharAtPos(j, c0, (boolean) getField(italicStyleFr, MC.fontRenderer));

                if (flag1)
                {
                    setField(posXFr, (float)((int)f1) + (float) getField(posXFr, MC.fontRenderer), MC.fontRenderer);
                	setField(posYFr, (float)((int)f1) + (float) getField(posYFr, MC.fontRenderer), MC.fontRenderer);
                }

                if ((boolean) getField(boldStyleFr, MC.fontRenderer))
                {
                	setField(posXFr, (float) getField(posXFr, MC.fontRenderer) + f1, MC.fontRenderer);

                    if (flag1)
                    {
                        setField(posXFr, (float) getField(posXFr, MC.fontRenderer) - f1, MC.fontRenderer);
                        setField(posYFr, (float) getField(posYFr, MC.fontRenderer) - f1, MC.fontRenderer);
                    }

                    this.renderCharAtPos(j, c0, (boolean) getField(italicStyleFr, MC.fontRenderer));
                    setField(posXFr, (float) getField(posXFr, MC.fontRenderer) - f1, MC.fontRenderer);

                    if (flag1)
                    {
                    	setField(posXFr, (float)((int)f1) + (float) getField(posXFr, MC.fontRenderer), MC.fontRenderer);
                    	setField(posYFr, (float)((int)f1) + (float) getField(posYFr, MC.fontRenderer), MC.fontRenderer);
                    }

                    ++f;
                }

                this.doDraw(f);
            }
        }
    }
	
	protected void doDraw(float f)
    {
        {
            {
                Tessellator tessellator;

                if ((boolean) getField(strikethroughStyleFr, MC.fontRenderer))
                {
                    tessellator = Tessellator.instance;
                    glDisable(GL_TEXTURE_2D);
                    tessellator.startDrawingQuads();
                    tessellator.addVertex((double)(float) getField(posXFr, MC.fontRenderer), (double)((float) getField(posYFr, MC.fontRenderer) + (float)(MC.fontRenderer.FONT_HEIGHT / 2)), 0.0D);
                    tessellator.addVertex((double)((float) getField(posXFr, MC.fontRenderer) + f), (double)((float) getField(posYFr, MC.fontRenderer) + (float)(MC.fontRenderer.FONT_HEIGHT / 2)), 0.0D);
                    tessellator.addVertex((double)((float) getField(posXFr, MC.fontRenderer) + f), (double)((float) getField(posYFr, MC.fontRenderer) + (float)(MC.fontRenderer.FONT_HEIGHT / 2) - 1.0F), 0.0D);
                    tessellator.addVertex((double)(float) getField(posXFr, MC.fontRenderer), (double)((float) getField(posYFr, MC.fontRenderer) + (float)(MC.fontRenderer.FONT_HEIGHT / 2) - 1.0F), 0.0D);
                    tessellator.draw();
                    glEnable(GL_TEXTURE_2D);
                }

                if ((boolean) getField(underlineStyleFr, MC.fontRenderer))
                {
                    tessellator = Tessellator.instance;
                    glDisable(GL_TEXTURE_2D);
                    tessellator.startDrawingQuads();
                    int l = (boolean) getField(underlineStyleFr, MC.fontRenderer) ? -1 : 0;
                    tessellator.addVertex((double)((float) getField(posXFr, MC.fontRenderer) + (float)l), (double)((float) getField(posYFr, MC.fontRenderer) + (float)MC.fontRenderer.FONT_HEIGHT), 0.0D);
                    tessellator.addVertex((double)((float) getField(posXFr, MC.fontRenderer) + f), (double)((float) getField(posYFr, MC.fontRenderer) + (float)MC.fontRenderer.FONT_HEIGHT), 0.0D);
                    tessellator.addVertex((double)((float) getField(posXFr, MC.fontRenderer) + f), (double)((float) getField(posYFr, MC.fontRenderer) + (float)MC.fontRenderer.FONT_HEIGHT - 1.0F), 0.0D);
                    tessellator.addVertex((double)((float) getField(posXFr, MC.fontRenderer) + (float)l), (double)((float) getField(posYFr, MC.fontRenderer) + (float)MC.fontRenderer.FONT_HEIGHT - 1.0F), 0.0D);
                    tessellator.draw();
                    glEnable(GL_TEXTURE_2D);
                }

                setField(posXFr, (float)((int)f) + (float) getField(posXFr, MC.fontRenderer), MC.fontRenderer);
            }
        }
    }
	
	public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight)
    {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)x, (double)(y + height), 0.0D, ((double)(u * f)), (double)((v + (float)vHeight) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + height), 0.0D, (double)((u + (float)uWidth) * f), (double)((v + (float)vHeight) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)y, 0.0D, (double)((u + (float)uWidth) * f), (double)(v * f1));
        tessellator.addVertexWithUV((double)x, (double)y, 0.0D, (double)(u * f), (double)(v * f1));
        tessellator.draw();
    }
	
	private float renderCharAtPos(int p_78278_1_, char p_78278_2_, boolean p_78278_3_) {
        return p_78278_2_ == 32 ? 4.0F : ("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(p_78278_2_) != -1 && !MC.fontRenderer.getUnicodeFlag() ? renderDefaultChar(p_78278_1_, p_78278_3_) : renderUnicodeChar(p_78278_2_, p_78278_3_));
    }

	private static float renderUnicodeChar(char p_78278_2_, boolean p_78278_3_) {
		try {
			Method renderUnicodeChar = FontRenderer.class.getDeclaredMethod(devEnv ? "renderUnicodeChar" : "func_78277_a", char.class, boolean.class);
			renderUnicodeChar.setAccessible(true);
			return (Float) renderUnicodeChar.invoke(MC.fontRenderer, p_78278_2_, p_78278_3_);
		} catch (NoSuchMethodException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (IllegalAccessException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (IllegalArgumentException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (InvocationTargetException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);

		}
		return 0;
		
	}
	
	private static Object getField(String name, FontRenderer obj) {
		try {
			Field field = FontRenderer.class.getDeclaredField(name);
			field.setAccessible(true);
			return field.get(obj);
		} catch (IllegalAccessException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (IllegalArgumentException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (NoSuchFieldException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (SecurityException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		}
		return 0F;
	}
	
	private static void setField(String name, Object value, FontRenderer obj) {
		try {
			Field field = FontRenderer.class.getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (IllegalAccessException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (IllegalArgumentException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (NoSuchFieldException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (SecurityException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		}
	}

	private static float renderDefaultChar(int p_78278_1_, boolean p_78278_3_) {
		
		try {
			Method renderDefaultChar = FontRenderer.class.getDeclaredMethod(devEnv ? "renderDefaultChar" : "func_78266_a", int.class, boolean.class);
			renderDefaultChar.setAccessible(true);
			return (Float) renderDefaultChar.invoke(MC.fontRenderer, p_78278_1_, p_78278_3_);
		} catch (NoSuchMethodException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (IllegalAccessException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (IllegalArgumentException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);
		} catch (InvocationTargetException e) {
			DefaultSettings.log.log(Level.SEVERE, "Reflection exception: ", e);

		}
		return 0;
	}
}
