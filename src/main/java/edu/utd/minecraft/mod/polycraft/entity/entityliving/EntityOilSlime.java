package edu.utd.minecraft.mod.polycraft.entity.entityliving;

import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.material.PolycraftMaterial;
import edu.utd.minecraft.mod.polycraft.config.Armor;
import edu.utd.minecraft.mod.polycraft.config.CompoundVessel;
import edu.utd.minecraft.mod.polycraft.config.Fuel;
import edu.utd.minecraft.mod.polycraft.config.PolycraftEntity;
import edu.utd.minecraft.mod.polycraft.render.PolyParticleSpawner;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class EntityOilSlime extends EntityLiving implements IMob
{
	private static PolycraftEntity config;
	public final static BiomeGenBase[] biomes = new BiomeGenBase[] {BiomeGenBase.desert,BiomeGenBase.plains,BiomeGenBase.beach,BiomeGenBase.birchForest,BiomeGenBase.birchForestHills,BiomeGenBase.coldBeach,BiomeGenBase.coldTaiga,BiomeGenBase.coldTaigaHills,BiomeGenBase.deepOcean,BiomeGenBase.desertHills,BiomeGenBase.extremeHills,BiomeGenBase.extremeHillsEdge,BiomeGenBase.extremeHillsPlus,BiomeGenBase.forest,BiomeGenBase.forestHills,BiomeGenBase.frozenOcean,BiomeGenBase.frozenRiver,BiomeGenBase.iceMountains,BiomeGenBase.icePlains,BiomeGenBase.jungle,BiomeGenBase.jungleEdge,BiomeGenBase.jungleHills,BiomeGenBase.megaTaiga,BiomeGenBase.megaTaigaHills,BiomeGenBase.mesa,BiomeGenBase.mesaPlateau,BiomeGenBase.mesaPlateau_F,BiomeGenBase.mushroomIsland,BiomeGenBase.mushroomIslandShore,BiomeGenBase.ocean,BiomeGenBase.river,BiomeGenBase.roofedForest,BiomeGenBase.stoneBeach,BiomeGenBase.swampland,BiomeGenBase.taiga,BiomeGenBase.taigaHills};
	private final static String OIL_SLIME_BALL = "1hl";
	
	public Block oil = PolycraftMod.blockOil;
    public float squishAmount;
    public float squishFactor;
    public float prevSquishFactor;
    /** ticks until this slime jumps again */
    private int slimeJumpDelay;
    private static final String __OBFID = "CL_00001698";
    private int healDelay;

    public EntityOilSlime(World p_i1742_1_)
    {
        super(p_i1742_1_);
        int i = 1 << this.rand.nextInt(3);
        this.yOffset = 0.0F;
        this.slimeJumpDelay = this.rand.nextInt(20) + 10;
        this.setSlimeSize(i);
        this.getNavigator().setAvoidsWater(true);
        this.healDelay=0;
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte)1));
    }
    
    

    protected void setSlimeSize(int p_70799_1_)
    {
        this.dataWatcher.updateObject(16, new Byte((byte)p_70799_1_));
        this.setSize(0.6F * (float)p_70799_1_, 0.6F * (float)p_70799_1_);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue((double)(3*p_70799_1_ * p_70799_1_));
        this.setHealth(this.getMaxHealth());
        this.fireResistance=p_70799_1_*10;
        this.experienceValue = p_70799_1_;
        this.healDelay=0;
    }
    

    /**
     * Returns the size of the slime.
     */
    public int getSlimeSize()
    {
        return this.dataWatcher.getWatchableObjectByte(16);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound p_70014_1_)
    {
        super.writeEntityToNBT(p_70014_1_);
        p_70014_1_.setInteger("Size", this.getSlimeSize() - 1);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound p_70037_1_)
    {
        super.readEntityFromNBT(p_70037_1_);
        int i = p_70037_1_.getInteger("Size");

        if (i < 0)
        {
            i = 0;
        }

        this.setSlimeSize(i + 1);
    }

    /**
     * Returns the name of a particle effect that may be randomly created by EntitySlime.onUpdate()
     */
    protected String getSlimeParticle()
    {
        return "slime";
    }

    /**
     * Returns the name of the sound played when the slime jumps.
     */
    protected String getJumpSound()
    {
        return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void heal()
    {
    	if(this.healDelay<=0)
    	{
    		this.heal(3);
    		this.healDelay=30;
    		if(this.worldObj.isRemote)
            {
        		int i = this.getSlimeSize();
        		for (int j = 0; j < i * 8; ++j)
	            {
	                float f = this.rand.nextFloat() * (float)Math.PI * 2.0F;
	                float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
	                float f2 = MathHelper.sin(f) * (float)i * 0.5F * f1;
	                float f3 = MathHelper.cos(f) * (float)i * 0.5F * f1;
	                PolyParticleSpawner.EntityHeal(this.posX + (double)f2, this.boundingBox.minY+.6, this.posZ + (double)f3, 0.0D, 0.0D, 0.0D);
	            }
            }
    	}
    }
    public void onUpdate()
    {
    	if(this.healDelay>0)
    	{
    		this.healDelay--;
    	}
        if (!this.worldObj.isRemote && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL && this.getSlimeSize() > 0)
        {
            this.isDead = true;
        }
        if(worldObj.getBlock((int)this.posX, (int)this.posY, (int)this.posZ)==oil)
        {
        	this.heal();
        	
        }

        this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
        this.prevSquishFactor = this.squishFactor;
        boolean flag = this.onGround;
        super.onUpdate();
        int i;

        if (this.onGround && !flag)
        {
            i = this.getSlimeSize();
            if(this.worldObj.isRemote)
            {
	            for (int j = 0; j < i * 8; ++j)
	            {
	                float f = this.rand.nextFloat() * (float)Math.PI * 2.0F;
	                float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
	                float f2 = MathHelper.sin(f) * (float)i * 0.5F * f1;
	                float f3 = MathHelper.cos(f) * (float)i * 0.5F * f1;
	                //this.worldObj.spawnParticle(this.getSlimeParticle(), this.posX + (double)f2, this.boundingBox.minY, this.posZ + (double)f3, 0.0D, 0.0D, 0.0D);
	                //Minecraft.getMinecraft().effectRenderer.addEffect(new EntityBreakingFX(this.worldObj, this.posX + (double)f2, this.boundingBox.minY, this.posZ + (double)f3, Items.slime_ball));
	                PolyParticleSpawner.EntityBreakingParticle(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(OIL_SLIME_BALL)), this.posX + (double)f2, this.boundingBox.minY, this.posZ + (double)f3, 0.0D, 0.0D, 0.0D);
	            }
            }

            if (this.makesSoundOnLand())
            {
                this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            }

            this.squishAmount = -0.5F;
        }
        else if (!this.onGround && flag)
        {
            this.squishAmount = 1.0F;
        }

        this.alterSquishAmount();

        if (this.worldObj.isRemote)
        {
            i = this.getSlimeSize();
            this.setSize(0.6F * (float)i, 0.6F * (float)i);
        }
    }

    protected void updateEntityActionState()
    {
        this.despawnEntity();
        EntityPlayer entityplayer = this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
        

        if (entityplayer != null)
        {
            this.faceEntity(entityplayer, 10.0F, 20.0F);
        }
        if(this.isInWater())
        {
        	this.motionY=0;
        	this.moveEntity(0, 4, 0);
        	//this.moveStrafing = 1.0F - this.rand.nextFloat() * 2.0F;
        	//this.moveForward = (float)(1 * this.getSlimeSize());
        }
        else if(worldObj.getBlock((int)this.posX, (int) (this.posY-.2D), (int)this.posZ).getMaterial()==Material.water)
        {
        	//this.motionY=0;
        	this.moveStrafing = 1.0F - this.rand.nextFloat() * 2.0F;
        	this.moveForward = (float)(1 * this.getSlimeSize());
        }

        if (this.onGround && this.slimeJumpDelay-- <= 0)
        {
            this.slimeJumpDelay = this.getJumpDelay();

            if (entityplayer != null)
            {
                this.slimeJumpDelay /= 3;
            }

            this.isJumping = true;

            if (this.makesSoundOnJump())
            {
                this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
            }

            this.moveStrafing = 1.0F - this.rand.nextFloat() * 2.0F;
            this.moveForward = (float)(1 * this.getSlimeSize());
        }
        else
        {
            this.isJumping = false;

            if (this.onGround)
            {
                this.moveStrafing = this.moveForward = 0.0F;
            }
        }
    }

    protected void alterSquishAmount()
    {
        this.squishAmount *= 0.6F;
    }

    /**
     * Gets the amount of time the slime needs to wait between jumps.
     */
    protected int getJumpDelay()
    {
        return this.rand.nextInt(20) + 10;
    }

    protected EntityOilSlime createInstance()
    {
        return new EntityOilSlime(this.worldObj);
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead()
    {
        int i = this.getSlimeSize();

        if (!this.worldObj.isRemote && i > 1 && this.getHealth() <= 0.0F)
        {
            int j = 2 + this.rand.nextInt(3);

            for (int k = 0; k < j; ++k)
            {
                float f = ((float)(k % 2) - 0.5F) * (float)i / 4.0F;
                float f1 = ((float)(k / 2) - 0.5F) * (float)i / 4.0F;
                EntityOilSlime entityslime = this.createInstance();
                entityslime.setSlimeSize(i / 2);
                entityslime.setLocationAndAngles(this.posX + (double)f, this.posY + 0.5D, this.posZ + (double)f1, this.rand.nextFloat() * 360.0F, 0.0F);
                this.worldObj.spawnEntityInWorld(entityslime);
                if(this.isBurning())
                {
                	entityslime.setFire(100);
                }
            }
        }

        super.setDead();
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer p_70100_1_)
    {
        if (this.canDamagePlayer())
        {
            int i = this.getSlimeSize();

            if (this.canEntityBeSeen(p_70100_1_) && this.getDistanceSqToEntity(p_70100_1_) < 0.6D * (double)i * 0.6D * (double)i && p_70100_1_.attackEntityFrom(DamageSource.causeMobDamage(this), (float)this.getAttackStrength()))
            {
                this.playSound("mob.attack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }
        }
    }

    /**
     * Indicates weather the slime is able to damage the player (based upon the slime's size)
     */
    protected boolean canDamagePlayer()
    {
        return this.getSlimeSize() > 1;
    }

    /**
     * Gets the amount of damage dealt to the player when "attacked" by the slime.
     */
    protected int getAttackStrength()
    {
    	if(!this.isBurning())
    	{
    		return this.getSlimeSize()*2;
    	}
    	else
    	{
    		return this.getSlimeSize()*4;
    	}
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
    }

    protected Item getDropItem()
    {
        return this.getSlimeSize() == 1 ?  GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(OIL_SLIME_BALL)) : Item.getItemById(0);
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        Chunk chunk = this.worldObj.getChunkFromBlockCoords(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));

        /*if (this.worldObj.getWorldInfo().getTerrainType().handleSlimeSpawnReduction(rand, worldObj))
        {
            return false;
        }
        else
        {*/
            if (this.getSlimeSize() == 1 || this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL)
            {
                BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
                
                if(worldObj.getBlock((int)this.posX, (int)this.posY, (int)this.posZ)==oil)//.getMaterial()== PolycraftMaterial.oil
                {
                	return this.worldObj.checkNoEntityCollision(this.boundingBox);
                }
                
                if (biomegenbase == BiomeGenBase.desert && this.posY > 50.0D && this.posY < 70.0D && this.rand.nextFloat() < 0.5F && this.rand.nextFloat() < this.worldObj.getCurrentMoonPhaseFactor())//&& this.worldObj.getBlockLightValue(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) <= this.rand.nextInt(8)
                {
                    return super.getCanSpawnHere();
                }

                if (this.rand.nextInt(10) == 0 && chunk.getRandomWithSeed(987234911L).nextInt(10) == 0 && this.posY < 40.0D) 
                {
                    return super.getCanSpawnHere();
                }
            }

            return false;
        //}
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 0.4F * (float)this.getSlimeSize();
    }

    /**
     * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
     * use in wolves.
     */
    public int getVerticalFaceSpeed()
    {
        return 0;
    }

    /**
     * Returns true if the slime makes a sound when it jumps (based upon the slime's size)
     */
    protected boolean makesSoundOnJump()
    {
        return this.getSlimeSize() > 0;
    }

    /**
     * Returns true if the slime makes a sound when it lands after a jump (based upon the slime's size)
     */
    protected boolean makesSoundOnLand()
    {
        return this.getSlimeSize() > 2;
    }
    
    public static final void register(final PolycraftEntity polycraftEntity) {
		EntityOilSlime.config = polycraftEntity;
		PolycraftEntityLiving.register(EntityOilSlime.class, config.entityID, config.name, 0x777777, 0x888888);
		EntityRegistry.addSpawn(EntityOilSlime.class, 20, 3, 2, EnumCreatureType.monster, biomes);
	}
    
}