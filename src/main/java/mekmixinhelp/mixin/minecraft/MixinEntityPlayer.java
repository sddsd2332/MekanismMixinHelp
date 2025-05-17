package mekmixinhelp.mixin.minecraft;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static mekanism.common.CardboardArmorHandler.testForStealth;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {

    @Shadow
    public float eyeHeight;

    @Shadow public abstract float getDefaultEyeHeight();

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @Unique
    private boolean canSet;


    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;onLivingUpdate()V"))
    public void setPlayerSize(CallbackInfo ci) {
        if (testForStealth(this)) {
            setSize(0.6F, 0.6F);
            eyeHeight = 0.6F;
            canSet = true;
        } else if (canSet) {
            setSize(0.6F, 1.8F);
            eyeHeight = getDefaultEyeHeight();
            canSet = false;
        }
    }
}
