package de.pt400c.defaultsettings;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

/**
 * @author Jomcraft Network (PT400C)
 * @category NeptuneFX
 */
public class NEX {
	
	public static void color4f(float f, float f1, float f2, float f3) {
		GL11.glColor4f(f, f1, f2, f3);
	}
	
	public static void color3f(float f, float f1, float f2) {
		GL11.glColor3f(f, f1, f2);
	}
	
	public static void en(int i) {
		GL11.glEnable(i);
	}
	
	public static void dis(int i) {
		GL11.glDisable(i);
	}
	
	public static void blend(int i, int i1) {
		GL11.glBlendFunc(i, i1);
	}
	
	public static void pushMX() {
		GL11.glPushMatrix();
	}
	
	public static void popMX() {
		GL11.glPopMatrix();
	}
	
	public static void scalef(float f, float f1, float f2) {
		GL11.glScalef(f, f1, f2);
	}
	
	public static void blendSep(int i, int i1, int i2, int i3) {
		GL14.glBlendFuncSeparate(i, i1, i2, i3);
	}

	public static void clear(int i) {
		GL11.glClear(i);
	}

	public static void bindFBO(int i, int i1) {
		GL30.glBindFramebuffer(i, i1);
	}

	public static void blitFBO(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
		GL30.glBlitFramebuffer(i, i1, i2, i3, i4, i5, i6, i7, i8, i9);
	}

	public static void bindTex(int i, int i1) {
		GL11.glBindTexture(i, i1);	
	}

	public static void begin(int i) {
		GL11.glBegin(i);
	}

	public static void texParI(int i, int i1, int i2) {
		GL11.glTexParameteri(i, i1, i2);	
	}

	public static void texCrd2f(float f, float f1) {
		GL11.glTexCoord2f(f, f1);
	}

	public static void end() {
		GL11.glEnd();
	}

	public static void vert3f(float f, float f1, float f2) {
		GL11.glVertex3d(f, f1, f2);
	}

	public static void shadeModel(int i) {
		GL11.glShadeModel(i);
	}

	public static void scissor(int i, int i1, int i2, int i3) {
		GL11.glScissor(i, i1, i2, i3);
	}

	public static void disState(int i) {
		GL11.glDisableClientState(i);
	}
	
	public static void enState(int i) {
		GL11.glEnableClientState(i);
	}

	public static void colorPointer(int i, boolean b, int i1, ByteBuffer buffer) {
		GL11.glColorPointer(i, b, i1, buffer);
	}

	public static void vtPointer(int i, int i1, FloatBuffer buffer) {
		GL11.glVertexPointer(i, i1, buffer);
	}

	public static void drawArrays(int i, int i1, int i2) {
		GL11.glDrawArrays(i, i1, i2);
	}

	public static void pointSize(float f) {
		GL11.glPointSize(f);
	}

	public static void lineWidth(float f) {
		GL11.glLineWidth(f);
	}

	public static void matrixMode(int i) {
		GL11.glMatrixMode(i);
	}

	public static void loadIdentity() {
		GL11.glLoadIdentity();
	}

	public static void ortho(double d, double d1, double d2, double d3, double d4, double d5) {
		GL11.glOrtho(d, d1, d2, d3, d4, d5);
	}

	public static void translatef(float f, float f1, float f2) {
		GL11.glTranslatef(f, f1, f2);
	}
}
