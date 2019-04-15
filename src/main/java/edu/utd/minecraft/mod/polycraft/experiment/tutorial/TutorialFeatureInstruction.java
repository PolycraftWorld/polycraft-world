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
import net.minecraft.util.Vec3;

public class TutorialFeatureInstruction extends TutorialFeature{
	public enum InstructionType{
		MOUSE_LEFT,
		MOUSE_RIGHT,
		WASD,
		JUMP,
		FLOAT,
		SPRINT,
		JUMP_SPRINT,
		FAIL,
		INVENTORY1,
		INVENTORY2,
		INVENTORY3,
		CRAFT_PICK,
		PLACE_BLOCKS,
		BREAK_BLOCKS,
		CART_START,
		CART_END,
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
		case FLOAT:
			super.onServerTickUpdate(exp);
			break;
		case KBB:
			//super.onServerTickUpdate(exp);
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(exp.world.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(
						Math.min(pos1.xCoord, pos2.xCoord), Math.min(pos1.yCoord, pos2.yCoord), Math.min(pos1.zCoord, pos2.zCoord),
						Math.max(pos1.xCoord, pos2.xCoord), Math.min(pos1.yCoord, pos2.yCoord), Math.min(pos1.zCoord, pos2.zCoord))).isEmpty()) {
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
		case CART_START:
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
						Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))).contains(player)) {
					
					EntityMinecart entityminecart = EntityMinecart.createMinecart(exp.world, (double)((float)x1 + 0.5F), (double)((float)y1 + 0.5F), (double)((float)z1 + 0.5F), 0);
					exp.world.spawnEntityInWorld(entityminecart);
					player.mountEntity(entityminecart);
					
					canProceed = true;
					isDone = true;
				}
			}
			break;
		case CART_END:
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
						Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))).contains(player)) {
					//EntityMinecartEmpty minecart=(EntityMinecartEmpty)entityminecart;
					
					player.ridingEntity.setDead();
					player.setPosition(player.posX, player.posY+1, player.posZ);
					canProceed = true;
					isDone = true;
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
						canProceed = true;
						isDone = true;
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
							Math.max(pos1.xCoord, pos2.xCoord), Math.min(pos1.yCoord, pos2.yCoord), Math.min(pos1.zCoord, pos2.zCoord))).contains(player)) {
						this.inFail=true;				
					}
				}
			}
			else
			{
				for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
					if(!exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(
							Math.min(pos1.xCoord, pos2.xCoord), Math.min(pos1.yCoord, pos2.yCoord), Math.min(pos1.zCoord, pos2.zCoord),
							Math.max(pos1.xCoord, pos2.xCoord), Math.min(pos1.yCoord, pos2.yCoord), Math.min(pos1.zCoord, pos2.zCoord))).contains(player)) {
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
							exp.world.setBlock(x, y, z,  Blocks.packed_ice);
						}
					}
				}
				this.isDone=true;
			}
			break;
		case WASD:
			//super.onServerTickUpdate(exp);
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(exp.world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2),
						Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2))).contains(player)) {
					canProceed = true;
					isDone = true;
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
							TutorialRender.instance.renderTutorialManageInventory(entity);
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
			super.render(entity);
			TutorialRender.instance.renderTutorialJump(entity);
			break;
		case FLOAT:
			super.render(entity);
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
		case CART_START:
			super.render(entity);
			break;
		case CART_END:
			super.render(entity);
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
