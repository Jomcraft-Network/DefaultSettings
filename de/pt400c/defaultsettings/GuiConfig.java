package de.pt400c.defaultsettings;

import static de.pt400c.defaultsettings.FileUtil.MC;
import java.nio.channels.ClosedByInterruptException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;

public class GuiConfig extends GuiScreen
{
	private final GuiScreen parentScreen;
    private HoverChecker hoverS;
    private HoverChecker hoverK;
    private HoverChecker hoverO;
    private GuiButton buttonS;
    private GuiButton buttonK;
    private GuiButton buttonO;
    private ExecutorService tpe = new ThreadPoolExecutor(1, 3, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private ButtonState[] cooldowns = new ButtonState[] {new ButtonState(false, 0), new ButtonState(false, 0), new ButtonState(false, 0)};

    public GuiConfig(GuiScreen parentScreen)
    {
        this.mc = MC;
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.controlList.add(this.buttonO = new GuiButton(0, this.width / 2 - 49, this.height / 2 - 20, 98, 20, "Save options"));
        this.controlList.add(this.buttonS = new GuiButton(1, this.width / 2 - 149, this.height / 2 - 20, 98, 20, "Save servers"));
        this.controlList.add(this.buttonK = new GuiButton(2, this.width / 2 + 51, this.height / 2 - 20, 98, 20, "Save keys"));
        this.controlList.add(new GuiButton(3, this.width / 2 - 49, this.height / 2 + 24, 98, 20, "Quit"));
        this.hoverS = new HoverChecker(this.buttonS, 0);
        this.hoverK = new HoverChecker(this.buttonK, 0);
        this.hoverO = new HoverChecker(this.buttonO, 0);
    }

    @Override
    public void onGuiClosed() {
    	Keyboard.enableRepeatEvents(false);
    	tpe.shutdownNow();
    }

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 2) {
			this.cooldowns[2].setProgress(true);
			tpe.execute(new Runnable() {
				@Override
				public void run() {
					try {
						saveKeys();
					} catch (ClosedByInterruptException e) {
						DefaultSettings.log.log(Level.FINEST, "An exception occurred while saving the key configuration: Interruption exception");
					}
				}
			});

		} else if (button.id == 0) {
			this.cooldowns[0].setProgress(true);
			tpe.execute(new Runnable() {
				@Override
				public void run() {
					try {
						saveOptions();
					} catch (ClosedByInterruptException e) {
						DefaultSettings.log.log(Level.FINEST, "An exception occurred while saving the default game options: Interruption exception");
					}
				}
			});

		} else if (button.id == 1) {
			this.cooldowns[1].setProgress(true);
			tpe.execute(new Runnable() {
				@Override
				public void run() {
					try {
						saveServers();
					} catch (ClosedByInterruptException e) {
						DefaultSettings.log.log(Level.FINEST, "An exception occurred while saving the server list: Interruption exception");
					}
				}
			});
		} else if (button.id == 3) {
			MC.displayGuiScreen(this.parentScreen);
		}
	}

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        for(int id = 0; id < cooldowns.length; id++) {
        	if(cooldowns[id].renderCooldown > 0)
        		cooldowns[id].renderCooldown--;
        	else if(cooldowns[id].renderCooldown < 0) 
        		cooldowns[id].renderCooldown++;	
        }
    }
    
    public void saveServers() throws ClosedByInterruptException {
    	try {
			FileUtil.saveServers();
			this.cooldowns[1].renderCooldown = 30;
		} catch (ClosedByInterruptException e){
			throw e;
		} catch (Exception e) {
			DefaultSettings.log.log(Level.SEVERE, "An exception occurred while saving the server list:", e);
			this.cooldowns[1].renderCooldown = -30;
		}
    	this.cooldowns[1].setProgress(false);
    }
    
    public void saveOptions() throws ClosedByInterruptException {
    	try {
			FileUtil.saveOptions();
			this.cooldowns[0].renderCooldown = 30;
		} catch (ClosedByInterruptException e){
			throw e;
		} catch (Exception e) {
			DefaultSettings.log.log(Level.SEVERE, "An exception occurred while saving the default game options:", e);
			this.cooldowns[0].renderCooldown = -30;
		}
    	this.cooldowns[0].setProgress(false);
    }
    
    public void saveKeys() throws ClosedByInterruptException {
    	try {
			FileUtil.saveKeys();
			this.cooldowns[2].renderCooldown = 30;
			FileUtil.restoreKeys();
		} catch (ClosedByInterruptException e){
			throw e;
		} catch (Exception e) {
			DefaultSettings.log.log(Level.SEVERE, "An exception occurred while saving the key configuration:", e);
			this.cooldowns[2].renderCooldown = -30;
		}
    	this.cooldowns[2].setProgress(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
    	
        this.drawDefaultBackground();
        this.drawGradientRect(10, 10, this.width - 10, this.height - 10, -1072689136, -804253680);
        this.drawCenteredString(this.fontRenderer, "- DefaultSettings -", this.width / 2, 20, 16777215);
        this.drawCenteredString(this.fontRenderer, "Control GUI", this.width / 2, 30, 16777215);
        
        buttonS.displayString = (cooldowns[1].getProgress() ? ColorEnum.GOLD : cooldowns[1].renderCooldown < 0 ? ColorEnum.RED : cooldowns[1].renderCooldown > 0 ? ColorEnum.GREEN : "") + "Save servers";
        buttonK.displayString = (cooldowns[2].getProgress() ? ColorEnum.GOLD : cooldowns[2].renderCooldown < 0 ? ColorEnum.RED : cooldowns[2].renderCooldown > 0 ? ColorEnum.GREEN : "") + "Save keys";
        buttonO.displayString = (cooldowns[0].getProgress() ? ColorEnum.GOLD : cooldowns[0].renderCooldown < 0 ? ColorEnum.RED : cooldowns[0].renderCooldown > 0 ? ColorEnum.GREEN : "") + "Save options";
        
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.hoverS.checkHover(mouseX, mouseY))
            this.drawToolTip(MC.fontRenderer.listFormattedStringToWidth("Save your servers", 300), mouseX, mouseY, MC.fontRenderer);
        if (this.hoverK.checkHover(mouseX, mouseY))
            this.drawToolTip(MC.fontRenderer.listFormattedStringToWidth("Save keybindings", 300), mouseX, mouseY, MC.fontRenderer);
        if (this.hoverO.checkHover(mouseX, mouseY))
            this.drawToolTip(MC.fontRenderer.listFormattedStringToWidth("Save all default game options", 300), mouseX, mouseY, MC.fontRenderer);
    }

    public void drawToolTip(List stringList, int x, int y, FontRenderer font)
    {
    	if (!stringList.isEmpty())
        {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            Iterator iterator = stringList.iterator();

            while (iterator.hasNext())
            {
                String s = (String) iterator.next();
                int l = font.getStringWidth(s);

                if (l > k)
                {
                    k = l;
                }
            }

            int j2 = x + 12;
            int k2 = y - 12;
            int i1 = 8;

            if (stringList.size() > 1)
            {
                i1 += 2 + (stringList.size() - 1) * 10;
            }

            if (j2 + k > this.width)
            {
                j2 -= 28 + k;
            }

            if (k2 + i1 + 6 > this.height)
            {
                k2 = this.height - i1 - 6;
            }

            this.zLevel = 300.0F;
            int j1 = -267386864;
            this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
            this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
            this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
            this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
            int k1 = 1347420415;
            int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
            this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
            this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
            this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

            for (int i2 = 0; i2 < stringList.size(); ++i2)
            {
                String s1 = (String) stringList.get(i2);
                font.drawStringWithShadow(s1, j2, k2, -1);

                if (i2 == 0)
                {
                    k2 += 2;
                }

                k2 += 10;
            }

            this.zLevel = 0.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }
    
    private class ButtonState {
    	
    	private boolean inProgress = false;
    	public int renderCooldown = 0;
    	
    	public ButtonState(boolean inProgress, int renderCooldown) {
			this.inProgress = inProgress;
			this.renderCooldown = renderCooldown;
		}
    	
    	public void setProgress(boolean inProgress) {
    		this.inProgress = inProgress;
    	}
    	
    	public boolean getProgress() {
    		return this.inProgress;
    	}
    	
    }
}