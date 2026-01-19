package mekmixinhelp.mixin.packageddraconic;

import mekanism.common.base.ITierUpgradeable;
import mekanism.common.tier.BaseTier;
import mekmixinhelp.common.config.MekceuMixinConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import thelm.packagedauto.tile.TileBase;
import thelm.packageddraconic.block.BlockMarkedInjector;
import thelm.packageddraconic.tile.TileMarkedInjector;

@Mixin(value = TileMarkedInjector.class, remap = false)
public abstract class MixinTileMarkedInjector extends TileBase implements ITierUpgradeable {


    @Shadow
    public abstract ItemStack getStackInPedestal();

    @Override
    public boolean CanInstalled() {
        return getStackInPedestal().isEmpty() && MekceuMixinConfig.current().config.PackagedDEUpgrade.val();
    }

    @Override
    public boolean upgrade(BaseTier tier) {
        if (tier == BaseTier.CREATIVE || tier == BaseTier.BASIC) {
            return false;
        }
        IBlockState block = world.getBlockState(getPos());
        if (block.getBlock() instanceof BlockMarkedInjector injector) {
            world.setBlockToAir(getPos());
            if (tier == BaseTier.ADVANCED) {
                world.setBlockState(getPos(), BlockMarkedInjector.WYVERN.getStateFromMeta(injector.getMetaFromState(block)));
            } else if (tier == BaseTier.ELITE) {
                world.setBlockState(getPos(), BlockMarkedInjector.DRACONIC.getStateFromMeta(injector.getMetaFromState(block)));
            } else if (tier == BaseTier.ULTIMATE) {
                world.setBlockState(getPos(), BlockMarkedInjector.CHAOTIC.getStateFromMeta(injector.getMetaFromState(block)));
            }
            if (world.getTileEntity(getPos()) instanceof TileMarkedInjector te) {
                te.setStackInPedestal(getStackInPedestal());
                return true;
            }
        }
        return false;
    }

}
