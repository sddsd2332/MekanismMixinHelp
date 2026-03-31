package mekmixinhelp.mixin.minecraft;

import mekmixinhelp.common.util.MagneticDropGuardAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(
            method = "entityDropItem(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/item/EntityItem;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void skipSuppressedDrop(ItemStack stack, float offsetY, CallbackInfoReturnable<EntityItem> cir) {
        Entity self = (Entity) (Object) this;
        if (!(self instanceof EntityPlayer player) || player.world.isRemote || stack.isEmpty()) {
            return;
        }
        if (player instanceof MagneticDropGuardAccess access && access.mekmixinhelp$consumeSuppressedDrop(stack, player.ticksExisted)) {
            cir.setReturnValue(null);
        }
    }
}
