package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Ingot;
import edu.utd.minecraft.mod.polycraft.config.Ore;

public class ItemIngot extends Item implements PolycraftItem {

	public final Ingot ingot;

	public ItemIngot(final Ingot ingot) {
		this.ingot = ingot;
		this.setCreativeTab(CreativeTabs.tabMaterials);
		//this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(ingot.name)));
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MATERIALS_INGOT;
	}
	
	public static void checkJSONs(Ingot ingot, String path){
		String texture = PolycraftMod.getFileSafeName(ingot.name);
		File json = new File(path + "models\\item\\" + texture + ".json");
		if (json.exists())
				return;
		else{
			try{
				//Item model file
				String fileContent = String.format("{\n" + 
						"    \"parent\": \"builtin/generated\",\n" + 
						"    \"textures\": {\n" + 
						"        \"layer0\": \"polycraft:items/%s\"\n" + 
						"    },\n" + 
						"    \"display\": {\n" + 
						"        \"thirdperson\": {\n" + 
						"            \"rotation\": [ -90, 0, 0 ],\n" + 
						"            \"translation\": [ 0, 1, -3 ],\n" + 
						"            \"scale\": [ 0.55, 0.55, 0.55 ]\n" + 
						"        },\n" + 
						"        \"firstperson\": {\n" + 
						"            \"rotation\": [ 0, -135, 25 ],\n" + 
						"            \"translation\": [ 0, 4, 2 ],\n" + 
						"            \"scale\": [ 1.7, 1.7, 1.7 ]\n" + 
						"        }\n" + 
						"    }\n" + 
						"}", texture);

				BufferedWriter writer = new BufferedWriter(new FileWriter(path + "models\\item\\" + texture + ".json"));

				writer.write(fileContent);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
	
	// removing for 1.8
//	@Override
//	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
//	{
//		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);		
//	}
}
