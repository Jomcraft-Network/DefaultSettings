package de.pt400c.defaultsettings.font;

import static de.pt400c.defaultsettings.FileUtil.MC;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import static org.lwjgl.opengl.GL11.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.logging.Level;
import net.minecraft.client.resources.Resource;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.client.resources.ResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL30;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.pt400c.defaultsettings.DefaultSettings;
import de.pt400c.defaultsettings.FileUtil;

@SideOnly(Side.CLIENT)
public class FontRendererClass implements ResourceManagerReloadListener {
    protected final float[] charWidthBold = new float[256];
    protected final float[] charYOffBold = new float[256];
    protected final float[] charWidth = new float[256];
    protected final float[] charYOff = new float[256];
    private static final ResourceLocation bold_tex = new ResourceLocation(DefaultSettings.MODID, "textures/gui/ascii_bold.png");
    private static final ResourceLocation tex = new ResourceLocation(DefaultSettings.MODID, "textures/gui/ascii.png");
    public int FONT_HEIGHT = 9;
    private final int[] colorCode = new int[32];
    protected float posX;
    protected float posY;
    private float red;
    private float blue;
    private float green;
    private float alpha;
    private int textColor;

    public FontRendererClass() {

    	bindTexture(false);
        GL30.glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        bindTexture(true);
        GL30.glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        for (int i = 0; i < 32; ++i) {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i >> 0 & 1) * 170 + j;

            if (i == 6) 
                k += 85;

            if (i >= 16) {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }

            this.colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
        }
    }
    
    @Override
	public void onResourceManagerReload(ResourceManager resourcemanager) {
    	this.readFontTexture();
		
	}

    private void readFontTexture() {
    	BitmapProperties mainJson = new BitmapProperties();
    	
    	InputStream stream = DefaultSettings.class.getClassLoader().getResourceAsStream("assets/defaultsettings/textures/gui/charProperties.json");

		try (Reader reader = new InputStreamReader(stream)) {
			mainJson = FileUtil.gson.fromJson(reader, BitmapProperties.class);

		} catch (Exception e) {
			DefaultSettings.log.log(Level.SEVERE, "Exception at processing fonts: ", e);
		}

        BitmapInfo[] map = mainJson.info;
        
        HashMap<Integer, BitmapInfo> list = new HashMap<Integer, BitmapInfo>();
        
        for(BitmapInfo info : map) {
        	list.put(info.id, info);
        }
        
        for(Entry<Integer, BitmapInfo> id : list.entrySet()) {
        	  this.charWidthBold[id.getKey()] = id.getValue().x;
              this.charYOffBold[id.getKey()] = id.getValue().y;
        }

        mainJson = new BitmapProperties();
    	
    	stream = DefaultSettings.class.getClassLoader().getResourceAsStream("assets/defaultsettings/textures/gui/charProperties_nonbold.json");

		try (Reader reader = new InputStreamReader(stream)) {
			mainJson = FileUtil.gson.fromJson(reader, BitmapProperties.class);

		} catch (Exception e) {
			DefaultSettings.log.log(Level.SEVERE, "Exception at processing fonts: ", e);
		}

        map = mainJson.info;
  
        list = new HashMap<Integer, BitmapInfo>();
        
        for(BitmapInfo info : map) {
        	list.put(info.id, info);
        }
        
        for(Entry<Integer, BitmapInfo> id : list.entrySet()) {
        	  this.charWidth[id.getKey()] = id.getValue().x;
              this.charYOff[id.getKey()] = id.getValue().y;
        }
    }

    private float renderChar(char ch, float factor, boolean bold) {
        if (ch == 160) return bold ? 4.0F : 3.5F; // forge: display nbsp as space. MC-2595
        if (ch == ' ') {
            return bold ? 4.0F : 3.5F;
        } else {
            int i = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?OABCDEFGHIJKLMN@PQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(ch);
            return this.renderDefaultChar(i, factor, bold);
        }
    }

    protected float renderDefaultChar(int ch, float factor, boolean bold) {
        int i = ch % 16 * 8;
        int j = ch / 16 * 8;
        bindTexture(bold);
        float l = bold ? this.charWidthBold[ch] : this.charWidth[ch];
        float f = (float)l - 0.01F;
        float offY = bold ? this.charYOffBold[ch] : this.charYOff[ch];
        glBegin(5);
        glTexCoord2f((float)i / 128.0F, (float)j / 128.0F);
        glVertex3f(this.posX, this.posY + offY * factor, 0.0F);
        glTexCoord2f((float)i / 128.0F, ((float)j + 7.99F) / 128.0F);
        glVertex3f(this.posX, this.posY + (7.99F + offY ) * factor, 0.0F);
        glTexCoord2f(((float)i + f - 1.0F) / 128.0F, (float)j / 128.0F);
        glVertex3f(this.posX + (f - 1.0F) * factor, this.posY + offY * factor, 0.0F);
        glTexCoord2f(((float)i + f - 1.0F) / 128.0F, ((float)j + 7.99F) / 128.0F);
        glVertex3f(this.posX + (f - 1.0F) * factor, this.posY + (7.99F + offY) * factor, 0.0F);
        glEnd();
        return (float)l;
    }

    public int drawString(String text, int x, int y, int color, float factor, boolean bold) {
        return this.drawString(text, (float)x, (float)y, color, factor, bold);
    }

    public int drawString(String text, float x, float y, int color, float factor, boolean bold) {
        enableAlpha();
        glEnable(GL_BLEND);
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        MC.getTextureManager().bindTexture(!bold ? bold_tex : tex);
        return this.renderString(text, x, y, color, factor, bold);
    }

    private void renderStringAtPos(String text, float factor, boolean bold) {
        for (int i = 0; i < text.length(); ++i) {
            char c0 = text.charAt(i);

            if (c0 == 167 && i + 1 < text.length()) {
                int i1 = "0123456789abcdefklmnor".indexOf(String.valueOf(text.charAt(i + 1)).toLowerCase(Locale.ROOT).charAt(0));

                if (i1 < 16) {

                    if (i1 < 0 || i1 > 15) {
                        i1 = 15;
                    }

                    int j1 = this.colorCode[i1];
                    this.textColor = j1;
                    setColor((float)(j1 >> 16) / 255.0F, (float)(j1 >> 8 & 255) / 255.0F, (float)(j1 & 255) / 255.0F, this.alpha);
                }else if (i1 == 21) {
                    setColor(this.red, this.blue, this.green, this.alpha);
                }

                ++i;
            }
            else {
                int j = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?OABCDEFGHIJKLMN@PQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(c0);

                float f1 = j == -1 || false ? 0.5f : 1f;
                boolean flag = (c0 == 0 || j == -1 || false);

                if (flag) {
                    this.posX -= f1;
                    this.posY -= f1;
                }

                float f = this.renderChar(c0, factor, bold);

                if (flag) {
                    this.posX += f1;
                    this.posY += f1;
                }
                doDraw(f, factor);
            }
        }
    }

    protected void doDraw(float f, float factor) {
    	this.posX += (float)((int)f * factor); 
    }

    private int renderStringAligned(String text, int x, int y, int width, int color, float factor, boolean bold) {
        return this.renderString(text, (float)x, (float)y, color, factor, bold);
    }
    
    private int renderString(String text, float x, float y, int color, float factor, boolean bold) {
        if (text == null) {
    		glDisable(GL_BLEND);
            return 0;
        } else {

            if ((color & -67108864) == 0) {
                color |= -16777216;
            }

            this.red = (float)(color >> 16 & 255) / 255.0F;
            this.blue = (float)(color >> 8 & 255) / 255.0F;
            this.green = (float)(color & 255) / 255.0F;
            this.alpha = (float)(color >> 24 & 255) / 255.0F;
            setColor(this.red, this.blue, this.green, this.alpha);
            this.posX = x;
            this.posY = y;
            this.renderStringAtPos(text, factor, bold);
    		glDisable(GL_BLEND);
            return (int)this.posX;
        }
    }

    public float getStringWidth(String text, float factor, boolean bold) {
        if (text == null) {
            return 0;
        } else {
            int i = 0;
            boolean flag = false;

            for (int j = 0; j < text.length(); ++j) {
                char c0 = text.charAt(j);
                float k = this.getCharWidth(c0, bold);

                if (k < 0 && j < text.length() - 1) {
                    ++j;
                    c0 = text.charAt(j);

                    if (c0 != 'l' && c0 != 'L') {
                        if (c0 == 'r' || c0 == 'R') {
                            flag = false;
                        }
                    } else {
                        flag = true;
                    }

                    k = 0;
                }

                i += k;

                if (flag && k > 0) {
                    ++i;
                }
            }

            return i * factor;
        }
    }

    public float getCharWidth(char character, boolean bold) {
        if (character == 160) return 4; // forge: display nbsp as space. MC-2595
        if (character == 167) {
            return -1;
        } else if (character == ' ') {
            return 3;
        } else {
            int i = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?OABCDEFGHIJKLMN@PQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(character);

            if (character > 0 && i != -1) {
                return bold ? this.charWidthBold[i] : this.charWidth[i];
            } else {
                return 0;
            }
        }
    }

    public String trimStringToWidth(String text, int width, boolean bold) {
        return this.trimStringToWidth(text, width, false, bold);
    }

    public String trimStringToWidth(String text, int width, boolean reverse, boolean bold) {
        StringBuilder stringbuilder = new StringBuilder();
        int i = 0;
        int j = reverse ? text.length() - 1 : 0;
        int k = reverse ? -1 : 1;
        boolean flag = false;
        boolean flag1 = false;

        for (int l = j; l >= 0 && l < text.length() && i < width; l += k) {
            char c0 = text.charAt(l);
            float i1 = this.getCharWidth(c0, bold);

            if (flag) {
                flag = false;

                if (c0 != 'l' && c0 != 'L') {
                    if (c0 == 'r' || c0 == 'R') {
                        flag1 = false;
                    }
                } else {
                    flag1 = true;
                }
            } else if (i1 < 0) {
                flag = true;
            } else {
                i += i1;

                if (flag1) {
                    ++i;
                }
            }

            if (i > width) {
                break;
            }

            if (reverse) {
                stringbuilder.insert(0, c0);
            } else {
                stringbuilder.append(c0);
            }
        }

        return stringbuilder.toString();
    }

    private String trimStringNewline(String text) {
        while (text != null && text.endsWith("\n")) {
            text = text.substring(0, text.length() - 1);
        }

        return text;
    }
    
    public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor, float factor, boolean bold) {
        this.textColor = textColor;
        str = this.trimStringNewline(str);
        this.renderSplitString(str, x, y, wrapWidth, factor, bold);
    }

    private void renderSplitString(String str, int x, int y, int wrapWidth, float factor, boolean bold) {
        for (String s : this.listFormattedStringToWidth(str, wrapWidth, bold)) {
            this.renderStringAligned(s, x, y, wrapWidth, this.textColor, factor, bold);
            y += this.FONT_HEIGHT;
        }
    }

    public int getWordWrappedHeight(String str, int maxLength, boolean bold) {
        return this.FONT_HEIGHT * this.listFormattedStringToWidth(str, maxLength, bold).size();
    }

    public List<String> listFormattedStringToWidth(String str, int wrapWidth, boolean bold) {
    	List<String> l = Arrays.<String>asList(this.wrapFormattedStringToWidth(str, wrapWidth, bold).split("\n"));
        return l;
    }

    String wrapFormattedStringToWidth(String str, int wrapWidth, boolean bold) {
        int i = this.sizeStringToWidth(str, wrapWidth, bold);
 
        if (str.length() <= i) {
            return str;
        } else {
            String s = str.substring(0, i);
            char c0 = str.charAt(i);
            boolean flag = c0 == ' ' || c0 == '\n';
            String s1 = getFormatFromString(s) + str.substring(i + (flag ? 1 : 0));
            return s + "\n" + this.wrapFormattedStringToWidth(s1, wrapWidth, bold);
        }
    }

    public int sizeStringToWidth(String str, int wrapWidth, boolean bold) {
        int j = 0;
        int k;
        int l = -1;
        boolean flag = false;
        for (k = 0; k < str.length(); ++k) {
            char c0 = str.charAt(k);

            switch (c0) {
                case '\n':
                    --k;
                    break;
                case ' ':
                    l = k;
                default:
                    j += this.getCharWidth(c0, bold);

                    if (flag) {
                        ++j;
                    }

                    break;
                case '\u00a7':

                    if (k < str.length() - 1) {
                        ++k;
                        char c1 = str.charAt(k);

                        if (c1 != 'l' && c1 != 'L') {
                            if (c1 == 'r' || c1 == 'R' || isFormatColor(c1)) {
                                flag = false;
                            }
                        }
                        else {
                            flag = true;
                        }
                    }
            }

            if (c0 == '\n') {
                ++k;
                l = k;
                break;
            }

            if (j > wrapWidth) {
                break;
            }
        }

        return k != str.length() && l != -1 && l < k ? l : k;
    }

    private static boolean isFormatColor(char colorChar) {
        return colorChar >= '0' && colorChar <= '9' || colorChar >= 'a' && colorChar <= 'f' || colorChar >= 'A' && colorChar <= 'F';
    }

    private static boolean isFormatSpecial(char formatChar) {
        return formatChar >= 'k' && formatChar <= 'o' || formatChar >= 'K' && formatChar <= 'O' || formatChar == 'r' || formatChar == 'R';
    }

    public static String getFormatFromString(String text) {
        String s = "";
        int i = -1;
        int j = text.length();

        while ((i = text.indexOf(167, i + 1)) != -1) {
            if (i < j - 1) {
                char c0 = text.charAt(i + 1);

                if (isFormatColor(c0)) {
                    s = "\u00a7" + c0;
                } else if (isFormatSpecial(c0)) {
                    s = s + "\u00a7" + c0;
                }
            }
        }

        return s;
    }

    protected void setColor(float r, float g, float b, float a) {
    	glColor4f(r, g, b, a);
    }

    protected void enableAlpha() {
    	glEnable(GL_ALPHA_TEST);
    }

    protected void bindTexture(boolean bold) {
    	MC.getTextureManager().bindTexture(bold ? bold_tex : tex);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }

    protected Resource getResource(ResourceLocation location) throws IOException {
        return MC.getResourceManager().getResource(location);
    }

    public int getColorCode(char character) {
        int i = "0123456789abcdef".indexOf(character);
        return i >= 0 && i < this.colorCode.length ? this.colorCode[i] : -1;
    } 
    
    public static class BitmapProperties {	
    	public static transient final long serialVersionUID = 491247L;
    	public BitmapInfo[] info = new BitmapInfo[256];
    }
    
    public static class BitmapInfo {
    	
    	public final int id;
    	public final float x;
    	public final float y;
    	
    	public BitmapInfo(int id, float x, float y) {
			this.id = id;
			this.x = x;
			this.y = y;
		}
    }
}