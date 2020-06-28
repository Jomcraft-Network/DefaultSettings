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
import com.mojang.blaze3d.matrix.MatrixStack;
import de.pt400c.defaultsettings.gui.*;
import de.pt400c.neptunefx.NEX;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL30.*;

@OnlyIn(Dist.CLIENT)
public class GuiConfig extends DefaultSettingsGUI {

    public final Screen parentScreen;
    public LeftMenu leftMenu;
    public PopupSegment popup;
    public ButtonControlSegment buttonS;
    public ButtonControlSegment buttonK;
    public ButtonControlSegment buttonO;
    public ButtonMenuSegment[] menuButtons = new ButtonMenuSegment[4];
    public ButtonMenuSegment selectedSegment = null;
    private ExecutorService tpe = new ThreadPoolExecutor(1, 3, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private ButtonState[] cooldowns = new ButtonState[] {new ButtonState(false, 0), new ButtonState(false, 0), new ButtonState(false, 0)};
    public FramebufferDefault framebufferMc;
    public boolean init = false;
	public HeaderPart headerPart = null;
	private int gcAmount;
	public ProfilesSegment scrollableProfiles;
	private int storeWidth;
	private int storeHeight;
	private final int framerateLimit;
	public double thingWidth = 0;
	public double thingHeight = 0;
	public int renderTick = 0;
	public long laterTick = 0;
	public long prevTick = 0;
	public float median = 0;
	
	public GuiConfig(Minecraft minecraft, Screen parentScreen) {
    	super(new TranslationTextComponent("defaultsettings.main.title"));
    	this.field_230706_i_ = minecraft;
        this.parentScreen = parentScreen;
        this.framerateLimit = MC.mainWindow.getLimitFramerate();
        MC.mainWindow.setFramerateLimit(60);
	}
    
    @Override
    public boolean func_231046_a_(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
    	if(p_keyPressed_1_ == 256 && this.popupField != null){
    		this.popupField.setOpening(false);
    		return true;
    	}else if(p_keyPressed_1_ == 290 && this.popupField == null) {
    		HelpSegment.openPopup(this);
    		return true;
    	}else if(p_keyPressed_1_ == 265 && this.popupField == null) {
    		this.headerPart.compiled = false;
    		if(this.selectedSegment.id > 0)
    			this.setActive(this.selectedSegment.id - 1);
    		else
    			this.setActive(3);
    		return true;
    	}else if(p_keyPressed_1_ == 264 && this.popupField == null) {
    		this.headerPart.compiled = false;
    		if(this.selectedSegment.id < 3)
    			this.setActive(this.selectedSegment.id + 1);
    		else
    			this.setActive(0);
    		return true;
    	}else if (p_keyPressed_1_ == 256 && this.func_231178_ax__()) {
			if(this.menu.exportActive.getByte() == 2 && FileUtil.exportMode() && FileUtil.mainJson.activeConfigs.size() != 0) {
				exportModeInfo();
			}else {
			    this.func_231175_as__();
			}
        }
		return super.func_231046_a_(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }
    
    public void setActive(int id) {
		if(this.selectedSegment != null)
    		this.selectedSegment.setActive(false, true);
    	this.menuButtons[id].activated = true;
    	this.selectedSegment = this.menuButtons[id];
    	this.menu.setIndex(id);
	}
    
    public void testInit() {
    	
    	Segment.resized++;

    	Segment.scaledFactor = MC.mainWindow.getGuiScaleFactor();
    	
    	new FileUtil.RegistryChecker();
    	
    	if(this.framebufferMc != null) 
    		this.framebufferMc.resize(MC.mainWindow.getWidth(), MC.mainWindow.getHeight());
			
    	MC.keyboardListener.enableRepeatEvents(true);
    	
    	if(this.gcAmount == 9) {
    		System.gc();
    		this.gcAmount = 0;
    	}
    	
    	this.thingWidth = MC.mainWindow.getWidth() / (int) Segment.scaledFactor;
    	
    	this.thingHeight = MC.mainWindow.getHeight() / (int) Segment.scaledFactor;

    	this.gcAmount++;
    	
    	if(!init) {
    		this.clearSegments();
        	this.framebufferMc = new FramebufferDefault(MC.mainWindow.getWidth(), MC.mainWindow.getHeight());
    	
        	this.addSegment(new QuitButtonSegment(this, i -> {return i.field_230708_k_ - 22;}, 2, 20, 20, button -> {
    		
        		if(GuiConfig.this.menu.exportActive.getByte() == 2 && FileUtil.exportMode() && FileUtil.mainJson.activeConfigs.size() != 0)
        			GuiConfig.this.exportModeInfo();
    			else 		
    				GuiConfig.this.field_230706_i_.displayGuiScreen(GuiConfig.this.parentScreen);
    		
        		return true;}, 5F, false));
        	
        	this.addSegment(new HelpSegment(this, i -> {return i.field_230708_k_ - 55;}, 30));
    	
    		this.menu = new MenuScreen(this, 74, 25);

    		this.leftMenu = new LeftMenu(this, 0, 25, 46, i -> {return i.field_230709_l_ - 25;});
    	
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

        					addChild(new ScrollableSegment(this, 20, 30, i -> {return i.field_230708_k_ - 74 - 90;}, i -> {return i.field_230709_l_ - 25 - 10 - 30;}, (byte) 0)))
        				 
        		.addVariant(new MenuArea(this, 74, 25)
						
        				.addChild(this.scrollableProfiles = new ProfilesSegment(this, 20, 30, i -> {return i.field_230708_k_ - 74 - 90;}, i -> {return i.field_230709_l_ - 25 - 10 - 30;}))
        		
        		
        		.addChild(new ProfilesSegment.AddSegment(this, i -> {return i.field_230708_k_ - 102;}, 5, 18, 18, false)))

        		
        				 .addVariant(new MenuArea(this, 74, 25).
        						 addChild(new AboutSegment(this, 10, 20, 20, 20, false))));
    
    		this.addSegment(this.leftMenu
    				.addChild(this.menuButtons[0] = new ButtonMenuSegment(0, this, 10, 9, "Save", button -> {return true;}, this.leftMenu, "textures/gui/save.png").setActive(true, false))
    				.addChild(this.menuButtons[1] = new ButtonMenuSegment(1, this, 10, 35, "Configs", button -> {return true;}, this.leftMenu, "textures/gui/config.png"))
    				.addChild(this.menuButtons[2] = new ButtonMenuSegment(2, this, 10, 61, "Profiles", button -> {return true;}, this.leftMenu, "textures/gui/profiles.png"))
    				.addChild(this.menuButtons[3] = new ButtonMenuSegment(3, this, 10, 87, "About", button -> {return true;}, this.leftMenu, "textures/gui/about.png"))
    				.addChild(new ExportSegment(this, 0, i -> {return i.field_230709_l_ - 80;}, 72, 43, this.leftMenu))
    				.addChild(new SplitterSegment(this, 72, 3, i -> {return i.field_230709_l_ - 30;}, this.leftMenu))
    				.addChild(new ButtonUpdateChecker(this, i -> {return i.field_230709_l_ - 30 - 25 + this.leftMenu.getPosY();}, this.leftMenu)));

    		this.addSegment(this.popup = new PopupSegment(this, 0, 0, this.field_230708_k_, this.field_230709_l_).setWindow(new PopupWindow(this, this.field_230708_k_ / 2 - 210 / 2, this.field_230709_l_ / 2 - 100 / 2, 210, 100, "").addChild(new QuitButtonSegment(this, 190, 5, 14, 14, button -> {

    		return true;}, 3F, true))));
    		init = true;
    		
    		this.headerPart = new HeaderPart(this, 0, 0, 0, i -> {return i.field_230708_k_;}, 26, true, false);
    		
        }
        
        if(GuiConfig.this.popupField != null) {
        	GuiConfig.this.popupField.setOpening(false);
        	GuiConfig.this.popupField.backgroundTimer = 0;
        	GuiConfig.this.popupField.windowTimer = 0;
        	GuiConfig.this.popupField.setVisible(false);
        }
        
    	this.popupField = null;
    	
    	super.func_231160_c_();
    	
    	if(init)
    		openDisclaimer();
    }
    
    private void openDisclaimer() {
		if(FileUtil.otherCreator) {
			this.popup.setOpening(true);
			this.popup.getWindow().title = "Warning";
			this.popup.getWindow().setPos(this.field_230708_k_ / 2 - 210 / 2, this.field_230709_l_ / 2 - 100 / 2);
			this.popupField = this.popup;
			this.popupField.getWindow().clearChildren();
			this.popupField.getWindow().addChild(new TextSegment(this, 5, 30, 20, 20, "You probably aren't the modpack's\ncreator. Being here might break stuff.\nIf you want to change the profile, use\nthe \u00a7bswitchprofile\u00a7r command, not this.", 0xffffffff, true));
			this.popupField.getWindow().addChild(new QuitButtonSegment(this, 190, 5, 14, 14, button -> {

				GuiConfig.this.popupField.setOpening(false);

				return true;
			}, 3F, true));

			this.popupField.getWindow().addChild(new ButtonRoundSegment(this, 105 - 30, 75, 60, 20, "Okay", null, button -> {

				GuiConfig.this.popupField.setOpening(false);

				return true;
			}, 0.8F, true));

			this.popup.setVisible(true);
		}
		
		return;
	}
    
    public void exportModeInfo() {
		this.popup.setOpening(true);
		this.popup.getWindow().title = "Export Mode";
		this.popup.getWindow().setPos(this.field_230708_k_ / 2 - 210 / 2, this.field_230709_l_ / 2 - 100 / 2);
		this.popupField = this.popup;
		this.popupField.getWindow().clearChildren();
		this.popupField.getWindow().addChild(new TextSegment(this, 5, 30, 20, 20, "The Export Mode has to be disabled\nin order to close this GUI", 0xffffffff, true));
		this.popupField.getWindow().addChild(new QuitButtonSegment(this, 190, 5, 14, 14, button -> {

			GuiConfig.this.popupField.setOpening(false);

			return true;
		}, 3F, true));

		this.popupField.getWindow().addChild(new ButtonRoundSegment(this, 105 - 30, 75, 60, 20, "Okay", null, button -> {

			GuiConfig.this.popupField.setOpening(false);

			return true;
		}, 0.8F, true));

		this.popup.setVisible(true);
    }
    
    public void changeSelected(ButtonMenuSegment segment) {
    	if(this.selectedSegment != null && this.selectedSegment != segment)
    		this.selectedSegment.setActive(false, true);
    	this.selectedSegment = segment;
    	this.menu.setIndex(segment.id);
    }

    @Override
    public void func_231175_as__() {
    	MC.keyboardListener.enableRepeatEvents(false);
    	tpe.shutdownNow();
    	BakeryRegistry.clearAll();
    	if(framebufferMc != null)
    		framebufferMc.deleteFramebuffer();
    	MC.mainWindow.setFramerateLimit(this.framerateLimit);
    	DefaultSettings.targetMS = 9;
    	super.func_231175_as__();
    }
    
    public void saveServers() {
		if (FileUtil.serversFileExists()) {
			this.popup.setOpening(true);
			this.popup.getWindow().title = "Save Servers";
			this.popup.getWindow().setPos(this.field_230708_k_ / 2 - 210 / 2, this.field_230709_l_ / 2 - 100 / 2);
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
		
		tpe.execute(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				GuiConfig.this.menu.exportActive.setByte((byte) 0);
				try {
		
					FileUtil.moveAllConfigs();
					FileUtil.checkMD5();
					
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
	}
	
	public void saveOptions() {
		if (FileUtil.optionsFilesExist()) {
			this.popup.setOpening(true);
			this.popup.getWindow().title = "Save Options";
			this.popup.getWindow().setPos(this.field_230708_k_ / 2 - 210 / 2, this.field_230709_l_ / 2 - 100 / 2);
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
			this.popup.getWindow().setPos(this.field_230708_k_ / 2 - 210 / 2, this.field_230709_l_ / 2 - 100 / 2);
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
 
    @Override
    public void func_231023_e_()
    {
    	super.func_231023_e_();
        for(int id = 0; id < cooldowns.length; id++) {
        	if(cooldowns[id].renderCooldown > 0)
        		cooldowns[id].renderCooldown--;
        	else if(cooldowns[id].renderCooldown < 0) 
        		cooldowns[id].renderCooldown++;	
        }
    }

    @Override
    public void func_230430_a_(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
    	
    	if(MC.mainWindow.getWidth() != this.storeWidth || MC.mainWindow.getHeight() != this.storeHeight) {
    		this.storeWidth = MC.mainWindow.getWidth();
    		this.storeHeight = MC.mainWindow.getHeight();
    		testInit();
    	}

		if(this.renderTick == Integer.MAX_VALUE)
			this.renderTick = -1;
		
		this.renderTick++;
    	
		this.prevTick = this.laterTick;
    	this.laterTick = System.currentTimeMillis();
    	float diff = (float) (laterTick - prevTick);
    	float fps = ((1F / diff) * 1000);
		
    	median+= fps; 
    	
    	if(this.renderTick % 120 == 0) {
    		float medFPS = median / 120F;
    
    		if(medFPS < 50 && DefaultSettings.targetMS > 1) {
    			DefaultSettings.targetMS -= 1;
    			testInit();
    		}
    		median = 0;
    	}

    	glBindFramebuffer(GL_FRAMEBUFFER, this.framebufferMc.framebuffer);
		glClear(16640);
		glEnable(GL_TEXTURE_2D);
		glMatrixMode(5889);
		glLoadIdentity();
		glOrtho(0.0D, MC.mainWindow.getScaledWidth(), MC.mainWindow.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
		glMatrixMode(5888);
		glLoadIdentity();
		glTranslatef(0.0F, 0.0F, -2000.0F);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_ALPHA_TEST);

		AbstractGui.func_238467_a_(stack, 0, 0, this.field_230708_k_, this.field_230709_l_, 0xff2c2c2c);

		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);	

		this.buttonS.color = cooldowns[1].getProgress() ? 0xffccab14 : cooldowns[1].renderCooldown < 0 ? 0xffcc1414 : cooldowns[1].renderCooldown > 0 ? 0xff5dcc14 : 0xffe6e6e6;
		this.buttonK.color = cooldowns[2].getProgress() ? 0xffccab14 : cooldowns[2].renderCooldown < 0 ? 0xffcc1414 : cooldowns[2].renderCooldown > 0 ? 0xff5dcc14 : 0xffe6e6e6;
		this.buttonO.color = cooldowns[0].getProgress() ? 0xffccab14 : cooldowns[0].renderCooldown < 0 ? 0xffcc1414 : cooldowns[0].renderCooldown > 0 ? 0xff5dcc14 : 0xffe6e6e6;

		headerPart.render(mouseX, mouseY, partialTicks);
		
		super.func_230430_a_(stack, mouseX, mouseY, partialTicks);
		
		glBindFramebuffer(GL_READ_FRAMEBUFFER, this.framebufferMc.framebuffer);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, this.framebufferMc.interFramebuffer);
        glBlitFramebuffer(0, 0, MC.mainWindow.getWidth(), MC.mainWindow.getHeight(), 0, 0, MC.mainWindow.getWidth(), MC.mainWindow.getHeight(), GL_COLOR_BUFFER_BIT, GL_NEAREST);
		
        MC.getFramebuffer().bindFramebuffer(true);
		glBindTexture(GL_TEXTURE_2D, this.framebufferMc.screenTexture);
		
		glColor4f(1, 1, 1, 1);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		glEnable(GL_BLEND);
		glDisable(GL_ALPHA_TEST);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

		glBegin(GL_QUADS);
	
		glTexCoord2f(0, 0); glVertex3d(0, thingHeight, 0);
		glTexCoord2f(1, 0); glVertex3d(thingWidth, thingHeight, 0);
		glTexCoord2f(1, 1); glVertex3d(thingWidth, 0, 0);
		glTexCoord2f(0, 1); glVertex3d(0, 0, 0);
		glEnd();
		
		glEnable(GL_ALPHA_TEST);
		glDisable(GL_BLEND);
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
		
		public static String tabName;

		public HeaderPart(Screen gui, int id, float posX, float posY, Function<GuiConfig, Integer> width, float height, boolean stat, boolean popupSegment) {
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
				
				int offs = 0;
				if(index == 0) {	
					tabName = "Save";
					offs = 0;
				}else if(index == 1) {		
					tabName = "Configs";
					offs = 20;
				}else if(index == 2) {
					tabName = "Profiles";
					offs = 22;
				} else if(index == 3) {
					tabName = "About";
					offs = 5;
				}
				
				NEX.drawRect(0, 25, this.gui.field_230708_k_, 26, 0xff161616, false, null, false);

				NEX.drawRect(0, 0, 72 - 25F / 2F, 25, 0xff282828, false, null, false);

				NEX.drawRect(72 - 25F / 2F, 0, this.gui.field_230708_k_, 25, 0xff505050, false, null, false);
				
				NEX.drawRect(72 - 25F / 2F, 0, 110 + offs, 25, 0xff787878, false, null, false);
				
		        glColor4f(40F / 255F, 40F / 255F, 40F / 255F, 1);
				
				NEX.drawCircle(72 - 25F / 2F, 25F / 2F, 25F / 2F, 270, 50);

				glColor4f(120F / 255F, 120F / 255F, 120F / 255F, 1);
				
				NEX.drawCircle(110 + offs, 25F / 2F, 25F / 2F, 270, 50);

				glEnable(GL_TEXTURE_2D);
				DefaultSettings.fontRenderer.drawString("Tab", MathUtil.clamp(72 / 2 - (DefaultSettings.fontRenderer.getStringWidth("Tab", 1.2F, true) / 2), 0, Integer.MAX_VALUE), 7, 0xffffffff, 1.4F, true);

				DefaultSettings.fontRenderer.drawString("- DefaultSettings -", 100 + (this.gui.field_230708_k_ - 100) / 2 - DefaultSettings.fontRenderer.getStringWidth("- DefaultSettings -", 1.2F, true) / 2, 8, 0xffffffff, 1.2F, true);
				
				DefaultSettings.fontRenderer.drawString(tabName, 80, 8, 0xffffffff, 1.2F, true);

				glDisable(GL_BLEND);
				postRender(1, false);
			}

			drawTexture(1);
		}
	}
}