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
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;

public class TutorialFeatureInstruction extends TutorialFeature{
	public enum InstructionType{
		MOUSE_LEFT,
		MOUSE_RIGHT,
		WASD,
		JUMP,
		SPRINT,
		JUMP_SPRINT,
		FAIL,
		INVENTORY1,
		INVENTORY2,
		INVENTORY3,
		CRAFT_PICK,
		PLACE_BLOCKS,
		BREAK_BLOCKS,
		KBB,
		CRAFT_FKB
	};
	private InstructionType type;
	
	@SideOnly(Side.CLIENT)
	GuiPolyButtonCycle<TutorialFeatureInstruction.InstructionType> btnInstructionType;
	
	public boolean sprintDoorOpen;
	public int failCount;
	public boolean inFail;
	public boolean setAng;
	RenderBox box;
	private final static String KBB = "1hv";
	
	public TutorialFeatureInstruction() {}
	
	
	
	public TutorialFeatureInstruction(String name, Vec3 pos, InstructionType type){
		super(name, pos, Color.MAGENTA);
		this.type = type;
		this.featureType = TutorialFeatureType.INSTRUCTION;
		this.sprintDoorOpen=false;
		this.failCount=0;
		this.inFail=false;
		this.setAng=false;

	}
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		switch(type) {
		case CRAFT_FKB:
			super.onServerTickUpdate(exp);
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
								this.isDone=true;
								this.canProceed=true;
								this.isDirty=true;
								player.addChatMessage(new ChatComponentText("You got the building materials!"));
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
								this.isDone=true;
								this.canProceed=true;
								this.isDirty=true;
								player.addChatMessage(new ChatComponentText("You got the crafting materials!"));
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
								this.isDone=true;
								this.canProceed=true;
								this.isDirty=true;
								player.addChatMessage(new ChatComponentText("You got the KnockBack Bomb!"));
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
								this.isDone=true;
								this.canProceed=true;
								this.isDirty=true;
								player.addChatMessage(new ChatComponentText("You crafted an Iron Pickaxe!"));
							}
						}
					}
				}
			}
			break;
		case JUMP:
			super.onServerTickUpdate(exp);
			break;
		case KBB:
			//super.onServerTickUpdate(exp);
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(exp.world.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(Math.min(x1, x1), Math.min(y1, y1-1), Math.min(z1, z1),
						Math.max(x1, x1+13), Math.max(y1, y1+8), Math.max(z1, z1+13))).isEmpty()) {
					canProceed = true;
					isDone = true;
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
			if(!(exp.world.getBlock((int)x1, (int)y1, (int)z1)==Blocks.air)) {
				canProceed = true;
				isDone = true;
			}
			break;
		case BREAK_BLOCKS:
			//super.onServerTickUpdate(exp);
			if((exp.world.getBlock((int)x1, (int)y1, (int)z1)==Blocks.air)) {
				canProceed = true;
				isDone = true;
			}
			break;
		case SPRINT:
			super.onServerTickUpdate(exp);
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(player!=null)
				{
					if(player.isSprinting()) 
					{
						int x=(int) exp.pos.xCoord;
						int z=(int) exp.pos.zCoord;
						int xOffset=-37;
						int zOffset=-14;
						exp.world.setBlock(x+xOffset, 109, z+zOffset, Blocks.air);
						exp.world.setBlock(x+xOffset, 110, z+zOffset, Blocks.air);
						exp.world.setBlock(x+xOffset, 109, z+zOffset+1, Blocks.air);
						exp.world.setBlock(x+xOffset, 110, z+zOffset+1, Blocks.air);
						//MovePistons or Turn Blocks to Air (Relative to Map position)
						this.sprintDoorOpen=true;
					}
					else
					{
						int x=(int) exp.pos.xCoord;
						int z=(int) exp.pos.zCoord;
						int xOffset=-37;
						int zOffset=-14;
						exp.world.setBlock(x+xOffset, 109, z+zOffset, Blocks.planks);
						exp.world.setBlock(x+xOffset, 110, z+zOffset, Blocks.planks);
						exp.world.setBlock(x+xOffset, 109, z+zOffset+1, Blocks.planks);
						exp.world.setBlock(x+xOffset, 110, z+zOffset+1, Blocks.planks);
						//MovePistons or Turn Air to Blocks
						this.sprintDoorOpen=false;
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
					if(exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(Math.min(x1, x1-2), Math.min(y1, y2), Math.min(z1, z1-3),
							Math.max(x1, x2), Math.max(y1, y1+1), Math.max(z1, z2))).contains(player)) {
						this.inFail=true;				
					}
				}
			}
			else
			{
				for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
					if(!exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(Math.min(x1, x1-2), Math.min(y1, y2), Math.min(z1, z1-3),
							Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))).contains(player)) {
						this.inFail=false;
						this.failCount++;
						player.addChatMessage(new ChatComponentText("You missed your jump "+failCount+" time(s)"));
						
					}
				}
			}
			if(failCount>=2)
			{
				for(int x=0;x>=-2;x--)
					for(int z=0;z>=-3;z--)
						exp.world.setBlock((int)x1+x, (int)y1, (int)z1+z, Blocks.packed_ice);
			}
			break;
		case WASD:
			super.onServerTickUpdate(exp);
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
			break;
		case INVENTORY1:
			super.render(entity);

			if(entity instanceof EntityPlayer)	
				player=(EntityPlayer)(entity);
				
				if(player!=null)
				{
					if(player.openContainer!=null) 
					{
						if(player.openContainer!=player.inventoryContainer)
						{
							//TutorialRender.instance.renderTutorialManageInventory(entity);
							//player.addChatMessage(new ChatComponentText("You have opened a Container"));
						}
						else
						{
							TutorialRender.instance.renderTutorialAccessInventory(entity);
							//Gui to instruct player to click on the chest
						}
					}
				}
			break;
		case INVENTORY2:
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
		case INVENTORY3:
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
		case JUMP:
			break;
		case KBB:
			//super.render(entity);
			TutorialRender.instance.renderTutorialUseKBB(entity);
			break;
		case MOUSE_LEFT:
			super.render(entity);	//super needs to run before overlay render. Because I don't know how to undo mc.entityRenderer.setupOverlayRendering()
			if(!this.setAng)
			{
				TutorialRender.instance.setAng(entity);
				this.setAng=true;
			}
			if(TutorialRender.instance.renderTutorialTurnLeft(entity))
			{
				this.canProceed=true;
				this.isDone=true;
				this.setAng=false;
				this.isDirty=true;
			}
			break;
		case MOUSE_RIGHT:
			super.render(entity);	//super needs to run before overlay render. Because I don't know how to undo mc.entityRenderer.setupOverlayRendering()
			if(!this.setAng)
			{
				TutorialRender.instance.setAng(entity);
				this.setAng=true;
			}
			if(TutorialRender.instance.renderTutorialTurnRight(entity))
			{
				this.canProceed=true;
				this.isDone=true;
				this.setAng=false;
				this.isDirty=true;
			}
			break;
		case PLACE_BLOCKS:
			//super.render(entity);
			this.box.render(entity);
			break;
		case BREAK_BLOCKS:
			//super.render(entity);
			this.box.renderFill(entity);
			break;
		case SPRINT:
			super.render(entity);	//super needs to run before overlay render. Because I don't know how to undo mc.entityRenderer.setupOverlayRendering()
			//TutorialRender.instance.renderTutorialSprint(entity);
			break;
		case JUMP_SPRINT:
			super.render(entity);
			break;
		case FAIL:
			super.render(entity);
			break;
		case WASD:
			super.render(entity);	//super needs to run before overlay render. Because I don't know how to undo mc.entityRenderer.setupOverlayRendering()
			TutorialRender.instance.renderTutorialWalkForward(entity);
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
		nbt.setBoolean("setAng", setAng);
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
		this.setAng=nbtFeat.getBoolean("setAng");
		
		this.box= new RenderBox(this.getPos().xCoord, this.getPos().zCoord, this.getPos2().xCoord, this.getPos2().zCoord, 
				Math.min(this.getPos().yCoord, this.getPos2().yCoord), Math.max(Math.abs(this.getPos().yCoord- this.getPos2().yCoord), 1), 1, this.getName());
		box.setColor(this.getColor());
	}
}
