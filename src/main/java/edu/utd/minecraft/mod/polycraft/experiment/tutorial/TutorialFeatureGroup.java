package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiDevTool;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyButtonCycle;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class TutorialFeatureGroup extends TutorialFeature{
	
	public enum GroupType{
		START,
		END
	};
	
	private GroupType type;
	
	//GuiFields for Parameters
	GuiPolyButtonCycle<TutorialFeatureGroup.GroupType> btnInstructionType;
	
	public TutorialFeatureGroup() {}
	
	public TutorialFeatureGroup(String name, Vec3 pos, GroupType type){
		super(name, pos, Color.YELLOW);
		this.type = type;
		super.featureType = TutorialFeatureType.GROUP;
	}
	
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		complete(exp);
	}
	

	
	@Override
	public void updateValues() {
		super.updateValues();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void buildGuiParameters(GuiDevTool guiDevTool, int x_pos, int y_pos) {
		// TODO Auto-generated method stub
		super.buildGuiParameters(guiDevTool, x_pos, y_pos);
		
        btnInstructionType = new GuiPolyButtonCycle<TutorialFeatureGroup.GroupType>(
        		guiDevTool.buttonCount + 1, x_pos + 10, y_pos + 65, (int) (guiDevTool.X_WIDTH * .9), 20, 
        		"Type",  TutorialFeatureGroup.GroupType.START);
        guiDevTool.addBtn(btnInstructionType);
        
        //addFields(guiDevTool, x_pos, y_pos);
	}
	
	
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
	}
}
