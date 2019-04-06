package de.pt400c.defaultsettings;

import static de.pt400c.defaultsettings.FileUtil.MC;
import java.nio.channels.ClosedByInterruptException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.client.config.HoverChecker;

public class GuiConfig extends GuiScreen
{
    public final GuiScreen parentScreen;
    public HoverChecker hoverS;
    public HoverChecker hoverK;
    public HoverChecker hoverO;
    public GuiButton buttonS;
    public GuiButton buttonK;
    public GuiButton buttonO;
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
        this.buttonList.add(this.buttonO = new GuiButton(0, this.width / 2 - 49, this.height / 2 - 20, 98, 20, "Save options"));
        this.buttonList.add(this.buttonS = new GuiButton(1, this.width / 2 - 149, this.height / 2 - 20, 98, 20, "Save servers"));
        this.buttonList.add(this.buttonK = new GuiButton(2, this.width / 2 + 51, this.height / 2 - 20, 98, 20, "Save keys"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 49, this.height / 2 + 24, 98, 20, "Quit"));
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
						DefaultSettings.log.log(Level.DEBUG, "An exception occurred while saving the key configuration: Interruption exception");
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
						DefaultSettings.log.log(Level.DEBUG, "An exception occurred while saving the default game options: Interruption exception");
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
						DefaultSettings.log.log(Level.DEBUG, "An exception occurred while saving the server list: Interruption exception");
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

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
    	
        this.drawDefaultBackground();
        this.drawGradientRect(10, 10, this.width - 10, this.height - 10, -1072689136, -804253680);
        this.drawCenteredString(this.fontRendererObj, "- DefaultSettings -", this.width / 2, 20, 16777215);
        this.drawCenteredString(this.fontRendererObj, "Control GUI", this.width / 2, 30, 16777215);
        
        buttonS.displayString = (cooldowns[1].getProgress() ? EnumChatFormatting.GOLD : cooldowns[1].renderCooldown < 0 ? EnumChatFormatting.RED : cooldowns[1].renderCooldown > 0 ? EnumChatFormatting.GREEN : "") + "Save servers";
        buttonK.displayString = (cooldowns[2].getProgress() ? EnumChatFormatting.GOLD : cooldowns[2].renderCooldown < 0 ? EnumChatFormatting.RED : cooldowns[2].renderCooldown > 0 ? EnumChatFormatting.GREEN : "") + "Save keys";
        buttonO.displayString = (cooldowns[0].getProgress() ? EnumChatFormatting.GOLD : cooldowns[0].renderCooldown < 0 ? EnumChatFormatting.RED : cooldowns[0].renderCooldown > 0 ? EnumChatFormatting.GREEN : "") + "Save options";
        
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.hoverS.checkHover(mouseX, mouseY))
            this.drawToolTip(MC.fontRendererObj.listFormattedStringToWidth("Save your servers", 300), mouseX, mouseY);
        if (this.hoverK.checkHover(mouseX, mouseY))
            this.drawToolTip(MC.fontRendererObj.listFormattedStringToWidth("Save keybindings", 300), mouseX, mouseY);
        if (this.hoverO.checkHover(mouseX, mouseY))
            this.drawToolTip(MC.fontRendererObj.listFormattedStringToWidth("Save all default game options", 300), mouseX, mouseY);
    }
    
    public void drawToolTip(List stringList, int x, int y)
    {
    	GuiUtils.drawHoveringText(stringList, x, y, width, height, 300, fontRendererObj);
    }
    
    public void saveServers() throws ClosedByInterruptException {
    	try {
			FileUtil.saveServers();
			this.cooldowns[1].renderCooldown = 30;
		} catch (ClosedByInterruptException e){
			throw e;
		} catch (Exception e) {
			DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
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
			DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the default game options:", e);
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
			DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
			this.cooldowns[2].renderCooldown = -30;
		}
    	this.cooldowns[2].setProgress(false);
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