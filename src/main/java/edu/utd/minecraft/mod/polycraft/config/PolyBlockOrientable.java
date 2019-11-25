package edu.utd.minecraft.mod.polycraft.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import com.google.common.collect.ImmutableList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.block.BlockPolyDirectional;
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerHelper.EnumColor;

public class PolyBlockOrientable extends CustomObject{

	public PolyBlockOrientable(int[] version, String gameID, String name, String type, String maxStackSize,
			String flashlightRange, String[] paramNames, String[] paramValues, int paramsOffset) {
		super(version, gameID, name, type, maxStackSize, flashlightRange, paramNames, paramValues, paramsOffset);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getBlock(this), size, 15); //15 is white by default
	}

	public ItemStack getItemStack(int size, int metadata) {
		return new ItemStack(PolycraftRegistry.getBlock(this), size, metadata);
	}

	public static void checkBlockOrientableJSONs(CustomObject customObject, String path){
		String blockName = PolycraftMod.getFileSafeName(customObject.name);
		String texture = blockName;
		String blockStateContent = "{\n" +
				"  \"variants\": {\n";
		BufferedWriter writer;
		
		File json = new File(path + "blockstates\\" + blockName + ".json");
		if (json.exists())
				return;
		else{
			try{
				blockStateContent += String.format("    \"normal\": { \"model\": \"polycraft:" + blockName + "\" },\r\n" + 
						"	\"facing=down,half=top\":  { \"model\": \"polycraft:" + blockName + "\", \"x\": 180 },\r\n" + 
						"	\"facing=up,half=top\":    { \"model\": \"polycraft:" + blockName + "\" },\r\n" + 
						"	\"facing=north,half=top\": { \"model\": \"polycraft:" + blockName + "\" },\r\n" + 
						"	\"facing=south,half=top\": { \"model\": \"polycraft:" + blockName + "\", \"y\": 180 },\r\n" + 
						"	\"facing=west,half=top\":  { \"model\": \"polycraft:" + blockName + "\", \"y\": 270 },\r\n" + 
						"	\"facing=east,half=top\":  { \"model\": \"polycraft:" + blockName + "\", \"y\": 90 },\r\n" + 
						"	\"facing=down,half=bottom\":  { \"model\": \"polycraft:" + blockName + "\", \"x\": 180 },\r\n" + 
						"	\"facing=up,half=bottom\":    { \"model\": \"polycraft:" + blockName + "\" },\r\n" + 
						"	\"facing=north,half=bottom\": { \"model\": \"polycraft:" + blockName + "\" },\r\n" + 
						"	\"facing=south,half=bottom\": { \"model\": \"polycraft:" + blockName + "\", \"y\": 180 },\r\n" + 
						"	\"facing=west,half=bottom\":  { \"model\": \"polycraft:" + blockName + "\", \"y\": 270 },\r\n" + 
						"	\"facing=east,half=bottom\":  { \"model\": \"polycraft:" + blockName + "\", \"y\": 90 }\r\n" + 
						"  }\r\n" + 
						"}");
				
				//Model file
				String fileContent = String.format("{\n" +
						"  \"parent\": \"block/orientable\",\n" +
						"  \"textures\": {\n" +
						"    \"top\": \"polycraft:blocks/%s\",\n" +
						"    \"front\": \"polycraft:blocks/%s\",\n" +
						"    \"side\": \"polycraft:blocks/%s\"\n" +
						"  }\n" +
						"}", texture, texture, texture);

				writer = new BufferedWriter(new FileWriter(path + "models\\block\\" + blockName + ".json"));

				writer.write(fileContent);
				writer.close();

				//Item model file
				fileContent = String.format("{\n" +
						"    \"parent\": \"polycraft:%s\",\n" +
						"    \"display\": {\n" +
						"        \"thirdperson\": {\n" +
						"            \"rotation\": [ 10, -45, 170 ],\n" +
						"            \"translation\": [ 0, 1.5, -2.75 ],\n" +
						"            \"scale\": [ 0.375, 0.375, 0.375 ]\n" +
						"        }\n" +
						"    }\n" +
						"}", blockName);

				writer = new BufferedWriter(new FileWriter(path + "models\\item\\" + blockName + ".json"));

				writer.write(fileContent);
				writer.close();
				
//				//blockstate files for items (apparently needed)
//				//Model file
//				fileContent = String.format("{\n" + 
//						"  \"variants\": {\n" + 
//						"    \"normal\": { \"model\": \"polycraft:%s\" }\n" + 
//						"  }\n" + 
//						"}", blockName);
//
//				writer = new BufferedWriter(new FileWriter(path + "blockstates\\" + blockName + ".json"));
//
//				writer.write(fileContent);
//				writer.close();
				
				writer = new BufferedWriter(new FileWriter(path + "blockstates\\" + blockName + ".json"));

				writer.write(blockStateContent);
				writer.close();

				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}