package mekmixinhelp.mixin.minecraft;

import mekanism.api.transmitters.TransmissionType;
import mekanism.client.gui.element.GuiUtils;
import mekanism.common.block.states.BlockStateTransmitter;
import mekanism.common.item.ItemBlockTransmitter;
import mekanism.common.tier.BaseTier;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(RenderItem.class)
@SideOnly(Side.CLIENT)
public abstract class MixinRenderItem implements IResourceManagerReloadListener {


    @Inject(method = "renderItemOverlayIntoGUI",at = @At("TAIL"))
    public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text, CallbackInfo ci){
        if (!stack.isEmpty()) {
            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            if (stack.getItem() instanceof ItemBlockTransmitter transmitter) {
                TransmissionType transmission = BlockStateTransmitter.TransmitterType.values()[stack.getItemDamage()].getTransmission();
                if (transmission == TransmissionType.GAS || transmission == TransmissionType.HEAT || transmission == TransmissionType.ENERGY) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0, 0, 200);
                    BlockStateTransmitter.TransmitterType type = BlockStateTransmitter.TransmitterType.get(stack.getItemDamage());
                    String name = type.getTranslationKey();
                    if (type.hasTiers()) {
                        BaseTier tier = transmitter.getBaseTier(stack);
                        name = tier.getSimpleName() + name;
                    }
                    Minecraft.getMinecraft().renderEngine.bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI_ICONS, name.toLowerCase(Locale.ROOT) + ".png"));
                    GuiUtils.blit(xPosition, yPosition, 0, 0, 16, 16, 16, 16);
                    GlStateManager.popMatrix();
                }
            }
        }
    }


}
