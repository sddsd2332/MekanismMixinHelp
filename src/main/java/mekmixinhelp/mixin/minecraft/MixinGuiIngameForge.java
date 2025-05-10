package mekmixinhelp.mixin.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(GuiIngameForge.class)
public abstract class MixinGuiIngameForge extends GuiIngame {
    @Shadow(remap = false)
    public static int left_height;

    @Shadow(remap = false)
    public static int right_height;

    @Shadow(remap = false)
    @Final
    private static int WHITE;

    @Shadow(remap = false)
    private FontRenderer fontrenderer;

    public MixinGuiIngameForge(Minecraft mcIn) {
        super(mcIn);
    }

    @Inject(method = "renderRecordOverlay", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void renderRecordOverlay(int width, int height, float partialTicks, CallbackInfo ci) {
        if (overlayMessageTime > 0) {
            mc.profiler.startSection("overlayMessage");
            float hue = (float) overlayMessageTime - partialTicks;
            int opacity = (int) (hue * 256.0F / 20.0F);
            if (opacity > 255) {
                opacity = 255;
            }
            if (opacity > 8) {
                //Include a shift based on the bar height plus the difference between the height that renderSelectedItemName
                // renders at (59) and the height that the overlay/status bar renders at (68) by default
                int yShift = Math.max(left_height, right_height) + (68 - 59);
                GlStateManager.pushMatrix();
                GlStateManager.translate(width / 2D, height - Math.max(yShift, 68), 0.0D);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                int color = (animateOverlayMessageColor ? Color.HSBtoRGB(hue / 50.0F, 0.7F, 0.6F) & WHITE : WHITE);
                int messageWidth = fontrenderer.getStringWidth(overlayMessage);
                fontrenderer.drawString(overlayMessage, -messageWidth / 2, -4, color | (opacity << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            mc.profiler.endSection();

        }


        ci.cancel();
    }
}
