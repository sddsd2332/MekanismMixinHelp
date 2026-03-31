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
import net.minecraft.item.ItemFishingRod;
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
            GlStateManager.translate((float) x, (float) y + 1.5F, (float) z);
            GlStateManager.rotate(180, 1, 0, 0);
            bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.RENDER, "meka_fishing_rod.png"));
            rod.render(0.0625F);
            GlStateManager.enableCull();
            GlStateManager.popMatrix();

            float swing = entityplayer.getSwingProgress(partialTicks);
            float swingSin = MathHelper.sin(MathHelper.sqrt(swing) * (float) Math.PI);
            Vec3d handPos = getPlayerHandPos(entityplayer, swingSin, partialTicks);
            Vec3d hookPos = new Vec3d(
                    entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks,
                    entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + 0.25D,
                    entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks
            );
            double xDiff = handPos.x - hookPos.x;
            double yDiff = handPos.y - hookPos.y;
            double zDiff = handPos.z - hookPos.z;

            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);

            for (int i = 0; i <= 16; ++i) {
                float stringFraction = fraction(i, 16);
                bufferbuilder.pos(
                        x + xDiff * (double) stringFraction,
                        y + yDiff * (double) (stringFraction * stringFraction + stringFraction) * 0.5D + 0.25D,
                        z + zDiff * (double) stringFraction
                ).color(70, 240, 149, 255).endVertex();
            }

            tessellator.draw();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    @Unique
    private Vec3d getPlayerHandPos(EntityPlayer player, float swingSin, float partialTicks) {
        int handSign = player.getPrimaryHand() == EnumHandSide.RIGHT ? 1 : -1;
        ItemStack itemstack = player.getHeldItemMainhand();
        if (!(itemstack.getItem() instanceof ItemFishingRod)) {
            handSign = -handSign;
        }

        if ((this.renderManager.options == null || this.renderManager.options.thirdPersonView <= 0) && player == Minecraft.getMinecraft().player) {
            float fov = this.renderManager.options.fovSetting / 100.0F;
            Vec3d handOffset = new Vec3d(
                    (double) handSign * (-0.36D * (double) fov + 0.02D),
                    -0.045D * (double) fov + 0.14D,
                    0.4D
            );
            handOffset = handOffset.rotatePitch(-(player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks) * 0.017453292F);
            handOffset = handOffset.rotateYaw(-(player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks) * 0.017453292F);
            handOffset = handOffset.rotateYaw(swingSin * 0.5F);
            handOffset = handOffset.rotatePitch(-swingSin * 0.7F);

            double baseX = player.prevPosX + (player.posX - player.prevPosX) * (double) partialTicks;
            double baseY = player.prevPosY + (player.posY - player.prevPosY) * (double) partialTicks + player.getEyeHeight();
            double baseZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) partialTicks;
            return new Vec3d(baseX + handOffset.x, baseY + handOffset.y, baseZ + handOffset.z);
        }

        float bodyYaw = (player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks) * 0.017453292F;
        double sinBodyYaw = MathHelper.sin(bodyYaw);
        double cosBodyYaw = MathHelper.cos(bodyYaw);
        double sideOffset = (double) handSign * 0.35D;
        double baseX = player.prevPosX + (player.posX - player.prevPosX) * (double) partialTicks;
        double baseY = player.prevPosY + (player.posY - player.prevPosY) * (double) partialTicks + player.getEyeHeight();
        double baseZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) partialTicks;
        double sneak = player.isSneaking() ? -0.1875D : 0.0D;

        Vec3d right = new Vec3d(cosBodyYaw, 0.0D, sinBodyYaw);
        Vec3d forward = new Vec3d(-sinBodyYaw, 0.0D, cosBodyYaw);
        Vec3d baseHandPos = new Vec3d(baseX, baseY, baseZ).add(
                -cosBodyYaw * sideOffset - sinBodyYaw * 0.8D,
                sneak - 0.45D,
                -sinBodyYaw * sideOffset + cosBodyYaw * 0.8D
        );
        return baseHandPos
                .add(right.scale(handSign * 0.02D))
                .add(forward.scale(0.80D))
                .add(0.0D, 0.23D, 0.0D);
    }

    @Unique
    private static float fraction(int numerator, int denominator) {
        return (float) numerator / (float) denominator;
    }
}
