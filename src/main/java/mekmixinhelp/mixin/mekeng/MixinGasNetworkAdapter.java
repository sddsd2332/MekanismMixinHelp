package mekmixinhelp.mixin.mekeng;

import com.mekeng.github.common.me.inventory.impl.GasNetworkAdapter;
import mekanism.api.gas.Gas;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GasNetworkAdapter.class,remap = false)
public class MixinGasNetworkAdapter {

    @Inject(method = "canReceiveGas",at = @At("HEAD"), cancellable = true)
    public void isRadiationGas(EnumFacing side, Gas type, CallbackInfoReturnable<Boolean> cir){
        if (type != null && type.isRadiation()){
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
