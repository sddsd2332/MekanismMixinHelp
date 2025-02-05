package mekmixinhelp.mixin.tconevo;

import mekanism.common.MekanismItems;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import xyz.phanta.tconevo.integration.mekanism.MekanismHooks;
import xyz.phanta.tconevo.integration.mekanism.MekanismHooksImpl;

@Mixin(MekanismHooksImpl.class)
public abstract class MixinMekanismHooksImpl implements MekanismHooks {

    /**
     * @author sddsd2332
     * @reason 修复移除的HDPE
     */
    @Overwrite(remap = false)
    public void onInit(FMLInitializationEvent event) {
        OreDictionary.registerOre("pelletHDPE",new ItemStack(MekanismItems.HDPE_PELLET));
        OreDictionary.registerOre("rodHDPE", new ItemStack(MekanismItems.HDPE_ROD));
        OreDictionary.registerOre("sheetHDPE", new ItemStack(MekanismItems.HDPE_SHEET));
        OreDictionary.registerOre("stickHDPE", new ItemStack(MekanismItems.HDPE_STICK));
    }
}
