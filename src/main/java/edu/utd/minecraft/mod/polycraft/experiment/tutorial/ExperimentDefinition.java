package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftChunkProvider;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ExperimentDefinition {

	ArrayList<TutorialFeature> features = new ArrayList<TutorialFeature>();
	public TutorialOptions tutOptions = new TutorialOptions();
	public JsonObject novCon = new JsonObject();
	
	public ExperimentDefinition() {
		
	}
	
	public ArrayList<TutorialFeature> getFeatures(){
		return features;
	}
	
	public TutorialOptions getOptions() {
		return tutOptions;
	}
	
	
	public void save(String outputFileDir, String outputFileName) {
		NBTTagCompound nbtFeatures = new NBTTagCompound();
		NBTTagList nbtList = new NBTTagList();
		if (tutOptions.pos == null)
			tutOptions.pos = new BlockPos(0, 0, 0);
		if (tutOptions.pos2 == null)
			tutOptions.pos2 = new BlockPos(0, 0, 0);
		for(int i =0;i<features.size();i++) {
//				NBTTagCompound nbt = new NBTTagCompound();
//				int pos[] = {(int)features.get(i).getPos().xCoord, (int)features.get(i).getPos().yCoord, (int)features.get(i).getPos().zCoord};
//				nbt.setIntArray("Pos",pos);
//				nbt.setString("name", features.get(i).getName());
			nbtList.appendTag(features.get(i).save());
		}
		nbtFeatures.setTag("features", nbtList);
		nbtFeatures.setTag("options", tutOptions.save());
		nbtFeatures.setTag("AreaData", saveArea());
		FileOutputStream fout = null;
		
		try {
			File dir = new File(outputFileDir);
			if(!dir.exists())
				dir.mkdir();
			File file = new File(outputFileDir + outputFileName);
			fout = new FileOutputStream(file);
			
			if (!file.exists()) {
				file.createNewFile();
			}
			CompressedStreamTools.writeCompressed(nbtFeatures, fout);
			fout.flush();
			fout.close();
			
		}catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}catch (IOException e) {
				e.printStackTrace();
		}catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	public void saveJson(String outputFileDir, String outputFileName) {
		JsonObject experimentJson = new JsonObject();
		JsonArray featureListJson = new JsonArray();
		if (tutOptions.pos == null)
			tutOptions.pos = new BlockPos(0, 0, 0);
		if (tutOptions.pos2 == null)
			tutOptions.pos2 = new BlockPos(0, 0, 0);
		for(int i =0;i<features.size();i++) {
			featureListJson.add(features.get(i).saveJson());
		}
		experimentJson.add("features", featureListJson);
		experimentJson.add("options", tutOptions.saveJson());
		experimentJson.add("novelty_config", novCon);
		FileOutputStream fout = null, foutArea = null;
		
		try {
			File dir = new File(outputFileDir);
			if(!dir.exists())
				dir.mkdir();
			File file = new File(outputFileDir + outputFileName);
			File fileArea = new File(outputFileDir + outputFileName + 2);
			fout = new FileOutputStream(file);
			foutArea = new FileOutputStream(fileArea);
			if (!file.exists()) {
				file.createNewFile();
			}
			if (!fileArea.exists()) {
				fileArea.createNewFile();
			}
			
			//format the Json to be readable
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(experimentJson.toString());
			String prettyJsonString = gson.toJson(je);
			//write pretty print json to file
			fout.write(prettyJsonString.getBytes());
			fout.flush();
			fout.close();
			
			//output area data to separate file
			CompressedStreamTools.writeCompressed(saveArea(), foutArea);
			foutArea.flush();
			foutArea.close();
			
		}catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}catch (IOException e) {
				e.printStackTrace();
		}catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	
	public void loadJson(World world, String path) {
		boolean oldVersion = true;
		try {
        	features.clear();

        	JsonParser parser = new JsonParser();
            JsonObject expJson = (JsonObject) parser.parse(new FileReader(path));
            JsonArray featListJson = expJson.get("features").getAsJsonArray();
			for(int i =0;i<featListJson.size();i++) {
				JsonObject featJobj=featListJson.get(i).getAsJsonObject();
				TutorialFeature test = (TutorialFeature)Class.forName(TutorialFeatureType.valueOf(featJobj.get("type").getAsString()).className).newInstance();
				System.out.println(TutorialFeatureType.valueOf(featJobj.get("type").getAsString()).className);
				test.loadJson(featJobj);
				features.add(test);
			}

			tutOptions.loadJson(expJson.get("options").getAsJsonObject());
			
			//load area data from additional file
			File file = new File(path + 2);
        	InputStream is = new FileInputStream(file);

            NBTTagCompound nbtArea = CompressedStreamTools.readCompressed(is);
        	int chunkXMax = nbtArea.getInteger("ChunkXSize");
        	int chunkZMax = nbtArea.getInteger("ChunkZSize");
        	
        	for(int chunkX = 0; chunkX <= chunkXMax; chunkX++) {
        		for(int chunkZ = 0; chunkZ <= chunkZMax; chunkZ++) {
            		Chunk chunk = PolycraftChunkProvider.readChunkFromNBT(world, nbtArea.getCompoundTag("chunk," + chunkX + "," + chunkZ));
            		world.getChunkFromChunkCoords(chunkX, chunkZ).setStorageArrays(chunk.getBlockStorageArray());
            		world.getChunkFromChunkCoords(chunkX, chunkZ).setHeightMap(chunk.getHeightMap());
            		world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
        		}
        	}
			
        	
            is.close();

        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println("I can't load schematic, because " + e.getStackTrace()[0]);
        }
	}
	
	public void load(World world, String outputFileDir, String outputFileName) {
		boolean oldVersion = true;
		try {
        	features.clear();
        	
        	File file = new File(outputFileDir + outputFileName);
        	InputStream is = new FileInputStream(file);

            NBTTagCompound nbtFeats = CompressedStreamTools.readCompressed(is);
            NBTTagList nbtFeatList = (NBTTagList) nbtFeats.getTag("features");
			for(int i =0;i<nbtFeatList.tagCount();i++) {
				NBTTagCompound nbtFeat=nbtFeatList.getCompoundTagAt(i);
				TutorialFeature test = (TutorialFeature)Class.forName(TutorialFeatureType.valueOf(nbtFeat.getString("type")).className).newInstance();
				System.out.println(TutorialFeatureType.valueOf(nbtFeat.getString("type")).className);
				test.load(nbtFeat);
				features.add(test);
			}
			oldVersion = nbtFeats.getCompoundTag("AreaData").getString("version").isEmpty();
			if(!oldVersion) {
	        	int chunkXMax = nbtFeats.getCompoundTag("AreaData").getInteger("ChunkXSize");
	        	int chunkZMax = nbtFeats.getCompoundTag("AreaData").getInteger("ChunkZSize");
	        	
	        	for(int chunkX = 0; chunkX <= chunkXMax; chunkX++) {
	        		for(int chunkZ = 0; chunkZ <= chunkZMax; chunkZ++) {
	            		Chunk chunk = PolycraftChunkProvider.readChunkFromNBT(world, nbtFeats.getCompoundTag("AreaData").getCompoundTag("chunk," + chunkX + "," + chunkZ));
	            		world.getChunkFromChunkCoords(chunkX, chunkZ).setStorageArrays(chunk.getBlockStorageArray());
	            		world.getChunkFromChunkCoords(chunkX, chunkZ).setHeightMap(chunk.getHeightMap());
	            		world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
	        		}
	        	}
			}
        	
			tutOptions.load(nbtFeats.getCompoundTag("options"));
            is.close();

        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println("I can't load schematic, because " + e.getStackTrace()[0]);
        }
	}
	
//	@SideOnly(Side.SERVER)
//	private void load() {
//		return;
//	}
	
	@SideOnly(Side.CLIENT)
	private NBTTagCompound saveArea() {
		if(tutOptions.pos.getY() != 0 || tutOptions.pos2.getY() != 0)
		{
			int minX = Math.min(tutOptions.pos.getX(), tutOptions.pos2.getX());
			int maxX = Math.max(tutOptions.pos.getX(), tutOptions.pos2.getX());
			int minY = Math.min(tutOptions.pos.getY(), tutOptions.pos2.getY());
			int maxY = Math.max(tutOptions.pos.getY(), tutOptions.pos2.getY());
			int minZ = Math.min(tutOptions.pos.getZ(), tutOptions.pos2.getZ());
			int maxZ = Math.max(tutOptions.pos.getZ(), tutOptions.pos2.getZ());
			int[] intArray;
			short height;
			short length;
			short width;
			
			length=(short)(maxX-minX+1);
			height=(short)(maxY-minY+1);
			width=(short)(maxZ-minZ+1);
			int[] blocks = new int[length*height*width];
			byte[] data = new byte[length*height*width];
			int count=0;
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagList tiles = new NBTTagList();

			int chunkXMin = minX >> 4;
			int chunkZMin = minZ >> 4;
            int chunkXMax = maxX >> 4;
			int chunkZMax = maxZ >> 4;	
			nbt.setString("version", "1.01");
			nbt.setInteger("ChunkXSize", chunkXMax - chunkXMin);
			nbt.setInteger("ChunkZSize", chunkZMax - chunkZMin);
			TileEntity tile;
			
			for(int chunkX = chunkXMin; chunkX <= chunkXMax; chunkX++) {
				for(int chunkZ = chunkZMin; chunkZ <= chunkZMax; chunkZ++) {
					NBTTagCompound nbtChunk = new NBTTagCompound();
					Chunk chunk = Minecraft.getMinecraft().theWorld.getChunkFromChunkCoords(chunkX, chunkZ);
					PolycraftChunkProvider.writeChunkToNBT(chunk, Minecraft.getMinecraft().theWorld, nbtChunk);
					nbtChunk.setInteger("xPos", chunkX - chunkXMin);
					nbtChunk.setInteger("zPos", chunkZ - chunkZMin);
					nbt.setTag("chunk," + (chunkX - chunkXMin) + "," + (chunkZ - chunkZMin), nbtChunk);
				}
			}
			
			//nbt.setTag("TileEntity", tiles);
//			nbt.setIntArray("Blocks", blocks);
//			nbt.setByteArray("Data", data);
			return nbt;
		}else {
			return null;
		}
	}
}
