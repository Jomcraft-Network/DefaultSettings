package net.jomcraft.defaultsettings.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
//? if (!vanilla) && (>=1.20) {
/*import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ArgumentTypeInfos.class)
*///?}
//? if (!vanilla) && (<1.20) {
@Mixin(Minecraft.class)
//?}
public interface ArgumentTypeInfosMixin {

    //? if (!vanilla) && (>=1.20) {
    /*@Accessor("BY_CLASS")
    static Map<Class<?>, ArgumentTypeInfo<?, ?>> getBY_CLASS(){
        throw new AssertionError();
    }
    *///?}

}
