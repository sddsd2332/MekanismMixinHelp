package mekmixinhelp.mixin.ic2;

import ic2.core.item.armor.ItemArmorHazmat;
import ic2.core.item.armor.ItemArmorUtility;
import ic2.core.ref.ItemName;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.radiation.item.RadiationShieldingHandler;
import mekanism.common.item.armor.ItemHazmatSuitArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ItemArmorHazmat.class, remap = false)
public abstract class MixinItemArmorHazmat extends ItemArmorUtility {

    public MixinItemArmorHazmat(ItemName name, String armorName, EntityEquipmentSlot type) {
        super(name, armorName, type);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return new ItemCapabilityWrapper(stack, RadiationShieldingHandler.create(item -> ItemHazmatSuitArmor.getShieldingByArmor(armorType)));
    }
}
