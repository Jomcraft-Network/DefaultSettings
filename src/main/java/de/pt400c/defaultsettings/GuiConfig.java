package de.pt400c.defaultsettings;

import static de.pt400c.defaultsettings.FileUtil.MC;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;
import de.pt400c.defaultsettings.gui.*;
import de.pt400c.neptunefx.NEX;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class GuiConfig extends DefaultSettingsGUI {
	
    public final GuiScreen parentScreen;
   	public LeftMenu leftMenu;
    public PopupSegment popup;
    public ButtonControlSegment buttonS;
    public ButtonControlSegment buttonK;
    public ButtonControlSegment buttonO;
    public ButtonMenuSegment selectedSegment = null;
    private ExecutorService tpe = new ThreadPoolExecutor(1, 3, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private ButtonState[] cooldowns = new ButtonState[] {new ButtonState(false, 0), new ButtonState(false, 0), new ButtonState(false, 0)};
	public FramebufferObject framebufferMc;
	public boolean init = false;
	public HeaderPart headerPart = null;

    public GuiConfig(GuiScreen parentScreen) {
        this.mc = MC;
        this.parentScreen = parentScreen;  
    }
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    	if(keyCode == 1 && this.popupField != null)
    		this.popupField.setOpening(false);
    	else
    		super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void initGui() {
    	Segment.resized++;
    
    	if(DefaultSettings.is180)
			Segment.scaledresolution = new ScaledResolution(MC, MC.displayWidth, MC.displayHeight);
		else
			Segment.scaledresolution = new ScaledResolution(MC);
    
    	new FileUtil.RegistryChecker();

    	if(this.framebufferMc != null) 
    		this.framebufferMc.resize(MC.displayWidth, MC.displayHeight);
 
        Keyboard.enableRepeatEvents(true);
        
        if(!init) {

        	this.framebufferMc = new FramebufferObject(MC.displayWidth, MC.displayHeight);
    	
        	this.addSegment(new QuitButtonSegment(this, i -> {return i.width - 22;}, 2, 20, 20, button -> {
    		
        		GuiConfig.this.mc.displayGuiScreen(GuiConfig.this.parentScreen);
        		return true;}, 5F, false));
        	
        	this.addSegment(new HelpSegment(this, i -> {return i.width - 55;}, 30));
    	
    		this.menu = new MenuScreen(this, 74, 25);

    		this.leftMenu = new LeftMenu(this, 0, 25, 46, i -> {return i.height - 25;});
    	
    		this.addSegment(this.menu.
        		addVariant(new MenuArea(this, 74, 25).
        				
        				addChild(this.buttonO = new ButtonControlSegment(this, i -> {return i.getWidth() / 2 - 90 / 2 + i.getPosX() - 15;}, i -> {return i.getHeight() / 2 - 30 + i.getPosY() + 5;}, 90, 30, this.menu, 1, "Save Options", "Save all default game options", button -> {
        	    			saveOptions();
        	    			return true;
        				})).
        				
        				addChild(this.buttonS = new ButtonControlSegment(this, i -> {return i.getWidth() / 2 - 90 / 2 + i.getPosX() + 98;}, i -> {return i.getHeight() / 2 - 30 + i.getPosY() + 5;}, 90, 30, this.menu, 2, "Save Servers", "Save your servers", button -> {
        					saveServers();
							return true;
        				})).
        				
        				addChild(this.buttonK = new ButtonControlSegment(this, i -> {return i.getWidth() / 2 - 90 / 2 + i.getPosX() - 128;}, i -> {return i.getHeight() / 2 - 30 + i.getPosY() + 5;}, 90, 30, this.menu, 0, "Save Keys", "Save keybindings", button -> {
        					saveKeys();
            				return true;
        				}))
        				
        				).addVariant(new MenuArea(this, 74, 25).

        					addChild(new ScrollableSegment(this, 20, 30, i -> {return i.width - 74 - 90;}, i -> {return i.height - 25 - 10 - 30;}, (byte) 0)))
        				 
        				 .addVariant(new MenuArea(this, 74, 25).
        						 addChild(new AboutSegment(this, 10, 20, 20, 20, false))));
    
    		this.addSegment(this.leftMenu
    				.addChild(new ButtonMenuSegment(0, this, 10, 9, "Save", button -> {return true;}, this.leftMenu, "textures/gui/save.png").setActive(true, false))
    				.addChild(new ButtonMenuSegment(1, this, 10, 35, "Configs", button -> {return true;}, this.leftMenu, "textures/gui/config.png"))
    				.addChild(new ButtonMenuSegment(2, this, 10, 61, "About", button -> {return true;}, this.leftMenu, "textures/gui/about.png"))
    				.addChild(new ExportSegment(this, 0, i -> {return i.height - 80;}, 72, 43, this.leftMenu))
    				.addChild(new SplitterSegment(this, 72, 3, i -> {return i.height - 30;}, this.leftMenu))
    				.addChild(new ButtonUpdateChecker(this, i -> {return i.height - 30 - 25 + this.leftMenu.getPosY();}, this.leftMenu)));

    		this.addSegment(this.popup = new PopupSegment(this, 0, 0, this.width, this.height).setWindow(new PopupWindow(this, this.width / 2 - 210 / 2, this.height / 2 - 100 / 2, 210, 100, "").addChild(new QuitButtonSegment(this, 190, 5, 14, 14, button -> {

    			
    		
    		
    		return true;}, 3F, true))));
    		init = true;
    		
    		this.headerPart = new HeaderPart(this, 0, 0, 0, i -> {return i.width;}, 26, true, false);
    		
        }
        
        if(GuiConfig.this.popupField != null) {
        	GuiConfig.this.popupField.setOpening(false);
        	GuiConfig.this.popupField.backgroundTimer = 0;
        	GuiConfig.this.popupField.windowTimer = 0;
        	GuiConfig.this.popupField.setVisible(false);
        }
        
    	this.popupField = null;
    	
    	super.initGui();
    }

    @Override
    public void onGuiClosed() {
    	Keyboard.enableRepeatEvents(false);
    	BakeryRegistry.clearAll();
    	if(framebufferMc != null)
    		framebufferMc.deleteFramebuffer();
    	tpe.shutdownNow();
    }
    
    public void changeSelected(ButtonMenuSegment segment) {
    	if(this.selectedSegment != null && this.selectedSegment != segment)
    		this.selectedSegment.setActive(false, true);
    	this.selectedSegment = segment;
    	this.menu.setIndex(segment.id);
    }

    @Override
    public void updateScreen() {
    	super.updateScreen();
        for(int id = 0; id < cooldowns.length; id++) {
        	if(cooldowns[id].renderCooldown > 0)
        		cooldowns[id].renderCooldown--;
        	else if(cooldowns[id].renderCooldown < 0) 
        		cooldowns[id].renderCooldown++;	
        }
    }

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.mc.getFramebuffer().unbindFramebuffer();
		glPushMatrix();
		glClear(16640);
		this.framebufferMc.bindFramebuffer(true);
		glEnable(GL_MULTISAMPLE);
		glEnable(GL_TEXTURE_2D);

		glClear(256);
		glMatrixMode(5889);
		glLoadIdentity();
		glOrtho(0.0D, Segment.scaledresolution.getScaledWidth_double(), Segment.scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
		glMatrixMode(5888);
		glLoadIdentity();
		glTranslatef(0.0F, 0.0F, -2000.0F);

		glClear(256);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_ALPHA_TEST);

		Gui.drawRect(0, 0, this.width, this.height, 0xff2c2c2c);

		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);	

		this.buttonS.color = cooldowns[1].getProgress() ? 0xffccab14 : cooldowns[1].renderCooldown < 0 ? 0xffcc1414 : cooldowns[1].renderCooldown > 0 ? 0xff5dcc14 : 0xffe6e6e6;
		this.buttonK.color = cooldowns[2].getProgress() ? 0xffccab14 : cooldowns[2].renderCooldown < 0 ? 0xffcc1414 : cooldowns[2].renderCooldown > 0 ? 0xff5dcc14 : 0xffe6e6e6;
		this.buttonO.color = cooldowns[0].getProgress() ? 0xffccab14 : cooldowns[0].renderCooldown < 0 ? 0xffcc1414 : cooldowns[0].renderCooldown > 0 ? 0xff5dcc14 : 0xffe6e6e6;

		headerPart.render(mouseX, mouseY, partialTicks);
		
		super.drawScreen(mouseX, mouseY, partialTicks);

		glPushMatrix();

		glPopMatrix();
		this.framebufferMc.unbindFramebuffer();
		glPopMatrix();

		this.mc.getFramebuffer().bindFramebuffer(true);
		glPushMatrix();

		glBindFramebuffer(GL_READ_FRAMEBUFFER, framebufferMc.framebufferObject);
		glBlitFramebuffer(0, 0, MC.displayWidth, MC.displayHeight, 0, 0, MC.displayWidth, MC.displayHeight, GL_COLOR_BUFFER_BIT, GL_NEAREST);

		glPopMatrix();
	}
	
	public void saveServers() {
		if (FileUtil.serversFileExists()) {
			this.popup.setOpening(true);
			this.popup.getWindow().title = "Save Servers";
			this.popup.getWindow().setPos(this.width / 2 - 210 / 2, this.height / 2 - 100 / 2);
			this.popupField = this.popup;
			this.popupField.getWindow().clearChildren();
			this.popupField.getWindow().addChild(new TextSegment(this, 5, 30, 20, 20, "The server list already exists\n\nWould you like to overwrite it?", 0xffffffff, true));
			this.popupField.getWindow().addChild(new QuitButtonSegment(this, 190, 5, 14, 14, button -> {

				GuiConfig.this.popupField.setOpening(false);

				return true;
			}, 3F, true));

			this.popupField.getWindow().addChild(new ButtonRoundSegment(this, 105 - 30, 75, 60, 20, "Overwrite", null, button -> {
				GuiConfig.this.cooldowns[1].setProgress(true);

				GuiConfig.this.popupField.setOpening(false);
				tpe.execute(new Runnable() {
					@Override
					public void run() {
						try {
							FileUtil.saveServers();
							GuiConfig.this.cooldowns[1].renderCooldown = 30;
						} catch (ClosedByInterruptException e) {
							DefaultSettings.log.log(Level.DEBUG, "An exception occurred while saving the server list: Interruption exception");
						} catch (Exception e) {
							DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
							GuiConfig.this.cooldowns[1].renderCooldown = -30;
						}
						GuiConfig.this.cooldowns[1].setProgress(false);
						FileUtil.servers_exists = FileUtil.serversFileExists();
					}
				});

				return true;
			}, 0.8F, true));

			this.popup.setVisible(true);
		} else {
			
			tpe.execute(new Runnable() {
				@Override
				public void run() {

					GuiConfig.this.cooldowns[1].setProgress(true);

					try {
						FileUtil.saveServers();
						GuiConfig.this.cooldowns[1].renderCooldown = 30;
					} catch (ClosedByInterruptException e) {
						DefaultSettings.log.log(Level.DEBUG, "An exception occurred while saving the server list: Interruption exception");
					} catch (Exception e) {
						DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
						GuiConfig.this.cooldowns[1].renderCooldown = -30;
					}
					GuiConfig.this.cooldowns[1].setProgress(false);
					FileUtil.servers_exists = FileUtil.serversFileExists();
				}
			});

		}
	}
    
	public void copyConfigs() {
		
		tpe.execute(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				GuiConfig.this.menu.exportActive.setByte((byte) 0);
				try {
					FileUtil.restoreConfigs();
				} catch (IOException e) {
					if(e instanceof ClosedByInterruptException)
						return;
					DefaultSettings.getInstance().log.log(Level.ERROR, "An exception occurred while trying to move the configs:", e);
				}
				GuiConfig.this.menu.exportActive.setByte((byte) 1);
				for(MenuArea variant : GuiConfig.this.menu.getVariants()) {
					variant.getChildren().stream().filter(segment -> segment instanceof ScrollableSegment).forEach(segment -> segment.guiContentUpdate(((ScrollableSegment) segment).searchbar.query));
				}
			}
		});
	}
	
	public void deleteConfigs() {
		
		if(!FileUtil.exportMode()) {
			this.popup.setOpening(true);
			this.popup.getWindow().title = "Delete Config Folder";
			this.popup.getWindow().setPos(this.width / 2 - 210 / 2, this.height / 2 - 100 / 2);
			this.popupField = this.popup;
			this.popupField.getWindow().clearChildren();
			this.popupField.getWindow().addChild(new TextSegment(this, 5, 30, 20, 20, "Do you want to delete every file\nfrom the config folder?\n\n(The defaultsettings folder will stay)", 0xffffffff, true));
			this.popupField.getWindow().addChild(new QuitButtonSegment(this, 190, 5, 14, 14, button -> {

				GuiConfig.this.popupField.setOpening(false);

				return true;
			}, 3F, true));

			this.popupField.getWindow().addChild(new ButtonRoundSegment(this, 105 - 80, 75, 60, 20, "Proceed", null, button -> {

				GuiConfig.this.popupField.setOpening(false);
				tpe.execute(new Runnable() {
					@SuppressWarnings("static-access")
					@Override
					public void run() {
						GuiConfig.this.menu.exportActive.setByte((byte) 0);
						try {
							FileUtil.setExportMode();
							
						} catch (IOException e) {
							if(e instanceof ClosedByInterruptException)
								return;
							DefaultSettings.getInstance().log.log(Level.ERROR, "An exception occurred while trying to move the configs:", e);
						}
						for(MenuArea variant : GuiConfig.this.menu.getVariants()) {
							variant.getChildren().stream().filter(segment -> segment instanceof ScrollableSegment).forEach(segment -> segment.guiContentUpdate(((ScrollableSegment) segment).searchbar.query));
						}
						GuiConfig.this.menu.exportActive.setByte((byte) 2);
					}
				});

				return true;
			}, 0.8F, true));
			
			this.popupField.getWindow().addChild(new ButtonRoundSegment(this, 105 + 20, 75, 60, 20, "Move", "Move all contents from the config folder to DS's config management", button -> {

				GuiConfig.this.popupField.setOpening(false);
				tpe.execute(new Runnable() {
					@SuppressWarnings("static-access")
					@Override
					public void run() {
						GuiConfig.this.menu.exportActive.setByte((byte) 0);
						try {
							FileUtil.moveAllConfigs(true);
						} catch (IOException e) {
							if(e instanceof ClosedByInterruptException)
								return;
							DefaultSettings.getInstance().log.log(Level.ERROR, "An exception occurred while trying to move the configs:", e);
						}
						GuiConfig.this.menu.exportActive.setByte((byte) (FileUtil.exportMode() ? 2 : 1));
						for(MenuArea variant : GuiConfig.this.menu.getVariants()) {
							variant.getChildren().stream().filter(segment -> segment instanceof ScrollableSegment).forEach(segment -> segment.guiContentUpdate(((ScrollableSegment) segment).searchbar.query));
						}
					}
				});

				return true;
			}, 0.8F, true));
			
			this.popup.setVisible(true);
			
		}else {
			tpe.execute(new Runnable() {
				@SuppressWarnings("static-access")
				@Override
				public void run() {
					try {
						FileUtil.setExportMode();
					} catch (IOException e) {
						if(e instanceof ClosedByInterruptException)
							return;
						DefaultSettings.getInstance().log.log(Level.ERROR, "An exception occurred while trying to move the configs:", e);
					}
					GuiConfig.this.menu.exportActive.setByte((byte) 2);
					for(MenuArea variant : GuiConfig.this.menu.getVariants()) {
						variant.getChildren().stream().filter(segment -> segment instanceof ScrollableSegment).forEach(segment -> segment.guiContentUpdate(((ScrollableSegment) segment).searchbar.query));
					}
				}
			});
		}
	}
	
	public void saveOptions() {
		if (FileUtil.optionsFilesExist()) {
			this.popup.setOpening(true);
			this.popup.getWindow().title = "Save Options";
			this.popup.getWindow().setPos(this.width / 2 - 210 / 2, this.height / 2 - 100 / 2);
			this.popupField = this.popup;
			this.popupField.getWindow().clearChildren();
			this.popupField.getWindow().addChild(new TextSegment(this, 5, 30, 20, 20, "The default options already exist\n\nWould you like to overwrite them?", 0xffffffff, true));
			this.popupField.getWindow().addChild(new QuitButtonSegment(this, 190, 5, 14, 14, button -> {

				GuiConfig.this.popupField.setOpening(false);

				return true;
			}, 3F, true));

			this.popupField.getWindow().addChild(new ButtonRoundSegment(this, 105 - 30, 75, 60, 20, "Overwrite", null, button -> {
				GuiConfig.this.cooldowns[0].setProgress(true);

				GuiConfig.this.popupField.setOpening(false);
				tpe.execute(new Runnable() {
					@Override
					public void run() {
						try {
							FileUtil.saveOptions();
							GuiConfig.this.cooldowns[0].renderCooldown = 30;
						} catch (ClosedByInterruptException e) {
							DefaultSettings.log.log(Level.DEBUG, "An exception occurred while saving the default game options: Interruption exception");
						} catch (Exception e) {
							DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the default game options:", e);
							GuiConfig.this.cooldowns[0].renderCooldown = -30;
						}
						GuiConfig.this.cooldowns[0].setProgress(false);
						FileUtil.options_exists = FileUtil.optionsFilesExist();
					}
				});

				return true;
			}, 0.8F, true));

			this.popup.setVisible(true);
		} else {
			tpe.execute(new Runnable() {
				@Override
				public void run() {
					GuiConfig.this.cooldowns[0].setProgress(true);

					try {
						FileUtil.saveOptions();
						GuiConfig.this.cooldowns[0].renderCooldown = 30;
					} catch (ClosedByInterruptException e) {
						DefaultSettings.log.log(Level.DEBUG, "An exception occurred while saving the default game options: Interruption exception");
					} catch (Exception e) {
						DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the default game options:", e);
						GuiConfig.this.cooldowns[0].renderCooldown = -30;
					}
					GuiConfig.this.cooldowns[0].setProgress(false);
					FileUtil.options_exists = FileUtil.optionsFilesExist();
				}
			});	
		}
	}
    
	public void saveKeys() {
		if (FileUtil.keysFileExist()) {
			this.popup.setOpening(true);
			this.popup.getWindow().title = "Save Keybindings";
			this.popup.getWindow().setPos(this.width / 2 - 210 / 2, this.height / 2 - 100 / 2);
			this.popupField = this.popup;
			this.popupField.getWindow().clearChildren();
			this.popupField.getWindow().addChild(new TextSegment(this, 5, 30, 20, 20, "The default keybindings already exist\n\nWould you like to overwrite them?", 0xffffffff, true));
			this.popupField.getWindow().addChild(new QuitButtonSegment(this, 190, 5, 14, 14, button -> {

				GuiConfig.this.popupField.setOpening(false);

				return true;
			}, 3F, true));

			this.popupField.getWindow().addChild(new ButtonRoundSegment(this, 105 - 30, 75, 60, 20, "Overwrite", null,button -> {
				GuiConfig.this.cooldowns[2].setProgress(true);

				GuiConfig.this.popupField.setOpening(false);
				tpe.execute(new Runnable() {
					@Override
					public void run() {
						try {
							FileUtil.saveKeys();
							GuiConfig.this.cooldowns[2].renderCooldown = 30;
							FileUtil.restoreKeys();
						} catch (ClosedByInterruptException e) {
							DefaultSettings.log.log(Level.DEBUG, "An exception occurred while saving the key configuration: Interruption exception");
						} catch (Exception e) {
							DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
							GuiConfig.this.cooldowns[2].renderCooldown = -30;
						}
						GuiConfig.this.cooldowns[2].setProgress(false);
						FileUtil.keys_exists = FileUtil.keysFileExist();
					}
				});

				return true;
			}, 0.8F, true));

			this.popup.setVisible(true);

		} else {
			tpe.execute(new Runnable() {
				@Override
				public void run() {
					GuiConfig.this.cooldowns[2].setProgress(true);

					try {
						FileUtil.saveKeys();
						GuiConfig.this.cooldowns[2].renderCooldown = 30;
						FileUtil.restoreKeys();
					} catch (ClosedByInterruptException e) {
						DefaultSettings.log.log(Level.DEBUG, "An exception occurred while saving the key configuration: Interruption exception");
					} catch (Exception e) {
						DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
						GuiConfig.this.cooldowns[2].renderCooldown = -30;
					}
					GuiConfig.this.cooldowns[2].setProgress(false);
					FileUtil.keys_exists = FileUtil.keysFileExist();
				}
			});		
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
	
	public static class HeaderPart extends BakedSegment {
		
			private final Function<GuiConfig, Integer> widthF;

			public HeaderPart(GuiScreen gui, int id, float posX, float posY, Function<GuiConfig, Integer> width, float height, boolean stat, boolean popupSegment) {
				super(gui, id, posX, posY, width.apply((GuiConfig) gui), height, 0, 0, 0, stat, popupSegment);
				this.widthF = width;
			}
			
			@Override
			public void render(int mouseX, int mouseY, float partialTicks) {
				
				if(resized != this.resized_mark) 
					this.width = this.widthF.apply((GuiConfig) this.gui);

				setup();

				if(!compiled) {
					preRender();
					glEnable(GL_BLEND);
					glDisable(GL_TEXTURE_2D);
					glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

					final int index = ((GuiConfig) gui).menu.index;
					
					String msg = "";
					int offs = 0;
					if(index == 0) {	
						msg = "Save";
						offs = 0;
					}else if(index == 1) {		
						msg = "Configs";
						offs = 20;
					}else if(index == 2) {
						msg = "About";
						offs = 5;
					}
					
					NEX.drawRect(0, 25, this.gui.width, 26, 0xff161616, false, null, false);

					NEX.drawRect(0, 0, 72 - 25F / 2F, 25, 0xff282828, false, null, false);

					NEX.drawRect(72 - 25F / 2F, 0, this.gui.width, 25, 0xff505050, false, null, false);
					
					NEX.drawRect(72 - 25F / 2F, 0, 110 + offs, 25, 0xff787878, false, null, false);
					
			        glColor4f(40F / 255F, 40F / 255F, 40F / 255F, 1);
					
					NEX.drawCircle(72 - 25F / 2F, 25F / 2F, 25F / 2F, 270, 50);

					glColor4f(120F / 255F, 120F / 255F, 120F / 255F, 1);
					
					NEX.drawCircle(110 + offs, 25F / 2F, 25F / 2F, 270, 50);

					glEnable(GL_TEXTURE_2D);
					DefaultSettings.fontRenderer.drawString("Tab", MathUtil.clamp(72 / 2 - (DefaultSettings.fontRenderer.getStringWidth("Tab", 1.2F, true) / 2), 0, Integer.MAX_VALUE), 7, 0xffffffff, 1.4F, true);

					DefaultSettings.fontRenderer.drawString("- DefaultSettings -", 100 + (this.gui.width - 100) / 2 - DefaultSettings.fontRenderer.getStringWidth("- DefaultSettings -", 1.2F, true) / 2, 8, 0xffffffff, 1.2F, true);
					
					DefaultSettings.fontRenderer.drawString(msg, 80, 8, 0xffffffff, 1.2F, true);

					glDisable(GL_BLEND);
					postRender(1, false);
				}

				drawTexture(1);
			}
	}
}