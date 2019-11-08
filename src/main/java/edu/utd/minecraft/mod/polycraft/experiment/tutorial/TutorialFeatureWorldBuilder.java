package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import edu.utd.minecraft.mod.polycraft.aitools.BotAPI;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonCycle;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.client.gui.exp.creation.GuiExpCreator;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.util.Format;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TutorialFeatureWorldBuilder extends TutorialFeature{
	private BlockPos pos2;
	boolean spawnRand;
	
	//working parameters
	private int count = 30;
	
	//Gui Parameters
	@SideOnly(Side.CLIENT)
	protected GuiPolyNumField pitchField, yawField, xPos2Field, yPos2Field, zPos2Field;
	@SideOnly(Side.CLIENT)
	protected GuiPolyButtonCycle<FeatureType> toggleRandomSpawn;
	
	
	public enum FeatureType{
		TREES,
		HILLS
	}
	
	public TutorialFeatureWorldBuilder() {}
	
	public TutorialFeatureWorldBuilder(String name, BlockPos pos, BlockPos lookDir){
		super(name, pos, Color.GREEN);
		this.pos2 = pos;
		this.spawnRand = false;
		this.featureType = TutorialFeatureType.WORLDGEN;
	}
	
	@Override
	public void preInit(ExperimentTutorial exp) {
		super.preInit(exp);
		pos2 = pos2.add(exp.posOffset.xCoord, exp.posOffset.yCoord, exp.posOffset.zCoord);
	}
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		Random rand = new Random();
		while(count-- > 0) {
			IBlockState iblockstate = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
            IBlockState iblockstate1 = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
            WorldGenerator worldgenerator = new WorldGenTrees(true, 4 + rand.nextInt(7), iblockstate, iblockstate1, false);
            for(int x=0;x<5;x++){	//try to generate the tree 5 times before giving up
            	if(worldgenerator.generate(exp.world, rand, pos.add(Math.random() * (pos2.getX() - pos.getX()), 0, Math.random() * (pos2.getZ() - pos.getZ()))))
            		break;
            }
		}
		
		for(int i = 0; i <= pos2.getX(); i++) {
			for(int k = 0; k <= pos2.getZ(); k++) {
				for(int j = 0; j < 3; j++) {
					if(exp.getWorld().getBlockState(pos.add(i, j, k)).getBlock() == Blocks.leaves)
						exp.getWorld().setBlockToAir(pos.add(i, j, k));
				}
			}
		}
			
		canProceed = true;
		this.complete(exp);
	}
	
	@Override
	public void buildGuiParameters(GuiExpCreator guiDevTool, int x_pos, int y_pos) {
		// TODO Auto-generated method stub
		super.buildGuiParameters(guiDevTool, x_pos, y_pos);
		
		FontRenderer fr = guiDevTool.getFontRenderer();
        
        y_pos += 15;
        
//        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
//        		"Pitch:"));
//        pitchField = new GuiPolyNumField(fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
//        pitchField.setMaxStringLength(32);
//        pitchField.setText(Integer.toString((int)lookDir.getX()));
//        pitchField.setTextColor(16777215);
//        pitchField.setVisible(true);
//        pitchField.setCanLoseFocus(true);
//        pitchField.setFocused(false);
//        guiDevTool.textFields.add(pitchField);
        
//        toggleRandomSpawn = new GuiPolyButtonCycle<GuiPolyButtonCycle.Toggle>(
//        		guiDevTool.buttonCount++, x_pos + 10, y_pos + 45, (int) (guiDevTool.X_WIDTH * .9), 14, 
//        		"Random Spawn",  GuiPolyButtonCycle.Toggle.fromBool(spawnRand));
//        guiDevTool.addBtn(toggleRandomSpawn);
        y_pos += 15;
        //Add random spawn area setting
        //add some labels for position fields 
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Pos"));
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +30, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"X:"));
        //add position text fields
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
	
	@Override
	public void updateValues() {
		this.pos2 = new BlockPos(Integer.parseInt(xPos2Field.getText())
				,Integer.parseInt(yPos2Field.getText())
				,Integer.parseInt(zPos2Field.getText()));
//		spawnRand = GuiPolyButtonCycle.Toggle.toBool(toggleRandomSpawn.getCurrentOption());
		super.updateValues();
	}
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
//		int lookDir[] = {(int)this.lookDir.getX(), (int)this.lookDir.getY(), (int)this.lookDir.getZ()};
//		nbt.setIntArray("lookDir",lookDir);
		int pos2[] = {(int)this.pos2.getX(), (int)this.pos2.getY(), (int)this.pos2.getZ()};
		nbt.setIntArray("pos2",pos2);
//		nbt.setInteger("dim", dim);
//		nbt.setBoolean("spawnedInServer", spawnedInServer);
//		nbt.setBoolean("spawnedInClient", spawnedInClient);
		nbt.setBoolean("spawnRand", spawnRand);
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
//		int featLookDir[]=nbtFeat.getIntArray("lookDir");
//		this.lookDir=new BlockPos(featLookDir[0], featLookDir[1], featLookDir[2]);
		int featPos2[]=nbtFeat.getIntArray("pos2");
		this.pos2=new BlockPos(featPos2[0], featPos2[1], featPos2[2]);
//		this.dim = nbtFeat.getInteger("dim");
//		this.spawnedInServer = nbtFeat.getBoolean("spawnedInServer");
//		this.spawnedInClient = nbtFeat.getBoolean("spawnedInClient");
		this.spawnRand = nbtFeat.getBoolean("spawnRand");
	}
	
}
