package edu.utd.minecraft.mod.polycraft.client.gui.experiment;

import java.awt.Color;

import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.client.gui.exp.creation.GuiExpCreator;
import edu.utd.minecraft.mod.polycraft.experiment.old.Experiment1PlayerCTB;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentCTB;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentFlatCTB;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentParameters;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ExperimentDef{
	protected String name;
	protected int teamCount;
	protected int playersPerTeam;
	protected ExperimentType expType;	//used when loading/saving
	protected int id = -1;	//id -1 by default for new expDefs
	protected ExperimentParameters params = new ExperimentParameters(true);
	protected boolean isEnabled = false;
	
	//Gui Parameters
	@SideOnly(Side.CLIENT)
	protected GuiTextField nameField;
	@SideOnly(Side.CLIENT)
	protected GuiPolyNumField teamsField, playersPerTeamField, zPosField;
	

	protected NBTTagCompound nbt = new NBTTagCompound();
	
	
	public enum ExperimentType{
		CTB_FLAT_1_PLAYER(Experiment1PlayerCTB.class.getName()),
		CTB_STOOP(ExperimentCTB.class.getName()),
		CTB_FLAT(ExperimentFlatCTB.class.getName());
		
		public String className;
		
		ExperimentType(String className) {
			this.className = className;
		}
		
		public ExperimentType next() {
		    if (ordinal() == values().length - 1)
		    	return values()[0];
		    return values()[ordinal() + 1];
		}
		
		public ExperimentType previous() {
		    if (ordinal() == 0)
		    	return values()[values().length - 1];
		    return values()[ordinal() - 1];
		}
	}
	
	public ExperimentDef(){}
	
	
	// addExp type schem name teams playersPerTeam
	public ExperimentDef(String name, int teamCount, int playersPerTeam ) {
		this.name = name;
		this.teamCount = teamCount;
		this.playersPerTeam = playersPerTeam;
		this.expType = ExperimentType.CTB_FLAT;
	}

	public ExperimentType getExpType() {
		return expType;
	}
	
	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public ExperimentParameters getParams() {
		return this.params;
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}
	
	
	
	
	public int getTeamCount() {
		return teamCount;
	}


	public void setTeamCount(int teamCount) {
		this.teamCount = teamCount;
	}


	public int getPlayersPerTeam() {
		return playersPerTeam;
	}


	public void setPlayersPerTeam(int playersPerTeam) {
		this.playersPerTeam = playersPerTeam;
	}


	/*
	 * Called to update values based on edits done in GuiDevTool
	 * Values are pulled from parameter fields
	 */
	public void updateValues() {
		this.name = nameField.getText();
		this.teamCount = Integer.parseInt(teamsField.getText());
		this.playersPerTeam = Integer.parseInt(playersPerTeamField.getText());
	}
	
	public void buildGuiParameters(GuiExperimentManager guiExpManager , int x_pos, int y_pos) {
		FontRenderer fr = guiExpManager.getFontRenderer();
		
		//Name Field
		nameField = new GuiTextField(400, fr, x_pos + 5, y_pos + 30, (int) (guiExpManager.X_WIDTH * .9), 14);
		nameField.setMaxStringLength(32);
		nameField.setText(name);
		nameField.setTextColor(16777215);
		nameField.setVisible(true);
		nameField.setCanLoseFocus(true);
		nameField.setFocused(true);
        guiExpManager.textFields.add(nameField);	//add name field to textfields (This is the list that GuiDevTool uses to render textFields)
        //add some labels for position fields 
        guiExpManager.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Teams:"));
        //add position text fields
        teamsField = new GuiPolyNumField(fr, x_pos + 40, y_pos + 49, (int) (guiExpManager.X_WIDTH * .1), 10);
        teamsField.setMaxStringLength(32);
        teamsField.setText("2");
        teamsField.setTextColor(16777215);
        teamsField.setVisible(true);
        teamsField.setCanLoseFocus(true);
        teamsField.setFocused(false);
        guiExpManager.textFields.add(teamsField);
        guiExpManager.labels.add(new GuiPolyLabel(fr, x_pos +76, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Players/Team:"));
        playersPerTeamField = new GuiPolyNumField(fr, x_pos + 150, y_pos + 49, (int) (guiExpManager.X_WIDTH * .1), 10);
        playersPerTeamField.setMaxStringLength(32);
        playersPerTeamField.setText("2");
        playersPerTeamField.setTextColor(16777215);
        playersPerTeamField.setVisible(true);
        playersPerTeamField.setCanLoseFocus(true);
        playersPerTeamField.setFocused(false);
        guiExpManager.textFields.add(playersPerTeamField);
	}
	
	public NBTTagCompound save()
	{
		nbt = new NBTTagCompound();	//erase current nbt so we don't get duplicates?

		nbt.setString("name", this.name);
		nbt.setString("type", expType.name());
		nbt.setInteger("teamCount", teamCount);
		nbt.setInteger("playersPerTeam", playersPerTeam);
		nbt.setInteger("id", id);
		nbt.setTag("params", params.save());
		nbt.setBoolean("isEnabled", isEnabled);
		return nbt;
	}
	
	public void load(NBTTagCompound nbtExpDef)
	{
		int featPos[]=nbtExpDef.getIntArray("pos");
		this.name = nbtExpDef.getString("name");
		this.expType = ExperimentType.valueOf(nbtExpDef.getString("type"));
		this.teamCount = nbtExpDef.getInteger("teamCount");
		this.playersPerTeam = nbtExpDef.getInteger("playersPerTeam");
		this.id = nbtExpDef.getInteger("id");
		this.params.load(nbtExpDef.getCompoundTag("params"));
		this.isEnabled = nbtExpDef.getBoolean("isEnabled");
	}

}
