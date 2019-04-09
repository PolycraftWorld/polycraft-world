package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiDevTool;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyButtonCycle;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class TutorialFeatureInstruction extends TutorialFeature{
	public enum InstructionType{
		MOUSE,
		WASD,
		JUMP,
		SPRINT,
		INVENTORY,
		PLACE_BLOCKS,
		KBB,
		CRAFT_FKB
	};
	private InstructionType type;
	
	@SideOnly(Side.CLIENT)
	GuiPolyButtonCycle<TutorialFeatureInstruction.InstructionType> btnInstructionType;
	
	public boolean sprintDoorOpen;
	
	public TutorialFeatureInstruction() {}
	
	public TutorialFeatureInstruction(String name, Vec3 pos, InstructionType type){
		super(name, pos, Color.MAGENTA);
		this.type = type;
		this.featureType = TutorialFeatureType.INSTRUCTION;
		this.sprintDoorOpen=false;
	}
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		switch(type) {
		case CRAFT_FKB:
			super.onServerTickUpdate(exp);
			break;
		case INVENTORY:
			super.onServerTickUpdate(exp);
			break;
		case JUMP:
			super.onServerTickUpdate(exp);
			break;
		case KBB:
			super.onServerTickUpdate(exp);
			break;
		case MOUSE:
			//super.onServerTickUpdate(exp);
			break;
		case PLACE_BLOCKS:
			super.onServerTickUpdate(exp);
			break;
		case SPRINT:
			super.onServerTickUpdate(exp);
			for(EntityPlayer player: exp.scoreboard.getPlayersAsEntity()) {
				if(player!=null)
				{
					if(player.isSprinting()) 
					{
						int x=(int) exp.pos1.xCoord;
						int z=(int) exp.pos1.zCoord;
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
						int x=(int) exp.pos1.xCoord;
						int z=(int) exp.pos1.zCoord;
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
		case WASD:
			super.onServerTickUpdate(exp);
			break;
		default:
			break;
		
		}
	}
	
	
	@Override
	public void render(Entity entity) {
		switch(type) {
		case CRAFT_FKB:
			break;
		case INVENTORY:
			super.render(entity);
			TutorialRender.renderTutorialAccessInventory(entity);
			break;
		case JUMP:
			break;
		case KBB:
			super.render(entity);
			TutorialRender.renderTutorialUseKBB(entity);
			break;
		case MOUSE:
			super.render(entity);	//super needs to run before overlay render. Because I don't know how to undo mc.entityRenderer.setupOverlayRendering()
			TutorialRender.start(entity);
			if(TutorialRender.renderTutorialTurnLeft(entity))
			{
				if(TutorialRender.renderTutorialTurnRight(entity))
				{
					this.canProceed=true;
					this.isDone=true;
					this.isDirty=true;
				}
			}
			break;
		case PLACE_BLOCKS:
			break;
		case SPRINT:
			super.render(entity);	//super needs to run before overlay render. Because I don't know how to undo mc.entityRenderer.setupOverlayRendering()
			//TutorialRender.renderTutorialSprint(entity);
			break;
		case WASD:
			super.render(entity);	//super needs to run before overlay render. Because I don't know how to undo mc.entityRenderer.setupOverlayRendering()
			TutorialRender.renderTutorialWalkForward(entity);
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
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		InstructionType tmp = null;
		type=tmp.valueOf(nbtFeat.getString("instructionType"));
	}
}
