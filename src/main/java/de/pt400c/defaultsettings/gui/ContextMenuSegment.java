package de.pt400c.defaultsettings.gui;

import net.minecraft.client.gui.GuiScreen;
import static de.pt400c.defaultsettings.DefaultSettings.fontRenderer;
import static de.pt400c.neptunefx.NEX.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.pt400c.defaultsettings.FileUtil;
import de.pt400c.defaultsettings.GuiConfig;

@SideOnly(Side.CLIENT)
public class ContextMenuSegment extends Segment {
	
	public float backgroundTimer = 0;
	public boolean openContext = false;
	public final ProfilesSegment parent;
	private RenameSegment nameField;

	@Nullable
	public String id = null;
	public float alpha;
	
	public ContextMenuSegment(GuiScreen gui, ProfilesSegment parent, float posX, float posY, float width, float height) {	
		super(gui, posX, posY, width, height, false);
		this.parent = parent;
	}
	
	public int isSelectedSome(int mouseX, int mouseY) {
		if(!(mouseX >= this.getPosX() + 0.5F && mouseX < this.getPosX() + this.getWidth() - 0.5F))
			return -1;
		
		return mouseY >= this.getPosY() + 2 && mouseY < this.getPosY() + 15 ? 0 : mouseY >= this.getPosY() + 17 && mouseY < this.getPosY() + 30 ? 1 : mouseY >= this.getPosY() + 32 && mouseY < this.getPosY() + 45 ? 2 : mouseY >= this.getPosY() + 47 && mouseY < this.getPosY() + 60 ? 3 : -1;
	}
	
	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int button) {
		
		int selected = isSelectedSome(mouseX, mouseY);
		
		if(selected > -1 && this.openContext) {
			clickSound();
			
			if(selected == 0) {
			
			if(parent.selectedName.equals(this.id) || FileUtil.mainJson.mainProfile.equals(this.id)) {
				this.openContext = false;
				return true;
			}
			try {
				File toDelete = new File(FileUtil.getMainFolder(), this.id);

				if(toDelete.isDirectory())
					FileUtils.deleteDirectory(toDelete);
				else
					Files.delete(toDelete.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.id = null;
			this.parent.guiContentUpdate(this.parent.searchbar.query);
			} else if(selected == 1) {
				
				openRenamePopup(this.id);
				
			} else if(selected == 2) {
				
				openRenameClone(this.id);
				
			}else if(selected == 3) {
				
				FileUtil.mainJson.mainProfile = this.id;
				FileUtil.mainJson.save();
				
			}
			this.openContext = false;
			return true;
		}else {
			return false;
		}
	}
	
	private void openRenameClone(String text) {
		GuiConfig gui = ((GuiConfig) this.gui);
		gui.popup.setOpening(true);
		gui.popup.getWindow().title = "Clone";
		gui.popup.getWindow().setPos(this.width / 2 - 210 / 2, this.height / 2 - 100 / 2);
		gui.popupField = gui.popup;
		gui.popupField.getWindow().clearChildren();
		
		gui.popupField.getWindow().addChild(this.nameField = new RenameSegment(gui, text, 10, 35, 190, 18, true, 2));
		gui.popupField.getWindow().addChild(new QuitButtonSegment(gui, 190, 5, 14, 14, button -> {

			gui.popupField.setOpening(false);

			return true;
		}, 3F, true));

		MenuScreen menu = ((GuiConfig) this.gui).menu;
		menu.getVariants().get(menu.index).selected = this.nameField;
		this.nameField.query = text;
		this.nameField.cursorPosition = text.length();
		gui.popupField.getWindow().addChild(new ButtonRoundSegment(gui, 105 - 30, 75, 60, 20, "Okay", null, button -> {

			if(!gui.scrollableProfiles.profiles.contains(this.nameField.query.toLowerCase())) {

			File fileDir = new File(FileUtil.getMainFolder(), this.nameField.tex);
			File fileDir2 = new File(FileUtil.getMainFolder(), this.nameField.query);
			try {
				FileUtils.copyDirectory(fileDir, fileDir2);
			} catch (IOException e) {
				e.printStackTrace();
			}
			ContextMenuSegment.this.parent.guiContentUpdate(ContextMenuSegment.this.parent.searchbar.query);

			gui.scrollableProfiles.context.id = this.nameField.query;
			gui.popupField.setOpening(false);
			
			
			ContextMenuSegment.this.backgroundTimer = 2.5F * (MathUtil.PI / 3);
			ContextMenuSegment.this.setPos(-100, -100);
			}else {
				this.nameField.denied = true;
			}
			return true;
		}, 0.8F, true));

		gui.popup.setVisible(true);
		
	}

	
	private void openRenamePopup(String text) {
		GuiConfig gui = ((GuiConfig) this.gui);
		gui.popup.setOpening(true);
		gui.popup.getWindow().title = "Rename";
		gui.popup.getWindow().setPos(this.width / 2 - 210 / 2, this.height / 2 - 100 / 2);
		gui.popupField = gui.popup;
		gui.popupField.getWindow().clearChildren();
		
		gui.popupField.getWindow().addChild(this.nameField = new RenameSegment(gui, text, 10, 35, 190, 18, true, 0));
		gui.popupField.getWindow().addChild(new QuitButtonSegment(gui, 190, 5, 14, 14, button -> {

			gui.popupField.setOpening(false);

			return true;
		}, 3F, true));

		MenuScreen menu = ((GuiConfig) this.gui).menu;
		menu.getVariants().get(menu.index).selected = this.nameField;
		
		gui.popupField.getWindow().addChild(new ButtonRoundSegment(gui, 105 - 30, 75, 60, 20, "Okay", null, button -> {

			if(!gui.scrollableProfiles.profiles.contains(this.nameField.query.toLowerCase())) {
			
			if(ContextMenuSegment.this.parent.selectedName.equals(ContextMenuSegment.this.id)) 
				ContextMenuSegment.this.parent.selectedName = ContextMenuSegment.this.nameField.query;
			
			if(FileUtil.privateJson.currentProfile.equals(ContextMenuSegment.this.id)) {
				FileUtil.activeProfile = ContextMenuSegment.this.nameField.query;
				FileUtil.privateJson.currentProfile = ContextMenuSegment.this.nameField.query;
				FileUtil.privateJson.save();
			}
			
			if(FileUtil.mainJson.mainProfile.equals(ContextMenuSegment.this.id)) {
				FileUtil.mainJson.mainProfile = ContextMenuSegment.this.nameField.query;
				FileUtil.mainJson.save();
			}
			
			if(FileUtil.privateJson.targetProfile.equals(ContextMenuSegment.this.id)) {
				FileUtil.privateJson.targetProfile = ContextMenuSegment.this.nameField.query;
				FileUtil.privateJson.save();
			}

			File fileDir = new File(FileUtil.getMainFolder(), ContextMenuSegment.this.id);
			fileDir.renameTo(new File(FileUtil.getMainFolder(), ContextMenuSegment.this.nameField.query));
			ContextMenuSegment.this.parent.guiContentUpdate(ContextMenuSegment.this.parent.searchbar.query);

			gui.popupField.setOpening(false);
			
			
			ContextMenuSegment.this.backgroundTimer = 2.5F * (MathUtil.PI / 3);
			ContextMenuSegment.this.setPos(-100, -100);
			}else {
				this.nameField.denied = true;
			}
			return true;
		}, 0.8F, true));

		gui.popup.setVisible(true);
		
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		if (this.openContext) {
			if (this.backgroundTimer <= MathUtil.PI / 3)
				this.backgroundTimer += 0.05;

		} else {
			if (this.backgroundTimer > 0)
				this.backgroundTimer -= 0.05;

		}
		
		this.alpha = (float) ((Math.sin(3 * MathUtil.clamp(this.backgroundTimer, 0, MathUtil.PI / 3) - (MathUtil.PI / 2)) + 1) / 2);
		
		if(openContext && ((DefaultSettingsGUI) this.gui).menu.getVariants().get(((DefaultSettingsGUI) this.gui).menu.index).selected != this.parent) {
			openContext = false;
		}
		
		int color = 0xff232323;

		int value = (int) ((((color >> 24 & 255) / 255.0F) - (1 - alpha)) * 255F) ;
	     
        color = ((value & 0x0ff) << 24) | (((color >> 16 & 255) & 0x0ff) << 16) | (((color >> 8 & 255) & 0x0ff) << 8) | ((color & 255) & 0x0ff);
		
		glEnable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		drawRectRoundedCorners(this.posX, this.posY, this.posX + this.width, this.posY + this.height, color, 2);
		int selected = isSelectedSome(mouseX, mouseY);
		if(selected > -1) {
			color = 0xff545454;
			value = (int) ((((color >> 24 & 255) / 255.0F) - (1 - alpha)) * 255F) ;
	     
        	color = ((value & 0x0ff) << 24) | (((color >> 16 & 255) & 0x0ff) << 16) | (((color >> 8 & 255) & 0x0ff) << 8) | ((color & 255) & 0x0ff);
		
        	if(selected == 0)
        	
        		drawRectRoundedCorners(this.posX + 0.5F, this.posY + 2, this.posX + this.width - 0.5F, this.posY + 15, color, 2);
        	
        	else if(selected == 1)
        		drawRectRoundedCorners(this.posX + 0.5F, this.posY + 17, this.posX + this.width - 0.5F, this.posY + 30, color, 2);
        	
        	else if(selected == 2)
        		drawRectRoundedCorners(this.posX + 0.5F, this.posY + 32, this.posX + this.width - 0.5F, this.posY + 45, color, 2);
        	
        	else if(selected == 3)
        		drawRectRoundedCorners(this.posX + 0.5F, this.posY + 47, this.posX + this.width - 0.5F, this.posY + 60, color, 2);

		}
		
		color = 0xffffffff;
		value = (int) ((((color >> 24 & 255) / 255.0F) - (1 - alpha)) * 255F) ;
     
    	color = ((value & 0x0ff) << 24) | (((color >> 16 & 255) & 0x0ff) << 16) | (((color >> 8 & 255) & 0x0ff) << 8) | ((color & 255) & 0x0ff);
		
    	drawRect(this.posX + 1.5F, this.posY + 15.75F, this.posX + this.width - 1.5F, this.posY + 16.25F, color, false, null, false);
    	
		drawRect(this.posX + 1.5F, this.posY + 30.75F, this.posX + this.width - 1.5F, this.posY + 31.25F, color, false, null, false);
		
		drawRect(this.posX + 1.5F, this.posY + 45.75F, this.posX + this.width - 1.5F, this.posY + 46.25F, color, false, null, false);
		
		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);
		
		glEnable(GL_BLEND);
		glBlendFuncSeparate(770, 771, 1, 0);

		fontRenderer.drawString("Delete", this.getPosX() + 3, this.getPosY() + 5, calcAlpha(0xffffffff, 1 - alpha).getRGB(), 0.9F, true);
		
		fontRenderer.drawString("Rename", this.getPosX() + 3, this.getPosY() + 20, calcAlpha(0xffffffff, 1 - alpha).getRGB(), 0.9F, true);
		
		fontRenderer.drawString("Clone", this.getPosX() + 3, this.getPosY() + 35, calcAlpha(0xffffffff, 1 - alpha).getRGB(), 0.9F, true);
		
		fontRenderer.drawString("Set Main", this.getPosX() + 3, this.getPosY() + 50, calcAlpha(0xffffffff, 1 - alpha).getRGB(), 0.9F, true);
		glDisable(GL_BLEND);
		
	}
}