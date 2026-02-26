package mekmixinhelp.mixin.ic2;

import ic2.core.item.armor.ItemArmorElectric;
import ic2.core.item.armor.ItemArmorQuantumSuit;
import ic2.core.ref.ItemName;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.radiation.item.RadiationShieldingHandler;
import mekanism.common.item.armor.ItemHazmatSuitArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(value = ItemArmorQuantumSuit.class, remap = false)
public abstract class MixinItemArmorQuantumSuit extends ItemArmorElectric {


    public MixinItemArmorQuantumSuit(ItemName name, String armorName, EntityEquipmentSlot armorType, double maxCharge, double transferLimit, int tier) {
        super(name, armorName, armorType, maxCharge, transferLimit, tier);
    }


    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return new ItemCapabilityWrapper(stack, RadiationShieldingHandler.create(item -> ItemHazmatSuitArmor.getShieldingByArmor(armorType)));
    }

}
