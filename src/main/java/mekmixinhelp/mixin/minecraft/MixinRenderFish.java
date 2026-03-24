package mekmixinhelp.mixin.minecraft;

import mekanism.client.model.ModelFishRod;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderFish;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderFish.class)
public abstract class MixinRenderFish extends Render<EntityFishHook> {

    @Unique
    private static final ModelFishRod rod = new ModelFishRod();

    protected MixinRenderFish(RenderManager renderManager) {
        super(renderManager);
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/projectile/EntityFishHook;DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void setMekaFish(EntityFishHook entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        Capabilities.getMekaFishCap(entity).ifPresent(
                iMekaFishHook -> {
                    if (iMekaFishHook.isMekaFishHook()) {
                        ci.cancel();
                        renderMekaFish(entity, x, y, z, entityYaw, partialTicks);
                    }
                });
    }

    @Unique
    private void renderMekaFish(EntityFishHook entity, double x, double y, double z, float entityYaw, float partialTicks) {
        EntityPlayer entityplayer = entity.getAngler();
        if (entityplayer != null && !this.renderOutlines) {

            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            GlStateManager.translate((float) x, (float) y + 1.5, (float) z);
            GlStateManager.rotate(180, 1, 0, 0);
            bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.RENDER, "meka_fishing_rod.png"));
            rod.render(0.0625F);
            GlStateManager.enableCull();
            GlStateManager.popMatrix();

            int k = entityplayer.getPrimaryHand() == EnumHandSide.RIGHT ? 1 : -1;
            ItemStack itemstack = entityplayer.getHeldItemMainhand();
            if (!(itemstack.getItem() instanceof net.minecraft.item.ItemFishingRod)) {
                k = -k;
            }
            float f7 = entityplayer.getSwingProgress(partialTicks);
            float f8 = MathHelper.sin(MathHelper.sqrt(f7) * (float) Math.PI);
            float f9 = (entityplayer.prevRenderYawOffset + (entityplayer.renderYawOffset - entityplayer.prevRenderYawOffset) * partialTicks) * 0.017453292F;
            double d0 = MathHelper.sin(f9);
            double d1 = MathHelper.cos(f9);
            double d2 = (double) k * 0.35D;
            double d4;
            double d5;
            double d6;
            double d7;
            if ((this.renderManager.options == null || this.renderManager.options.thirdPersonView <= 0) && entityplayer == Minecraft.getMinecraft().player) {
                float f10 = this.renderManager.options.fovSetting;
                f10 = f10 / 100.0F;
                Vec3d vec3d = new Vec3d((double) k * -0.36D * (double) f10, -0.045D * (double) f10, 0.4D);
                vec3d = vec3d.rotatePitch(-(entityplayer.prevRotationPitch + (entityplayer.rotationPitch - entityplayer.prevRotationPitch) * partialTicks) * 0.017453292F);
                vec3d = vec3d.rotateYaw(-(entityplayer.prevRotationYaw + (entityplayer.rotationYaw - entityplayer.prevRotationYaw) * partialTicks) * 0.017453292F);
                vec3d = vec3d.rotateYaw(f8 * 0.5F);
                vec3d = vec3d.rotatePitch(-f8 * 0.7F);
                d4 = entityplayer.prevPosX + (entityplayer.posX - entityplayer.prevPosX) * (double) partialTicks + vec3d.x;
                d5 = entityplayer.prevPosY + (entityplayer.posY - entityplayer.prevPosY) * (double) partialTicks + vec3d.y;
                d6 = entityplayer.prevPosZ + (entityplayer.posZ - entityplayer.prevPosZ) * (double) partialTicks + vec3d.z;
                d7 = entityplayer.getEyeHeight();
            } else {
                d4 = entityplayer.prevPosX + (entityplayer.posX - entityplayer.prevPosX) * (double) partialTicks - d1 * d2 - d0 * 0.8D;
                d5 = entityplayer.prevPosY + (double) entityplayer.getEyeHeight() + (entityplayer.posY - entityplayer.prevPosY) * (double) partialTicks - 0.45D;
                d6 = entityplayer.prevPosZ + (entityplayer.posZ - entityplayer.prevPosZ) * (double) partialTicks - d0 * d2 + d1 * 0.8D;
                d7 = entityplayer.isSneaking() ? -0.1875D : 0.0D;
            }

            double d13 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
            double d8 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + 0.25D;
            double d9 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;
            double d10 = (float) (d4 - d13);
            double d11 = (double) ((float) (d5 - d8)) + d7;
            double d12 = (float) (d6 - d9);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);

            for (int i1 = 0; i1 <= 16; ++i1) {
                float f11 = (float) i1 / 16.0F;
                bufferbuilder.pos(x + d10 * (double) f11, y + d11 * (double) (f11 * f11 + f11) * 0.5D + 0.25D, z + d12 * (double) f11).color(70, 240, 149, 255).endVertex();
            }

            tessellator.draw();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }
}
