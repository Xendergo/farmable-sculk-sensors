package farmable_sculk_sensors.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import farmable_sculk_sensors.FeedSheepSculksDispenserBehavior;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
    @Inject(method = "getBehaviorForItem", at = @At("HEAD"), cancellable = true)
    private void getBehaviorForItem(ItemStack items, CallbackInfoReturnable<DispenserBehavior> info) {
        System.out.println("YEEEEEE");
        if (items.isOf(Items.SCULK_SENSOR)) {
            info.setReturnValue(new FeedSheepSculksDispenserBehavior());
            info.cancel();
        }
    }
}
