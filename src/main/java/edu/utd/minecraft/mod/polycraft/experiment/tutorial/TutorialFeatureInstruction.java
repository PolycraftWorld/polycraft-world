package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiDevTool;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyButtonCycle;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.entity.Physics.EntityGravelCannonBall;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.inventory.cannon.GravelCannonInventory;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

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
		FAIL,
		INVENTORY1,
		INVENTORY2,
		INVENTORY3,
		INVENTORY4,
		CRAFT_PICK,
		PLACE_BLOCKS,
		BREAK_BLOCKS,
		CART_START,
		CART_END,
		KBB,
		CRAFT_FKB,
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
	
	private Vec3 pos1;
	private Vec3 pos2;
	
	public TutorialFeatureInstruction() {}
	
	
	
	public TutorialFeatureInstruction(String name, Vec3 pos, InstructionType type){
		super(name, pos, Color.MAGENTA);
		this.type = type;
		this.featureType = TutorialFeatureType.INSTRUCTION;
		this.sprintDoorOpen=false;
		this.failCount=0;
		this.inFail=false;
		this.setAng=false;
		this.tickWait=0;
		this.pos1= pos1.createVectorHelper(pos.xCoord, pos.yCoord, pos.zCoord);
		this.pos2= pos2.createVectorHelper(pos.xCoord, pos.yCoord, pos.zCoord);

	}
	
	

	
	@Override
	public void preInit(ExperimentTutorial exp) {
		super.preInit(exp);
		pos1.xCoord += exp.posOffset.xCoord;
		pos1.yCoord += exp.posOffset.yCoord;
		pos1.zCoord += exp.posOffset.zCoord;
		pos2.xCoord += exp.posOffset.xCoord;
		pos2.yCoord += exp.posOffset.yCoord;
		pos2.zCoord += exp.posOffset.zCoord;
		
	}
	
	@Override
	public void updateValues() {
		this.pos1.xCoord = Integer.parseInt(xPos1Field.getText());
		this.pos1.yCoord = Integer.parseInt(yPos1Field.getText());
		this.pos1.zCoord = Integer.parseInt(zPos1Field.getText());
		this.pos2.xCoord = Integer.parseInt(xPos2Field.getText());
		this.pos2.yCoord = Integer.parseInt(yPos2Field.getText());
		this.pos2.zCoord = Integer.parseInt(zPos2Field.getText());
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
							Item fkbb =  GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(FREEZE_KBB));
							if(player.inventory.hasItem(fkbb))
							{
								this.canProceed=true;
								this.isDirty=true;
								player.addChatMessage(new ChatComponentText("You got the Frozen Knockback Bomb!"));
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
		case KBB:
			//super.onServerTickUpdate(exp);
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(exp.world.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(
						Math.min(pos1.xCoord, pos2.xCoord), Math.min(pos1.yCoord, pos2.yCoord), Math.min(pos1.zCoord, pos2.zCoord),
						Math.max(pos1.xCoord, pos2.xCoord), Math.max(pos1.yCoord, pos2.yCoord), Math.max(pos1.zCoord, pos2.zCoord))).isEmpty()) {
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
			if((exp.world.getBlock((int)x1, (int)y1, (int)z1)==Blocks.planks) || (exp.world.getBlock((int)x1, (int)y1, (int)z1)==Blocks.spruce_stairs)) {
				this.canProceed = true;
				this.complete(exp);
			}
			break;
		case BREAK_BLOCKS:
			//super.onServerTickUpdate(exp);
			if((exp.world.getBlock((int)x1, (int)y1, (int)z1)==Blocks.air)) {
				this.canProceed = true;
				this.complete(exp);
			}
			break;
		case CART_START:
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
						Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))).contains(player)) {
					
					EntityMinecart entityminecart = EntityMinecart.createMinecart(exp.world, (double)((float)x1 + 0.5F), (double)((float)y1 + 0.5F), (double)((float)z1 + 0.5F), 0);
					exp.world.spawnEntityInWorld(entityminecart);
					player.mountEntity(entityminecart);
					
					this.canProceed = true;
					this.complete(exp);
				}
			}
			break;
		case CART_END:
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
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
						for(int x=(int)Math.min(pos1.xCoord, pos2.xCoord);x<=(int)Math.max(pos1.xCoord, pos2.xCoord);x++)
						{
							for(int y=(int)Math.min(pos1.yCoord, pos2.yCoord);y<=(int)Math.max(pos1.yCoord, pos2.yCoord);y++)
							{
								for(int z=(int)Math.min(pos1.zCoord, pos2.zCoord);z<=(int)Math.max(pos1.zCoord, pos2.zCoord);z++)
								{
									exp.world.setBlock(x, y, z, Blocks.air);
								}
							}
						}
						this.sprintDoorOpen=true;
					}
					else
					{
						for(int x=(int)Math.min(pos1.xCoord, pos2.xCoord);x<=(int)Math.max(pos1.xCoord, pos2.xCoord);x++)
						{
							for(int y=(int)Math.min(pos1.yCoord, pos2.yCoord);y<=(int)Math.max(pos1.yCoord, pos2.yCoord);y++)
							{
								for(int z=(int)Math.min(pos1.zCoord, pos2.zCoord);z<=(int)Math.max(pos1.zCoord, pos2.zCoord);z++)
								{
									exp.world.setBlock(x, y, z, Blocks.planks);
								}
							}
						}
						this.sprintDoorOpen=false;
					}
					if(exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
							Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))).contains(player)) {
						this.canProceed = true;
						this.complete(exp);
						for(int x=(int)Math.min(pos1.xCoord, pos2.xCoord);x<=(int)Math.max(pos1.xCoord, pos2.xCoord);x++)
						{
							for(int y=(int)Math.min(pos1.yCoord, pos2.yCoord);y<=(int)Math.max(pos1.yCoord, pos2.yCoord);y++)
							{
								for(int z=(int)Math.min(pos1.zCoord, pos2.zCoord);z<=(int)Math.max(pos1.zCoord, pos2.zCoord);z++)
								{
									exp.world.setBlock(x, y, z, Blocks.planks);
								}
							}
						}
					}
				}
			}
			break;
		case JUMP_SPRINT:
			super.onServerTickUpdate(exp);
			break;
		case FAIL:
			this.canProceed = true;
			if(!inFail)
			{
				for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
					if(exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(
							Math.min(pos1.xCoord, pos2.xCoord), Math.min(pos1.yCoord, pos2.yCoord), Math.min(pos1.zCoord, pos2.zCoord),
							Math.max(pos1.xCoord, pos2.xCoord), Math.max(pos1.yCoord, pos2.yCoord), Math.max(pos1.zCoord, pos2.zCoord))).contains(player)) {
						this.inFail=true;				
					}
				}
			}
			else
			{
				for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
					if(!exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(
							Math.min(pos1.xCoord, pos2.xCoord), Math.min(pos1.yCoord, pos2.yCoord), Math.min(pos1.zCoord, pos2.zCoord),
							Math.max(pos1.xCoord, pos2.xCoord), Math.max(pos1.yCoord, pos2.yCoord), Math.max(pos1.zCoord, pos2.zCoord))).contains(player)) {
						this.inFail=false;
						this.failCount++;
						player.addChatMessage(new ChatComponentText("You missed your jump "+failCount+" time(s)"));
						
					}
				}
			}
			if(failCount>=2)
			{
				for(int x=(int)Math.min(pos1.xCoord, pos2.xCoord);x<=(int)Math.max(pos1.xCoord, pos2.xCoord);x++)
				{
					for(int y=(int)Math.min(pos1.yCoord, pos2.yCoord);y<=(int)Math.max(pos1.yCoord, pos2.yCoord);y++)
					{
						for(int z=(int)Math.min(pos1.zCoord, pos2.zCoord);z<=(int)Math.max(pos1.zCoord, pos2.zCoord);z++)
						{
							exp.world.setBlock(x, y-1, z,  Blocks.packed_ice);
						}
					}
				}
				this.complete(exp);
			}
			break;
		case WASD:
			//super.onServerTickUpdate(exp);
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
						Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))).contains(player)) {
					this.canProceed = true;
					this.complete(exp);
					for(int x=(int)Math.min(pos1.xCoord, pos2.xCoord);x<=(int)Math.max(pos1.xCoord, pos2.xCoord);x++)
					{
						for(int y=(int)Math.min(pos1.yCoord, pos2.yCoord);y<=(int)Math.max(pos1.yCoord, pos2.yCoord);y++)
						{
							for(int z=(int)Math.min(pos1.zCoord, pos2.zCoord);z<=(int)Math.max(pos1.zCoord, pos2.zCoord);z++)
							{
								if(exp.world.getBlock(x, y, z)==Blocks.iron_door)
								{
									BlockDoor door1 = (BlockDoor)exp.world.getBlock(x, y, z);
									door1.func_150014_a(exp.world, x, y, z, true);
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
				if(!exp.world.getEntitiesWithinAABB(EntityGravelCannonBall.class, AxisAlignedBB.getBoundingBox(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
						Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))).isEmpty()) {
					
					isDone = true;
					exp.scoreboard.updateScore(exp.scoreboard.getTeams().get(0), 1);
					this.score= Math.round((exp.scoreboard.getTeamScores().get(exp.scoreboard.getTeams().get(0))));
					//player.addChatMessage(new ChatComponentText("You hit "+score+" target(s)"));
					
					
					for(int x=(int)Math.min(pos1.xCoord, pos2.xCoord);x<=(int)Math.max(pos1.xCoord, pos2.xCoord);x++)
					{
						for(int y=(int)Math.min(pos1.yCoord, pos2.yCoord);y<=(int)Math.max(pos1.yCoord, pos2.yCoord);y++)
						{
							for(int z=(int)Math.min(pos1.zCoord, pos2.zCoord);z<=(int)Math.max(pos1.zCoord, pos2.zCoord);z++)
							{
								if(exp.world.getBlock(x, y, z)==GameData.getBlockRegistry().getObject(PolycraftMod.getAssetName(BlockNaturalRubber)))
								{
									exp.world.setBlockMetadataWithNotify(x, y, z, 2, 4);
									exp.world.markBlockForUpdate(x, y, z);
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
				GravelCannonInventory cannon1 = (GravelCannonInventory)exp.world.getTileEntity((int)x1, (int)y1, (int)z1);
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
				GravelCannonInventory cannon1 = (GravelCannonInventory)exp.world.getTileEntity((int)x1, (int)y1, (int)z1);
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
				GravelCannonInventory cannon1 = (GravelCannonInventory)exp.world.getTileEntity((int)x1, (int)y1, (int)z1);
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
			TutorialRender.instance.renderTutorialDrawString("Craft a freezing KnockBack Bomb",5,5);
			player=null;
			if(entity instanceof EntityPlayer)	
				player=(EntityPlayer)(entity);
				
				if(player!=null)
				{
					if(player.openContainer!=null) 
					{
						if(player.openContainer!=player.inventoryContainer)
						{
							//TutorialRender.instance.renderTutorialAccessInventory(entity);
							//player.addChatMessage(new ChatComponentText("You have opened a Container"));
						}
					}
					else
					{
						//TutorialRender.instance.renderTutorialOpenChest(entity);
						//Gui to instruct player to click on the chest
					}
				}
			break;
		case INVENTORY1:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Take the contentes of the chest",5,5);
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
			TutorialRender.instance.renderTutorialDrawString("Take the contentes of the chest",5,5);
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
			TutorialRender.instance.renderTutorialDrawString("Take the contentes of the chest",5,5);
			player=null;
			if(entity instanceof EntityPlayer)	
				player=(EntityPlayer)(entity);
				
				if(player!=null)
				{
					if(player.openContainer!=null) 
					{
						if(player.openContainer!=player.inventoryContainer)
						{
							//TutorialRender.instance.renderTutorialAccessInventory(entity);
							//player.addChatMessage(new ChatComponentText("You have opened a Container"));
						}
					}
					else
					{
						//TutorialRender.instance.renderTutorialOpenChest(entity);
						//Gui to instruct player to click on the chest
					}
				}
			break;
		case INVENTORY4:
			super.render(entity);
			player=null;
			if(entity instanceof EntityPlayer)	
				player=(EntityPlayer)(entity);
				
				if(player!=null)
				{
					if(player.openContainer!=null) 
					{
						if(player.openContainer!=player.inventoryContainer)
						{
							//TutorialRender.instance.renderTutorialAccessInventory(entity);
							//player.addChatMessage(new ChatComponentText("You have opened a Container"));
						}
					}
					else
					{
						//TutorialRender.instance.renderTutorialOpenChest(entity);
						//Gui to instruct player to click on the chest
					}
				}
			break;
		case CRAFT_PICK:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Craft an Iron Pickaxe",5,5);
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
			TutorialRender.instance.renderTutorialDrawString("Press 'Space' and 'W' to jump past obstacles",5,5);
			TutorialRender.instance.renderTutorialJump(entity);
			break;
		case FLOAT1:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Hold 'Space' and 'W' to float and move in water",5,5);
			TutorialRender.instance.renderTutorialFloatJungle(entity);
			break;
		case FLOAT2:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Hold 'Space' and 'W' to float and move in water",5,5);
			TutorialRender.instance.renderTutorialFloatSwamp(entity);
			break;
		case KBB:
			super.render(entity);
			//super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Use the KnockBack Bomb on the mobs",5,5);
			TutorialRender.instance.renderTutorialUseKBB(entity);
			break;
		case MOUSE_LEFT:
			super.render(entity);	//super needs to run before overlay render. Because I don't know how to undo mc.entityRenderer.setupOverlayRendering()
			TutorialRender.instance.renderTutorialDrawString("Turn the mouse to the left",5,5);
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
			TutorialRender.instance.renderTutorialDrawString("Turn the mouse to the right",5,5);
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
			TutorialRender.instance.renderTutorialDrawString("Place the blocks in the highlighted location",150,5);
			TutorialRender.instance.renderTutorialPlacingBlocks(entity);
			break;
		case BREAK_BLOCKS:
			//super.render(entity);
			this.box.renderFill(entity);
			TutorialRender.instance.renderTutorialDrawString("Break the Highlighted blocks",150,5);
			TutorialRender.instance.renderTutorialMining(entity);
			break;
		case CART_START:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Walk to the start of the ride",5,5);
			break;
		case CART_END:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Keep hands and feet in the minecart at all times",5,5);
			break;
		case SPRINT:
			super.render(entity);	//super needs to run before overlay render. Because I don't know how to undo mc.entityRenderer.setupOverlayRendering()
			TutorialRender.instance.renderTutorialDrawString("Sprint to open the path",5,5);
			TutorialRender.instance.renderTutorialSprint(entity);
			break;
		case JUMP_SPRINT:
			super.render(entity);
			TutorialRender.instance.renderTutorialDrawString("Sprint and jump to get to the other side",5,5);
			TutorialRender.instance.renderTutorialSprintJump(entity);
			break;
		case FAIL:
			super.render(entity);
			break;
		case WASD:
			super.render(entity);	//super needs to run before overlay render. Because I don't know how to undo mc.entityRenderer.setupOverlayRendering()
			TutorialRender.instance.renderTutorialDrawString("Press 'W' to walk forward" ,5,5);
			TutorialRender.instance.renderTutorialWalkForward(entity);
			break;
		case HOTBAR:
			break;
		case LOOK:
//			super.render(entity);
//			player=null;
//			if(entity instanceof EntityPlayer)	
//				player=(EntityPlayer)(entity);
//			boolean test=false;
//	        Vec3 vec3 = player.getPosition(1.0F);
//	        Vec3 vec32 = player.getLook(1.0F);
//	        //player.addChatMessage(new ChatComponentText("Anglex: "+vec32.xCoord+" Anglez: "+vec32.zCoord));
//	        
//	        
//	        //Vec3 vec32 = vec3.addVector(vec31.xCoord , vec31.yCoord , vec31.zCoord );
//	        
//	        
//	        Vec3 vec01=null;
//	        vec01 = vec01.createVectorHelper(pos1.xCoord, pos1.yCoord, pos1.zCoord);
//
//			Vec3 vec02 = vec3.addVector(-vec01.xCoord , -vec01.yCoord , -vec01.zCoord);
//			
//			player.addChatMessage(new ChatComponentText("Anglex: "+vec02.xCoord+" Anglez: "+vec02.zCoord));
//			
//			 if(vec32.zCoord<0)
//		     {
//		       // test=true;
//		     }
//			 
//			
//			vec02.yCoord=0;
//			vec32.yCoord=0;
//			
//			vec02=vec02.normalize();
//			vec32=vec32.normalize();
//			
//			
//			double u1=vec02.lengthVector();
//			double u2=vec32.lengthVector();
//			
//			double dot=vec02.dotProduct(vec32);
//			
//			double ang = Math.acos(dot/(u1*u2));
//			
//			
//			if(!test)
//			{
//				ang=-ang;
//			}
//			
//			//player.addChatMessage(new ChatComponentText("Angle: "+ang));
//			TutorialRender.instance.renderTutorialLook(player, ang);
//			
//			
//			
//			
////		    @SideOnly(Side.CLIENT)
////		    public MovingObjectPosition rayTrace(double p_70614_1_, float p_70614_3_)
////		    {
////		        Vec3 vec3 = this.getPosition(p_70614_3_);
////		        Vec3 vec31 = this.getLook(p_70614_3_);
////		        Vec3 vec32 = vec3.addVector(vec31.xCoord * p_70614_1_, vec31.yCoord * p_70614_1_, vec31.zCoord * p_70614_1_);
////		        return this.worldObj.func_147447_a(vec3, vec32, false, false, true);
////		    }
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
	
	@SideOnly(Side.CLIENT)
	@Override
	public void buildGuiParameters(GuiDevTool guiDevTool, int x_pos, int y_pos) {
		// TODO Auto-generated method stub
		super.buildGuiParameters(guiDevTool, x_pos, y_pos);
		
        btnInstructionType = new GuiPolyButtonCycle<TutorialFeatureInstruction.InstructionType>(
        		guiDevTool.buttonCount + 1, x_pos + 10, y_pos + 65, (int) (guiDevTool.X_WIDTH * .9), 20, 
        		"Type",  TutorialFeatureInstruction.InstructionType.WASD);
        guiDevTool.addBtn(btnInstructionType);
        
        addFields(guiDevTool, x_pos, y_pos);
	}
	
	public void addFields(GuiDevTool guiDevTool, int x_pos, int y_pos)
	{
		FontRenderer fr = guiDevTool.getFontRenderer();
		
		y_pos += 45;
		
		guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Pos1"));
		guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +30, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"X:"));
        xPos1Field = new GuiPolyNumField(fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        xPos1Field.setMaxStringLength(32);
        xPos1Field.setText(Integer.toString((int)pos1.xCoord));
        xPos1Field.setTextColor(16777215);
        xPos1Field.setVisible(true);
        xPos1Field.setCanLoseFocus(true);
        xPos1Field.setFocused(false);
        guiDevTool.textFields.add(xPos1Field);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +85, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Y:"));
        yPos1Field = new GuiPolyNumField(fr, x_pos + 95, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        yPos1Field.setMaxStringLength(32);
        yPos1Field.setText(Integer.toString((int)pos1.yCoord));
        yPos1Field.setTextColor(16777215);
        yPos1Field.setVisible(true);
        yPos1Field.setCanLoseFocus(true);
        yPos1Field.setFocused(false);
        guiDevTool.textFields.add(yPos1Field);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +140, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Z:"));
        zPos1Field = new GuiPolyNumField(fr, x_pos + 150, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        zPos1Field.setMaxStringLength(32);
        zPos1Field.setText(Integer.toString((int)pos1.zCoord));
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
        xPos2Field.setText(Integer.toString((int)pos2.xCoord));
        xPos2Field.setTextColor(16777215);
        xPos2Field.setVisible(true);
        xPos2Field.setCanLoseFocus(true);
        xPos2Field.setFocused(false);
        guiDevTool.textFields.add(xPos2Field);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +85, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Y:"));
        yPos2Field = new GuiPolyNumField(fr, x_pos + 95, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        yPos2Field.setMaxStringLength(32);
        yPos2Field.setText(Integer.toString((int)pos2.yCoord));
        yPos2Field.setTextColor(16777215);
        yPos2Field.setVisible(true);
        yPos2Field.setCanLoseFocus(true);
        yPos2Field.setFocused(false);
        guiDevTool.textFields.add(yPos2Field);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +140, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Z:"));
        zPos2Field = new GuiPolyNumField(fr, x_pos + 150, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        zPos2Field.setMaxStringLength(32);
        zPos2Field.setText(Integer.toString((int)pos2.zCoord));
        zPos2Field.setTextColor(16777215);
        zPos2Field.setVisible(true);
        zPos2Field.setCanLoseFocus(true);
        zPos2Field.setFocused(false);
        guiDevTool.textFields.add(zPos2Field);
	}

	public InstructionType getType() {
		return type;
	}

	public void setType(InstructionType type) {
		this.type = type;
	}
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		nbt.setString("instructionType", type.toString());
		nbt.setBoolean("sprintDoorOpen", sprintDoorOpen);
		nbt.setBoolean("inFail", inFail);
		nbt.setInteger("failcount", this.failCount);
		nbt.setBoolean("setAng", setAng);
		int pos1[] = {(int)this.pos1.xCoord, (int)this.pos1.yCoord, (int)this.pos1.zCoord};
		nbt.setIntArray("pos1",pos1);
		int pos2[] = {(int)this.pos2.xCoord, (int)this.pos2.yCoord, (int)this.pos2.zCoord};
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
		this.pos1=Vec3.createVectorHelper(featPos1[0], featPos1[1], featPos1[2]);
		
		int featPos2[]=nbtFeat.getIntArray("pos2");
		this.pos2=Vec3.createVectorHelper(featPos2[0], featPos2[1], featPos2[2]);
		
		this.box= new RenderBox(this.getPos().xCoord, this.getPos().zCoord, this.getPos2().xCoord, this.getPos2().zCoord, 
				Math.min(this.getPos().yCoord, this.getPos2().yCoord), Math.max(Math.abs(this.getPos().yCoord- this.getPos2().yCoord), 1), 1, this.getName());
		box.setColor(this.getColor());
	}
}
