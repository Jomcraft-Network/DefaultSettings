package de.pt400c.defaultsettings.gui;

import java.util.ArrayList;
import java.util.List;

import de.pt400c.defaultsettings.FileUtil;
import net.minecraft.client.gui.GuiScreen;

public class MenuScreen extends Segment {
	
	private List<MenuArea> variants = new ArrayList<MenuArea>();
	private int index = 0;
	public volatile MutableByte exportActive = new MutableByte((byte) 0); //0 : Not clear, 1 : false : 2 : true

	public MenuScreen(GuiScreen gui, float posX, float posY) {
		super(gui, posX, posY, gui.width - posX, gui.height - posY, false);
		new Thread(new ThreadRunnable(exportActive) {
			
			@Override
			public void run() {
				if(FileUtil.exportMode())
					this.supply.setByte((byte) 2);
				else
					this.supply.setByte((byte) 1);
			}
		}).start();

	}
	
	@Override
    public void render(float mouseX, float mouseY, float partialTicks) {

        synchronized (this.variants) {
        	this.variants.get(this.index).render(mouseX, mouseY, partialTicks);
        }

	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		synchronized (this.variants) {
			this.variants.get(this.index).mouseClicked(mouseX, mouseY, mouseButton);
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_) {
		synchronized (this.variants) {
			this.variants.get(this.index).mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_);

		}
		return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_);
	}
	
	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
		synchronized (this.variants) {

			this.variants.get(this.index).mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);

		}
		return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
	}
	
	public MenuScreen addVariant(MenuArea segment) {
		synchronized (this.variants) {
			this.variants.add(segment);
		}
		return this;
	}

	public void setIndex(int id) {
		this.index = id;
	}
	
	abstract private class ThreadRunnable implements Runnable {
	 	   
        final MutableByte supply;

        ThreadRunnable(MutableByte supply) {
            this.supply = supply;
        }
    }
	
	public class MutableByte {
    	
    	private byte supply;
    	
    	public MutableByte(byte supply) {
			this.supply = supply;
		}
    	
    	public byte getByte() {
    		return this.supply;
    	}
    	
    	public void setByte(byte supply) {
    		this.supply = supply;
    	}
    }

}