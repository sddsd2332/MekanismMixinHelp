package mekmixinhelp.mixin.minecraft;

import mekanism.common.item.armor.ItemMekaSuitArmor;
import mekanism.common.lib.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LayerArmorBase.class)
public abstract class LayerArmorBaseMixin {

    @Redirect(method = "renderArmorLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V",ordinal = 0))
    private void mekanism$applyMekaSuitTint(float red, float green, float blue, float alpha, EntityLivingBase entityLivingBaseIn, float limbSwing,
          float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn) {
        ItemStack stack = entityLivingBaseIn.getItemStackFromSlot(slotIn);
        Color color = ItemMekaSuitArmor.getColorModulation(stack);
        if (color != null) {
            float tintStrength = color.af();
            red = red * tintStrength + (1.0F - tintStrength);
            green = green * tintStrength + (1.0F - tintStrength);
            blue = blue * tintStrength + (1.0F - tintStrength);
        }
        GlStateManager.color(red, green, blue, alpha);
    }
}
