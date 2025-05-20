package mekmixinhelp.mixin.mekeng;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import com.mekeng.github.common.me.data.IAEGasStack;
import com.mekeng.github.common.me.inventory.impl.GasHandlerAdapter;
import mekanism.api.gas.GasStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GasHandlerAdapter.class,remap = false)
public class MixinGasHandlerAdapter {

   @Inject(method = "injectItems(Lcom/mekeng/github/common/me/data/IAEGasStack;Lappeng/api/config/Actionable;Lappeng/api/networking/security/IActionSource;)Lcom/mekeng/github/common/me/data/IAEGasStack;",at = @At("HEAD"), cancellable = true)
   public void isRadiationGas(IAEGasStack input, Actionable type, IActionSource src, CallbackInfoReturnable<IAEGasStack> cir){
       if (input != null && input.getStackSize() != 0L) {
           GasStack gasStack = input.getGasStack();
           if (gasStack != null && gasStack.getGas() != null && gasStack.getGas().isRadiation()){
               cir.setReturnValue(null);
               cir.cancel();
           }
       }
   }
}
