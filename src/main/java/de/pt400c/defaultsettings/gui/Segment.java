package de.pt400c.defaultsettings.gui;

import static de.pt400c.defaultsettings.FileUtil.MC;
import static de.pt400c.defaultsettings.FileUtil.devEnv;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Bidi;
import java.util.logging.Level;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.input.Mouse;
import de.pt400c.defaultsettings.DefaultSettings;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
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
	protected final boolean isPopupSegment;
	
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
