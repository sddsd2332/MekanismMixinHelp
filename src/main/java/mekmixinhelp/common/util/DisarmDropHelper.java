package mekmixinhelp.common.util;

import mekanism.api.gear.Magnetic;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.IItemHandler;

public final class DisarmDropHelper {

    private static final int DISARM_PICKUP_DELAY = 90;

    private DisarmDropHelper() {
    }

    /**
     * 判断物品是否应该在缴械时保留。
     *
     * @param stack 待检查的物品堆。
     * @return 如果物品为空，或物品实现了 Mekanism 的磁吸接口且当前物品堆启用了磁吸，则返回 true。
     */
    public static boolean shouldKeep(ItemStack stack) {
        return stack.isEmpty() || stack.getItem() instanceof Magnetic magnetic && magnetic.isMagnetic(stack);
    }

    /**
     * 判断物品是否允许被缴械掉落。
     *
     * @param player 被缴械的玩家。
     * @param stack  待掉落的物品堆。
     * @return 如果物品不需要保留，并且物品自身允许通过 onDroppedByPlayer 掉落，则返回 true。
     */
    public static boolean canDisarmDrop(EntityPlayer player, ItemStack stack) {
        return !shouldKeep(stack) && stack.getItem().onDroppedByPlayer(stack, player);
    }

    /**
     * 从玩家主背包指定槽位执行缴械掉落。
     *
     * @param player  被缴械的玩家。
     * @param slot    玩家背包槽位索引。
     * @param dropAll true 表示掉落整个物品堆，false 表示只掉落 1 个物品。
     * @return 成功生成的掉落实体；如果物品被保护、物品拒绝掉落、槽位非法或掉落事件被取消，则返回 null。
     */
    public static EntityItem dropInventorySlot(EntityPlayer player, int slot, boolean dropAll) {
        if (slot < 0 || slot >= player.inventory.getSizeInventory()) {
            return null;
        }

        ItemStack stack = player.inventory.getStackInSlot(slot);
        if (!canDisarmDrop(player, stack)) {
            return null;
        }

        int count = dropAll ? stack.getCount() : 1;
        ItemStack removed = player.inventory.decrStackSize(slot, count);
        EntityItem item = toss(player, removed);
        if (item == null) {
            restoreInventorySlot(player.inventory, slot, removed);
        }
        return item;
    }

    /**
     * 从任意 IInventory 指定槽位执行缴械掉落。
     *
     * @param player    被缴械的玩家，用于触发物品掉落回调和 Forge 掉落事件。
     * @param inventory 要取出物品的库存。
     * @param slot      库存槽位索引。
     * @param dropAll   true 表示掉落整个物品堆，false 表示只掉落 1 个物品。
     * @return 成功生成的掉落实体；如果物品被保护、物品拒绝掉落、槽位非法或掉落事件被取消，则返回 null。
     */
    public static EntityItem dropInventorySlot(EntityPlayer player, IInventory inventory, int slot, boolean dropAll) {
        if (slot < 0 || slot >= inventory.getSizeInventory()) {
            return null;
        }

        ItemStack stack = inventory.getStackInSlot(slot);
        if (!canDisarmDrop(player, stack)) {
            return null;
        }

        int count = dropAll ? stack.getCount() : 1;
        ItemStack removed = inventory.decrStackSize(slot, count);
        EntityItem item = toss(player, removed);
        if (item == null) {
            restoreInventorySlot(inventory, slot, removed);
        }
        return item;
    }

    /**
     * 从任意 IItemHandler 指定槽位执行缴械掉落。
     *
     * @param player  被缴械的玩家，用于触发物品掉落回调和 Forge 掉落事件。
     * @param handler 要取出物品的物品处理器，例如饰品栏或其他能力库存。
     * @param slot    物品处理器槽位索引。
     * @return 成功生成的掉落实体；如果物品被保护、物品拒绝掉落、槽位非法或掉落事件被取消，则返回 null。
     */
    public static EntityItem dropItemHandlerSlot(EntityPlayer player, IItemHandler handler, int slot) {
        if (slot < 0 || slot >= handler.getSlots()) {
            return null;
        }

        ItemStack stack = handler.getStackInSlot(slot);
        if (!canDisarmDrop(player, stack)) {
            return null;
        }

        ItemStack removed = handler.extractItem(slot, stack.getCount(), false);
        EntityItem item = toss(player, removed);
        if (item == null) {
            restoreItemHandlerSlot(handler, slot, removed);
        }
        return item;
    }

    /**
     * 直接将一个物品堆作为缴械物品抛出。
     *
     * @param player 被缴械的玩家，用于触发物品掉落回调和 Forge 掉落事件。
     * @param stack  要抛出的物品堆。调用方需要自行负责从原容器移除或回滚。
     * @return 成功生成的掉落实体；如果物品被保护、物品拒绝掉落或掉落事件被取消，则返回 null。
     */
    public static EntityItem tossStack(EntityPlayer player, ItemStack stack) {
        if (!canDisarmDrop(player, stack)) {
            return null;
        }
        return toss(player, stack);
    }

    /**
     * 触发 Forge 玩家丢弃物品事件并设置缴械掉落的拾取延迟。
     *
     * @param player 被缴械的玩家。
     * @param stack  已经从容器中取出的物品堆。
     * @return 成功生成的掉落实体；如果物品为空或事件被取消，则返回 null。
     */
    private static EntityItem toss(EntityPlayer player, ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        EntityItem item = ForgeHooks.onPlayerTossEvent(player, stack, true);
        if (item != null) {
            item.setPickupDelay(DISARM_PICKUP_DELAY);
        }
        return item;
    }

    /**
     * 在 IInventory 来源的掉落失败时尝试将物品放回原槽位。
     *
     * @param inventory 原库存。
     * @param slot      原槽位索引。
     * @param stack     需要回滚的物品堆。
     */
    private static void restoreInventorySlot(IInventory inventory, int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        ItemStack current = inventory.getStackInSlot(slot);
        if (current.isEmpty()) {
            inventory.setInventorySlotContents(slot, stack);
            return;
        }

        if (canMerge(current, stack)) {
            int moved = Math.min(stack.getCount(), current.getMaxStackSize() - current.getCount());
            if (moved > 0) {
                current.grow(moved);
                stack.shrink(moved);
            }
        }

        if (!stack.isEmpty() && inventory instanceof net.minecraft.entity.player.InventoryPlayer) {
            ((net.minecraft.entity.player.InventoryPlayer) inventory).addItemStackToInventory(stack);
        }
    }

    /**
     * 在 IItemHandler 来源的掉落失败时尝试将物品放回原槽位。
     *
     * @param handler 原物品处理器。
     * @param slot    原槽位索引。
     * @param stack   需要回滚的物品堆。
     */
    private static void restoreItemHandlerSlot(IItemHandler handler, int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        ItemStack remainder = stack;
        if (handler instanceof IItemHandlerModifiable) {
            IItemHandlerModifiable modifiable = (IItemHandlerModifiable) handler;
            ItemStack current = modifiable.getStackInSlot(slot);
            if (current.isEmpty()) {
                modifiable.setStackInSlot(slot, remainder);
                return;
            }

            if (canMerge(current, remainder)) {
                int moved = Math.min(remainder.getCount(), current.getMaxStackSize() - current.getCount());
                if (moved > 0) {
                    current.grow(moved);
                    remainder.shrink(moved);
                    modifiable.setStackInSlot(slot, current);
                }
            }
        }

        if (!remainder.isEmpty()) {
            handler.insertItem(slot, remainder, false);
        }
    }

    /**
     * 判断两个物品堆是否可以合并。
     *
     * @param first  目标物品堆。
     * @param second 待合并的物品堆。
     * @return 如果物品、NBT 相同且目标堆未满，则返回 true。
     */
    private static boolean canMerge(ItemStack first, ItemStack second) {
        return ItemStack.areItemsEqual(first, second)
                && ItemStack.areItemStackTagsEqual(first, second)
                && first.getCount() < first.getMaxStackSize();
    }
}
