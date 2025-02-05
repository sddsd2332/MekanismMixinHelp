package mekmixinhelp.mixin.minecraft;

import com.mojang.authlib.GameProfile;
import mekanism.api.mixninapi.ElytraMixinHelp;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {


    @Shadow
    @Final
    public NetHandlerPlayClient connection;


    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }


    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/entity/EntityPlayerSP;getItemStackFromSlot(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    public void elytraLikeFallFlying(CallbackInfo ci) {
        ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (itemstack.getItem() instanceof ElytraMixinHelp help && help.canElytraFly(itemstack, this) && this.tryToStartFallFlying()) {
            this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_FALL_FLYING));
        }
    }


    @Unique
    public boolean tryToStartFallFlying() {
        if (!this.onGround && !this.isElytraFlying() && !this.isInWater() && !this.isPotionActive(MobEffects.LEVITATION)) {
            ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (itemstack.getItem() instanceof ElytraMixinHelp help && help.canElytraFly(itemstack, this)) {
                this.setFlag(7, true);
                return true;
            }
        }
        return false;
    }

}
