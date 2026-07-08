package mekmixinhelp.mixin.jei;

import mezz.jei.gui.GuiScreenHelper;
import mezz.jei.gui.overlay.bookmarks.LeftAreaDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LeftAreaDispatcher.class, remap = false)
public abstract class MixinLeftAreaDispatcher {

    @Shadow
    @Final
    private GuiScreenHelper guiScreenHelper;

    @Inject(method = "handleMouseScrolled", at = @At("HEAD"), cancellable = true)
    private void mekmixinhelp$skipScrollInGuiExclusionArea(int mouseX, int mouseY, int scrollDelta, CallbackInfoReturnable<Boolean> cir) {
        if (guiScreenHelper.isInGuiExclusionArea(mouseX, mouseY)) {
            cir.setReturnValue(false);
        }
    }
}
