package de.pt400c.neptunefx;

import static org.lwjgl.opengl.GL11.*;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import de.pt400c.defaultsettings.gui.MathUtil;
import de.pt400c.defaultsettings.gui.MathUtil.Vec2f;
import net.minecraft.client.renderer.GLAllocation;

public class NEX {
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
	public static final int RED_MASK = 255 << 16;
	public static final int GREEN_MASK = 255 << 8;
	public static final int BLUE_MASK = 255;
	
	public static Color darkenColor(int color, float darken) {
		return new Color((int) (((color & RED_MASK) >> 16) * darken), (int) (((color & GREEN_MASK) >> 8) * darken), (int) ((color & BLUE_MASK) * darken), 255);
	}
	
	public static float distanceBetweenPoints(float posX, float posY, float mouseX, float mouseY) {
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
	
	public static Color calcAlpha(int color, float alpha) {
		return new Color(getRed(color), getGreen(color), getBlue(color), MathUtil.clamp((int) ((1 - alpha) * 255F), 4, 255));
	}
	
	public static void drawRect(float x1, float y1, float x2, float y2, Integer color, boolean blending, Float alpha, boolean multiply) {
        
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

        addVertex((float) x2, (float) y1, 0);
        addVertex((float) x1, (float) y1, 0);
        addVertex((float) x1, (float) y2, 0);
        addVertex((float) x2, (float) y2, 0);

		draw(false);

		if(blending) {
			glEnable(GL_TEXTURE_2D);
        	glDisable(GL_BLEND);
        	glEnable(GL_ALPHA_TEST);
		}
    }
	
	public static void drawRectRoundedCornersHollow(float x1, float y1, float x2, float y2, Integer color, float radius, float innerRad) {
        
        float dist1 = Math.abs(y2 - y1) / 2;
        float dist2 = Math.abs(x2 - x1) / 2;
        
        if(radius > dist1 && radius > dist2)
        	radius = Math.min(dist1, dist2);

        if(color != null) {
        	final float f3 = (float)(color >> 24 & 255) / 255.0F;
        	final float f = (float)(color >> 16 & 255) / 255.0F;
        	final float f1 = (float)(color >> 8 & 255) / 255.0F;
            final float f2 = (float)(color & 255) / 255.0F;
            glColor4f(f, f1, f2, f3);
        }
        
        addVertex((float) x1 + radius - innerRad, (float) y1 + radius, 0);
        addVertex((float) x1, (float) y1 + radius, 0);
        addVertex((float) x1, (float) y2 - radius, 0);
        addVertex((float) x1 + radius - innerRad, (float) y2 - radius, 0);

		draw(false);
        
        addVertex((float) x2 - radius, (float) y1, 0);
        addVertex((float) x1 + radius, (float) y1, 0);
        addVertex((float) x1 + radius, (float) y1 + radius - innerRad, 0);
        addVertex((float) x2 - radius, (float) y1 + radius - innerRad, 0);

		draw(false);
		
		addVertex((float) x2 - radius, (float) y2 - radius + innerRad, 0);
        addVertex((float) x1 + radius, (float) y2 - radius + innerRad, 0);
        addVertex((float) x1 + radius, (float) y2, 0);
        addVertex((float) x2 - radius, (float) y2, 0);

		draw(false);
		
		addVertex((float) x2, (float) y1 + radius, 0);
        addVertex((float) x2 - radius + innerRad, (float) y1 + radius, 0);
        addVertex((float) x2 - radius + innerRad, (float) y2 - radius, 0);
        addVertex((float) x2, (float) y2 - radius, 0);

		draw(false);
		
		drawCircleHollow(x1 + radius, y2 - radius, radius, innerRad, 90, 75);
		
		drawCircleHollow(x1 + radius, y1 + radius, radius, innerRad, 180, 75);
		
		drawCircleHollow(x2 - radius, y2 - radius, radius, innerRad, 0, 75);
		
		drawCircleHollow(x2 - radius, y1 + radius, radius, innerRad, 270, 75);
    }
	
	public static void drawRectRoundedCorners(float x1, float y1, float x2, float y2, Integer color, float radius) {
        
        float dist1 = Math.abs(y2 - y1) / 2;
        float dist2 = Math.abs(x2 - x1) / 2;
        
        if(radius > dist1 && radius > dist2)
        	radius = Math.min(dist1, dist2);

        if(color != null) {
        	final float f3 = (float)(color >> 24 & 255) / 255.0F;
        	final float f = (float)(color >> 16 & 255) / 255.0F;
        	final float f1 = (float)(color >> 8 & 255) / 255.0F;
            final float f2 = (float)(color & 255) / 255.0F;
            glColor4f(f, f1, f2, f3);
        }

        addVertex((float) x1 + radius, (float) y1 + radius, 0);
        addVertex((float) x1, (float) y1 + radius, 0);
        addVertex((float) x1, (float) y2 - radius, 0);
        addVertex((float) x1 + radius, (float) y2 - radius, 0);

		draw(false);
        
        addVertex((float) x2 - radius, (float) y1, 0);
        addVertex((float) x1 + radius, (float) y1, 0);
        addVertex((float) x1 + radius, (float) y2, 0);
        addVertex((float) x2 - radius, (float) y2, 0);

		draw(false);
		
		addVertex((float) x2, (float) y1 + radius, 0);
        addVertex((float) x2 - radius, (float) y1 + radius, 0);
        addVertex((float) x2 - radius, (float) y2 - radius, 0);
        addVertex((float) x2, (float) y2 - radius, 0);

		draw(false);
		
		drawCircle(x1 + radius, y2 - radius, radius, 90, 75);
		
		drawCircle(x1 + radius, y1 + radius, radius, 180, 75);
		
		drawCircle(x2 - radius, y2 - radius, radius, 0, 75);
		
		drawCircle(x2 - radius, y1 + radius, radius, 270, 75);
    }

	public static void drawGradient(float x1, float y1, float x2, float y2, int color1, int color2, final int rotation) {     
        int f3 = (int)(color1 >> 24 & 255);
        int f = (int)(color1 >> 16 & 255);
        int f1 = (int)(color1 >> 8 & 255);
        int f2 = (int)(color1 & 255);

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
        
        f3 = (int)(color2 >> 24 & 255);
        f = (int)(color2 >> 16 & 255);
        f1 = (int)(color2 >> 8 & 255);
        f2 = (int)(color2 & 255);
        
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
	
	public static void drawDot(float red, float green, float blue, float alpha, float factor, float size, Vec2f vector) {
		glColor4f(red, green, blue, alpha);

		glEnable(GL_POINT_SMOOTH);

		glPointSize(size * (factor / 2F));

		glBegin(GL_POINTS);

		glVertex3f(vector.x, vector.y, 0.0f);

		glEnd();
		glDisable(GL_POINT_SMOOTH);
	}
	
	public static void setColor(int par1, int par2, int par3, int par4) {

        hasColor = true;

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
            color = par4 << 24 | par3 << 16 | par2 << 8 | par1;
        
        else
            color = par1 << 24 | par2 << 16 | par3 << 8 | par4;

	}
	
	public static void drawDots(float red, float green, float blue, float alpha, float factor, Vec2f... vertices) {
		glColor4f(red, green, blue, alpha);

		glEnable(GL_POINT_SMOOTH);

		glPointSize(5.5F * (factor / 2F));

		glBegin(GL_POINTS);
		
		for(Vec2f vector : vertices) 
			glVertex3f(vector.x, vector.y, 0.0f);
		
		glEnd();
		glDisable(GL_POINT_SMOOTH);
	}
	
	public static void drawLine2D_2(float red, float green, float blue, float alpha, int factor, float size, Vec2f... vertices) {
		glEnable(GL_LINE_SMOOTH);

		glLineWidth(size * (factor / 2F));

		glBegin(GL_LINE_STRIP);
		glColor4f(red, green, blue, alpha);
		
		for(Vec2f vector : vertices) 
			glVertex3f(vector.x, vector.y, 0.0f);

		glEnd();
		glDisable(GL_LINE_SMOOTH);
		
		glEnable(GL_POINT_SMOOTH);
	
		glPointSize(size * (factor / 2F));

		glBegin(GL_POINTS);
		
		for(Vec2f vector : vertices)
			glVertex3f(vector.x, vector.y, 0.0f);

		glEnd();
		glDisable(GL_POINT_SMOOTH);
	}
	
	public static void drawLine2D(float red, float green, float blue, float alpha, int factor, Vec2f... vertices) {
		glEnable(GL_LINE_SMOOTH);
		if(!(factor == 1))
			glLineWidth(3.0F * (factor - 1));
		else
			glLineWidth(1F);

		glBegin(GL_LINE_STRIP);
		glColor4f(red, green, blue, alpha);
		
		for(Vec2f vector : vertices) 
			glVertex3f(vector.x, vector.y, 0.0f);
		
		glEnd();
		glDisable(GL_LINE_SMOOTH);
		
		
		glEnable(GL_POINT_SMOOTH);
		if(!(factor == 1))
			glPointSize(3.0F * (factor - 1));
		else
			glPointSize(1F);
		glBegin(GL_POINTS);
		
		for(Vec2f vector : vertices) 
			glVertex3f(vector.x, vector.y, 0.0f);

		glEnd();
		glDisable(GL_POINT_SMOOTH);
	}
	
	public static void drawRectRoundedUpper(float x1, float y1, float x2, float y2, int color) {
        float f = (float)(color >> 24 & 255) / 255.0F;
        float f1 = (float)(color >> 16 & 255) / 255.0F;
        float f2 = (float)(color >> 8 & 255) / 255.0F;
        float f3 = (float)(color & 255) / 255.0F;

        glEnable(GL_BLEND);
        glDisable(GL_ALPHA_TEST);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(f1, f2, f3, f);
        
        drawCircle(x1 + 10, y1 + 10, 10, 180F, 75);

        drawCircle(x2 - 10, y1 + 10, 10, 270F, 75);

        drawRect(x1 + 10, y1, x2 - 10, y1 + 10, null, false, null, false);
        
        drawRect(x1, y1 + 10, x2, y2, null, false, null, false);

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
    }

	public static void drawRectRoundedLower(float x1, float y1, float x2, float y2, int color) {
		final float f = (float)(color >> 24 & 255) / 255.0F;
        final float f1 = (float)(color >> 16 & 255) / 255.0F;
        final float f2 = (float)(color >> 8 & 255) / 255.0F;
        final float f3 = (float)(color & 255) / 255.0F;

        glEnable(GL_BLEND);
        glDisable(GL_ALPHA_TEST);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(f1, f2, f3, f);
        
        drawCircle(x1 + 10, y2 - 10, 10, 90F, 75);
        
        drawCircle(x2 - 10, y2 - 10, 10, 0F, 75);
        
        drawRect(x1, y1, x2, y2 - 10, null, false, null, false);
        
        drawRect(x1 + 10, y2 - 10, x2 - 10, y2, null, false, null, false);

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);    
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

	public static void drawCircleHollow(float cx, float cy, float r, float r2, float rotation, float percentage) {

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

			addVertex((float) posX2, (float) posY2, 0);
			
			addVertex((float) posX1, (float) posY1, 0);
			
	
			float lel = 1 - (r - r2) / r;
			
			
			addVertex((float) cx + (posX1 - cx) * lel, (float) cy + (posY1 - cy) * lel, 0);
			
			addVertex((float) cx + (posX2 - cx) * lel, (float) cy + (posY2 - cy) * lel, 0);

			draw(false);

			float tx = -y;
			float ty = x;

			x += tx * tangetialFactor;
			y += ty * tangetialFactor;

			x *= radialFactor;
			y *= radialFactor;
		}
	}

	public static void addVertex(float x, float y, float z) {
		if (hasColor)
			buffer[bufferIndex + 5] = color;
        
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
				glColorPointer(4, GL_UNSIGNED_BYTE, 32, byteBuffer);
				glEnableClientState(GL_COLOR_ARRAY);
			}
			
			byteBuffer.position(0);
			glVertexPointer(3, GL_FLOAT, 32, floatBuffer);
			glEnableClientState(GL_VERTEX_ARRAY);
			glDrawArrays(GL_QUADS, 0, 4);
			glDisableClientState(GL_VERTEX_ARRAY);
			
			if (hasColor)
                glDisableClientState(GL_COLOR_ARRAY);
			
		} else {
			intBuffer.clear();
			intBuffer.put(buffer, 0, 24);
			
			
			if (hasColor) {
				byteBuffer.position(20);
				glColorPointer(4, GL_UNSIGNED_BYTE, 32, byteBuffer);
				glEnableClientState(GL_COLOR_ARRAY);
			}
			
			
			byteBuffer.position(0);
			glVertexPointer(3, GL_FLOAT, 32, floatBuffer);
			glEnableClientState(GL_VERTEX_ARRAY);
			glDrawArrays(GL_TRIANGLES, 0, 3);
			glDisableClientState(GL_VERTEX_ARRAY);
			
			if (hasColor)
                glDisableClientState(GL_COLOR_ARRAY);

		}
		
		hasColor = false;

		reset();
	}

	private static void reset() {
		byteBuffer.clear();
		bufferIndex = 0;
	}
	
	public static void drawButton(float left, float top, float right, float bottom, int color, int color2, int border) {
		drawRect(left, top, right, bottom, color, true, null, false);
		drawRect(left + border, top + border, right - border, bottom - border, color2, true, null, false);
	}
	
	public static void drawScaledTex(float x, float y, int width, int height) {
		glBegin(GL_QUADS);
		glTexCoord2f(0, 1); glVertex3d(x, height + y, 0);
		glTexCoord2f(1, 1); glVertex3d(width + x, height + y, 0);
		glTexCoord2f(1, 0); glVertex3d(width + x, y, 0);
		glTexCoord2f(0, 0); glVertex3d(x, y, 0);
		glEnd();
    }
}