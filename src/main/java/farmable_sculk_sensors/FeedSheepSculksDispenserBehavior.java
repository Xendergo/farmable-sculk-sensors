package farmable_sculk_sensors;

import java.util.List;

import farmable_sculk_sensors.fakes.SheepEntityInterface;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

// https://github.com/gnembon/carpet-extra/blob/4c9e2a1bbe7897cf7236161ea23e2299e5044ec0/src/main/java/carpetextra/helpers/CarpetDispenserBehaviours.java#L182
public class FeedSheepSculksDispenserBehavior extends ItemDispenserBehavior {
    @Override
    protected ItemStack dispenseSilently(BlockPointer dispenser, ItemStack sensors) {
        System.out.println("Dispnesing");

        BlockPos pos = dispenser.getBlockPos().offset(dispenser.getBlockState().get(DispenserBlock.FACING));
        List<SheepEntity> sheeps = dispenser.getWorld().getEntitiesByClass(SheepEntity.class, new Box(pos), EntityPredicates.VALID_ENTITY);

        for (SheepEntity sheep : sheeps) {
            if (((SheepEntityInterface) sheep).getFuse() > 0) continue;

            ((SheepEntityInterface) sheep).feedSheepSculk(sensors);

            sensors.decrement(1);

            return sensors;
        }

        return super.dispenseSilently(dispenser, sensors);
    }
}
