package de.pt400c.defaultsettings;

import static de.pt400c.defaultsettings.FileUtil.MC;

import java.awt.Color;
import java.nio.channels.ClosedByInterruptException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;
import de.pt400c.defaultsettings.gui.ButtonMenuSegment;
import de.pt400c.defaultsettings.gui.ButtonSegment;
import de.pt400c.defaultsettings.gui.DefaultSettingsGUI;
import de.pt400c.defaultsettings.gui.MenuArea;
import de.pt400c.defaultsettings.gui.MenuScreen;
import de.pt400c.defaultsettings.gui.QuitButtonSegment;
import de.pt400c.defaultsettings.gui.SplitterSegment;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;

public class GuiConfig extends DefaultSettingsGUI {
    public final GuiScreen parentScreen;
    private MenuScreen menu;
    public ButtonSegment buttonS;
    public ButtonSegment buttonK;
    public ButtonSegment buttonO;
    public ButtonMenuSegment selectedSegment = null;
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
        this.clearSegments();
    	this.menu = new MenuScreen(this, 74, 25);

    	this.addSegment(this.menu.
        		addVariant(new MenuArea(this, 74, 25).
        				addChild(this.buttonO = new ButtonSegment(this, this.menu.getWidth() / 2 - 40, this.menu.getHeight() / 2 - 30, "Save options", button -> {
        					GuiConfig.this.cooldowns[0].setProgress(true);
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
        				return true;
                
        	}, 80, 25, 3, "Save all default game options")).
        				
        				addChild(this.buttonS = new ButtonSegment(this, this.menu.getWidth() / 2 + 57, this.menu.getHeight() / 2 - 30, "Save servers", button -> {
        					GuiConfig.this.cooldowns[1].setProgress(true);
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
            				return true;
                    
            	}, 80, 25, 3, "Save your servers")). 
        				addChild(this.buttonK = new ButtonSegment(this, this.menu.getWidth() / 2 - 137, this.menu.getHeight() / 2 - 30, "Save keys", button -> {
        					GuiConfig.this.cooldowns[2].setProgress(true);
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
            				return true;
                    
            	}, 80, 25, 3, "Save keybindings"))
        				).addVariant(new MenuArea(this, 74, 25).
        						
        					addChild(new ButtonSegment(this, 24, 10, "Dummy", button -> {return true;}
        			
        			, 80, 25, 3))).addVariant(new MenuArea(this, 74, 25).
        			
        					addChild(new ButtonSegment(this, 83, 56, "Useless", button -> {return true;
        			
        			}, 80, 25, 3))));
    	
    	this.addSegment(new ButtonMenuSegment(0, this, 10, 34, "Save", button -> {return true;}).setActive(true, false));

    	this.addSegment(new ButtonMenuSegment(1, this, 10, 56, "Files", button -> {return true;}));
    	
    	this.addSegment(new ButtonMenuSegment(2, this, 10, 78, "About", button -> {return true;}));
    	
    	this.addSegment(new SplitterSegment(this, 72, 32, this.height - 32 - 10));
    	
    	this.addSegment(new QuitButtonSegment(this, this.width - 22, 2, button -> {
    		
    		GuiConfig.this.mc.displayGuiScreen(GuiConfig.this.parentScreen);
    		return true;}));
    }

    @Override
    public void onGuiClosed() {
    	Keyboard.enableRepeatEvents(false);
    	tpe.shutdownNow();
    }
    
    public void changeSelected(ButtonMenuSegment segment) {
    	if(this.selectedSegment != null && this.selectedSegment != segment)
    		this.selectedSegment.setActive(false, true);
    	this.selectedSegment = segment;
    	this.menu.setIndex(segment.id);
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
    	
    	this.drawRect(0, 0, this.width, this.height, Color.WHITE.getRGB());
        
        this.drawRect(0, 0, 72, 25, 0xff9f9f9f);
        
        this.drawRect(72, 0, width, 25, 0xffe0e0e0);
        
        this.fontRenderer.drawStringWithShadow("Tab", MathHelper.clamp(72 / 2 - (this.fontRenderer.getStringWidth("Tab") / 2), 0, Integer.MAX_VALUE), 10, 16777215);
        
        int posX = MathHelper.clamp((this.width - 74) / 2 + 74 - (this.fontRenderer.getStringWidth("- DefaultSettings -") / 2), 74, Integer.MAX_VALUE);
        
        this.fontRenderer.drawString("- DefaultSettings -", posX + 1, 10 + 1, Color.WHITE.getRGB());
        
        this.fontRenderer.drawString("- DefaultSettings -", posX, 10, 0xff5d5d5d);

        buttonS.color = cooldowns[1].getProgress() ? 0xffccab14 : cooldowns[1].renderCooldown < 0 ? 0xffcc1414 : cooldowns[1].renderCooldown > 0 ? 0xff5dcc14 : 0xffa4a4a4;
        buttonK.color = cooldowns[2].getProgress() ? 0xffccab14 : cooldowns[2].renderCooldown < 0 ? 0xffcc1414 : cooldowns[2].renderCooldown > 0 ? 0xff5dcc14 : 0xffa4a4a4;
        buttonO.color = cooldowns[0].getProgress() ? 0xffccab14 : cooldowns[0].renderCooldown < 0 ? 0xffcc1414 : cooldowns[0].renderCooldown > 0 ? 0xff5dcc14 : 0xffa4a4a4;
        super.drawScreen(mouseX, mouseY, partialTicks);
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