package edu.utd.minecraft.mod.polycraft.entity.boss;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.config.PolycraftEntity;
import edu.utd.minecraft.mod.polycraft.entity.effect.EntityLightningAttack;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.PolycraftEntityLiving;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class TestTerritoryFlagBoss extends EntityMob implements IBossDisplayData, IRangedAttackMob {

	public static PolycraftEntity config;
	public static final int DETECTION_RANGE = 64;

	private static final IEntitySelector attackEntitySelector = new IEntitySelector() {
		public boolean isEntityApplicable(Entity entity) {
			return entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode;
		}
	};

	private static class Attack {
		public double x, y, z, r, h;
		public float d;
		public int warnTicks;
		public Entity target;

		public Attack(double x, double y, double z, double r, double h, float d, int warnTicks) {
			super();
			this.x = x;
			this.y = y;
			this.z = z;
			this.r = r;
			this.h = h;
			this.d = d;
			this.warnTicks = warnTicks;
		}

		public Attack setTarget(Entity entity) {
			this.target = entity;
			return this;
		}
	}

	private LinkedList<Attack> lightningAttacks;
	private LinkedList<Attack> siphonAttacks;
	private LinkedList<Attack> summonAttacks;
	private EntityAIArrowAttack attacker;

	public TestTerritoryFlagBoss(World world) {
		super(world);
		this.setSize(1.0F, 8.0F);
		this.isImmuneToFire = true;
		this.experienceValue = 100;
		this.lightningAttacks = new LinkedList<Attack>();
		this.siphonAttacks = new LinkedList<Attack>();
		this.summonAttacks = new LinkedList<Attack>();
		attacker = new EntityAIArrowAttack(this, 1.0D, 1, 1, DETECTION_RANGE);
		this.tasks.addTask(4, attacker);
		// this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class,
		// 8.0F));
		// Note: boolean val determines if to call for help. Should be false.
		this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
		// Note: boolean val determines if LoS is required.
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(DETECTION_RANGE);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(1000D);
		// maxHurtResistantTime = 10;
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}

	// Initial State
	private boolean active = false;

	// Arrow Attacks
	private static final int ARROW_COOLDOWN = 20;
	private static final int ARROWS = 8;
	private int arrowCooldown = ARROW_COOLDOWN;
	private int arrows = ARROWS;

	// Lightning Attacks
	private static final int LIGHTNING_ATTACK_RADIUS = 3;
	private static final int LIGHTNING_MAX_COOLDOWN = 200;
	private static final int LIGHTNING_MIN_COOLDOWN = 40;
	private static final int LIGHTNING_RECHARGE = 60;

	private int lightningBolts = 0;
	private int lightningCooldown = LIGHTNING_MIN_COOLDOWN;
	private boolean lightningDischarge = false;
	private int lightningRecharge = LIGHTNING_RECHARGE;

	// Siphon Attacks
	private static final int SIPHON_ATTACK_RADIUS = 1;

	// Spawner Attacks
	private static final int SUMMONING_RADIUS = 4;
	private static final int SUMMON_MAX_COOLDOWN = 20;// 2400;
	private static final int SUMMON_MIN_COOLDOWN = 20;// 600;
	private int summonCooldown = SUMMON_MIN_COOLDOWN;

	public float scaleToHealth(int min, int max) {
		float fraction = getHealth() / getMaxHealth();
		if (fraction > 0.9)
			fraction = 1;
		else if (fraction > 0.1)
			fraction = (fraction - 0.1F) / 0.8F;
		return min + fraction * (max - min);
	}

	public void castLightning(Entity victim, int h) {
		AttackWarning.sendPackets(
				new AttackWarning(victim.posX, victim.posZ, LIGHTNING_ATTACK_RADIUS, h, LIGHTNING_ATTACK_RADIUS * 2, 60)
						.setColor(Color.YELLOW));
		lightningAttacks.add(
				new Attack(victim.posX, h, victim.posZ, LIGHTNING_ATTACK_RADIUS, LIGHTNING_ATTACK_RADIUS * 2, 20, 60));
		lightningBolts--;
		lightningCooldown = (int) scaleToHealth(LIGHTNING_MIN_COOLDOWN, LIGHTNING_MAX_COOLDOWN);
	}

	@Override
	public void onLivingUpdate() {
		if (active)
			updateAITasks();
		World world = this.worldObj;
		if (active && world instanceof WorldServer) {
			if (world.isDaytime())
				world.setWorldTime(world.getWorldTime() + 1000);
			WorldServer worldServer = (WorldServer) world;
			List victims = world.selectEntitiesWithinAABB(EntityPlayer.class,
					this.boundingBox.expand(DETECTION_RANGE, DETECTION_RANGE, DETECTION_RANGE), attackEntitySelector);
			int numPlayers = victims.size();
			// Reset accidental target.
			if (this.getAITarget() == this)
				this.setTarget(null);
			// Siphoning health from other mobs.
			else if ((this.getAITarget() instanceof EntityMob)) {
				this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0D);
				this.getAITarget().hurtResistantTime = 0;
				if (this.ticksExisted % 20 == 0) {
					EntityMob victim = (EntityMob) this.getAITarget();
					float percent = this.getAITarget().getMaxHealth() / 10F;
					int y = worldServer.getPrecipitationHeight((int) Math.round(victim.posX),
							(int) Math.round(victim.posZ));
					double height = victim.height + Math.abs(victim.posY - y);
					siphonAttacks
							.add(new Attack(victim.posX, y, victim.posZ, SIPHON_ATTACK_RADIUS, height, percent, 20));
					AttackWarning.sendPackets(
							new AttackWarning(victim.posX, victim.posZ, SIPHON_ATTACK_RADIUS, y, height, 20));
				}
			} else { // Attacks on players.
				if (lightningBolts >= victims.size()
						&& lightningCooldown < scaleToHealth(LIGHTNING_MIN_COOLDOWN, LIGHTNING_MAX_COOLDOWN) / 2) {
					for (int i = 0; i < victims.size(); i++) {
						EntityPlayer victim = (EntityPlayer) victims.get(i);
						if (!victim.capabilities.isCreativeMode) {
							int h = worldServer.getPrecipitationHeight((int) Math.round(victim.posX),
									(int) Math.round(victim.posZ));
							castLightning(victim, h);
						}
					}
				}
				if (victims.size() > 0) {
					// Create Summoning Attack
					Entity victim = (Entity) victims.get(this.rand.nextInt(victims.size()));
					if (summonCooldown == 0 && victims.size() > 0) {
						double xm = (this.posX + victim.posX) / 2;
						double zm = (this.posZ + victim.posZ) / 2;
						int h = worldServer.getPrecipitationHeight((int) Math.round(xm), (int) Math.round(zm));
						summonAttacks.add(
								new Attack(xm, h, zm, SUMMONING_RADIUS, SUMMONING_RADIUS, 0, 60).setTarget(victim));
						AttackWarning.sendPackets(new AttackWarning(xm, zm, SUMMONING_RADIUS, h, SUMMONING_RADIUS, 60)
								.setColor(Color.GREEN));
						summonCooldown = (int) scaleToHealth(SUMMON_MIN_COOLDOWN, SUMMON_MAX_COOLDOWN);
					}
					if (this.getAITarget() == null && victims.size() > 0)
						this.setTarget(victim);
				}
			}

			while (!lightningAttacks.isEmpty() && lightningAttacks.getFirst().warnTicks == 0) {
				Attack attack = lightningAttacks.removeFirst();
				worldServer.addWeatherEffect(
						new EntityLightningAttack(this, attack.x, attack.y, attack.z, attack.r, attack.h, attack.d));
			}
			for (Attack attack : lightningAttacks)
				attack.warnTicks--;

			// Execute Siphon Attacks
			while (!siphonAttacks.isEmpty() && siphonAttacks.getFirst().warnTicks == 0) {
				Attack attack = siphonAttacks.removeFirst();
				List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
						AxisAlignedBB.getBoundingBox(attack.x - attack.r, attack.y, attack.z - attack.r,
								attack.x + attack.r, attack.y + attack.h, attack.z + attack.r));
				DamageSource siphon = new EntityDamageSource("siphon", this).setDamageIsAbsolute();
				for (int i = 0; i < list.size(); i++) {
					Entity entity = (Entity) list.get(i);
					if (entity instanceof EntityLivingBase) {
						((EntityLivingBase) entity).attackEntityFrom(siphon, attack.d);
						this.heal(attack.d);
					}
				}
			}
			for (Attack attack : siphonAttacks)
				attack.warnTicks--;

			// Execute Spawning Attacks
			while (!summonAttacks.isEmpty() && summonAttacks.getFirst().warnTicks == 0) {
				Attack attack = summonAttacks.removeFirst();
				int maxSpawn = this.rand.nextInt(Math.max((numPlayers + 1) / 2, 1)) + 1;
				for (int i = 0; i < maxSpawn; i++) {
					EntityMob toSpawn;
					if (this.rand.nextBoolean())
						toSpawn = new EntityZombie(world);
					else
						toSpawn = new EntitySkeleton(world);
					toSpawn.forceSpawn = true;
					toSpawn.setPosition(attack.x + (this.rand.nextDouble() - 0.5) * attack.r * 2, attack.y + 1,
							attack.z + (this.rand.nextDouble() - 0.5) * attack.r * 2);
					world.spawnEntityInWorld(toSpawn);
					try {
						toSpawn.setTarget(attack.target);
					} catch (Exception e) {
						toSpawn.setTarget(this.getAITarget());
					}
				}
			}
			for (Attack attack : summonAttacks)
				attack.warnTicks--;

			// Arrow Cooldown
			if (arrowCooldown > 0)
				arrowCooldown--;
			if (arrowCooldown == 0) {
				arrows++;
				arrowCooldown = ARROW_COOLDOWN;
			}

			// Lightning Cooldown
			if (lightningCooldown > 0)
				lightningCooldown--;
			if (lightningRecharge > 0)
				lightningRecharge--;
			if (lightningRecharge == 0 && lightningBolts < numPlayers) {
				lightningBolts++;
				lightningRecharge = LIGHTNING_RECHARGE;
			}

			// Summon Cooldown
			if (summonCooldown > 0)
				summonCooldown--;

		} else if (world.isRemote) {
			BossStatus.setBossStatus(this, true);
		}

	}

	@Override
	protected boolean isMovementBlocked() {
		return true;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float p_70097_2_) {
		boolean result = super.attackEntityFrom(source, p_70097_2_);
		if (result) {
			if (!active) {
				active = true;
				this.worldObj.playBroadcastSound(1013, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
				// Wither scary noise lol
			} else
				lightningCooldown /= 2;
		}
		return result;
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase victim, float _) {
		World world = victim.worldObj;
		if (world instanceof WorldServer) {

			WorldServer worldServer = (WorldServer) world;

			int h = worldServer.getPrecipitationHeight((int) Math.round(victim.posX), (int) Math.round(victim.posZ));
			if (lightningCooldown == 0 && lightningBolts > 0 && h >= victim.posY
					&& victim.posY <= h + LIGHTNING_ATTACK_RADIUS * 2) {
				castLightning(victim, h);
			} else if (arrows > 0) {

				EntityArrow entityarrow = new EntityArrow(this.worldObj, this, victim, 1.6F, 8F);
				entityarrow.canBePickedUp = 1;
				entityarrow.setDamage(4D + this.rand.nextGaussian() * 0.25D);

				this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
				this.worldObj.spawnEntityInWorld(entityarrow);
				arrows--;
				arrowCooldown = ARROW_COOLDOWN;
			}
		}
	}

	public static final void register(final PolycraftEntity polycraftEntity) {
		TestTerritoryFlagBoss.config = polycraftEntity;
		PolycraftEntityLiving.register(TestTerritoryFlagBoss.class, config.entityID, config.name, 0xCCCCCC, 0x666666);
	}
}
