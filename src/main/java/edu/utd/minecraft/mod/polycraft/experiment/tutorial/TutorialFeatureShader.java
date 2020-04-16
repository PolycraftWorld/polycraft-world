package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.aitools.BotAPI;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonCycle;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.client.gui.exp.creation.GuiExpCreator;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.util.PathConfiguration;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.util.PathConfiguration.Location;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.util.PathConfiguration.PathType;
import edu.utd.minecraft.mod.polycraft.util.Format;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.util.JsonException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TutorialFeatureShader extends TutorialFeature{
	
	private enum Shader{
		ANTIALIAS(new ResourceLocation("shaders/post/antialias.json")),
		ART(new ResourceLocation("shaders/post/art.json")),
		BITS(new ResourceLocation("shaders/post/bits.json")),
		BLOBS(new ResourceLocation("shaders/post/blobs.json")),
		BLOBS2(new ResourceLocation("shaders/post/blobs2.json")),
		BLUR(new ResourceLocation("shaders/post/blur.json")),
		BUMPY(new ResourceLocation("shaders/post/bumpy.json")),
		COLOR_CONVOLVE(new ResourceLocation("shaders/post/color_convolve.json")),
		CREEPER(new ResourceLocation("shaders/post/creeper.json")),
		CUSTOM(PolycraftMod.getAssetName("shaders/green.json")),
		NONE(PolycraftMod.getAssetName("shaders/reset.json")),
		DECONVERGE(new ResourceLocation("shaders/post/deconverge.json")),
		DESATURATE(new ResourceLocation("shaders/post/desaturate.json")),
		ENTITY_OUTLINE(new ResourceLocation("shaders/post/entity_outline.json")),
		FLIP(new ResourceLocation("shaders/post/flip.json")),
		FXAA(new ResourceLocation("shaders/post/fxaa.json")),
		GRAYSCALE(new ResourceLocation("shaders/post/grayscale.json")),
		GREEN(new ResourceLocation("shaders/post/green.json")),
		INVERT(new ResourceLocation("shaders/post/invert.json")),
		NOTCH(new ResourceLocation("shaders/post/notch.json")),
		NTSC(new ResourceLocation("shaders/post/ntsc.json")),
		OUTLINE(new ResourceLocation("shaders/post/outline.json")),
		PENCIL(new ResourceLocation("shaders/post/pencil.json")),
		PHOSPHOR(new ResourceLocation("shaders/post/phosphor.json")),
		SCAN_PINCUSHION(new ResourceLocation("shaders/post/scan_pincushion.json")),
		SOBEL(new ResourceLocation("shaders/post/sobel.json")),
		SPIDER(new ResourceLocation("shaders/post/spider.json")),
		WOBBLE(new ResourceLocation("shaders/post/wobble.json"));
		
		private ResourceLocation resource;
		
		Shader(ResourceLocation resourceLocation){
			resource = resourceLocation;
		}
		
		public ResourceLocation getResource() {
			return resource;
		}
	}
	
	private Shader shaderType;
	public int intensity;
		
	public TutorialFeatureShader() {}
	
	public TutorialFeatureShader(String name, BlockPos pos){
		super(name, pos, Color.GREEN);
		this.featureType = TutorialFeatureType.SHADER;
	}
	
	@Override
	public void preInit(ExperimentTutorial exp) {
		super.preInit(exp);
	}
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		// do nothing on server side
	}
	
	@Override
	public void onClientTickUpdate(ExperimentTutorial exp) {
		if(shaderType == Shader.CUSTOM) {
			JsonParser jsonparser = new JsonParser();
	        InputStream inputstream = null;
	        if(intensity < 0)
	        	intensity = 0;
	        if(intensity > 100)
	        	intensity = 100;
			try
	        {
	            IResource iresource = Minecraft.getMinecraft().getResourceManager().getResource(Shader.CUSTOM.resource);
	            inputstream = iresource.getInputStream();
	            JsonObject jsonobject = jsonparser.parse(IOUtils.toString(inputstream, Charsets.UTF_8)).getAsJsonObject();

	            if (JsonUtils.isJsonArray(jsonobject, "passes"))
	            {
	                JsonArray jsonarray1 = jsonobject.getAsJsonArray("passes");

	                for (JsonElement jsonelement1 : jsonarray1)
	                {
	                    if(jsonelement1.getAsJsonObject().get("name").getAsString().equals("color_convolve")) {
	                    	JsonArray jsonarray2 = jsonelement1.getAsJsonObject().getAsJsonArray("uniforms");

	    	                for (JsonElement jsonelement2 : jsonarray2)
	    	                {
    	                    	Gson gson = new Gson();
    	                		ArrayList<Float> map = new ArrayList<Float>();
	    	                    if(jsonelement2.getAsJsonObject().get("name").getAsString().equals("RedMatrix")) {
	    	                		map.add(1f - intensity/200f);	// R
	    	                		map.add(intensity/200f);	// G
	    	                		map.add(intensity/200f);	// B
	    	                		jsonelement2.getAsJsonObject().add("values", gson.toJsonTree(map));
	    	                    }else if(jsonelement2.getAsJsonObject().get("name").getAsString().equals("GreenMatrix")) {
	    	                    	map.add(intensity/200f);	// R
	    	                		map.add(1f - intensity/200f);	// G
	    	                		map.add(intensity/200f);	// B
	    	                		jsonelement2.getAsJsonObject().add("values", gson.toJsonTree(map));
	    	                    }else if(jsonelement2.getAsJsonObject().get("name").getAsString().equals("BlueMatrix")) {
	    	                    	map.add(intensity/200f);	// R
	    	                		map.add(intensity/200f);	// G
	    	                		map.add(1f - intensity/200f);	// B
	    	                		jsonelement2.getAsJsonObject().add("values", gson.toJsonTree(map));
	    	                    }
	    	                }
	                    }
	                }
	            }
	            FileOutputStream fout = null, foutArea = null;
	            try {
	    			File dir = new File("..\\src\\main\\resources\\assets\\polycraft\\shaders\\temp");
	    			if(!dir.exists())
	    				dir.mkdir();
	    			File file = new File("..\\src\\main\\resources\\assets\\polycraft\\shaders\\temp\\custom.json");
	    			fout = new FileOutputStream(file);
	    			if (!file.exists()) {
	    				file.createNewFile();
	    			}
	    			//format the Json to be readable
	    			Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    			JsonElement je = jsonparser.parse(jsonobject.toString());
	    			String prettyJsonString = gson.toJson(je);
	    			//write pretty print json to file
	    			fout.write(prettyJsonString.getBytes());
	    			fout.flush();
	    			fout.close();
	    			
	    			Minecraft.getMinecraft().entityRenderer.loadShader(PolycraftMod.getAssetName("shaders/temp/custom.json"));
	    		}catch (FileNotFoundException e1) {
	    			e1.printStackTrace();
	    		}catch (IOException e) {
	    				e.printStackTrace();
	    		}catch (NullPointerException e) {
	    			e.printStackTrace();
	    		}
	        }
	        catch (Exception e)
	        {
	            PolycraftMod.logger.error("Error processing TutorialFeatureShaders json");
	            e.printStackTrace();
	        }
	        finally
	        {
	            IOUtils.closeQuietly(inputstream);
	        }
		}else {
			Minecraft.getMinecraft().entityRenderer.loadShader(shaderType.resource);
		}
		
		canProceed = true;
		this.complete(exp);
	}
	
	@Override
	public void buildGuiParameters(GuiExpCreator guiDevTool, int x_pos, int y_pos) {
		// TODO Auto-generated method stub
		super.buildGuiParameters(guiDevTool, x_pos, y_pos);
	}
	
	@Override
	public void updateValues() {
		super.updateValues();
		
		this.shaderType = Shader.NONE;
	}
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		nbt.setString("shaderType", shaderType.name());
		nbt.setInteger("intensity", intensity);
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
		this.shaderType = Shader.valueOf(nbtFeat.getString("shaderType"));
		intensity = nbtFeat.getInteger("intensity");
	}
	
	@Override
	public JsonObject saveJson()
	{
		super.saveJson();
		jobj.addProperty("shaderType", shaderType.name());
		jobj.addProperty("intensity", intensity);
		return jobj;
	}
	
	@Override
	public void loadJson(JsonObject featJson)
	{
		super.loadJson(featJson);
		this.shaderType = Shader.valueOf(featJson.get("shaderType").getAsString());
		intensity = featJson.get("intensity").getAsInt();
	}
	
	
}
