package net.minecraft.command;

import net.minecraft.util.IChatComponent;
import net.minecraft.util.text.ITextComponent;

public interface ICommandSender {
    void func_145747_a(IChatComponent p_145747_1_);

    default void sendMessage(ITextComponent component)
    {
    }
}