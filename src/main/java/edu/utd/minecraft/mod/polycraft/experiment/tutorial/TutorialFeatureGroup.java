package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonCycle;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.client.gui.exp.creation.GuiExpCreator;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeatureInstruction.InstructionType;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TutorialFeatureGroup extends TutorialFeature{
	
	public enum GroupType{
		START,
		END
	};
	
	private GroupType type;
	
	//GuiFields for Parameters
	GuiPolyButtonCycle<TutorialFeatureGroup.GroupType> btnGroupType;
	
	public TutorialFeatureGroup() {}
	
	public TutorialFeatureGroup(String name, BlockPos pos, GroupType type){
		super(name, pos, Color.YELLOW);
		this.type = type;
		super.featureType = TutorialFeatureType.GROUP;
	}
	
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		complete(exp);
	}
	
	public GroupType getType() {
		return type;
	}
	
	//Should only use UpdateValues to update variables
	@Deprecated
	public void setType(GroupType type) {
		this.type = type;
	}

	
	@Override
	public void updateValues() {
		this.type = btnGroupType.getCurrentOption();
		super.updateValues();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void buildGuiParameters(GuiExpCreator guiDevTool, int x_pos, int y_pos) {
		// TODO Auto-generated method stub
		super.buildGuiParameters(guiDevTool, x_pos, y_pos);
		
        btnGroupType = new GuiPolyButtonCycle<TutorialFeatureGroup.GroupType>(
        		guiDevTool.buttonCount + 1, x_pos + 10, y_pos + 65, (int) (guiDevTool.X_WIDTH * .9), 20, 
        		"Type",  TutorialFeatureGroup.GroupType.START);
        guiDevTool.addBtn(btnGroupType);
        
        //addFields(guiDevTool, x_pos, y_pos);
	}
	
	
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		nbt.setString("groupType", type.toString());
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		GroupType tmp = null;
		type=tmp.valueOf(nbtFeat.getString("groupType"));
	}
}
