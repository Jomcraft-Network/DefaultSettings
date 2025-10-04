package net.jomcraft.defaultsettings.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Redirect(/*remap = false, */method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;save()V"))
    private void redirected(Options instance) {

    }
}