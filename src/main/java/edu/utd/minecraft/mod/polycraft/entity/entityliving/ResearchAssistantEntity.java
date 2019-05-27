package edu.utd.minecraft.mod.polycraft.entity.entityliving;

import java.util.ArrayList;
import java.util.Random;

import net.minecraftforge.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Armor;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.config.PolycraftEntity;
import edu.utd.minecraft.mod.polycraft.config.Tool;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.computer.ComputerBlock;
import edu.utd.minecraft.mod.polycraft.inventory.computer.ComputerInventory;
import edu.utd.minecraft.mod.polycraft.item.ArmorSlot;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class ResearchAssistantEntity extends EntityMob{

	private static PolycraftEntity config;
	
	private static Random rngesus = new Random();
	private static ArrayList<ItemSword> swords = null;
	private static ArrayList<ItemArmor[]> armors = null;

	private boolean inLab;
	
	public ResearchAssistantEntity(World p_i1738_1_, boolean lab) {
		super(p_i1738_1_);
		this.inLab = lab;
		//this.getNavigator().setBreakDoors(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIOpenDoor(this, false));
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, true));
		this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityTameable.class, 1.0D, true));
		this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		// Note: boolean val determines if to call for help. Should be false.
		this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
		// Note: boolean val determines if LoS is required.
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, false, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityTameable.class, false, true));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, false, false));
		if (armors == null)
			setEquipment();
		ItemSword hold = swords.get(rngesus.nextInt(swords.size()));
		if (hold != null)
			this.setCurrentItemOrArmor(0, new ItemStack(hold));
		ItemArmor[] wear = armors.get(rngesus.nextInt(armors.size()));
		if (wear[0] != null)
			for (int i = 0; i < 4; i++)
				this.setCurrentItemOrArmor(i + 1, new ItemStack(wear[i]));
		this.equipmentDropChances = new float[] { 0.5F, 0.1F, 0.1F, 0.1F, 0.1F };
	}
	
	
	public ResearchAssistantEntity(World p_i1738_1_) {
		this(p_i1738_1_, false);
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(64.0D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
	}

	@Override
	public boolean canDespawn() {
		return !inLab;
	}

	/**
	 * Checks if the entity's current position is a valid location to spawn this
	 * entity.
	 */
	@Override
	public boolean getCanSpawnHere() {
		return this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL
				&& this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox())
				&& this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()
				&& !this.worldObj.isAnyLiquid(this.getEntityBoundingBox());
	}

	@Override
	public boolean isAIDisabled() {
		return false;
	}

	private static void setEquipment() {
		armors = new ArrayList<ItemArmor[]>(Armor.registry.size() + 1);
		armors.add(new ItemArmor[] { null, null, null, null });
		swords = new ArrayList<ItemSword>(Tool.registry.size() + 1);
		swords.add(null);
		for (Armor armor : Armor.registry.values()) {
			ItemArmor[] armorSet = new ItemArmor[4];
			armorSet[ArmorSlot.HEAD.getInventoryArmorSlot()] = (ItemArmor) GameData.getItemRegistry()
					.getObject(PolycraftMod.getAssetName(armor.componentGameIDs[ArmorSlot.HEAD.getValue()]));
			armorSet[ArmorSlot.CHEST.getInventoryArmorSlot()] = (ItemArmor) GameData.getItemRegistry()
					.getObject(PolycraftMod.getAssetName(armor.componentGameIDs[ArmorSlot.CHEST.getValue()]));
			armorSet[ArmorSlot.LEGS.getInventoryArmorSlot()] = (ItemArmor) GameData.getItemRegistry()
					.getObject(PolycraftMod.getAssetName(armor.componentGameIDs[ArmorSlot.LEGS.getValue()]));
			armorSet[ArmorSlot.FEET.getInventoryArmorSlot()] = (ItemArmor) GameData.getItemRegistry()
					.getObject(PolycraftMod.getAssetName(armor.componentGameIDs[ArmorSlot.FEET.getValue()]));
			armors.add(armorSet);
		}
		for (Tool tool : Tool.registry.values()) {
			ItemSword sword = (ItemSword) GameData.getItemRegistry()
					.getObject(PolycraftMod.getAssetName(tool.typeGameIDs[Tool.Type.SWORD.ordinal()]));
			swords.add(sword);
		}
	}
	
	public static final void register(final PolycraftEntity polycraftEntity) {
		ResearchAssistantEntity.config = polycraftEntity;
		PolycraftEntityLiving.register(ResearchAssistantEntity.class, config.entityID, config.name, 0xFFFFFF, 0x88D7FC);
	}

}
