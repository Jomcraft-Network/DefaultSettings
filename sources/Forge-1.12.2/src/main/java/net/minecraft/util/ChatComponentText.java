package net.minecraft.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ChatComponentText implements IChatComponent {
    private final String text;

    public ChatComponentText(String p_i45159_1_) {
        this.text = p_i45159_1_;
    }

    @NotNull
    @Override
    public Iterator iterator() {
        return null;
    }
}