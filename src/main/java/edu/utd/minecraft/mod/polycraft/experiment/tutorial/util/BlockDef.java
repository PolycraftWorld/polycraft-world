package edu.utd.minecraft.mod.polycraft.experiment.tutorial.util;

import com.google.gson.JsonObject;

import net.minecraft.nbt.NBTTagCompound;

public class BlockDef {
	public String blockName;
	public int blockMeta;
	
	public BlockDef() {}
	
	public BlockDef(NBTTagCompound nbt) {
		this.load(nbt);
	}
	
	public BlockDef(JsonObject jobj) {
		this.loadJson(jobj);
	}
	
	public BlockDef(String name, int meta){
		blockName = name;
		blockMeta = meta;
	}
	
	public NBTTagCompound save() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("blockName", blockName);
		nbt.setInteger("blockMeta", blockMeta);
		
		return nbt;
	}
	
	public void load(NBTTagCompound nbt) {
		blockName = nbt.getString("blockName");
		if(nbt.hasKey("blockMeta"))
			blockMeta = nbt.getInteger("blockMeta");
		else
			blockMeta = 0;
	}
	
	public JsonObject toJson() {
		JsonObject jobj = new JsonObject();
		jobj.addProperty("blockName", blockName);
		jobj.addProperty("blockMeta", blockMeta);
		return jobj;
	}
	
	public void loadJson(JsonObject jobj) {
		blockName = jobj.get("blockName").getAsString();
		if(jobj.has("blockMeta"))
			blockMeta = jobj.get("blockMeta").getAsInt();
		else
			blockMeta = 0;
	}
}
