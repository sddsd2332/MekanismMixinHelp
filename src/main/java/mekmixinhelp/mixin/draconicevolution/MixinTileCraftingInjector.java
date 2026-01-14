package mekmixinhelp.mixin.draconicevolution;

import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingInjector;
import mekanism.common.base.ITierUpgradeable;
import mekanism.common.tier.BaseTier;
import mekmixinhelp.common.config.MekceuMixinConfig;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = TileCraftingInjector.class,remap = false)
public abstract class MixinTileCraftingInjector extends TileInventoryBase implements ITierUpgradeable {


    @Shadow
    @Final
    public ManagedByte facing;

    @Shadow
    @Final
    public ManagedBool singleItem;

    @Shadow
    public abstract ItemStack getStackInPedestal();

    @Shadow
    public abstract int getPedestalTier();

    @Override
    public boolean CanInstalled() {
        return getStackInPedestal().isEmpty() && MekceuMixinConfig.current().config.DEUpgrade.val();
    }

    @Override
    public boolean upgrade(BaseTier tier) {
        if (tier == BaseTier.CREATIVE || tier == BaseTier.BASIC) {
            return false;
        }
        world.setBlockToAir(getPos());
        world.setBlockState(getPos(),DEFeatures.craftingInjector.getStateFromMeta(tier.ordinal()));
        if (world.getTileEntity(getPos()) instanceof TileCraftingInjector injector) {
            injector.setStackInPedestal(getStackInPedestal());
            injector.facing.value = facing.value;
            injector.singleItem.value = singleItem.value;
            this.updateBlock();
            return true;
        }
        return false;
    }
}
