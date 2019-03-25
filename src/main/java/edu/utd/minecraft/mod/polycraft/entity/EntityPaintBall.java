package edu.utd.minecraft.mod.polycraft.entity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.item.ItemSlingshot;
import edu.utd.minecraft.mod.polycraft.item.ItemSlingshot.SlingshotType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityPaintBall extends EntityPellet{
	private int field_145791_d = -1;
	private int field_145792_e = -1;
	private int field_145789_f = -1;
	private Block field_145790_g;
	private int inData;
	private boolean inGround;
	/** 1 if the player can pick up the arrow */
	public int canBePickedUp;
	/** Seems to be some sort of timer for animating an arrow. */
	public int arrowShake;
	/** The owner of this arrow. */
	public Entity shootingEntity;
	private int ticksInGround;
	private int ticksInAir;
	private double damage = 2.0D;
	/** The amount of knockback an arrow applies when it hits a mob. */
	private int knockbackStrength;
	private static final String __OBFID = "CL_00001715";
	ItemSlingshot.SlingshotType type;
	public boolean isChildPellet = false, createdChildren = false;
	private World world;
	
	private static double SCATTER_SPLIT_DEGREE = 10.0;
	
	public EntityPaintBall(World world) {
		super(world);
		this.world = world;
	}
	
	public EntityPaintBall(World world, EntityPlayer shootingEntity, float float1, ItemSlingshot.SlingshotType type) {
		super(world, shootingEntity, float1);
		this.type = type;
		this.world = world;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		super.onEntityUpdate();

		if (!(this.worldObj.isAirBlock((int)this.posX, (int)this.posY, (int)this.posZ)) || this.ticksExisted >= 64){
			this.setDead();
		}
		
		if(this.ticksExisted >= 8 && type == SlingshotType.SCATTER && !isChildPellet && !createdChildren) {
			EntityPaintBall child1 = new EntityPaintBall(world, (EntityPlayer) this.shootingEntity, 2F, this.type), child2 = new EntityPaintBall(world, (EntityPlayer) this.shootingEntity, 2F, this.type);
			this.createdChildren = true;
			child1.isChildPellet = child2.isChildPellet = true;
			child1.positionXCurrent = child2.positionXCurrent = this.positionXCurrent;
			child1.positionYCurrent = child2.positionYCurrent = this.positionYCurrent;
			child1.positionZCurrent = child2.positionZCurrent = this.positionZCurrent;
			child1.currentBlock = child2.currentBlock = this.currentBlock;
			child1.ticksExisted = child2.ticksExisted = this.ticksExisted;
			
			child1.rotationYaw += SCATTER_SPLIT_DEGREE;
			child2.rotationYaw -= SCATTER_SPLIT_DEGREE;
		}
		
		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D
					/ Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f) * 180.0D
					/ Math.PI);
		}

		Block block = this.worldObj.getBlock(this.positionXCurrent, this.positionYCurrent, this.positionZCurrent);

		if (block.getMaterial() != Material.air) {
			block.setBlockBoundsBasedOnState(this.worldObj, this.positionXCurrent, this.positionYCurrent,
					this.positionZCurrent);
			AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(this.worldObj, this.positionXCurrent,
					this.positionYCurrent, this.positionZCurrent);

			if (axisalignedbb != null
					&& axisalignedbb.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ))) {
				this.inGround = true;
			}
		}

		if (this.arrowShake > 0) {
			--this.arrowShake;
		}

		if (this.inGround) {
			int j = this.worldObj.getBlockMetadata(this.positionXCurrent, this.positionYCurrent, this.positionZCurrent);

			if (block == this.currentBlock && j == this.inData) {
				++this.ticksInGround;

				if (this.ticksInGround == 1200) {
					this.setDead();
				}
			} else {
				this.inGround = false;
				this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
				this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
				this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);
				this.ticksInGround = 0;
				this.ticksInAir = 0;
			}
		} else {
			++this.ticksInAir;
			Vec3 vec31 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
			Vec3 vec3 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY,
					this.posZ + this.motionZ);
			MovingObjectPosition movingobjectposition = this.worldObj.func_147447_a(vec31, vec3, false, true, false);
			vec31 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
			vec3 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY,
					this.posZ + this.motionZ);

			if (movingobjectposition != null) {
				vec3 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord,
						movingobjectposition.hitVec.zCoord);
			}

			Entity entity = null;
			List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
					this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
			double d0 = 0.0D;
			int i;
			float f1;

			for (i = 0; i < list.size(); ++i) {
				Entity entity1 = (Entity) list.get(i);

				if (entity1.canBeCollidedWith() && (entity1 != this.shootingEntity || this.ticksInAir >= 5)) {
					f1 = 0.3F;
					AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand((double) f1, (double) f1, (double) f1);
					MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec31, vec3);

					if (movingobjectposition1 != null) {
						double d1 = vec31.distanceTo(movingobjectposition1.hitVec);

						if (d1 < d0 || d0 == 0.0D) {
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}

			if (entity != null) {
				movingobjectposition = new MovingObjectPosition(entity);
			}

			if (movingobjectposition != null && movingobjectposition.entityHit != null
					&& movingobjectposition.entityHit instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) movingobjectposition.entityHit;

				if (entityplayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer
						&& !((EntityPlayer) this.shootingEntity).canAttackPlayer(entityplayer)) {
					movingobjectposition = null;
				}
			}

			float f2;
			float f4;

			if (movingobjectposition != null) {
				if (movingobjectposition.entityHit != null) {
					f2 = MathHelper.sqrt_double(
							this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
					int k = MathHelper.ceiling_double_int((double) f2 * this.damage);

					if (this.getIsCritical()) {
						k += this.rand.nextInt(k / 2 + 2);
					}

					DamageSource damagesource = null;

					if (this.shootingEntity == null) {
						damagesource = DamageSource.causeThrownDamage(this, this);
					} else {
						damagesource = DamageSource.causeThrownDamage(this, this.shootingEntity);
					}

					if (this.isBurning() && !(movingobjectposition.entityHit instanceof EntityEnderman)) {
						movingobjectposition.entityHit.setFire(5);
					}

					if (movingobjectposition.entityHit.attackEntityFrom(damagesource, (float) k)) {
						if (movingobjectposition.entityHit instanceof EntityLivingBase) {
							EntityLivingBase entitylivingbase = (EntityLivingBase) movingobjectposition.entityHit;

							if (!this.worldObj.isRemote) {
								entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
							}

							if (this.knockbackStrength > 0) {
								f4 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);

								if (f4 > 0.0F) {
									movingobjectposition.entityHit.addVelocity(
											this.motionX * (double) this.knockbackStrength * 0.6000000238418579D
											/ (double) f4,
											0.1D, this.motionZ * (double) this.knockbackStrength * 0.6000000238418579D
											/ (double) f4);
								}
							}

							if (this.shootingEntity != null && this.shootingEntity instanceof EntityLivingBase) {
								EnchantmentHelper.func_151384_a(entitylivingbase, this.shootingEntity);
								EnchantmentHelper.func_151385_b((EntityLivingBase) this.shootingEntity,
										entitylivingbase);
							}

							if (this.shootingEntity != null && movingobjectposition.entityHit != this.shootingEntity
									&& movingobjectposition.entityHit instanceof EntityPlayer
									&& this.shootingEntity instanceof EntityPlayerMP) {
								((EntityPlayerMP) this.shootingEntity).playerNetServerHandler
								.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
							}
						}

						this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

						if (!(movingobjectposition.entityHit instanceof EntityEnderman)) {
							this.setDead();
						}
					} else {
						this.motionX *= -0.10000000149011612D;
						this.motionY *= -0.10000000149011612D;
						this.motionZ *= -0.10000000149011612D;
						this.rotationYaw += 180.0F;
						this.prevRotationYaw += 180.0F;
						this.ticksInAir = 0;
					}
				} else {
					this.positionXCurrent = movingobjectposition.blockX;
					this.positionYCurrent = movingobjectposition.blockY;
					this.positionZCurrent = movingobjectposition.blockZ;
					this.currentBlock = this.worldObj.getBlock(this.positionXCurrent, this.positionYCurrent,
							this.positionZCurrent);
					this.inData = this.worldObj.getBlockMetadata(this.positionXCurrent, this.positionYCurrent,
							this.positionZCurrent);
					this.motionX = (double) ((float) (movingobjectposition.hitVec.xCoord - this.posX));
					this.motionY = (double) ((float) (movingobjectposition.hitVec.yCoord - this.posY));
					this.motionZ = (double) ((float) (movingobjectposition.hitVec.zCoord - this.posZ));
					f2 = MathHelper.sqrt_double(
							this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
					this.posX -= this.motionX / (double) f2 * 0.05000000074505806D;
					this.posY -= this.motionY / (double) f2 * 0.05000000074505806D;
					this.posZ -= this.motionZ / (double) f2 * 0.05000000074505806D;
					this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
					this.inGround = true;
					this.arrowShake = 7;
					this.setIsCritical(false);

					if (this.currentBlock.getMaterial() != Material.air) {
						this.currentBlock.onEntityCollidedWithBlock(this.worldObj, this.positionXCurrent,
								this.positionYCurrent, this.positionZCurrent, this);
					}
				}
			}

			if (this.getIsCritical()) {
				for (i = 0; i < 4; ++i) {
					this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double) i / 4.0D,
							this.posY + this.motionY * (double) i / 4.0D, this.posZ + this.motionZ * (double) i / 4.0D,
							-this.motionX, -this.motionY + 0.2D, -this.motionZ);
				}
			}

			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;
			f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

			for (this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f2) * 180.0D
					/ Math.PI); this.rotationPitch
					- this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
				;
			}

			while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
				this.prevRotationPitch += 360.0F;
			}

			while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
				this.prevRotationYaw -= 360.0F;
			}

			while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
				this.prevRotationYaw += 360.0F;
			}

			this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
			this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
			float f3 = 0.99F;
			
			//removed gravity on pellet
			//f1 = 0.05F;
			f1 = 0F;

			if (this.isInWater()) {
				for (int l = 0; l < 4; ++l) {
					f4 = 0.25F;
					this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double) f4,
							this.posY - this.motionY * (double) f4, this.posZ - this.motionZ * (double) f4,
							this.motionX, this.motionY, this.motionZ);
				}

				f3 = 0.8F;
			}

			if (this.isWet()) {
				this.extinguish();
			}

			//Tactical speed adjustment
			if(type == SlingshotType.TACTICAL) {
				f3 *= 2;
			}

			this.motionX *= (double) f3;
			this.motionY *= (double) f3;
			this.motionZ *= (double) f3;
			this.motionY -= (double) f1;
			this.setPosition(this.posX, this.posY, this.posZ);
			this.func_145775_I();
		}
	}
//	@Override
//	public void onUpdate() {
//		super.onUpdate();
//
//		if (!(this.worldObj.isAirBlock((int)this.posX, (int)this.posY, (int)this.posZ)))
//				{
//			this.setDead();
//		}
//		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
//			float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
//			this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D
//					/ Math.PI);
//			this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f) * 180.0D
//					/ Math.PI);
//		}
//
//		Block block = this.worldObj.getBlock(this.field_145791_d, this.field_145792_e, this.field_145789_f);
//
//		if (block.getMaterial() != Material.air) {
//			block.setBlockBoundsBasedOnState(this.worldObj, this.field_145791_d, this.field_145792_e,
//					this.field_145789_f);
//			AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(this.worldObj, this.field_145791_d,
//					this.field_145792_e, this.field_145789_f);
//
//			if (axisalignedbb != null
//					&& axisalignedbb.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ))) {
//				this.inGround = true;
//			}
//		}
//
//		if (this.arrowShake > 0) {
//			--this.arrowShake;
//		}
//
//		if (this.inGround) {
//			int j = this.worldObj.getBlockMetadata(this.field_145791_d, this.field_145792_e, this.field_145789_f);
//
//			if (block == this.field_145790_g && j == this.inData) {
//				++this.ticksInGround;
//
//				if (this.ticksInGround == 1200) {
//					this.setDead();
//				}
//			} else {
//				this.inGround = false;
//				this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
//				this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
//				this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);
//				this.ticksInGround = 0;
//				this.ticksInAir = 0;
//			}
//		} else {
//			++this.ticksInAir;
//			Vec3 vec31 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
//			Vec3 vec3 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY,
//					this.posZ + this.motionZ);
//			MovingObjectPosition movingobjectposition = this.worldObj.func_147447_a(vec31, vec3, false, true, false);
//			vec31 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
//			vec3 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY,
//					this.posZ + this.motionZ);
//
//			if (movingobjectposition != null) {
//				vec3 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord,
//						movingobjectposition.hitVec.zCoord);
//			}
//
//			Entity entity = null;
//			List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
//					this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
//			double d0 = 0.0D;
//			int i;
//			float f1;
//
//			for (i = 0; i < list.size(); ++i) {
//				Entity entity1 = (Entity) list.get(i);
//
//				if (entity1.canBeCollidedWith() && (entity1 != this.shootingEntity || this.ticksInAir >= 5)) {
//					f1 = 0.3F;
//					AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand((double) f1, (double) f1, (double) f1);
//					MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec31, vec3);
//
//					if (movingobjectposition1 != null) {
//						double d1 = vec31.distanceTo(movingobjectposition1.hitVec);
//
//						if (d1 < d0 || d0 == 0.0D) {
//							entity = entity1;
//							d0 = d1;
//						}
//					}
//				}
//			}
//
//			if (entity != null) {
//				movingobjectposition = new MovingObjectPosition(entity);
//			}
//
//			if (movingobjectposition != null && movingobjectposition.entityHit != null
//					&& movingobjectposition.entityHit instanceof EntityPlayer) {
//				EntityPlayer entityplayer = (EntityPlayer) movingobjectposition.entityHit;
//
//				if (entityplayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer
//						&& !((EntityPlayer) this.shootingEntity).canAttackPlayer(entityplayer)) {
//					movingobjectposition = null;
//				}
//			}
//
//			float f2;
//			float f4;
//
//			if (movingobjectposition != null) {
//				if (movingobjectposition.entityHit != null) {
//					f2 = MathHelper.sqrt_double(
//							this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
//					int k = MathHelper.ceiling_double_int((double) f2 * this.damage);
//
//					if (this.getIsCritical()) {
//						k += this.rand.nextInt(k / 2 + 2);
//					}
//
//					DamageSource damagesource = null;
//
//					if (this.shootingEntity == null) {
//						damagesource = DamageSource.causeThrownDamage(this, this);
//					} else {
//						damagesource = DamageSource.causeThrownDamage(this, this.shootingEntity);
//					}
//
//					if (this.isBurning() && !(movingobjectposition.entityHit instanceof EntityEnderman)) {
//						movingobjectposition.entityHit.setFire(5);
//					}
//
//					if (movingobjectposition.entityHit.attackEntityFrom(damagesource, (float) k)) {
//						if (movingobjectposition.entityHit instanceof EntityLivingBase) {
//							EntityLivingBase entitylivingbase = (EntityLivingBase) movingobjectposition.entityHit;
//
//							if (!this.worldObj.isRemote) {
//								entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
//							}
//
//							if (this.knockbackStrength > 0) {
//								f4 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
//
//								if (f4 > 0.0F) {
//									movingobjectposition.entityHit.addVelocity(
//											this.motionX * (double) this.knockbackStrength * 0.6000000238418579D
//													/ (double) f4,
//											0.1D, this.motionZ * (double) this.knockbackStrength * 0.6000000238418579D
//													/ (double) f4);
//								}
//							}
//
//							if (this.shootingEntity != null && this.shootingEntity instanceof EntityLivingBase) {
//								EnchantmentHelper.func_151384_a(entitylivingbase, this.shootingEntity);
//								EnchantmentHelper.func_151385_b((EntityLivingBase) this.shootingEntity,
//										entitylivingbase);
//							}
//
//							if (this.shootingEntity != null && movingobjectposition.entityHit != this.shootingEntity
//									&& movingobjectposition.entityHit instanceof EntityPlayer
//									&& this.shootingEntity instanceof EntityPlayerMP) {
//								((EntityPlayerMP) this.shootingEntity).playerNetServerHandler
//										.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
//							}
//						}
//
//						this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
//
//						if (!(movingobjectposition.entityHit instanceof EntityEnderman)) {
//							this.setDead();
//						}
//					} else {
//						this.motionX *= -0.10000000149011612D;
//						this.motionY *= -0.10000000149011612D;
//						this.motionZ *= -0.10000000149011612D;
//						this.rotationYaw += 180.0F;
//						this.prevRotationYaw += 180.0F;
//						this.ticksInAir = 0;
//					}
//				} else {
//					this.field_145791_d = movingobjectposition.blockX;
//					this.field_145792_e = movingobjectposition.blockY;
//					this.field_145789_f = movingobjectposition.blockZ;
//					this.field_145790_g = this.worldObj.getBlock(this.field_145791_d, this.field_145792_e,
//							this.field_145789_f);
//					this.inData = this.worldObj.getBlockMetadata(this.field_145791_d, this.field_145792_e,
//							this.field_145789_f);
//					this.motionX = (double) ((float) (movingobjectposition.hitVec.xCoord - this.posX));
//					this.motionY = (double) ((float) (movingobjectposition.hitVec.yCoord - this.posY));
//					this.motionZ = (double) ((float) (movingobjectposition.hitVec.zCoord - this.posZ));
//					f2 = MathHelper.sqrt_double(
//							this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
//					this.posX -= this.motionX / (double) f2 * 0.05000000074505806D;
//					this.posY -= this.motionY / (double) f2 * 0.05000000074505806D;
//					this.posZ -= this.motionZ / (double) f2 * 0.05000000074505806D;
//					this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
//					this.inGround = true;
//					this.arrowShake = 7;
//					this.setIsCritical(false);
//
//					if (this.field_145790_g.getMaterial() != Material.air) {
//						this.field_145790_g.onEntityCollidedWithBlock(this.worldObj, this.field_145791_d,
//								this.field_145792_e, this.field_145789_f, this);
//					}
//				}
//			}
//
//			if (this.getIsCritical()) {
//				for (i = 0; i < 4; ++i) {
//					this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double) i / 4.0D,
//							this.posY + this.motionY * (double) i / 4.0D, this.posZ + this.motionZ * (double) i / 4.0D,
//							-this.motionX, -this.motionY + 0.2D, -this.motionZ);
//				}
//			}
//
//			this.posX += this.motionX;
//			this.posY += this.motionY;
//			this.posZ += this.motionZ;
//			f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
//			this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
//
//			for (this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f2) * 180.0D
//					/ Math.PI); this.rotationPitch
//							- this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
//				;
//			}
//
//			while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
//				this.prevRotationPitch += 360.0F;
//			}
//
//			while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
//				this.prevRotationYaw -= 360.0F;
//			}
//
//			while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
//				this.prevRotationYaw += 360.0F;
//			}
//
//			this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
//			this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
//			float f3 = 0.99F;
//			f1 = 0.05F;
//
//			if (this.isInWater()) {
//				for (int l = 0; l < 4; ++l) {
//					f4 = 0.25F;
//					this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double) f4,
//							this.posY - this.motionY * (double) f4, this.posZ - this.motionZ * (double) f4,
//							this.motionX, this.motionY, this.motionZ);
//				}
//
//				f3 = 0.8F;
//			}
//
//			if (this.isWet()) {
//				this.extinguish();
//			}
//
//			this.motionX *= (double) f3;
//			this.motionY *= (double) f3;
//			this.motionZ *= (double) f3;
//			//this.motionY -= (double) f1;
//			this.setPosition(this.posX, this.posY, this.posZ);
//			this.func_145775_I();
//		}
//	}
	
	@Override
	public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
		this.motionX = p_70016_1_;
		this.motionY = p_70016_3_;
		this.motionZ = p_70016_5_;

		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float f = MathHelper.sqrt_double(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
			this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(p_70016_1_, p_70016_5_) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(p_70016_3_, (double) f) * 180.0D
					/ Math.PI);
			this.prevRotationPitch = this.rotationPitch;
			this.prevRotationYaw = this.rotationYaw;
			this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			this.ticksInGround = 0;
		}
	}
//	   public void handleEntityVelocity(S12PacketEntityVelocity p_147244_1_)
//	    {
//	        Entity entity = this.clientWorldController.getEntityByID(p_147244_1_.func_149412_c());
//
//	        if (entity != null)
//	        {
//	            entity.setVelocity((double)p_147244_1_.func_149411_d() / 8000.0D, (double)p_147244_1_.func_149410_e() / 8000.0D, (double)p_147244_1_.func_149409_f() / 8000.0D);
//	        }
//	    }

	
}
