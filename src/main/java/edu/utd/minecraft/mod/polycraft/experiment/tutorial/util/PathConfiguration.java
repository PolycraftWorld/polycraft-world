package edu.utd.minecraft.mod.polycraft.experiment.tutorial.util;

import java.util.HashMap;

import com.google.gson.JsonObject;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;

public class PathConfiguration{

	public enum PathType{
		WALL,
		OPEN,
		DOOR,
		DOOR_FLIPPED,
		SECRETE_DOOR,
		ONE_WAY_DOOR,
		FALSE_DOOR,
		HIDDEN_DOOR,
		OPEN_FULL_HEIGHT,
		ONE_WAY_SECRET_DOOR,
		BREAKABLE_WALL;
	}
	
	public enum Location{
		NONE,
		INSIDE,
		OUTSIDE,
		BOTH
	}

	private PathType type;
	private boolean reversed;
	private Location buttonLocation;
	private Location pressurePlaceLocation;
	
	public PathConfiguration() {}
	
	public PathConfiguration(PathType type, boolean reversed) {
		this.type = type;
		this.reversed = reversed;
	}

	public PathType getType() {
		return type;
	}

	public void setType(PathType type) {
		this.type = type;
	}

	public boolean isReversed() {
		return reversed;
	}

	public void setReversed(boolean reversed) {
		this.reversed = reversed;
	}
	
	public Location getButtonLocation() {
		return buttonLocation;
	}

	public void setButtonLocation(Location buttonLocation) {
		this.buttonLocation = buttonLocation;
	}

	public Location getPressurePlaceLocation() {
		return pressurePlaceLocation;
	}

	public void setPressurePlateLocation(Location pressurePlaceLocation) {
		this.pressurePlaceLocation = pressurePlaceLocation;
	}

	public NBTTagCompound save()
	{
		NBTTagCompound pathConfigNbt = new NBTTagCompound();
		pathConfigNbt.setString("type", type.name());
		pathConfigNbt.setBoolean("reversed", reversed);
		return pathConfigNbt;
	}
	
	public void load(NBTTagCompound pathConfigNbt)
	{
		type = PathType.valueOf(pathConfigNbt.getString("type"));
		reversed = pathConfigNbt.getBoolean("reversed");
	}
	
	public JsonObject saveJson()
	{
		JsonObject jobj = new JsonObject();
		jobj.addProperty("type", type.name());
		jobj.addProperty("reversed", reversed);
		jobj.addProperty("buttonLocation", buttonLocation.name());
		jobj.addProperty("pressurePlaceLocation", pressurePlaceLocation.name());
		
		return jobj;
	}
	
	public void loadJson(JsonObject jobj)
	{
		type = PathType.valueOf(jobj.get("type").getAsString());
		reversed = jobj.get("reversed").getAsBoolean();
		buttonLocation = jobj.get("buttonLocation") == null? Location.NONE: Location.valueOf(jobj.get("buttonLocation").getAsString());
		pressurePlaceLocation = jobj.get("pressurePlaceLocation") == null? Location.NONE: Location.valueOf(jobj.get("pressurePlaceLocation").getAsString());
	}
}