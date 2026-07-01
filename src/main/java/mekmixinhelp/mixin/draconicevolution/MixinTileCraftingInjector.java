package mekmixinhelp.mixin.draconicevolution;

import com.brandon3055.brandonscore.blocks.TileInventoryBase;
import com.brandon3055.brandonscore.lib.datamanager.ManagedBool;
import com.brandon3055.brandonscore.lib.datamanager.ManagedByte;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCraftingInjector;
import mekanism.common.base.IUpgradeableTile;
import mekanism.common.tier.BaseTier;
import mekanism.common.upgrade.IUpgradeData;
import mekmixinhelp.common.config.MekceuMixinConfig;
import mekmixinhelp.common.upgrade.DraconicPedestalUpgradeData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(value = TileCraftingInjector.class,remap = false)
public abstract class MixinTileCraftingInjector extends TileInventoryBase implements IUpgradeableTile {


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

    @Shadow
    public abstract void setStackInPedestal(ItemStack stack);

    @Override
    public boolean canInstallUpgrade(BaseTier upgradeTier) {
        return upgradeTier != BaseTier.CREATIVE && upgradeTier != BaseTier.BASIC
                && upgradeTier.ordinal() > getPedestalTier()
                && getStackInPedestal().isEmpty()
                && MekceuMixinConfig.current().config.DEUpgrade.val();
    }

    @Nullable
    @Override
    public IBlockState getUpgradeResult(BaseTier upgradeTier) {
        if (!canInstallUpgrade(upgradeTier)) {
            return null;
        }
        return DEFeatures.craftingInjector.getStateFromMeta(upgradeTier.ordinal());
    }

    @Nullable
    @Override
    public IUpgradeData getUpgradeData(BaseTier upgradeTier) {
        if (getUpgradeResult(upgradeTier) == null) {
            return null;
        }
        return new DraconicPedestalUpgradeData(getStackInPedestal(), facing.value, singleItem.value);
    }

    @Override
    public boolean parseUpgradeData(IUpgradeData upgradeData) {
        if (upgradeData instanceof DraconicPedestalUpgradeData data && data.hasManagedData) {
            setStackInPedestal(data.pedestalStack.copy());
            facing.value = data.facing;
            singleItem.value = data.singleItem;
            updateBlock();
            return true;
        }
        return false;
    }
}
