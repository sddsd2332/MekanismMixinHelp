package mekmixinhelp.common.upgrade;

import mekanism.common.upgrade.IUpgradeData;
import net.minecraft.item.ItemStack;

public class DraconicPedestalUpgradeData implements IUpgradeData {

    public final ItemStack pedestalStack;
    public final boolean hasManagedData;
    public final byte facing;
    public final boolean singleItem;

    public DraconicPedestalUpgradeData(ItemStack pedestalStack) {
        this(pedestalStack, false, (byte) 0, false);
    }

    public DraconicPedestalUpgradeData(ItemStack pedestalStack, byte facing, boolean singleItem) {
        this(pedestalStack, true, facing, singleItem);
    }

    private DraconicPedestalUpgradeData(ItemStack pedestalStack, boolean hasManagedData, byte facing, boolean singleItem) {
        this.pedestalStack = pedestalStack.copy();
        this.hasManagedData = hasManagedData;
        this.facing = facing;
        this.singleItem = singleItem;
    }
}
