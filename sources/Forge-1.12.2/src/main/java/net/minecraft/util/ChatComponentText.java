package net.minecraft.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ChatComponentText implements IChatComponent {
    private final String text;
    private ChatStyle style;

    public ChatComponentText(String p_i45159_1_) {
        this.text = p_i45159_1_;
    }

    public ChatStyle func_150256_b() {
        if (this.style == null) {
            this.style = new ChatStyle();
            /*Iterator iterator = this.siblings.iterator();

            while (iterator.hasNext()) {
                IChatComponent ichatcomponent = (IChatComponent)iterator.next();
                ichatcomponent.getChatStyle().setParentStyle(this.style);
            }*/
        }

        return this.style;
    }

    @NotNull
    @Override
    public Iterator iterator() {
        return null;
    }
}