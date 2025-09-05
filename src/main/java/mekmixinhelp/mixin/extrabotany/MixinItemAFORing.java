package mekmixinhelp.mixin.extrabotany;

import com.meteor.extrabotany.common.item.equipment.bauble.ItemAFORing;
import mekmixinhelp.common.config.ExtraBotanyMixinConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemAFORing.class, remap = false)
public class MixinItemAFORing {

    @Inject(method = "onWornTick", at = @At("HEAD"), cancellable = true)
    public void fix(ItemStack stack, EntityLivingBase entity, CallbackInfo ci) {
        if (ExtraBotanyMixinConfig.closeAFORing) {
            ci.cancel();
        }
    }
}
