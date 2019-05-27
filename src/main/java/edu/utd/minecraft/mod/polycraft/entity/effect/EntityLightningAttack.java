package edu.utd.minecraft.mod.polycraft.entity.effect;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntityDamageSource;

public class EntityLightningAttack extends EntityLightningBolt {

	private int lightningState;
	private int boltLivingTime;
	private float damage;
	private double height;
	private double radius;
	private EntityDamageSource source;

	public EntityLightningAttack(EntityLiving entity, double x, double y, double z, double radius, double height,
			float damage) {
		super(entity.worldObj, x, y, z);
		this.lightningState = 2;
		this.boltLivingTime = this.rand.nextInt(3) + 1;
		this.damage = damage;
		this.height = height;
		this.radius = radius;
		this.source = new EntityDamageSource("lightning", entity);
	}

	public EntityLightningAttack setAttackDamage(float damage) {
		this.damage = damage;
		return this;
	}

	public EntityLightningAttack setAttackSize(double radius, double height) {
		this.height = height;
		this.radius = radius;
		return this;
	}

	@Override
	public void onUpdate() {
		onEntityUpdate();
		if (lightningState == 2) {
			this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "ambient.weather.thunder", 10000.0F,
					0.8F + this.rand.nextFloat() * 0.2F);
			this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.explode", 2.0F,
					0.5F + this.rand.nextFloat() * 0.2F);
			List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
					AxisAlignedBB.fromBounds(this.posX - radius, this.posY, this.posZ - radius, this.posX + radius,
							this.posY + height, this.posZ + radius));
			for (int i = 0; i < list.size(); i++) {
				Entity entity = (Entity) list.get(i);
				if (entity instanceof EntityPlayer) {
					((EntityPlayer) entity).attackEntityFrom(source, damage);
				} else if (!net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(entity, this))
					entity.onStruckByLightning(this);
			}
		}
		--this.lightningState;
		if (this.lightningState < 0) {
			if (this.boltLivingTime == 0) {
				this.setDead();
			} else if (this.lightningState < -this.rand.nextInt(10)) {
				--this.boltLivingTime;
				this.lightningState = 1;
				this.boltVertex = this.rand.nextLong();
			}

		}
	}

	protected void entityInit() {
	}

	protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
	}

	protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
	}
}
