package mekmixinhelp.mixin.minecraft;

import mekanism.api.gear.IModule;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.MekanismModules;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.item.ItemMekaFishingRod;
import mekanism.common.util.StackUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(EntityFishHook.class)
public abstract class MixinEntityFishHook extends Entity {

    @Shadow
    public int ticksCaughtDelay;

    @Shadow
    public EntityPlayer angler;

    @Shadow
    public int ticksCatchableDelay;

    public MixinEntityFishHook(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "shouldStopFishing", at = @At("HEAD"), cancellable = true)
    public void shouldStopFishingForMekaHook(CallbackInfoReturnable<Boolean> cir) {
        Capabilities.getMekaFishCap(this).ifPresent(iMekaFishHook -> {
            if (!iMekaFishHook.isMekaFishHook() || angler == null) {
                return;
            }
            ItemStack mainHand = angler.getHeldItemMainhand();
            ItemStack offHand = angler.getHeldItemOffhand();
            boolean hasMekaMain = !mainHand.isEmpty() && mainHand.getItem() instanceof ItemMekaFishingRod;
            boolean hasMekaOff = !offHand.isEmpty() && offHand.getItem() instanceof ItemMekaFishingRod;
            if (hasMekaMain || hasMekaOff) {
                cir.setReturnValue(false);
                return;
            }
            setDead();
            cir.setReturnValue(true);
        });
    }

    @Redirect(method = "catchingFish", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/projectile/EntityFishHook;ticksCaughtDelay:I", ordinal = 3, opcode = Opcodes.PUTFIELD))
    public void setMekaismSpeedA(EntityFishHook instance, int value) {
        ticksCaughtDelay = value;
        if (isMekfishRod()) {
            ticksCaughtDelay = Math.max(ticksCaughtDelay, 1);
        }
    }

    @Redirect(method = "catchingFish", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/projectile/EntityFishHook;ticksCatchableDelay:I", ordinal = 2, opcode = Opcodes.PUTFIELD))
    public void setMekaismSpeedB(EntityFishHook instance, int value) {
        ticksCatchableDelay = value;
        if (isMekfishRod()) {
            if (angler != null) {
                ItemStack stack = angler.getHeldItemMainhand();
                if (!stack.isEmpty() && stack.getItem() instanceof IModuleContainerItem item) {
                    IModule<?> speed = item.getModule(stack, MekanismModules.FISHING_SPEED_UNIT);
                    if (speed != null && speed.isEnabled()) {
                        ticksCatchableDelay = Math.max(ticksCatchableDelay * (1 - (speed.getInstalledCount() / speed.getData().getMaxStackSize())), 1);
                    }
                }
            }
        }
    }

    @Unique
    private boolean isMekfishRod() {
        if (angler != null) {
            ItemStack stack = angler.getHeldItemMainhand();
            if (!stack.isEmpty() && stack.getItem() instanceof IModuleContainerItem item) {
                return item.isModuleEnabled(stack, MekanismModules.FISHING_SPEED_UNIT);
            }
        }
        return false;
    }


    @Redirect(method = "handleHookRetraction", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/loot/LootTable;generateLootForPools(Ljava/util/Random;Lnet/minecraft/world/storage/loot/LootContext;)Ljava/util/List;"))
    public List<ItemStack> setMekfishMultiple(LootTable instance, Random random, LootContext rand) {
        List<ItemStack> stacks = instance.generateLootForPools(random, rand);
        List<ItemStack> drops = new ArrayList<>();
        if (angler != null) {
            ItemStack stack = angler.getHeldItemMainhand();
            if (!stack.isEmpty() && stack.getItem() instanceof ItemMekaFishingRod rod) {
                IModule<?> fish = rod.getModule(stack, MekanismModules.FISHING_MULTIPLE_UNIT);
                if (fish != null && fish.isEnabled()) {
                    int count = (fish.getInstalledCount() / fish.getData().getMaxStackSize());
                    for (ItemStack fishStack : stacks) {
                        if (fishStack.isEmpty()) {
                            continue;
                        }
                        //如果这里已经是最大物品堆叠了，则直接添加到掉落物内
                        if (fishStack.getCount() == fishStack.getMaxStackSize()) {
                            drops.add(fishStack);
                            continue;
                        }
                        //否则按照安装数量/最大安装数量的倍增器乘以物品的最大堆叠
                        drops.add(StackUtils.size(fishStack, fishStack.getMaxStackSize() * count));
                    }
                    return drops;
                }
            }
        }
        return stacks;
    }
}
