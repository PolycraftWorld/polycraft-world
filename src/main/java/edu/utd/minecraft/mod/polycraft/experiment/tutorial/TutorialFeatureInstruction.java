package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import net.minecraft.util.*;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonCycle;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.client.gui.exp.creation.GuiExpCreator;
import edu.utd.minecraft.mod.polycraft.entity.Physics.EntityGravelCannonBall;
import edu.utd.minecraft.mod.polycraft.entity.ai.EntityAICaptureBases;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.inventory.cannon.GravelCannonInventory;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;

public class TutorialFeatureInstruction extends TutorialFeature{
	public enum InstructionType{
		MOUSE_LEFT,
		MOUSE_RIGHT,
		WASD,
		JUMP,
		FLOAT1,
		FLOAT2,
		SPRINT,
		JUMP_SPRINT,
		INVENTORY1,
		INVENTORY2,
		INVENTORY3,
		INVENTORY4,
		CRAFT_PICK,
		PLACE_BLOCKS,
		BREAK_BLOCKS,
		CART_START,
		CART_END,
		SPAWN_MOBS,
		KBB,
		CRAFT_FKB,
		FKBB,
		HOTBAR,
		LOOK,
		CANNON_TARGET,
		CANNON1,
		CANNON2,
		CANNON3
	};
	private InstructionType type;
	
	@SideOnly(Side.CLIENT)
	GuiPolyButtonCycle<TutorialFeatureInstruction.InstructionType> btnInstructionType;
	
	public boolean sprintDoorOpen;
	public int failCount;
	public int tickWait;
	public int numCreepers = 4;
	public boolean inFail;
	public boolean setAng;
	public int score;
	RenderBox box;
	private final static String KBB = "1hv";
	private final static String FREEZE_KBB = "1hw";
	private final static String ItemGravelCannonBall = "1hS";
	private final static String BlockNaturalRubber = "Jx";
	
	protected GuiPolyNumField xPos1Field, yPos1Field, zPos1Field;
	protected GuiPolyNumField xPos2Field, yPos2Field, zPos2Field;
	
	private BlockPos pos1;
	private BlockPos pos2;
	
	public TutorialFeatureInstruction() {}
	
	
	
	public TutorialFeatureInstruction(String name, BlockPos pos, InstructionType type){
		super(name, pos, Color.MAGENTA);
		this.type = type;
		this.featureType = TutorialFeatureType.INSTRUCTION;
		this.sprintDoorOpen=false;
		this.failCount=0;
		this.inFail=false;
		this.setAng=false;
		this.tickWait=0;
		this.pos1= new BlockPos(pos);
		this.pos2= new BlockPos(pos);

	}
	
	

	
	@Override
	public void preInit(ExperimentTutorial exp) {
		super.preInit(exp);
		pos1 = pos1.add(exp.posOffset.xCoord,
			exp.posOffset.yCoord,
			exp.posOffset.zCoord);
		pos1 = pos2.add(exp.posOffset.xCoord,
				exp.posOffset.yCoord,
				exp.posOffset.zCoord);
		
		switch(type) {
		case INVENTORY1:
			ItemStack item1= new ItemStack(Blocks.spruce_stairs, 4);
			ItemStack item2= new ItemStack(Item.getItemFromBlock(Blocks.planks), 2, 1);
			BlockChest chest=null;
			TileEntityChest tile=null;
			if(exp.world.getBlockState(pos).getBlock() instanceof BlockChest)
				chest=(BlockChest)exp.world.getBlockState(pos).getBlock();
			if(chest!=null)
			{
				chest.createNewTileEntity(exp.world, 0);
				if(exp.world.getTileEntity(pos) instanceof TileEntityChest)
					tile=(TileEntityChest) exp.world.getTileEntity(pos);
				if(tile!=null)
				{
					tile.setInventorySlotContents(0, item1);
					tile.setInventorySlotContents(1, item2);
				}
			}
			break;
		case INVENTORY2:
			ItemStack item3= new ItemStack(Items.stick, 2);
			ItemStack item4= new ItemStack(Items.iron_ingot, 3);
			BlockChest chest2=null;
			TileEntityChest tile2=null;
			if(exp.world.getBlockState(pos).getBlock() instanceof BlockChest)
				chest2=(BlockChest)exp.world.getBlockState(pos).getBlock();
			if(chest2!=null)
			{
				chest2.createNewTileEntity(exp.world, 0);
				if(exp.world.getTileEntity(pos) instanceof TileEntityChest)
					tile2=(TileEntityChest) exp.world.getTileEntity(pos);
				if(tile2!=null)
				{
					tile2.setInventorySlotContents(0, item3);
					tile2.setInventorySlotContents(1, item4);
				}
			}
			break;
		case INVENTORY3:
			Item kbb =  GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(KBB));
			ItemStack item5= new ItemStack(kbb, 64);
			BlockChest chest3=null;
			TileEntityChest tile3=null;
			if(exp.world.getBlockState(pos).getBlock() instanceof BlockChest)
				chest3=(BlockChest)exp.world.getBlockState(pos).getBlock();
			if(chest3!=null)
			{
				chest3.createNewTileEntity(exp.world, 0);
				if(exp.world.getTileEntity(pos) instanceof TileEntityChest)
					tile3=(TileEntityChest) exp.world.getTileEntity(pos);
				if(tile3!=null)
				{
					tile3.setInventorySlotContents(0, item5);
				}
			}
			break;
		case INVENTORY4:
			Item kbb2 =  GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(KBB));
			ItemStack item6= new ItemStack(kbb2, 64);
			ItemStack item7= new ItemStack(Item.getItemFromBlock(Blocks.packed_ice), 64);
			BlockChest chest4=null;
			TileEntityChest tile4=null;
			if(exp.world.getBlockState(pos).getBlock() instanceof BlockChest)
				chest4=(BlockChest)exp.world.getBlockState(pos).getBlock();
			if(chest4!=null)
			{
				chest4.createNewTileEntity(exp.world, 0);
				if(exp.world.getTileEntity(pos) instanceof TileEntityChest)
					tile4=(TileEntityChest) exp.world.getTileEntity(pos);
				if(tile4!=null)
				{
					tile4.setInventorySlotContents(0, item6);
					tile4.setInventorySlotContents(1, item7);
				}
			}
			break;
		default:
			break;
		}
		
	}
	
	@Override
	public void updateValues() {
		this.pos1 = new BlockPos(Integer.parseInt(xPos1Field.getText()),
							Integer.parseInt(yPos1Field.getText()),
							Integer.parseInt(zPos1Field.getText()));
		this.pos2= new BlockPos(Integer.parseInt(xPos2Field.getText()),
							Integer.parseInt(yPos2Field.getText()),
							Integer.parseInt(zPos2Field.getText()));
		this.type = btnInstructionType.getCurrentOption();
		this.save();
		super.updateValues();
		
        //this.isDirty=true;
        //TutorialManager.INSTANCE.sendFeatureUpdate(0, 0, this, true);
	}
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		switch(type) {
		case CRAFT_FKB:
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(player!=null)
				{
					if(player.openContainer!=null) 
					{
						if(player.openContainer!=player.inventoryContainer)
						{
							Item fkbb =  GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(FREEZE_KBB));
							if(player.inventory.hasItem(fkbb))
							{
								this.canProceed=true;
								this.isDirty=true;
								player.addChatMessage(new ChatComponentText("You crafted a Freezing Knockback Bomb!"));
								this.complete(exp);
							}
						}
					}
				}
			}
			break;
		case INVENTORY1:
			//super.onServerTickUpdate(exp);
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(player!=null)
				{
					if(player.openContainer!=null) 
					{
						if(player.openContainer!=player.inventoryContainer)
						{
							Item blocks = null;
							blocks=blocks.getItemFromBlock(Blocks.planks);
							
							Item stairs = null;
							stairs=stairs.getItemFromBlock(Blocks.spruce_stairs);
							
							if(player.inventory.hasItem(blocks) && player.inventory.hasItem(stairs))
							{
								this.canProceed=true;
								this.isDirty=true;
								player.addChatMessage(new ChatComponentText("You got the building materials!"));
								this.complete(exp);
							}
						}
					}
				}
			}
			break;
		case INVENTORY2:
			//super.onServerTickUpdate(exp);
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(player!=null)
				{
					if(player.openContainer!=null) 
					{
						if(player.openContainer!=player.inventoryContainer)
						{
							Item sticks = Items.stick;
							Item iron = Items.iron_ingot;
							if(player.inventory.hasItem(sticks) && player.inventory.hasItem(iron))
							{
								this.canProceed=true;
								this.isDirty=true;
								player.addChatMessage(new ChatComponentText("You got the crafting materials!"));
								this.complete(exp);
							}
						}
					}
				}
			}
			break;
		case INVENTORY3:
			//super.onServerTickUpdate(exp);
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(player!=null)
				{
					if(player.openContainer!=null) 
					{
						if(player.openContainer!=player.inventoryContainer)
						{
							Item kbb =  GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(KBB));
							if(player.inventory.hasItem(kbb))
							{
								this.canProceed=true;
								this.isDirty=true;
								player.addChatMessage(new ChatComponentText("You got the Knockback Bomb!"));
								this.complete(exp);
							}
						}
					}
				}
			}
			break;
		case INVENTORY4:
			//super.onServerTickUpdate(exp);
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(player!=null)
				{
					if(player.openContainer!=null) 
					{
						if(player.openContainer!=player.inventoryContainer)
						{
							Item kbb =  GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(KBB));
							Item packed_ice=null;
							packed_ice= packed_ice.getItemFromBlock(Blocks.packed_ice);
							if(player.inventory.hasItem(kbb) && player.inventory.hasItem(packed_ice))
							{
								this.canProceed=true;
								this.isDirty=true;
								player.addChatMessage(new ChatComponentText("You got the crafting materials!"));
								this.complete(exp);
							}
						}
					}
				}
			}
			break;
		case CRAFT_PICK:
			//super.onServerTickUpdate(exp);
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(player!=null)
				{
					if(player.openContainer!=null) 
					{
						if(player.openContainer!=player.inventoryContainer)
						{
							Item pick = Items.iron_pickaxe;
							if(player.inventory.hasItem(pick))
							{
								this.canProceed=true;
								this.isDirty=true;
								player.addChatMessage(new ChatComponentText("You crafted an Iron Pickaxe!"));
								this.complete(exp);
							}
						}
					}
				}
			}
			break;
		case JUMP:
			super.onServerTickUpdate(exp);
			break;
		case FLOAT1:
			super.onServerTickUpdate(exp);
			break;
		case FLOAT2:
			super.onServerTickUpdate(exp);
			break;
			//Spawn Creepers
		case SPAWN_MOBS:
			EntityCreeper newCreeper;
			for(int currentAnimal = 0; currentAnimal < numCreepers; currentAnimal++) {
				int currentXvalue = (int) ((int) Math.round(Math.random()*((pos2.getX() - pos1.getX()))) + pos1.getX());
				int currentYvalue = (int) pos1.getY();
				int currentZvalue = (int) ((int) Math.round(Math.random()*((pos2.getZ() - pos1.getZ()))) + pos1.getZ());
				
				newCreeper = new EntityCreeper(exp.world);
				newCreeper.setPosition(currentXvalue, currentYvalue, currentZvalue);
				//newAnimal.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(animalSpeed);
				//newAnimal.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(64.0D);				
				exp.world.spawnEntityInWorld(newCreeper);
			}
				this.canProceed = true;
				this.complete(exp);
			break;
		case KBB:
			//super.onServerTickUpdate(exp);
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(exp.world.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.fromBounds(
						Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()),
						Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()))).isEmpty()) {
					this.canProceed = true;
					this.complete(exp);
				}
			}
			break;
		case FKBB:
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(exp.world.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.fromBounds(
						Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()),
						Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()))).isEmpty()) {
					this.canProceed = true;
					this.complete(exp);
				}
			}
			break;			
		case MOUSE_LEFT:
			//super.onServerTickUpdate(exp);
			break;
		case MOUSE_RIGHT:
			//super.onServerTickUpdate(exp);
			break;
		case PLACE_BLOCKS:
			//super.onServerTickUpdate(exp);
			if((exp.world.getBlockState(new BlockPos((int)x1, (int)y1, (int)z1)).getBlock()==Blocks.planks) ||
					(exp.world.getBlockState(new BlockPos((int)x1, (int)y1, (int)z1)).getBlock()==Blocks.spruce_stairs)) {
				this.canProceed = true;
				this.complete(exp);
			}
			break;
		case BREAK_BLOCKS:
			//super.onServerTickUpdate(exp);
			if((exp.world.getBlockState(new BlockPos((int)x1, (int)y1, (int)z1)).getBlock()==Blocks.air)) {
				this.canProceed = true;
				this.complete(exp);
			}
			break;
		case CART_START:
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.fromBounds(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
						Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))).contains(player)) {
					
					//EntityMinecart entityminecart = EntityMinecart.getMinecart(exp.world, (double)((float)x1 + 0.5F), (double)((float)y1 + 0.5F), (double)((float)z1 + 0.5F), EntityMinecart.EnumMinecartType.RIDEABLE);
					EntityMinecart entityminecart = EntityMinecart.func_180458_a(exp.world, (double)((float)x1 + 0.5F), (double)((float)y1 + 0.5F), (double)((float)z1 + 0.5F), EntityMinecart.EnumMinecartType.RIDEABLE);
					exp.world.spawnEntityInWorld(entityminecart);
					player.mountEntity(entityminecart);
					
					this.canProceed = true;
					this.complete(exp);
				}
			}
			break;
		case CART_END:
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.fromBounds(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
						Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))).contains(player)) {
					//EntityMinecartEmpty minecart=(EntityMinecartEmpty)entityminecart;
					if(player.ridingEntity!=null)
						player.ridingEntity.setDead();
					player.setPosition(player.posX, player.posY+1, player.posZ);
					this.canProceed = true;
					this.complete(exp);
				}
			}
			break;
		case SPRINT:
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(player!=null)
				{
					if(player.isSprinting()) 
					{
						for(int x=(int)Math.min(pos1.getX(), pos2.getX());x<=(int)Math.max(pos1.getX(), pos2.getX());x++)
						{
							for(int y=(int)Math.min(pos1.getY(), pos2.getY());y<=(int)Math.max(pos1.getY(), pos2.getY());y++)
							{
								for(int z=(int)Math.min(pos1.getZ(), pos2.getZ());z<=(int)Math.max(pos1.getZ(), pos2.getZ());z++)
								{
									exp.world.setBlockToAir(new BlockPos(x, y, z));
								}
							}
						}
						this.sprintDoorOpen=true;
					}
					else
					{
						for(int x=(int)Math.min(pos1.getX(), pos2.getX());x<=(int)Math.max(pos1.getX(), pos2.getX());x++)
						{
							for(int y=(int)Math.min(pos1.getY(), pos2.getY());y<=(int)Math.max(pos1.getY(), pos2.getY());y++)
							{
								for(int z=(int)Math.min(pos1.getZ(), pos2.getZ());z<=(int)Math.max(pos1.getZ(), pos2.getZ());z++)
								{
									exp.world.setBlockState(new BlockPos(x, y, z), Blocks.planks.getDefaultState(), 3);
								}
							}
						}
						this.sprintDoorOpen=false;
					}
					if(exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.fromBounds(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
							Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))).contains(player)) {
						this.canProceed = true;
						this.complete(exp);
						for(int x=(int)Math.min(pos1.getX(), pos2.getX());x<=(int)Math.max(pos1.getX(), pos2.getX());x++)
						{
							for(int y=(int)Math.min(pos1.getY(), pos2.getY());y<=(int)Math.max(pos1.getY(), pos2.getY());y++)
							{
								for(int z=(int)Math.min(pos1.getZ(), pos2.getZ());z<=(int)Math.max(pos1.getZ(), pos2.getZ());z++)
								{
									exp.world.setBlockState(new BlockPos(x, y, z), Blocks.planks.getDefaultState(), 3);
								}
							}
						}
					}
				}
			}
			break;
		case JUMP_SPRINT:
			super.onServerTickUpdate(exp);
			if(!inFail)
			{
				for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
					if(exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.fromBounds(
							Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()),
							Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()))).contains(player)) {
						this.inFail=true;				
					}
				}
			}
			else
			{
				for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
					if(!exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.fromBounds(
							Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()),
							Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()))).contains(player)) {
						this.inFail=false;
						this.failCount++;
						player.addChatMessage(new ChatComponentText("You missed your jump "+failCount+" time(s)"));
						
					}
				}
			}
			if(failCount>=2)
			{
				for(int x=(int)Math.min(pos1.getX(), pos2.getX());x<=(int)Math.max(pos1.getX(), pos2.getX());x++)
				{
					for(int y=(int)Math.min(pos1.getY(), pos2.getY());y<=(int)Math.max(pos1.getY(), pos2.getY());y++)
					{
						for(int z=(int)Math.min(pos1.getZ(), pos2.getZ());z<=(int)Math.max(pos1.getZ(), pos2.getZ());z++)
						{
							exp.world.setBlockState(new BlockPos(x, y-1, z),  Blocks.packed_ice.getDefaultState(), 3);
						}
					}
				}
				this.complete(exp);
			}
			break;
		case WASD:
			//super.onServerTickUpdate(exp);
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.fromBounds(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
						Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))).contains(player)) {
					this.canProceed = true;
					this.complete(exp);
					for(int x=(int)Math.min(pos1.getX(), pos2.getX());x<=(int)Math.max(pos1.getX(), pos2.getX());x++)
					{
						for(int y=(int)Math.min(pos1.getY(), pos2.getY());y<=(int)Math.max(pos1.getY(), pos2.getY());y++)
						{
							for(int z=(int)Math.min(pos1.getZ(), pos2.getZ());z<=(int)Math.max(pos1.getZ(), pos2.getZ());z++)
							{
								if(exp.world.getBlockState(new BlockPos(x, y, z)).getBlock()==Blocks.iron_door)
								{
									BlockDoor door1 = (BlockDoor)exp.world.getBlockState(new BlockPos(x, y, z)).getBlock();
									door1.toggleDoor(exp.world, new BlockPos(x, y, z), true);
								}
							}
						}
					}
				}
			}
			break;
		case HOTBAR:
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(player!=null)
				{
							
					Item pick = Items.iron_pickaxe; //This can be whatever item you need.
					for(int c=0;c<9;c++)
					{
						if(player.inventory.getStackInSlot(c)!=null)
						{
							if(player.inventory.getStackInSlot(c).getItem()==pick) //checks that the player has that item in their hotbar
							{
								this.canProceed=true;
								this.isDirty=true;
								player.addChatMessage(new ChatComponentText("Placed item into hotbar!"));
								this.complete(exp);
							}
						}
					}
				}
			}
			break;
		case LOOK:
			break;
					case CANNON_TARGET:
			//super.onServerTickUpdate(exp);
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				canProceed = true;
				if(!exp.world.getEntitiesWithinAABB(EntityGravelCannonBall.class, AxisAlignedBB.fromBounds(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
						Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))).isEmpty()) {
					
					isDone = true;
					exp.scoreboard.updateScore(exp.scoreboard.getTeams().get(0), 1);
					this.score= Math.round((exp.scoreboard.getTeamScores().get(exp.scoreboard.getTeams().get(0))));
					//player.addChatMessage(new ChatComponentText("You hit "+score+" target(s)"));
					
					
					for(int x=(int)Math.min(pos1.getX(), pos2.getX());x<=(int)Math.max(pos1.getX(), pos2.getX());x++)
					{
						for(int y=(int)Math.min(pos1.getY(), pos2.getY());y<=(int)Math.max(pos1.getY(), pos2.getY());y++)
						{
							for(int z=(int)Math.min(pos1.getZ(), pos2.getZ());z<=(int)Math.max(pos1.getZ(), pos2.getZ());z++)
							{
								if(exp.world.getBlockState(new BlockPos(x, y, z)).getBlock()==GameData.getBlockRegistry().getObject(PolycraftMod.getAssetName(BlockNaturalRubber)))
								{
									//exp.world.setBlockState(new BlockPos(x, y, z), 2, 4); TODO: What is this even for?
									//exp.world.markBlockForUpdate(x, y, z);
								}
							}
						}
					}
				}
			}
			break;
		case CANNON1:
			exp.scoreboard.updateScore(exp.scoreboard.getTeams().get(0), 0);
			//super.onServerTickUpdate(exp);
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				GravelCannonInventory cannon1 = (GravelCannonInventory)exp.world.getTileEntity(new BlockPos((int)x1, (int)y1, (int)z1));
				if(cannon1.slotHasItem(cannon1.getInputSlots().get(0)))
	        	{
			        if( !(cannon1.getStackInSlot(0).getItem()==GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(ItemGravelCannonBall))) )
			        {

//						canProceed = true;
//						isDone = true;
//						player.setLocationAndAngles(4, 57, 111, -90, 0);
			        }
	        	}
				else
				{
					if(this.tickWait>40)
					{
						canProceed = true;
						isDone = true;
						player.setLocationAndAngles(4, 57, 111, -90, 0);
					}
					this.tickWait+=1;
				}
			}
			break;
		case CANNON2:
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				GravelCannonInventory cannon1 = (GravelCannonInventory)exp.world.getTileEntity(new BlockPos((int)x1, (int)y1, (int)z1));
				if(cannon1.slotHasItem(cannon1.getInputSlots().get(0)))
	        	{
			        if( !(cannon1.getStackInSlot(0).getItem()==GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(ItemGravelCannonBall))) )
			        {

//						canProceed = true;
//						isDone = true;
//						player.setLocationAndAngles(4, 57, 193, -90, 0);
			        }
	        	}
				else
				{
					if(this.tickWait>40)
					{
						canProceed = true;
						isDone = true;
						player.setLocationAndAngles(4, 57, 193, -90, 0);
					}
					this.tickWait+=1;
				}
			}

			break;
		case CANNON3:
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				GravelCannonInventory cannon1 = (GravelCannonInventory)exp.world.getTileEntity(new BlockPos((int)x1, (int)y1, (int)z1));
				if(cannon1.slotHasItem(cannon1.getInputSlots().get(0)))
	        	{
			        if( !(cannon1.getStackInSlot(0).getItem()==GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(ItemGravelCannonBall))) )
			        {

//						canProceed = true;
//						isDone = true;
//						//player.setLocationAndAngles(4, 57, 111, -90, 0);
			        }
	        	}
				else
				{
					if(this.tickWait>40)
					{
						canProceed = true;
						isDone = true;
						this.score= Math.round((exp.scoreboard.getTeamScores().get(exp.scoreboard.getTeams().get(0))));
						//player.addChatMessage(new ChatComponentText("You hit "+score+" target(s)"));
					}
					this.tickWait+=1;
					//player.setLocationAndAngles(4, 57, 111, -90, 0);
				}
			}

			break;
			
		default:
			break;
		
		}
	}
	
	
	@Override
	public void render(Entity entity) {
		EntityPlayer player=null;
		switch(type) {
		case CRAFT_FKB:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Craft a Freezing Knockback Bomb",155,5);
			player=null;
			if(entity instanceof EntityPlayer)	
				player=(EntityPlayer)(entity);
				
				if(player!=null)
				{
					if(player.openContainer!=null) 
					{
						if(player.openContainer!=player.inventoryContainer)
						{
							TutorialRender.instance.renderTutorialCraftFKBB(entity);
							//player.addChatMessage(new ChatComponentText("You have opened a Container"));
						}
						else
						{
							TutorialRender.instance.renderTutorialAccessPolyTable(entity);
						}
					}
				}
			break;
		case INVENTORY1:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Take the contents of the chest",155,5);
			if(entity instanceof EntityPlayer)	
				player=(EntityPlayer)(entity);
				
				if(player!=null)
				{
					if(player.openContainer!=null) 
					{
						if(player.openContainer!=player.inventoryContainer)
						{
							TutorialRender.instance.renderTutorialManageInventory1(entity);
							//player.addChatMessage(new ChatComponentText("You have opened a Container"));
						}
						else
						{
							TutorialRender.instance.renderTutorialAccessInventory1(entity);
						}
					}
				}
			break;
		case INVENTORY2:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Take the contents of the chest",155,5);
			//player=null;
			if(entity instanceof EntityPlayer)	
				player=(EntityPlayer)(entity);
				
				if(player!=null)
				{
					if(player.openContainer!=null) 
					{
						if(player.openContainer!=player.inventoryContainer)
						{
							TutorialRender.instance.renderTutorialManageInventory2(entity);
						}
						else
						{
							TutorialRender.instance.renderTutorialAccessInventory2(entity);
						}
					}
				}
			break;
		case INVENTORY3:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Take the contents of the chest",155,5);
			player=null;
			if(entity instanceof EntityPlayer)	
				player=(EntityPlayer)(entity);
				
				if(player!=null)
				{
					if(player.openContainer!=null) 
					{
						if(player.openContainer!=player.inventoryContainer)
						{
							TutorialRender.instance.renderTutorialManageInventory3(entity);
							//player.addChatMessage(new ChatComponentText("You have opened a Container"));
						}
						else
						{
							TutorialRender.instance.renderTutorialAccessInventory3(entity);
						}
					}
					
				}
			break;
		case INVENTORY4:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Take the contents of the chest",155,5);
			player=null;
			if(entity instanceof EntityPlayer)	
				player=(EntityPlayer)(entity);
				
				if(player!=null)
				{
					if(player.openContainer!=null) 
					{
						if(player.openContainer!=player.inventoryContainer)
						{
							TutorialRender.instance.renderTutorialManageInventory4(entity);
							//player.addChatMessage(new ChatComponentText("You have opened a Container"));
						}
						else
						{
							TutorialRender.instance.renderTutorialAccessInventory4(entity);
						}
					}
				}
			break;
		case CRAFT_PICK:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Craft an Iron Pickaxe",155,5);
			player=null;
			if(entity instanceof EntityPlayer)	
				player=(EntityPlayer)(entity);
				
				if(player!=null)
				{
					if(player.openContainer!=null) 
					{
						if(player.openContainer!=player.inventoryContainer)
						{
							TutorialRender.instance.renderTutorialCraftingPick(entity);
						}
						else
						{
							TutorialRender.instance.renderTutorialAccessTable(entity);
							//Gui to instruct player to click on the chest
						}
					}
				}
			break;
		case JUMP:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Press 'Space' and 'W' to jump over obstacles",155,5);
			TutorialRender.instance.renderTutorialJump(entity);
			break;
		case FLOAT1:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Hold 'Space' and 'W' to float and move in water",155,5);
			TutorialRender.instance.renderTutorialFloatJungle(entity);
			break;
		case FLOAT2:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Hold 'Space' and 'W' to float and move in water",155,5);
			TutorialRender.instance.renderTutorialFloatSwamp(entity);
			break;
		case SPAWN_MOBS:
			super.render(entity);
			break;
		case KBB:
			super.render(entity);
			//super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Use the KnockBack Bomb on the mobs",155,5);
			TutorialRender.instance.renderTutorialUseKBB(entity);
			break;
		case FKBB:
			super.render(entity);
			//super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Use the Frozen Knockback Bomb on the mobs",155,5);
			TutorialRender.instance.renderTutorialUseFKBB(entity);
			break;
		case MOUSE_LEFT:
			super.render(entity);	//super needs to run before overlay render. Because I don't know how to undo mc.entityRenderer.setupOverlayRendering()
			TutorialRender.instance.renderTutorialDrawString("Turn the mouse to the left",155,5);
			if(!this.setAng)
			{
				TutorialRender.instance.setAng(entity);
				this.setAng=true;
			}
			if(TutorialRender.instance.renderTutorialTurnLeft(entity))
			{
				this.canProceed=true;
				this.setAng=false;
				this.isDirty=true;
				this.complete(entity);
			}
			break;
		case MOUSE_RIGHT:
			super.render(entity);	//super needs to run before overlay render. Because I don't know how to undo mc.entityRenderer.setupOverlayRendering()
			TutorialRender.instance.renderTutorialDrawString("Turn the mouse to the right",155,5);
			if(!this.setAng)
			{
				TutorialRender.instance.setAng(entity);
				this.setAng=true;
			}
			if(TutorialRender.instance.renderTutorialTurnRight(entity))
			{
				this.canProceed=true;
				this.setAng=false;
				this.isDirty=true;
				this.complete(entity);
			}
			break;
		case PLACE_BLOCKS:
			//super.render(entity);
			this.box.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Place the blocks in the highlighted location",155,5);
			TutorialRender.instance.renderTutorialPlacingBlocks(entity);
			break;
		case BREAK_BLOCKS:
			//super.render(entity);
			this.box.renderFill(entity);
			TutorialRender.instance.renderTutorialDrawString("Break the Highlighted blocks",155,5);
			TutorialRender.instance.renderTutorialMining(entity);
			break;
		case CART_START:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Walk to the start of the ride",155,5);
			break;
		case CART_END:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Look forward + press 'W' to begin - please keep hands & feet in the minecart at all times!",5,5);
			break;
		case SPRINT:
			super.render(entity);	//super needs to run before overlay render. Because I don't know how to undo mc.entityRenderer.setupOverlayRendering()
			TutorialRender.instance.renderTutorialDrawString("Sprint to open the path",155,5);
			TutorialRender.instance.renderTutorialSprint(entity);
			break;
		case JUMP_SPRINT:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Sprint and jump to get to the other side",155,5);
			TutorialRender.instance.renderTutorialSprintJump(entity);
			break;
		case WASD:
			super.render(entity);	//super needs to run before overlay render. Because I don't know how to undo mc.entityRenderer.setupOverlayRendering()
			TutorialRender.instance.renderTutorialDrawString("Press 'W' to walk forward" ,155,5);
			TutorialRender.instance.renderTutorialWalkForward(entity);
			break;
		case HOTBAR:
			break;
		case LOOK:
			super.render(entity);
			player=null;
			if(entity instanceof EntityPlayer)	
				player=(EntityPlayer)(entity);
	        Vec3 vec3 = new Vec3(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
	        Vec3 vec32 = player.getLook(1.0F);
	        //player.addChatMessage(new ChatComponentText("Anglex: "+vec32.xCoord+" Anglez: "+vec32.zCoord));
	        
	        
	        //Vec3 vec32 = vec3.addVector(vec31.xCoord , vec31.yCoord , vec31.zCoord );
	        Vec3 ref=null;
	        ref = new Vec3(1, 0, 0);
	        
	        Vec3 vec01=null;
	        vec01 = new Vec3(pos1.getX(), pos1.getY(), pos1.getZ());

			Vec3 vec02 = vec3.addVector(-(vec01.xCoord+.5) , -vec01.yCoord , -(vec01.zCoord+.5));
			
			double dist=vec02.lengthVector();

			double ang02 =-Math.atan2(vec02.zCoord, vec02.xCoord);
			double ang32 = -Math.atan2(vec32.zCoord, vec32.xCoord);
			double ang=ang32-ang02;
			TutorialRender.instance.renderLook(player, ang*180/Math.PI+90, dist);
			
			break;
		case CANNON_TARGET:
			super.render(entity);
			break;
		case CANNON1:
			//super.render(entity);
			TutorialRender.instance.renderTutorialCannon1(player);
			break;
		case CANNON2:
			//super.render(entity);
			TutorialRender.instance.renderTutorialCannon2(player);
			break;
		case CANNON3:
			//super.render(entity);
			TutorialRender.instance.renderTutorialCannon3(player);
			break;
			
		default:
			break;
		
		}
	}
	
	@Override
	public void renderScreen(Entity entity) {
		if (entity.worldObj.isRemote) {
			EntityPlayer player=null;
			switch(type) {
			case CRAFT_FKB:
				super.render(entity);
				TutorialRender.instance.renderTutorialDrawString("Craft a Freezing Knockback Bomb",155,5);
				player=null;
				if(entity instanceof EntityPlayer)	
					player=(EntityPlayer)(entity);
					
					if(player!=null)
					{
						if(player.openContainer!=null) 
						{
							if(player.openContainer!=player.inventoryContainer)
							{
								TutorialRender.instance.renderTutorialCraftFKBB(entity);
								//player.addChatMessage(new ChatComponentText("You have opened a Container"));
							}
							else
							{
								TutorialRender.instance.renderTutorialAccessPolyTable(entity);
							}
						}
					}
				break;
			case INVENTORY1:
				super.render(entity);
				TutorialRender.instance.renderTutorialDrawString("Take the contents of the chest",155,5);
				if(entity instanceof EntityPlayer)	
					player=(EntityPlayer)(entity);
					
					if(player!=null)
					{
						if(player.openContainer!=null) 
						{
							if(player.openContainer!=player.inventoryContainer)
							{
								TutorialRender.instance.renderTutorialManageInventory1(entity);
								//player.addChatMessage(new ChatComponentText("You have opened a Container"));
							}
							else
							{
								TutorialRender.instance.renderTutorialAccessInventory1(entity);
							}
						}
					}
				break;
			case INVENTORY2:
				super.render(entity);
				TutorialRender.instance.renderTutorialDrawString("Take the contents of the chest",155,5);
				//player=null;
				if(entity instanceof EntityPlayer)	
					player=(EntityPlayer)(entity);
					
					if(player!=null)
					{
						if(player.openContainer!=null) 
						{
							if(player.openContainer!=player.inventoryContainer)
							{
								TutorialRender.instance.renderTutorialManageInventory2(entity);
							}
							else
							{
								TutorialRender.instance.renderTutorialAccessInventory2(entity);
							}
						}
					}
				break;
			case INVENTORY3:
				super.render(entity);
				TutorialRender.instance.renderTutorialDrawString("Take the contents of the chest",155,5);
				player=null;
				if(entity instanceof EntityPlayer)	
					player=(EntityPlayer)(entity);
					
					if(player!=null)
					{
						if(player.openContainer!=null) 
						{
							if(player.openContainer!=player.inventoryContainer)
							{
								TutorialRender.instance.renderTutorialManageInventory3(entity);
								//player.addChatMessage(new ChatComponentText("You have opened a Container"));
							}
							else
							{
								TutorialRender.instance.renderTutorialAccessInventory3(entity);
							}
						}
						
					}
				break;
			case INVENTORY4:
				super.render(entity);
				TutorialRender.instance.renderTutorialDrawString("Take the contents of the chest",155,5);
				player=null;
				if(entity instanceof EntityPlayer)	
					player=(EntityPlayer)(entity);
					
					if(player!=null)
					{
						if(player.openContainer!=null) 
						{
							if(player.openContainer!=player.inventoryContainer)
							{
								TutorialRender.instance.renderTutorialManageInventory4(entity);
								//player.addChatMessage(new ChatComponentText("You have opened a Container"));
							}
							else
							{
								TutorialRender.instance.renderTutorialAccessInventory4(entity);
							}
						}
					}
				break;
			case CRAFT_PICK:
				super.render(entity);
				TutorialRender.instance.renderTutorialDrawString("Craft an Iron Pickaxe",155,5);
				player=null;
				if(entity instanceof EntityPlayer)	
					player=(EntityPlayer)(entity);
					
					if(player!=null)
					{
						if(player.openContainer!=null) 
						{
							if(player.openContainer!=player.inventoryContainer)
							{
								TutorialRender.instance.renderTutorialCraftingPick(entity);
							}
							else
							{
								TutorialRender.instance.renderTutorialAccessTable(entity);
								//Gui to instruct player to click on the chest
							}
						}
					}
				break;
			default:
				break;
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void buildGuiParameters(GuiExpCreator guiDevTool, int x_pos, int y_pos) {
		// TODO Auto-generated method stub
		super.buildGuiParameters(guiDevTool, x_pos, y_pos);
		
        btnInstructionType = new GuiPolyButtonCycle<TutorialFeatureInstruction.InstructionType>(
        		guiDevTool.buttonCount + 1, x_pos + 10, y_pos + 65, (int) (guiDevTool.X_WIDTH * .9), 20, 
        		"Type",  TutorialFeatureInstruction.InstructionType.WASD);
        guiDevTool.addBtn(btnInstructionType);
        
        addFields(guiDevTool, x_pos, y_pos);
	}
	
	public void addFields(GuiExpCreator guiDevTool, int x_pos, int y_pos)
	{
		FontRenderer fr = guiDevTool.getFontRenderer();
		
		y_pos += 45;
		
		guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Pos1"));
		guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +30, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"X:"));
        xPos1Field = new GuiPolyNumField(fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        xPos1Field.setMaxStringLength(32);
        xPos1Field.setText(Integer.toString((int)pos1.getX()));
        xPos1Field.setTextColor(16777215);
        xPos1Field.setVisible(true);
        xPos1Field.setCanLoseFocus(true);
        xPos1Field.setFocused(false);
        guiDevTool.textFields.add(xPos1Field);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +85, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Y:"));
        yPos1Field = new GuiPolyNumField(fr, x_pos + 95, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        yPos1Field.setMaxStringLength(32);
        yPos1Field.setText(Integer.toString((int)pos1.getY()));
        yPos1Field.setTextColor(16777215);
        yPos1Field.setVisible(true);
        yPos1Field.setCanLoseFocus(true);
        yPos1Field.setFocused(false);
        guiDevTool.textFields.add(yPos1Field);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +140, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Z:"));
        zPos1Field = new GuiPolyNumField(fr, x_pos + 150, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        zPos1Field.setMaxStringLength(32);
        zPos1Field.setText(Integer.toString((int)pos1.getZ()));
        zPos1Field.setTextColor(16777215);
        zPos1Field.setVisible(true);
        zPos1Field.setCanLoseFocus(true);
        zPos1Field.setFocused(false);
        guiDevTool.textFields.add(zPos1Field);
		
		y_pos += 15;
		
		guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Pos2"));
		guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +30, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"X:"));
        xPos2Field = new GuiPolyNumField(fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        xPos2Field.setMaxStringLength(32);
        xPos2Field.setText(Integer.toString((int)pos2.getX()));
        xPos2Field.setTextColor(16777215);
        xPos2Field.setVisible(true);
        xPos2Field.setCanLoseFocus(true);
        xPos2Field.setFocused(false);
        guiDevTool.textFields.add(xPos2Field);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +85, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Y:"));
        yPos2Field = new GuiPolyNumField(fr, x_pos + 95, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        yPos2Field.setMaxStringLength(32);
        yPos2Field.setText(Integer.toString((int)pos2.getY()));
        yPos2Field.setTextColor(16777215);
        yPos2Field.setVisible(true);
        yPos2Field.setCanLoseFocus(true);
        yPos2Field.setFocused(false);
        guiDevTool.textFields.add(yPos2Field);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +140, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Z:"));
        zPos2Field = new GuiPolyNumField(fr, x_pos + 150, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        zPos2Field.setMaxStringLength(32);
        zPos2Field.setText(Integer.toString((int)pos2.getZ()));
        zPos2Field.setTextColor(16777215);
        zPos2Field.setVisible(true);
        zPos2Field.setCanLoseFocus(true);
        zPos2Field.setFocused(false);
        guiDevTool.textFields.add(zPos2Field);
	}

	public InstructionType getType() {
		return type;
	}
	
	//Should only use UpdateValues to update variables
	@Deprecated
	public void setType(InstructionType type) {
		this.type = type;
	}
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		nbt.setString("instructionType", type.name());	//calling name is preferred over toString - SG
		nbt.setBoolean("sprintDoorOpen", sprintDoorOpen);
		nbt.setBoolean("inFail", inFail);
		nbt.setInteger("failcount", this.failCount);
		nbt.setBoolean("setAng", setAng);
		int pos1[] = {(int)this.pos1.getX(), (int)this.pos1.getY(), (int)this.pos1.getZ()};
		nbt.setIntArray("pos1",pos1);
		int pos2[] = {(int)this.pos2.getX(), (int)this.pos2.getY(), (int)this.pos2.getZ()};
		nbt.setIntArray("pos2",pos2);
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		InstructionType tmp = null;
		type=tmp.valueOf(nbtFeat.getString("instructionType"));
		this.sprintDoorOpen=nbtFeat.getBoolean("sprintDoorOpen");
		this.inFail=nbtFeat.getBoolean("inFail");
		this.failCount=nbtFeat.getInteger("failcount");
		this.setAng=nbtFeat.getBoolean("setAng");
		
		int featPos1[]=nbtFeat.getIntArray("pos1");
		this.pos1=new BlockPos(featPos1[0], featPos1[1], featPos1[2]);
		
		int featPos2[]=nbtFeat.getIntArray("pos2");
		this.pos2=new BlockPos(featPos2[0], featPos2[1], featPos2[2]);
		
		this.box= new RenderBox(this.getPos().getX(), this.getPos().getZ(), this.getPos2().getX(), this.getPos2().getZ(),
				Math.min(this.getPos().getY(), this.getPos2().getY()), Math.max(Math.abs(this.getPos().getY()- this.getPos2().getY()), 1), 1, this.getName());
		box.setColor(this.getColor());
	}
	

	@Override
	public JsonObject saveJson()
	{
		super.saveJson();
		jobj.add("pos1", blockPosToJsonArray(pos1));
		jobj.add("pos2", blockPosToJsonArray(pos2));
		jobj.addProperty("instructionType", type.name());
		jobj.addProperty("sprintDoorOpen", sprintDoorOpen);
		jobj.addProperty("inFail", inFail);
		jobj.addProperty("failCount", failCount);
		jobj.addProperty("setAng", setAng);
		return jobj;
	}
	
	@Override
	public void loadJson(JsonObject featJson)
	{
		super.loadJson(featJson);
		this.pos1 = blockPosFromJsonArray(featJson.get("pos1").getAsJsonArray());
		this.pos2 = blockPosFromJsonArray(featJson.get("pos2").getAsJsonArray());
		this.type = InstructionType.valueOf(featJson.get("instructionType").getAsString());
		this.sprintDoorOpen = featJson.get("sprintDoorOpen").getAsBoolean();
		this.inFail = featJson.get("inFail").getAsBoolean();
		this.failCount = featJson.get("failCount").getAsInt();
		this.setAng = featJson.get("setAng").getAsBoolean();
	}
}
