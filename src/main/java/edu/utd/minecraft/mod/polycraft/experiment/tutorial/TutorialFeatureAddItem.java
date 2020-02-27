package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;

import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyItemNameField;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.client.gui.exp.creation.GuiExpCreator;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class TutorialFeatureAddItem extends TutorialFeature{
	private String itemName;
	private int stackSize;
	private boolean isValid;
	
	//GuiFields for Parameters
	protected GuiPolyItemNameField itemField;
	protected GuiPolyNumField stackSizeField;

	public TutorialFeatureAddItem() {}
	
	public TutorialFeatureAddItem(String name, BlockPos pos){
		super(name, pos, Color.YELLOW);
		this.itemName = "minecraft:stick";
		this.stackSize = 1;
		isValid = false;
		super.featureType = TutorialFeatureType.ADD_ITEM;
	}
	
	@Override
	public void init() {
		super.init();
		if(Item.itemRegistry.containsKey(new ResourceLocation(itemName)))
			isValid = true;
		
	}
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		if(Item.itemRegistry.containsKey(new ResourceLocation(itemName))) {
			for(String playerName : exp.getPlayersInExperiment()) {
				EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(playerName);
				player.inventory.addItemStackToInventory(new ItemStack(Item.itemRegistry.getObject(new ResourceLocation(itemName)), stackSize));
				System.out.println("Attempting to add item to inventory: " + new ItemStack(Item.itemRegistry.getObject(new ResourceLocation(itemName)), stackSize).toString());
			}
		}
		canProceed = true;
		complete(exp);
	}
	
	@Override
	public void updateValues() {
		this.itemName = itemField.getText();
		this.stackSize = Integer.parseInt(stackSizeField.getText());
		this.save();
		super.updateValues();
	}
	
	@Override
	public void buildGuiParameters(GuiExpCreator guiDevTool, int x_pos, int y_pos) {
		// TODO Auto-generated method stub
		super.buildGuiParameters(guiDevTool, x_pos, y_pos);
		FontRenderer fr = guiDevTool.getFontRenderer();
		y_pos += 15;
		
		//add labels for item stack params 
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Pos"));
        itemField = new GuiPolyItemNameField(fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .8), 10);
        itemField.setMaxStringLength(64);
        itemField.setText(itemName);
        itemField.setTextColor(16777215);
        itemField.setVisible(true);
        itemField.setCanLoseFocus(true);
        itemField.setFocused(false);
        guiDevTool.textFields.add(itemField);
        
        y_pos += 15;
        //add some labels for position fields 
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Amount"));
        //add position text fields
        stackSizeField = new GuiPolyNumField(fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        stackSizeField.setMaxStringLength(32);
        stackSizeField.setText(Integer.toString(stackSize));
        stackSizeField.setTextColor(16777215);
        stackSizeField.setVisible(true);
        stackSizeField.setCanLoseFocus(true);
        stackSizeField.setFocused(false);
        guiDevTool.textFields.add(stackSizeField);
	}
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		nbt.setString("itemName",itemName);
		nbt.setInteger("stackSize", stackSize);
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		itemName=nbtFeat.getString("itemName");
		stackSize = nbtFeat.getInteger("stackSize");
	}
	
	@Override
	public JsonObject saveJson()
	{
		super.saveJson();
		jobj.addProperty("itemName", itemName);
		jobj.addProperty("stackSize", stackSize);
		return jobj;
	}
	
	@Override
	public void loadJson(JsonObject featJson)
	{
		super.loadJson(featJson);
		this.itemName = featJson.get("itemName").getAsString();
		this.stackSize = featJson.get("stackSize").getAsInt();
	}
}
