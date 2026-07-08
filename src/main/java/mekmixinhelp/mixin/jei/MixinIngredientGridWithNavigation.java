package mekmixinhelp.mixin.jei;

import mezz.jei.gui.overlay.IngredientGridWithNavigation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = IngredientGridWithNavigation.class, remap = false)
public abstract class MixinIngredientGridWithNavigation {

    @Shadow
    public abstract boolean isMouseOver(int mouseX, int mouseY);

    @Inject(method = "handleMouseScrolled", at = @At("HEAD"), cancellable = true)
    private void mekmixinhelp$skipScrollOutsideIngredientGrid(int mouseX, int mouseY, int scrollDelta, CallbackInfoReturnable<Boolean> cir) {
        if (!isMouseOver(mouseX, mouseY)) {
            cir.setReturnValue(false);
        }
    }
}
