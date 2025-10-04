//? if !vanilla {
package net.jomcraft.defaultsettings.mixin;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface KeyMappingMixin {

    @Accessor("defaultKey")
    @Mutable
    public void setDefaultKey(InputConstants.Key key);

    @Accessor("key")
    public InputConstants.Key getKey();

}
//?}