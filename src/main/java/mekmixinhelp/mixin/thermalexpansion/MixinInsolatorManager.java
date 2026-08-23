package mekmixinhelp.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.*;
import cofh.thermalfoundation.item.ItemFertilizer;
import mekanism.api.recipes.FarmChanceOutput;
import mekanism.common.MekanismFluids;
import mekanism.common.config.MekanismConfig;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;

@Mixin(value = InsolatorManager.class, remap = false)
public class MixinInsolatorManager {

    @Inject(method = "addRecipe(IILnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;ILcofh/thermalexpansion/util/managers/machine/InsolatorManager$Type;)Lcofh/thermalexpansion/util/managers/machine/InsolatorManager$InsolatorRecipe;", at = @At("HEAD"))
    private static void addMekanismFarm(int energy, int water, ItemStack primaryInput, ItemStack secondaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, Type type, CallbackInfoReturnable<InsolatorRecipe> cir) {
        if (secondaryInput == ItemFertilizer.fertilizerBasic || secondaryInput == ItemFertilizer.fertilizerRich || secondaryInput == ItemFertilizer.fertilizerFlux) {
            if (primaryInput.isEmpty() || primaryOutput.isEmpty()) {
                return;
            }
            if (!RecipeHandler.Recipe.ORGANIC_FARM.containsRecipe(primaryInput)) {
                int waterMultiplier = type == Type.TREE ? 6 : 3;
                if (!secondaryOutput.isEmpty()) {
                    double chance = type == Type.TREE ? MekanismConfig.current().mekce.log.val() : MekanismConfig.current().mekce.seed.val();
                    RecipeHandler.addOrganicFarmRecipe(primaryInput, MekanismFluids.NutrientSolution, StackUtils.size(primaryOutput, 24),
                            Collections.singletonList(new FarmChanceOutput(StackUtils.size(secondaryOutput, 4), chance)));
                    RecipeHandler.addOrganicFarmRecipe(primaryInput, new FluidStack(FluidRegistry.WATER, 1), StackUtils.size(primaryOutput, waterMultiplier),
                            Collections.singletonList(new FarmChanceOutput(StackUtils.size(secondaryOutput, 1), chance)));
                } else {
                    RecipeHandler.addOrganicFarmRecipe(primaryInput, MekanismFluids.NutrientSolution, StackUtils.size(primaryOutput, 24));
                    RecipeHandler.addOrganicFarmRecipe(primaryInput, new FluidStack(FluidRegistry.WATER, 1), StackUtils.size(primaryOutput, waterMultiplier));
                }
            }
        }
    }


}
