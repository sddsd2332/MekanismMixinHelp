package mekmixinhelp.mixin.nc;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasHandler;
import nc.tile.internal.fluid.TankVoid;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TankVoid.class, remap = false)
public abstract class MixinTankVoid implements IGasHandler {

    @Inject(method = "canReceiveGas", at = @At("HEAD"), cancellable = true)
    public void isNotRadiation(EnumFacing side, Gas gas, CallbackInfoReturnable<Boolean> cir) {
        if (gas.isRadiation()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "receiveGas", at = @At("HEAD"), cancellable = true)
    public void isNotRadiation(EnumFacing side, GasStack stack, boolean doTransfer, CallbackInfoReturnable<Integer> cir) {
        if (stack != null && stack.getGas() != null && stack.getGas().isRadiation()) {
            cir.setReturnValue(0);
        }
    }
}
