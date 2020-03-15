package edu.utd.minecraft.mod.polycraft.experiment.tutorial.novelty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentDefinition;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftChunkProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class NoveltyParser {
	
	ArrayList<ElementTransform> transforms = new ArrayList<ElementTransform>();
	public String templatePath = "";
	public long seed;
	
	public NoveltyParser() {
		//initialize any needed variables
	}
	
	public ExperimentDefinition transform(String path) {
		loadJson(path);
		return transform(path, seed);
	}
	
	public ExperimentDefinition transform(String path, long seed) {
		loadJson(path);
		this.seed = seed;	// override seed when we call from commandRESET
		ExperimentDefinition expDef = new ExperimentDefinition();
		if(templatePath.endsWith(".psm"))
			expDef.load(Minecraft.getMinecraft().theWorld, "", templatePath);
		else
			expDef.loadJson(Minecraft.getMinecraft().theWorld, templatePath);

		for(ElementTransform transform: transforms) {
			for(TutorialFeature feat: expDef.getFeatures()) {
				transform.applyTransform(feat, seed);
			}
		}
		return expDef;
	}
	
	
	public void loadJson(String path) {
		try {
        	transforms.clear();

        	JsonParser parser = new JsonParser();
            JsonObject expJson = (JsonObject) parser.parse(new FileReader(path));
            
            this.templatePath = expJson.get("templatePath").getAsString();
            this.seed = expJson.get("seed").getAsLong();
            
            JsonArray transformListJson = expJson.get("novelties").getAsJsonArray();
			for(int i =0;i<transformListJson.size();i++) {
				JsonObject transformJobj=transformListJson.get(i).getAsJsonObject();
				ElementTransform transform = (ElementTransform)Class.forName(ElementTransform.TransformType.valueOf(transformJobj.get("type").getAsString()).className).newInstance();
				System.out.println(ElementTransform.TransformType.valueOf(transformJobj.get("type").getAsString()).className);
				transform.loadJson(transformJobj);
				transforms.add(transform);
			}
        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println("I can't load schematic, because " + e.getStackTrace()[0]);
        }
	}
	
}
