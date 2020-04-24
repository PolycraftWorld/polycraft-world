package edu.utd.minecraft.mod.polycraft.experiment.tutorial.novelty;

import java.io.FileReader;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentDefinition;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import net.minecraft.client.Minecraft;

public class NoveltyParser {
	
	ArrayList<ElementTransform> transforms = new ArrayList<ElementTransform>();
	public String templatePath = "";
	public long seed;
	public JsonObject novCon;
	
	public NoveltyParser() {
		//initialize any needed variables
	}
	
	public ExperimentDefinition transform(String path) {
		loadJson(path, -1, -1);
		return transform(path, seed, -1);
	}
	
	public ExperimentDefinition transform(String path, long seed, int intensity) {
		loadJson(path, seed, intensity);
		novCon.addProperty("seedOverride", seed);
		novCon.addProperty("generatedTimeStamp", (new Timestamp(System.currentTimeMillis())).toString());
		this.seed = seed;	// override seed when we call from commandRESET
		ExperimentDefinition expDef = new ExperimentDefinition();
		if(templatePath.endsWith(".psm"))
			expDef.load(Minecraft.getMinecraft().theWorld, "", templatePath);
		else
			expDef.loadJson(Minecraft.getMinecraft().theWorld, templatePath);

		for(ElementTransform transform: transforms) {
			for(TutorialFeature feat: expDef.getFeatures()) {
				transform.applyTransform(feat, expDef.getFeatures(), seed);
			}
		}
		
		expDef.novCon = this.novCon;
		
		return expDef;
	}
	
	
	public void loadJson(String path, long seed, int intensity) {
		try {
        	transforms.clear();

        	JsonParser parser = new JsonParser();
        	novCon = (JsonObject) parser.parse(new FileReader(path));
            
            this.templatePath = novCon.get("templatePath").getAsString();
            this.seed = novCon.get("seed").getAsLong();
            
            JsonArray transformListJson = novCon.get("novelties").getAsJsonArray();
			for(int i =0;i<transformListJson.size();i++) {
				JsonObject transformJobj=transformListJson.get(i).getAsJsonObject();
				ElementTransform transform = (ElementTransform)Class.forName(ElementTransform.TransformType.valueOf(transformJobj.get("type").getAsString()).className).newInstance();
				System.out.println(ElementTransform.TransformType.valueOf(transformJobj.get("type").getAsString()).className);
				transform.loadJson(transformJobj, seed, intensity);
				transforms.add(transform);
			}
        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println("I can't load schematic, because " + e.getStackTrace()[0]);
        }
	}
	
}
