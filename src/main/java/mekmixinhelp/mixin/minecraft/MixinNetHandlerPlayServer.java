package mekmixinhelp.mixin.minecraft;

import mekanism.api.mixninapi.ElytraMixinHelp;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.ITickable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer implements INetHandlerPlayServer, ITickable {


    @Shadow
    public EntityPlayerMP player;

    @Inject(method = "processEntityAction", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/EntityPlayerMP;getItemStackFromSlot(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    public void elytraLikeFallFlying(CPacketEntityAction packetIn, CallbackInfo ci) {
        if (!player.onGround && !player.isElytraFlying() && !player.isInWater() && !player.isPotionActive(MobEffects.LEVITATION)) {
            ItemStack itemstack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (itemstack.getItem() instanceof ElytraMixinHelp help && help.canElytraFly(itemstack, player)) {
                this.player.setElytraFlying();
            }
        }
    }


}
