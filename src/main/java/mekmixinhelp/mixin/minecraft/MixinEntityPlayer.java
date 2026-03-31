package mekmixinhelp.mixin.minecraft;

import mekmixinhelp.common.util.MagneticDropGuardAccess;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static mekanism.common.CardboardArmorHandler.testForStealth;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase implements MagneticDropGuardAccess {

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @Unique
    private boolean canSet;

    @Unique
    private final List<ItemStack> mekmixinhelp$pendingSuppressedDrops = new ArrayList<>();

    @Unique
    private int mekmixinhelp$pendingSuppressedDropTick = -1;


    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;onLivingUpdate()V"))
    public void setPlayerSize(CallbackInfo ci) {
        EntityPlayer player = (EntityPlayer) (Object) this;
        if (testForStealth(this)) {
            setSize(0.6F, 0.6F);
            player.eyeHeight = 0.6F;
            canSet = true;
        } else if (canSet) {
            setSize(0.6F, 1.8F);
            player.eyeHeight = player.getDefaultEyeHeight();
            canSet = false;
        }
    }

    @Override
    public void mekmixinhelp$markSuppressedDrop(ItemStack stack) {
        if (this.mekmixinhelp$pendingSuppressedDropTick != this.ticksExisted) {
            this.mekmixinhelp$pendingSuppressedDrops.clear();
        }
        this.mekmixinhelp$pendingSuppressedDrops.add(stack.copy());
        this.mekmixinhelp$pendingSuppressedDropTick = this.ticksExisted;
    }

    @Override
    public boolean mekmixinhelp$consumeSuppressedDrop(ItemStack dropStack, int currentTick) {
        if (dropStack.isEmpty()) {
            return false;
        }
        if (this.mekmixinhelp$pendingSuppressedDrops.isEmpty()) {
            return false;
        }
        if (this.mekmixinhelp$pendingSuppressedDropTick != currentTick) {
            clearSuppressedDrop();
            return false;
        }
        for (int i = 0; i < this.mekmixinhelp$pendingSuppressedDrops.size(); i++) {
            if (isSameItem(this.mekmixinhelp$pendingSuppressedDrops.get(i), dropStack)) {
                this.mekmixinhelp$pendingSuppressedDrops.remove(i);
                if (this.mekmixinhelp$pendingSuppressedDrops.isEmpty()) {
                    clearSuppressedDrop();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void mekmixinhelp$clearSuppressedDrops() {
        clearSuppressedDrop();
    }

    @Unique
    private void clearSuppressedDrop() {
        this.mekmixinhelp$pendingSuppressedDrops.clear();
        this.mekmixinhelp$pendingSuppressedDropTick = -1;
    }

    @Unique
    private static boolean isSameItem(ItemStack a, ItemStack b) {
        if (!ItemStack.areItemsEqual(a, b)) {
            return false;
        }
        if (a.getCount() != b.getCount()) {
            return false;
        }
        return areTagsEqual(a.getTagCompound(), b.getTagCompound());
    }

    @Unique
    private static boolean areTagsEqual(@Nullable NBTTagCompound a, @Nullable NBTTagCompound b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }
}
