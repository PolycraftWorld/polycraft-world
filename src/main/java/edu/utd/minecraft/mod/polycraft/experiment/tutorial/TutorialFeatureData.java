package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.client.gui.exp.creation.GuiExpCreator;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.util.RenderBox;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TutorialFeatureData extends TutorialFeature{
	private BlockPos pos2;
	private String data;
	
	//GuiFields for Parameters
	@SideOnly(Side.CLIENT)
	protected GuiPolyNumField xPos2Field, yPos2Field, zPos2Field;
	@SideOnly(Side.CLIENT)
	protected GuiTextField dataField;
	
	private RenderBox rendBox;
	
	public TutorialFeatureData() {}
	
	public TutorialFeatureData(String name, BlockPos pos, BlockPos pos2){
		super(name, pos, Color.YELLOW);
		this.pos2 = pos2;
		super.featureType = TutorialFeatureType.DATA;
		this.canProceed = true;
		this.data = " ";
	}

	@Override
	public BlockPos getPos2() {
		return pos2;
	}

	@Override
	public void setPos2(BlockPos pos2) {
		this.pos2 = pos2;
	}
	
	@Override
	public void preInit(ExperimentTutorial exp) {
		super.preInit(exp);
		pos2 = pos2.add(exp.posOffset.xCoord,
				exp.posOffset.yCoord,
				exp.posOffset.zCoord);

//		if(this.name.startsWith("dest")) {
//			if(exp.world.getBlockState(pos.add(0,-1,0)).getBlock() != Blocks.lapis_block) {
//				exp.world.setBlockState(pos.add(0,-1,0), Blocks.lapis_block.getDefaultState());
//			}
//		}
		}
	
	@Override
	public void init() {
		super.init();
		x1 = Math.min(pos.getX(), pos2.getX());
		x2 = Math.max(pos.getX() + Integer.signum((int)pos.getX()), pos2.getX() + Integer.signum((int)pos2.getX()));	//increase value magnitude
		y1 = Math.min(pos.getY(), pos2.getY());
		y2 = Math.max(pos.getY(), pos2.getY()) + 1;	//Shouldn't have to worry if y coord is negative
		z1 = Math.min(pos.getZ(), pos2.getZ());
		z2 = Math.max(pos.getZ() + Integer.signum((int)pos.getZ()), pos2.getZ() + Integer.signum((int)pos2.getZ()));	//increase value magnitude

	}
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		return;	//do nothing
	}
	
	@Override
	public void updateValues() {
		this.pos2 = new BlockPos(Integer.parseInt(xPos2Field.getText()),
							Integer.parseInt(yPos2Field.getText()),
							Integer.parseInt(zPos2Field.getText()));
		this.data = dataField.getText();
		this.save();
		super.updateValues();
	}
	
	@Override
	public void buildGuiParameters(GuiExpCreator guiDevTool, int x_pos, int y_pos) {
		// TODO Auto-generated method stub
		super.buildGuiParameters(guiDevTool, x_pos, y_pos);
		FontRenderer fr = guiDevTool.getFontRenderer();
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
        
        y_pos += 15;
		
		guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Data: "));
        dataField = new GuiTextField(420, fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .6), 10);
        dataField.setMaxStringLength(64);
        dataField.setText(data);
        dataField.setTextColor(16777215);
        dataField.setVisible(true);
        dataField.setCanLoseFocus(true);
        dataField.setFocused(false);
        guiDevTool.textFields.add(dataField);
	}
	
	@Override
	public void render(Entity entity) {
		if (entity.worldObj.isRemote) {
			String[] args = data.split(" ");
			if(true) {
				if(data.contains("render")) {
					rendBox = new RenderBox(pos.getX(), pos.getZ(), pos2.getX(), pos2.getZ(), Math.min(pos.getY(), pos2.getY()), Math.abs(pos.getY() - pos2.getY()), 20);
					rendBox.setSolid(true);
					rendBox.setColor(Color.black);
					rendBox.renderFill(entity);
				}
			}
		}
	}
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		int[] pos = {(int)this.pos2.getX(), (int)this.pos2.getY(), (int)this.pos2.getZ()};
		nbt.setIntArray("pos2",pos);
		nbt.setString("data", data);
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		int[] featPos2=nbtFeat.getIntArray("pos2");
		this.pos2=new BlockPos(featPos2[0], featPos2[1], featPos2[2]);
		this.data = nbtFeat.getString("data");
	}
	
	@Override
	public JsonObject saveJson()
	{
		super.saveJson();
		jobj.add("pos2", blockPosToJsonArray(pos2));
		jobj.addProperty("data", data);
		return jobj;
	}
	
	@Override
	public void loadJson(JsonObject featJson)
	{
		super.loadJson(featJson);
		this.pos2 = blockPosFromJsonArray(featJson.get("pos2").getAsJsonArray());
		this.data = featJson.get("data").getAsString();
	}
	
}
