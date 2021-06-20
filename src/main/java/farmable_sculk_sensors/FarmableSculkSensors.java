package farmable_sculk_sensors;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class FarmableSculkSensors implements ModInitializer {
	private static final Feature<DefaultFeatureConfig> SCULK_PATCH = new SculkPatchFeature(DefaultFeatureConfig.CODEC);
	private static final ConfiguredFeature<?, ?> SCULK_PATCH_CONFIGURED = SCULK_PATCH.configure(FeatureConfig.DEFAULT).applyChance(1);

	@Override
	public void onInitialize() {
		Registry.register(Registry.FEATURE, new Identifier("farmable_sculk_sensors", "sculk_patch"), SCULK_PATCH);

		RegistryKey<ConfiguredFeature<?, ?>> sculkPatch = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier("farmable_sculk_sensors", "sculk_patch"));
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, sculkPatch.getValue(), SCULK_PATCH_CONFIGURED);
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_DECORATION, sculkPatch);
	}
}
