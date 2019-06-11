package net.minecraftforge.fml.client;

import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public interface IModGuiFactory {

    void initialize(Minecraft minecraftInstance);

    boolean hasConfigGui();
    
    GuiScreen createConfigGui(GuiScreen parentScreen);
    
    public Class<? extends GuiScreen> mainConfigGuiClass();
    
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element);

    Set<RuntimeOptionCategoryElement> runtimeGuiCategories();

    public static class RuntimeOptionCategoryElement {
        public final String parent;
        public final String child;

        public RuntimeOptionCategoryElement(String parent, String child)
        {
            this.parent = parent;
            this.child = child;
        }
    }
    
    public interface RuntimeOptionGuiHandler {

     
    }
}