package farmable_sculk_sensors;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SculkPatchFeature extends Feature<DefaultFeatureConfig> {
    public SculkPatchFeature(Codec<DefaultFeatureConfig> config) {
        super(config);
    }

    public boolean generate(StructureWorldAccess world, Random random, BlockPos pos) {
        for (int i = world.getBottomY(); i < world.getTopY(); i++) {
            var currentPos = pos.withY(i);
            if (world.getBlockState(currentPos).isAir() && world.getBlockState(currentPos.down()).isOpaque()) {
                if (!world.isSkyVisibleAllowingSea(currentPos)) {
                    generateSculkSensors(world, random, currentPos);
                }

                break;
            }
        }

        return false;
    }

    private void generateSculkSensors(StructureWorldAccess world, Random random, BlockPos pos) {
        world.setBlockState(pos, Blocks.SCULK_SENSOR.getDefaultState(), 3);

        if (random.nextInt(3) < 2) return;

        Direction direction = Direction.byId(random.nextInt(4) + 2);

        var nextPos = pos.offset(direction);

        if (world.getBlockState(nextPos).isOpaque()) {
            if (world.getBlockState(nextPos.up()).isAir()) {
                generateSculkSensors(world, random, nextPos.up());
            }

            return;
        }

        if (world.getBlockState(nextPos.down()).isAir()) {
            if (world.getBlockState(nextPos.down(2)).isOpaque()) {
                generateSculkSensors(world, random, nextPos.down());
            }

            return;
        }

        if (world.getBlockState(nextPos.down()).isOpaque() && world.getBlockState(nextPos).isAir()) {
            generateSculkSensors(world, random, nextPos);
        }
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        return generate(context.getWorld(), context.getRandom(), context.getOrigin());
    }
}
