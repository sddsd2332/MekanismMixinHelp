package mekmixinhelp.mixin.minecraft;

import mekanism.common.item.armor.ItemMekaSuitArmor;
import mekanism.common.lib.Color;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(RenderItem.class)
public abstract class RenderItemMixin {

    @Redirect(method = "renderQuads", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/model/pipeline/LightUtil;renderQuadColor(Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/client/renderer/block/model/BakedQuad;I)V",remap = false))
    private void mekanism$applyMekaSuitItemTint(BufferBuilder buffer, BakedQuad bakedQuad, int packedColor, BufferBuilder renderer,
          List<BakedQuad> quads, int color, ItemStack stack) {
        if (bakedQuad.hasTintIndex()) {
            Color modulation = ItemMekaSuitArmor.getColorModulation(stack);
            if (modulation != null) {
                packedColor = packTintColor(modulation);
            }
        }
        LightUtil.renderQuadColor(buffer, bakedQuad, packedColor);
    }

    private static int packTintColor(Color color) {
        double tintStrength = color.ad();
        int red = tintComponent(color.rd(), tintStrength);
        int green = tintComponent(color.gd(), tintStrength);
        int blue = tintComponent(color.bd(), tintStrength);
        return 0xFF000000 | red << 16 | green << 8 | blue;
    }

    private static int tintComponent(double component, double tintStrength) {
        return clampToByte((component * tintStrength + (1.0D - tintStrength)) * 255.0D);
    }

    private static int clampToByte(double value) {
        return (int) Math.max(0, Math.min(255, Math.round(value)));
    }
}
