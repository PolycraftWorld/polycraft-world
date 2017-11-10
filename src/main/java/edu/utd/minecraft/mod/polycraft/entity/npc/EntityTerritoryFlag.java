package edu.utd.minecraft.mod.polycraft.entity.npc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.condenser.CondenserBlock;
import edu.utd.minecraft.mod.polycraft.inventory.territoryflag.TerritoryFlagBlock;
import edu.utd.minecraft.mod.polycraft.inventory.territoryflag.TerritoryFlagInventory;
import edu.utd.minecraft.mod.polycraft.inventory.treetap.TreeTapBlock;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.SuperChunk;

public class EntityTerritoryFlag extends EntityLiving implements IBossDisplayData{
	
	public static final BlockContainer TERRITORY_FLAG = (BlockContainer) GameData.getBlockRegistry().getObject(PolycraftMod.getAssetName("1fq"));
	public int spawnDelay = 20; //TODO EDIT these for balancing
    private int minSpawnDelay = 20;
    private int maxSpawnDelay = 40;
    
    /** A counter for spawn tries. */
    //private int spawnCount = 4;
    private int maxNearbyEntities = 32; //TODO EDIT these for balancing
    private int spawnRange = 20;
    private int activateTime=12600;


    private boolean activated=false;
    private int explosionRadius = 5;
    private int x;
    private int Cx;
    private int Cz;
    private SuperChunk SupChunk;
    private PrivateProperty PP;

   // private int chooseMob;


	public EntityTerritoryFlag(World p_i1681_1_) {
		super(p_i1681_1_);
        this.setSize(1.0F, 8.0F);
        
       /* PP= new PrivateProperty(
    			false,//final boolean master,
    			null,//final JsonElement owner,
    			null,//final JsonElement name,
    			null,//final JsonElement message,
    			null,//final JsonArray chunks,
    			null);//final JsonArray permissions);
    			*/
        
       
	}
	public void setSuperChunk(SuperChunk SupChunk)
	{
		this.SupChunk=SupChunk;
	}
	
	
	
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(0.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0D);

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(250.0D);
        
    }
    @Override
    public void setInvisible(boolean p_82142_1_)
    {
       
    }
    
    @Override
    protected boolean isMovementBlocked()
    {
        return true;
    }
    @Override
    public void moveEntityWithHeading(float p_70612_1_, float p_70612_2_)
    {
    	
    }
    @Override
    public void knockBack(Entity p_70653_1_, float p_70653_2_, double p_70653_3_, double p_70653_5_)
    {
    	
    }


    
 
	
	public void onLivingUpdate()
    {
		
		
		x=(int)this.worldObj.getWorldTime()%24000;
		if(x>activateTime && x<(activateTime+20))
		{
			activated=true;
		}
		if(!this.worldObj.isRemote)
		{
			if (this.ticksExisted % 40 == 0)
            {
                this.heal(1.0F);
            }
			
			if(this.getHealth()<=0.0D)
			{
				func_146077_cc();
				
			}
			else
			{
			

				if(activated) {
						List mobs = this.worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBox((double)this.posX, (double)this.posY, (double)this.posZ, (double)(this.posX + 1), (double)(this.posY + 1), (double)(this.posZ + 1)).expand((double)(this.spawnRange * 2), 4.0D, (double)(this.spawnRange * 2)));
						int j=mobs.size();
	                    if (j >= this.maxNearbyEntities)
	                    {
	                        this.resetTimer();
	                        return;
	                    }
						if(spawnDelay>0)
						{
							spawnDelay--;
						}
						else
						{
							this.summonZombie();
							this.summonSkeleton();
							this.resetTimer();
						}
					
				}
				if(activated && ((23450-x)<=0))
				{

		        	
		        	
		        	worldObj.setBlock( (int)posX+2, (int)posY, (int)posZ-4,Blocks.bedrock);
					worldObj.setBlock( (int)posX+2, (int)posY, (int)posZ+2,Blocks.bedrock);
					worldObj.setBlock( (int)posX-4, (int)posY, (int)posZ-4,Blocks.bedrock);
					worldObj.setBlock( (int)posX-4, (int)posY, (int)posZ+2,Blocks.bedrock);
					
					worldObj.setBlock( (int)posX+2, (int)posY+1, (int)posZ-4,Blocks.torch);
					worldObj.setBlock( (int)posX+2, (int)posY+1, (int)posZ+2,Blocks.torch);
					worldObj.setBlock( (int)posX-4, (int)posY+1, (int)posZ-4,Blocks.torch);
					worldObj.setBlock( (int)posX-4, (int)posY+1, (int)posZ+2,Blocks.torch);
					
					for(int i=(int)posX-3;i<(((int)posX-3)+7);i++)
					{
						for(int k=(int)posZ-3;k<(((int)posZ-3)+7);k++)
						{
							worldObj.setBlock( i-1, (int)posY-1, k-1,Blocks.bedrock);
						}
					}
					this.setDead();
					
					
					worldObj.setBlock((int)posX-1, (int)posY, (int)posZ-1, TERRITORY_FLAG , 0, 2);
					TerritoryFlagBlock flagBlock = (TerritoryFlagBlock) worldObj.getBlock((int)posX-1, (int)posY, (int)posZ-1);
					flagBlock.onBlockPlacedBy(worldObj, (int)posX-1, (int)posY, (int)posZ-1,this, new ItemStack(TERRITORY_FLAG));
					
				}
			}
		}
		else
		{
			BossStatus.setBossStatus(this, false);//TODO should probably be moved to doRender in renderTerritoryFlag class?
			if(!activated) 
			{
				
				//"Chunk("+Cx+","+Cz+") "+
				if((activateTime-x)>0)
					BossStatus.bossName="Wave Start in.. "+Integer.toString((int)((activateTime-x)/20));
				else if((activateTime-x)<0)
					BossStatus.bossName="Wave Start in.. "+Integer.toString((int)((activateTime-x+24000)/20));
			}
			else
				BossStatus.bossName="Survive til Dawn "+Integer.toString((int)((23450-x)/20));
		}
		
    }
		  

    
	
	private void resetTimer()
	{
		
	    if (this.maxSpawnDelay <= this.minSpawnDelay)
	    {
	        this.spawnDelay = this.minSpawnDelay;
	    }
	    else
	    {
	        int i = this.maxSpawnDelay - this.minSpawnDelay;
	        this.spawnDelay = this.minSpawnDelay + this.worldObj.rand.nextInt(i);
	    }
	
	
	}
	
	 private void func_146077_cc()
	    {
	        if (!this.worldObj.isRemote)
	        {
	        	worldObj.setBlock( (int)posX+2, (int)posY+1, (int)posZ-4,Blocks.air);
				worldObj.setBlock( (int)posX+2, (int)posY+1, (int)posZ+2,Blocks.air);
				worldObj.setBlock( (int)posX-4, (int)posY+1, (int)posZ-4,Blocks.air);
				worldObj.setBlock( (int)posX-4, (int)posY+1, (int)posZ+2,Blocks.air);
	        	
	        	
	        	worldObj.setBlock( (int)posX+2, (int)posY, (int)posZ-4,Blocks.air);
				worldObj.setBlock( (int)posX+2, (int)posY, (int)posZ+2,Blocks.air);
				worldObj.setBlock( (int)posX-4, (int)posY, (int)posZ-4,Blocks.air);
				worldObj.setBlock( (int)posX-4, (int)posY, (int)posZ+2,Blocks.air);
				
				for(int i=(int)posX-3;i<(((int)posX-3)+7);i++)
				{
					for(int k=(int)posZ-3;k<(((int)posZ-3)+7);k++)
					{
						worldObj.setBlock( i-1, (int)posY-1, k-1,Blocks.air);
					}
				}
				
				
	            boolean flag = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
	            this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float)this.explosionRadius, flag);
	            this.setDead();
	        }
	    }
	 

	public void summonZombie()
	{
		 	int i = MathHelper.floor_double(this.posX);
	        int j = MathHelper.floor_double(this.posY);
	        int k = MathHelper.floor_double(this.posZ);
	
	        	
	            	EntityZombie entityTerritoryFlag;
	            	entityTerritoryFlag = new EntityZombie(this.worldObj);
	            	entityTerritoryFlag.forceSpawn=true;
	            
	
	            for (int l = 0; l < 50; ++l)
	            {
	                int i1 = i + MathHelper.getRandomIntegerInRange(this.rand, 4, 7) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
	                int j1 = j + MathHelper.getRandomIntegerInRange(this.rand, 0, 10) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
	                int k1 = k + MathHelper.getRandomIntegerInRange(this.rand, 4, 7) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
	
	                if (World.doesBlockHaveSolidTopSurface(this.worldObj, i1, j1 - 1, k1))
	                {
	                	entityTerritoryFlag.setPosition((double)i1, (double)j1, (double)k1);
	
	                    if (this.worldObj.checkNoEntityCollision(entityTerritoryFlag.boundingBox) && this.worldObj.getCollidingBoundingBoxes(entityTerritoryFlag, entityTerritoryFlag.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(entityTerritoryFlag.boundingBox))
	                    {
	                        this.worldObj.spawnEntityInWorld(entityTerritoryFlag);
	                        if (this != null) entityTerritoryFlag.setAttackTarget(this);
	                        entityTerritoryFlag.onSpawnWithEgg((IEntityLivingData)null);
	                        
	                        	entityTerritoryFlag.targetTasks.addTask(1, new EntityAINearestAttackableTarget(entityTerritoryFlag, EntityTerritoryFlag.class, 0, false));
	                        	entityTerritoryFlag.tasks.addTask(1, new EntityAIAttackOnCollide(entityTerritoryFlag, EntityTerritoryFlag.class, 1.0D, true));
	                      break;
	                    }
	                }
	            }
	}
	
	public void summonSkeleton()
	{
		 	int i = MathHelper.floor_double(this.posX);
	        int j = MathHelper.floor_double(this.posY);
	        int k = MathHelper.floor_double(this.posZ);
	        										
	    
	        	
	            	EntitySkeleton entityTerritoryFlag;
	            	entityTerritoryFlag = new EntitySkeleton(this.worldObj);
	            	entityTerritoryFlag.forceSpawn=true;
	
	            for (int l = 0; l < 50; ++l)
	            {
	                int i1 = i + MathHelper.getRandomIntegerInRange(this.rand, 4, 7) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
	                int j1 = j + MathHelper.getRandomIntegerInRange(this.rand, 0, 10) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
	                int k1 = k + MathHelper.getRandomIntegerInRange(this.rand, 4, 7) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
	
	                if (World.doesBlockHaveSolidTopSurface(this.worldObj, i1, j1 - 1, k1))
	                {
	                	entityTerritoryFlag.setPosition((double)i1, (double)j1, (double)k1);
	
	                    if (this.worldObj.checkNoEntityCollision(entityTerritoryFlag.boundingBox) && this.worldObj.getCollidingBoundingBoxes(entityTerritoryFlag, entityTerritoryFlag.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(entityTerritoryFlag.boundingBox))
	                    {
	                        this.worldObj.spawnEntityInWorld(entityTerritoryFlag);
	                        if (this != null) entityTerritoryFlag.setAttackTarget(this);
	                        entityTerritoryFlag.onSpawnWithEgg((IEntityLivingData)null);
	                        	
	                        entityTerritoryFlag.setCurrentItemOrArmor(0, new ItemStack(Items.bow));
	                        entityTerritoryFlag.setCombatTask();
	                        
	                        entityTerritoryFlag.targetTasks.addTask(1, new EntityAINearestAttackableTarget(entityTerritoryFlag, EntityTerritoryFlag.class, 0, false));
	                        
	                     break;
	                    }
	                }
	            }
	}



}
