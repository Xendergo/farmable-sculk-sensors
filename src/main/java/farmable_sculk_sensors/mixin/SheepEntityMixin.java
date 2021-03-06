package farmable_sculk_sensors.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import farmable_sculk_sensors.fakes.SheepEntityInterface;

@Mixin(SheepEntity.class)
public abstract class SheepEntityMixin extends MobEntity implements SheepEntityInterface {
	protected SheepEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
	}

	private int fuse = -1;

	public int getFuse() {
		return fuse;
	}

	@Inject(
		at = @At("HEAD"),
		method = "writeCustomDataToNbt"
	)
	private void writeCustomDataToNbt(NbtCompound tag, CallbackInfo info) {
		tag.putInt("fuse", fuse);
	}

	@Inject(
		at = @At("HEAD"),
		method = "readCustomDataFromNbt"
	)
	private void readCustomDataFromNbt(NbtCompound tag, CallbackInfo info) {
		fuse = tag.getInt("fuse");
	}

	@Inject(
		at = @At("HEAD"),
		method = "mobTick"
	)
	private void mobTick(CallbackInfo info) {
		// If the fuse tag doesn't exist, it'll be zero by default, so the baseline can't be zero
		if (fuse == 1) {
			explode();
		} else if (fuse > 1) {
			fuse--;
		}
	}

	@Inject(
		at = @At("HEAD"),
		method = "interactMob",
		cancellable = true
	)
	private void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
		ItemStack sensors = player.getStackInHand(hand);

		if (sensors.isOf(Items.SCULK_SENSOR)) {
			setAttacker(player);

			for (var goal : goalSelector.getGoals()) {
				if (goal.getGoal() instanceof EscapeDangerGoal) {
					goal.start();
					break;
				}
			}

			feedSheepSculk(sensors);

			info.setReturnValue(ActionResult.SUCCESS);
			info.cancel();
		}
	}

	public void feedSheepSculk(ItemStack sensors) {
		sensors.decrement(1);
		fuse = 101;
	}

	private void explode() {
		this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_SHEEP_DEATH, SoundCategory.NEUTRAL, 3, 1);

		if (!this.world.isClient) {
			Explosion.DestructionType destructionType = this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE;
			this.dead = true;
			this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 3, destructionType);
			this.discard();

			int amt = random.nextInt(3) + 1;
			for (int i = 0; i < amt; i++) {
				ItemEntity item = new ItemEntity(world, getX(), getY(), getZ(), new ItemStack(Items.SCULK_SENSOR));

				double angle1 = random.nextDouble() * Math.PI * 2;
				double angle2 = random.nextDouble() * Math.PI * 2;
				item.setVelocity(Math.cos(angle1) * Math.cos(angle2) * 0.5, Math.sin(angle2) * 0.5, Math.sin(angle1) * Math.cos(angle2) * 0.5);

				this.world.spawnEntity(item);
			}
		}
	}
}
