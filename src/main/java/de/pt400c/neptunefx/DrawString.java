package de.pt400c.neptunefx;

import static de.pt400c.defaultsettings.FileUtil.MC;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.logging.log4j.Level;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import de.pt400c.defaultsettings.DefaultSettings;
import de.pt400c.defaultsettings.FileUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;

public class DrawString {

	public static int drawString(String p_85187_1_, float p_85187_2_, float p_85187_3_, int p_85187_4_, boolean p_85187_5_) {
		glEnable(GL_ALPHA_TEST);
        resetStyles();
        int l = renderString(p_85187_1_, p_85187_2_, p_85187_3_, p_85187_4_, false);
        return l;
    }
	
	//TODO: Just use AccessTransformers? Why not??
	
	public static void resetStyles() {
		try {
			Method resetStyles = FontRenderer.class.getDeclaredMethod(FileUtil.isDev ? "resetStyles" : "func_78265_b", new Class<?>[0]);
			resetStyles.setAccessible(true);
			resetStyles.invoke(MC.fontRenderer, new Object[0]);
		} catch (IllegalAccessException e) {
			DefaultSettings.log.log(Level.ERROR, "Something went wrong :(", e);
		} catch (IllegalArgumentException e) {
			DefaultSettings.log.log(Level.ERROR, "Something went wrong :(", e);
		} catch (InvocationTargetException e) {
			DefaultSettings.log.log(Level.ERROR, "Something went wrong :(", e);
		} catch (NoSuchMethodException e) {
			DefaultSettings.log.log(Level.ERROR, "Something went wrong :(", e);
		} catch (SecurityException e) {
			DefaultSettings.log.log(Level.ERROR, "Something went wrong :(", e);
		}
	}
	
	public static int renderString(String p_78258_1_, float p_78258_2_, float p_78258_3_, int p_78258_4_, boolean p_78258_5_) {
		
		if (p_78258_1_ == null)
        {
            return 0;
        }
        else
        {
            if (MC.fontRenderer.getBidiFlag())
            {
                p_78258_1_ = bidiReorder(p_78258_1_);
            }

            if ((p_78258_4_ & -67108864) == 0)
            {
                p_78258_4_ |= -16777216;
            }

            if (p_78258_5_)
            {
                p_78258_4_ = (p_78258_4_ & 16579836) >> 2 | p_78258_4_ & -16777216;
            }

            MC.fontRenderer.red = (float)(p_78258_4_ >> 16 & 255) / 255.0F;
            MC.fontRenderer.blue = (float)(p_78258_4_ >> 8 & 255) / 255.0F;
            MC.fontRenderer.green = (float)(p_78258_4_ & 255) / 255.0F;
            MC.fontRenderer.alpha = (float)(p_78258_4_ >> 24 & 255) / 255.0F;
            glColor4f(MC.fontRenderer.red, MC.fontRenderer.blue, MC.fontRenderer.green, MC.fontRenderer.alpha);
            MC.fontRenderer.posX = (float)p_78258_2_;
            MC.fontRenderer.posY = (float)p_78258_3_;
            renderStringAtPos(p_78258_1_, p_78258_5_);
            return (int) MC.fontRenderer.posX;
        }
		
	}
	
	private static String bidiReorder(String p_147647_1_)
    {
        try
        {
            Bidi bidi = new Bidi((new ArabicShaping(8)).shape(p_147647_1_), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        }
        catch (ArabicShapingException arabicshapingexception)
        {
            return p_147647_1_;
        }
    }
	
	private static void renderStringAtPos(String p_78255_1_, boolean p_78255_2_) {
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
                	MC.fontRenderer.randomStyle = false;
                    MC.fontRenderer.boldStyle = false;
                    MC.fontRenderer.strikethroughStyle = false;
                    MC.fontRenderer.underlineStyle = false;
                    MC.fontRenderer.italicStyle = false;

                    if (j < 0 || j > 15)
                    {
                        j = 15;
                    }

                    if (p_78255_2_)
                    {
                        j += 16;
                    }

                    k = MC.fontRenderer.colorCode[j];
                    MC.fontRenderer.textColor = k;
                    glColor4f((float)(k >> 16) / 255.0F, (float)(k >> 8 & 255) / 255.0F, (float)(k & 255) / 255.0F, MC.fontRenderer.alpha);
                }
                else if (j == 16)
                {
                	MC.fontRenderer.randomStyle = true;
                }
                else if (j == 17)
                {
                	MC.fontRenderer.boldStyle = true;
                }
                else if (j == 18)
                {
                	MC.fontRenderer.strikethroughStyle = true;
                }
                else if (j == 19)
                {
                	MC.fontRenderer.underlineStyle = true;
                }
                else if (j == 20)
                {
                	MC.fontRenderer.italicStyle = true;
                }
                else if (j == 21)
                {
                	MC.fontRenderer.randomStyle = false;
                    MC.fontRenderer.boldStyle = false;
                    MC.fontRenderer.strikethroughStyle = false;
                    MC.fontRenderer.underlineStyle = false;
                    MC.fontRenderer.italicStyle = false;
                    glColor4f(MC.fontRenderer.red, MC.fontRenderer.blue, MC.fontRenderer.green, MC.fontRenderer.alpha);
                }

                ++i;
            }
            else
            {
                j = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(c0);

                if (MC.fontRenderer.randomStyle && j != -1)
                {
                    do
                    {
                        k = MC.fontRenderer.fontRandom.nextInt(MC.fontRenderer.charWidth.length);
                    }
                    while (MC.fontRenderer.charWidth[j] != MC.fontRenderer.charWidth[k]);

                    j = k;
                }

                float f1 = MC.fontRenderer.getUnicodeFlag() ? 0.5F : 1.0F;
                boolean flag1 = (c0 == 0 || j == -1 || MC.fontRenderer.getUnicodeFlag()) && p_78255_2_;

                if (flag1)
                {
                	MC.fontRenderer.posX -= f1;
                	MC.fontRenderer.posY -= f1;
                }

                float f = renderCharAtPos(j, c0, MC.fontRenderer.italicStyle);

                if (flag1)
                {
                	MC.fontRenderer.posX += f1;
                	MC.fontRenderer.posY += f1;
                }

                if (MC.fontRenderer.boldStyle)
                {
                	MC.fontRenderer.posX += f1;

                    if (flag1)
                    {
                    	MC.fontRenderer.posX -= f1;
                    	MC.fontRenderer.posY -= f1;
                    }

                    renderCharAtPos(j, c0, MC.fontRenderer.italicStyle);
                    MC.fontRenderer.posX -= f1;

                    if (flag1)
                    {
                    	MC.fontRenderer.posX += f1;
                    	MC.fontRenderer.posY += f1;
                    }

                    ++f;
                }

                doDraw(f);
            }
        }
    }
	
	protected static void doDraw(float f)
    {
        {
            {
                Tessellator tessellator;

                if (MC.fontRenderer.strikethroughStyle)
                {
                    tessellator = Tessellator.instance;
                    glDisable(GL_TEXTURE_2D);
                    tessellator.startDrawingQuads();
                    tessellator.addVertex(MC.fontRenderer.posX, MC.fontRenderer.posY + (float)(MC.fontRenderer.FONT_HEIGHT / 2), 0.0D);
                    tessellator.addVertex(MC.fontRenderer.posX + f, MC.fontRenderer.posY + (float)(MC.fontRenderer.FONT_HEIGHT / 2), 0.0D);
                    tessellator.addVertex(MC.fontRenderer.posX + f, MC.fontRenderer.posY + (float)(MC.fontRenderer.FONT_HEIGHT / 2) - 1.0F, 0.0D);
                    tessellator.addVertex(MC.fontRenderer.posX, MC.fontRenderer.posY + (float)(MC.fontRenderer.FONT_HEIGHT / 2) - 1.0F, 0.0D);
                    tessellator.draw();
                    glEnable(GL_TEXTURE_2D);
                }

                if (MC.fontRenderer.underlineStyle)
                {
                    tessellator = Tessellator.instance;
                    glDisable(GL_TEXTURE_2D);
                    tessellator.startDrawingQuads();
                    int l = MC.fontRenderer.underlineStyle ? -1 : 0;
                    tessellator.addVertex(MC.fontRenderer.posX + (float)l, MC.fontRenderer.posY + (float)MC.fontRenderer.FONT_HEIGHT, 0.0D);
                    tessellator.addVertex(MC.fontRenderer.posX + f, MC.fontRenderer.posY + (float)MC.fontRenderer.FONT_HEIGHT, 0.0D);
                    tessellator.addVertex(MC.fontRenderer.posX + f, MC.fontRenderer.posY + (float)MC.fontRenderer.FONT_HEIGHT - 1.0F, 0.0D);
                    tessellator.addVertex(MC.fontRenderer.posX + (float)l, MC.fontRenderer.posY + (float)MC.fontRenderer.FONT_HEIGHT - 1.0F, 0.0D);
                    tessellator.draw();
                    glEnable(GL_TEXTURE_2D);
                }

                MC.fontRenderer.posX += (float)((int)f);
            }
        }
    }
	
	private static float renderCharAtPos(int p_78278_1_, char p_78278_2_, boolean p_78278_3_) {
        return p_78278_2_ == 32 ? 4.0F : ("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(p_78278_2_) != -1 && !MC.fontRenderer.getUnicodeFlag() ? renderDefaultChar(p_78278_1_, p_78278_3_) : renderUnicodeChar(p_78278_2_, p_78278_3_));
    }

	private static float renderUnicodeChar(char p_78278_2_, boolean p_78278_3_) {
		try {
			Method renderUnicodeChar = FontRenderer.class.getDeclaredMethod(FileUtil.isDev ? "renderUnicodeChar" : "func_78277_a", char.class, boolean.class);
			renderUnicodeChar.setAccessible(true);
			return (Float) renderUnicodeChar.invoke(MC.fontRenderer, p_78278_2_, p_78278_3_);
		} catch (NoSuchMethodException e) {
			DefaultSettings.log.log(Level.ERROR, "Something went wrong :(", e);
		} catch (IllegalAccessException e) {
			DefaultSettings.log.log(Level.ERROR, "Something went wrong :(", e);
		} catch (IllegalArgumentException e) {
			DefaultSettings.log.log(Level.ERROR, "Something went wrong :(", e);
		} catch (InvocationTargetException e) {
			DefaultSettings.log.log(Level.ERROR, "Something went wrong :(", e);

		}
		return 0;
		
	}

	private static float renderDefaultChar(int p_78278_1_, boolean p_78278_3_) {
		
		try {
			Method renderDefaultChar = FontRenderer.class.getDeclaredMethod(FileUtil.isDev ? "renderDefaultChar" : "func_78266_a", int.class, boolean.class);
			renderDefaultChar.setAccessible(true);
			return (Float) renderDefaultChar.invoke(MC.fontRenderer, p_78278_1_, p_78278_3_);
		} catch (NoSuchMethodException e) {
			DefaultSettings.log.log(Level.ERROR, "Something went wrong :(", e);
		} catch (IllegalAccessException e) {
			DefaultSettings.log.log(Level.ERROR, "Something went wrong :(", e);
		} catch (IllegalArgumentException e) {
			DefaultSettings.log.log(Level.ERROR, "Something went wrong :(", e);
		} catch (InvocationTargetException e) {
			DefaultSettings.log.log(Level.ERROR, "Something went wrong :(", e);

		}
		return 0;
	}
}