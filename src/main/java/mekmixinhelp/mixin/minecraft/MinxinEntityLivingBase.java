package mekmixinhelp.mixin.minecraft;

import mekanism.api.mixninapi.ElytraMixinHelp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public abstract class MinxinEntityLivingBase extends Entity {

    @Shadow
    public abstract ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn);

    @Shadow
    public int ticksElytraFlying;

    @Shadow public abstract boolean isPotionActive(Potion potionIn);

    public MinxinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "updateElytra", at = @At(value = "HEAD"),cancellable = true)
    public void isElytra(CallbackInfo ci) {
        boolean flag = this.getFlag(7);
        if (flag && !this.onGround && !this.isRiding() && !this.isPotionActive(MobEffects.LEVITATION)) {
            ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (itemstack.getItem() == Items.ELYTRA && ItemElytra.isUsable(itemstack) || itemstack.getItem() instanceof ElytraMixinHelp help && help.canElytraFly(itemstack, ((EntityLivingBase) (Object) this)) && help.elytraFlightTick(itemstack, ((EntityLivingBase) (Object) this), this.ticksElytraFlying)) {
                flag = true;
                if (!this.world.isRemote && (this.ticksElytraFlying + 1) % 20 == 0 && itemstack.getItem() == Items.ELYTRA) {
                    itemstack.damageItem(1, ((EntityLivingBase) (Object) this));
                }
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }
        if (!this.world.isRemote) {
            this.setFlag(7, flag);
        }
        ci.cancel();
    }
}
