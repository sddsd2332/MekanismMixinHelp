package mekmixinhelp.common.util;

import net.minecraft.item.ItemStack;

public interface MagneticDropGuardAccess {

    void mekmixinhelp$markSuppressedDrop(ItemStack stack);

    boolean mekmixinhelp$consumeSuppressedDrop(ItemStack stack, int currentTick);

    void mekmixinhelp$clearSuppressedDrops();
}
