package mekmixinhelp.mixin.mekeng;

import com.mekeng.github.util.helpers.ItemGasHandler;
import mekanism.api.gas.GasStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemGasHandler.class,remap = false)
public class MixinItemGasHandler {

    @Inject(method = "addGas",at = @At("HEAD"), cancellable = true)
    public void isRadiationGas(GasStack gas, boolean add, CallbackInfoReturnable<Integer> cir){
        if (gas != null && gas.getGas() != null && gas.getGas().isRadiation()){
            cir.setReturnValue(0);
            cir.cancel();
        }
    }
}
