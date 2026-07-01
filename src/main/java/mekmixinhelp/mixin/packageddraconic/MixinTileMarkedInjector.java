package mekmixinhelp.mixin.packageddraconic;

import mekanism.common.base.IUpgradeableTile;
import mekanism.common.tier.BaseTier;
import mekanism.common.upgrade.IUpgradeData;
import mekmixinhelp.common.config.MekceuMixinConfig;
import mekmixinhelp.common.upgrade.DraconicPedestalUpgradeData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import thelm.packagedauto.tile.TileBase;
import thelm.packageddraconic.block.BlockMarkedInjector;
import thelm.packageddraconic.tile.TileMarkedInjector;

import javax.annotation.Nullable;

@Mixin(value = TileMarkedInjector.class, remap = false)
public abstract class MixinTileMarkedInjector extends TileBase implements IUpgradeableTile {


    @Shadow
    public abstract ItemStack getStackInPedestal();

    @Override
    public boolean canInstallUpgrade(BaseTier upgradeTier) {
        return upgradeTier != BaseTier.CREATIVE && upgradeTier != BaseTier.BASIC
                && getStackInPedestal().isEmpty()
                && MekceuMixinConfig.current().config.PackagedDEUpgrade.val()
                && getUpgradeResult(upgradeTier) != null;
    }

    @Shadow
    public abstract void setStackInPedestal(ItemStack stack);

    @Nullable
    @Override
    public IBlockState getUpgradeResult(BaseTier upgradeTier) {
        if (upgradeTier == BaseTier.CREATIVE || upgradeTier == BaseTier.BASIC || world == null || getPos() == null) {
            return null;
        }
        IBlockState block = world.getBlockState(getPos());
        if (block.getBlock() instanceof BlockMarkedInjector injector) {
            if (upgradeTier.ordinal() <= injector.tier) {
                return null;
            }
            int meta = injector.getMetaFromState(block);
            if (upgradeTier == BaseTier.ADVANCED) {
                return BlockMarkedInjector.WYVERN.getStateFromMeta(meta);
            } else if (upgradeTier == BaseTier.ELITE) {
                return BlockMarkedInjector.DRACONIC.getStateFromMeta(meta);
            } else if (upgradeTier == BaseTier.ULTIMATE) {
                return BlockMarkedInjector.CHAOTIC.getStateFromMeta(meta);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public IUpgradeData getUpgradeData(BaseTier upgradeTier) {
        if (!canInstallUpgrade(upgradeTier)) {
            return null;
        }
        return new DraconicPedestalUpgradeData(getStackInPedestal());
    }

    @Override
    public boolean parseUpgradeData(IUpgradeData upgradeData) {
        if (upgradeData instanceof DraconicPedestalUpgradeData data) {
            setStackInPedestal(data.pedestalStack.copy());
            return true;
        }
        return false;
    }

}
