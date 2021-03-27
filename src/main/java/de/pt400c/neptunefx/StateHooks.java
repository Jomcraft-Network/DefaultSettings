package de.pt400c.neptunefx;

import java.nio.IntBuffer;

import com.mojang.blaze3d.platform.GlStateManager;

@SuppressWarnings("deprecation")
public class StateHooks {

	public static void color4f(float f, float g, float h, float i) {
		GlStateManager.color4f(f, g, i, h);
	}

	public static void enableTexture() {
		GlStateManager.enableTexture();	
	}

	public static void clear(int i, boolean b) {
		GlStateManager.clear(i, b);
	}

	public static void viewport(int i, int j, int bufferWidth, int bufferHeight) {
		GlStateManager.viewport(i, j, bufferWidth, bufferHeight);
	}

	public static void bindFramebuffer(int glFramebuffer, int msFbo) {
		GlStateManager.bindFramebuffer(msFbo, glFramebuffer);
	}

	public static void enableBlend() {
		GlStateManager.enableBlend();
	}

	public static void bindTexture(int texture) {
		GlStateManager.bindTexture(texture);
	}

	public static void texParameter(int glTexture2d, int glTextureMinFilter, int glNearest) {
		GlStateManager.texParameter(glTexture2d, glTextureMinFilter, glNearest);
	}

	public static void disableAlphaTest() {
		GlStateManager.disableAlphaTest();
	}

	public static void glBlendFuncSeparate(int glSrcAlpha, int glOneMinusSrcAlpha, int glOne, int glZero) {
		GlStateManager.glBlendFuncSeparate(glSrcAlpha, glOneMinusSrcAlpha, glOne, glZero);
	}

	public static void enableAlphaTest() {
		GlStateManager.enableAlphaTest();
	}

	public static void disableBlend() {
		GlStateManager.disableBlend();
	}

	public static void disableTexture() {
		GlStateManager.disableTexture();
	}

	public static void deleteFramebuffers(int fbo) {
		GlStateManager.deleteFramebuffers(fbo);
	}

	public static void deleteTexture(int texture) {
		GlStateManager.deleteTexture(texture);
	}

	public static int genFramebuffers() {
		return GlStateManager.genFramebuffers();
	}

	public static int genTexture() {
		return GlStateManager.genTexture();
	}

	public static void texImage2D(int glTexture2d, int i, int glRgba8, int width, int height, int j, int glRgba, int glUnsignedByte, IntBuffer intBuffer) {
		GlStateManager.texImage2D(glTexture2d, glRgba8, i, width, height, j, glRgba, glUnsignedByte, intBuffer);
	}

	public static void framebufferTexture2D(int glFramebuffer, int glColorAttachment0, int glTexture2d, int texture, int i) {
		GlStateManager.framebufferTexture2D(glFramebuffer, glColorAttachment0, glTexture2d, texture, i);
	}
}
