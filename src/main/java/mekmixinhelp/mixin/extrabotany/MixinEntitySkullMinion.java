package mekmixinhelp.mixin.extrabotany;

import com.meteor.extrabotany.api.entity.IEntityWithShield;
import com.meteor.extrabotany.common.entity.gaia.EntitySkullMinion;
import mekmixinhelp.common.util.DisarmDropHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntitySkullMinion.class)
public abstract class MixinEntitySkullMinion extends EntityLiving implements IEntityWithShield {

    public MixinEntitySkullMinion(World worldIn) {
        super(worldIn);
    }


    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;dropItem(Z)Lnet/minecraft/entity/item/EntityItem;"))
    public EntityItem dropItem(EntityPlayer player, boolean dropAll) {
        return DisarmDropHelper.dropInventorySlot(player, player.inventory.currentItem, dropAll);
    }

}
