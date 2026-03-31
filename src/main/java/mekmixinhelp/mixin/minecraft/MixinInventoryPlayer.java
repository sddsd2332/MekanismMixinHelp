package mekmixinhelp.mixin.minecraft;

import mekanism.api.gear.Magnetic;
import mekmixinhelp.common.util.MagneticDropGuardAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InventoryPlayer.class, priority = 1)
public abstract class MixinInventoryPlayer {

    @Shadow
    public NonNullList<ItemStack> mainInventory;

    @Shadow
    public EntityPlayer player;

    @Final
    @Shadow
    public NonNullList<ItemStack> armorInventory;

    @Final
    @Shadow
    public NonNullList<ItemStack> offHandInventory;

    @Inject(method = "setInventorySlotContents", at = @At("HEAD"), cancellable = true)
    private void preventMagneticItemBeingCleared(int index, ItemStack stack, CallbackInfo ci) {
        if (!stack.isEmpty()) {
            return;
        }

        // Do not interfere with vanilla death flow (dropAllItems uses direct list.set).
        // Some mods may still call setInventorySlotContents while a player is dead/dying.
        if (this.player == null || !this.player.isEntityAlive() || this.player.getHealth() <= 0.0F) {
            if (this.player instanceof MagneticDropGuardAccess access) {
                access.mekmixinhelp$clearSuppressedDrops();
            }
            return;
        }

        if (index < 0) {
            return;
        }

        ItemStack stackAt = getStackAt(index);
        if (stackAt.isEmpty()) {
            return;
        }

        if (stackAt.getItem() instanceof Magnetic magnetic && magnetic.isMagnetic(stackAt)) {
            if (this.player != null && !this.player.world.isRemote) {
                if (this.player instanceof MagneticDropGuardAccess access) {
                    access.mekmixinhelp$markSuppressedDrop(stackAt);
                }
            }
            ci.cancel();
        }
    }

    @Unique
    private ItemStack getStackAt(int index) {
        if (index < this.mainInventory.size()) {
            return this.mainInventory.get(index);
        }

        int slot = index - this.mainInventory.size();
        if (slot < this.armorInventory.size()) {
            return this.armorInventory.get(slot);
        }

        slot -= this.armorInventory.size();
        if (slot < this.offHandInventory.size()) {
            return this.offHandInventory.get(slot);
        }

        return ItemStack.EMPTY;
    }
}
