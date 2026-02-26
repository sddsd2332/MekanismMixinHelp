package mekmixinhelp.mixin.mets;

import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.radiation.item.RadiationShieldingHandler;
import mekanism.common.item.armor.ItemHazmatSuitArmor;
import net.lrsoft.mets.armor.HeavyQuantumSuit;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HeavyQuantumSuit.class)
public abstract class MixinHeavyQuantumSuit extends ItemArmor {

    public MixinHeavyQuantumSuit(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return new ItemCapabilityWrapper(stack, RadiationShieldingHandler.create(item -> ItemHazmatSuitArmor.getShieldingByArmor(armorType)));
    }
}
