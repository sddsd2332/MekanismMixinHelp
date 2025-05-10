package mekmixinhelp.mixin.jei;

import mekanism.client.gui.GuiFactory;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.input.InputHandler;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = InputHandler.class, remap = false)
public class MixinInputHandler {

    @Shadow
    @Final
    private RecipesGui recipesGui;

    /**
     * @author sddsd2332
     * @reason 添加工厂版本的jei配方显示
     */
    @Inject(method = "handleMouseClick", at = @At(value = "INVOKE", target = "Lmezz/jei/input/InputHandler;handleGlobalKeybinds(I)Z"), cancellable = true)
    public void addFactoryRecipe(GuiScreen guiScreen, int mouseButton, int mouseX, int mouseY, CallbackInfoReturnable<Boolean> cir) {
        if (guiScreen instanceof GuiFactory factory) {
            if (factory.getJeiRecipe(mouseX, mouseY)) {
                List<String> recipeCategoryUids = factory.getRecipe();
                if (!recipeCategoryUids.isEmpty()) {
                    this.recipesGui.showCategories(recipeCategoryUids);
                    cir.setReturnValue(true);
                    cir.cancel();
                }
            }
        }
    }
}
