package edu.utd.minecraft.mod.polycraft.experiment.feature;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.world.World;

public abstract class ExperimentFeature {
	protected String name;
	protected int xPos;
	protected int yPos;
	protected int zPos;
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getxPos() {
		return xPos;
	}


	public void setxPos(int xPos) {
		this.xPos = xPos;
	}


	public int getyPos() {
		return yPos;
	}


	public void setyPos(int yPos) {
		this.yPos = yPos;
	}


	public int getzPos() {
		return zPos;
	}


	public void setzPos(int zPos) {
		this.zPos = zPos;
	}



	public ExperimentFeature(Integer x, Integer y, Integer z) {
		xPos = x;
		yPos = y;
		zPos = z;
		name = "";
	}
	
	
	public abstract void build(World world);
	
	public long[] getPositionArray() {
		return new long[] {xPos, yPos, zPos};
	}
	
	public static class ExperimentFeatureDeserializer implements JsonDeserializer<ExperimentFeature>{

		@Override
		public ExperimentFeature deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			// TODO Auto-generated method stub
			JsonObject jsonObject = json.getAsJsonObject();
			JsonElement name = jsonObject.get("name");
			JsonElement type = jsonObject.get("type");
			
			if(type != null) {
				switch (type.getAsString()) {
				case "FeatureSchematic":
					return context.deserialize(jsonObject, FeatureSchematic.class);
				case "FeatureBase":
					return context.deserialize(jsonObject, FeatureBase.class);
				case "FeatureSpawn":
					return context.deserialize(jsonObject, FeatureSpawn.class);
				}
			}
			
			
			return null;
		}
		
	}
	
	public class ExperimentFeatureListDeserializer implements JsonDeserializer<ArrayList<ExperimentFeature>>{

		@Override
		public ArrayList<ExperimentFeature> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			// TODO Auto-generated method stub
			ArrayList<ExperimentFeature> result = new ArrayList<>();
			
			JsonObject jObject = json.getAsJsonObject();
			JsonElement jElem = jObject.get("FeatureSchematic");
			if(jElem.isJsonObject()) {
				result.add(new FeatureSchematic(((JsonObject) jElem).get("schematic").getAsString()));
			}else {
				System.out.println("ERROR Reading the JSON file - FeatureSchematic");
				return null;
			}
			
			jElem = jObject.get("FeatureSpawn");
			if(jElem.isJsonArray()) {
				JsonArray jArr = (JsonArray) jElem;
				for(JsonElement jm : jArr) {
					if(jm.isJsonObject()) {	
						result.add(new FeatureSpawn(
								((JsonObject) jm).get("x").getAsInt(),
								((JsonObject) jm).get("y").getAsInt(),
								((JsonObject) jm).get("z").getAsInt(),
								((JsonObject) jm).get("xMax").getAsInt(),
								((JsonObject) jm).get("zMax").getAsInt()));
					}
				}
			}
			else {
				System.out.println("ERROR Reading the JSON file - FeatureSpawn");
				return null;
			}
			
			jElem = jObject.get("FeatureBase");
			if(jElem.isJsonArray()) {
				JsonArray jArr = (JsonArray) jElem;
				for(JsonElement jm : jArr) {
					if(jm.isJsonObject()) {	
						result.add(new FeatureBase(
								((JsonObject) jm).get("x").getAsInt(),
								((JsonObject) jm).get("y").getAsInt(),
								((JsonObject) jm).get("z").getAsInt(),
								((JsonObject) jm).get("baseSize").getAsInt(),
								((JsonObject) jm).get("baseHeight").getAsInt()));
					}
				}
			}
			else {
				System.out.println("ERROR Reading the JSON file - FeatureBase");
				return null;
			}
			
			
			
			return result;
		}
		
	}
}
