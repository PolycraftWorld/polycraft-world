package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentParameters;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment.State;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager.ExperimentListMetaData;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.minigame.RaceGame;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.proxy.ClientProxy;
import edu.utd.minecraft.mod.polycraft.schematic.Schematic;
import edu.utd.minecraft.mod.polycraft.scoreboards.ServerScoreboard;
import edu.utd.minecraft.mod.polycraft.scoreboards.Team;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TutorialManager {

	public static TutorialManager INSTANCE = new TutorialManager();
	private static int nextAvailableExperimentID = 1; 	//one indexed
	private static Hashtable<Integer, ExperimentTutorial> experiments = new Hashtable<Integer, ExperimentTutorial>();
	
	static ArrayList<TutorialFeature> features = new ArrayList<TutorialFeature>();
	static TutorialOptions tutOptions = new TutorialOptions();
	static String outputFileName = "output";
	static String outputFileExt = ".psm";
	
	private List<EntityPlayerMP> globalPlayerList;
	
	public int clientCurrentExperiment = -1; //Variable held in the static instance for memory purposes. In the future, this may need to be moved somewhere else 
	
	
	public enum PacketMeta{
		Features,
		ActiveFeatures,		//for updating active features, if it comes from the client side, it is a request to resend active features
		Feature,
		JoinNew,
		CompletedTutorialTrue,
		CompletedTutorialFalse, //for checking if a player has completed a tutorial before
	}
	
	public TutorialManager() {
		try {
			globalPlayerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			//MinecraftServer.getServer().getConfigurationManager().
		}catch (NullPointerException e) {
			//e.printStackTrace();//TODO: Remove this.
			globalPlayerList = null;
		}
		
	}
	
	
	public void onServerTickUpdate(final TickEvent.ServerTickEvent tick) {
		if(tick.phase == TickEvent.Phase.END) {
			for(ExperimentTutorial ex: experiments.values()){
				if(ex.currentState != ExperimentTutorial.State.Done) {
					ex.onServerTickUpdate();
				}
			}
		}
	}
	
	/**
	 * This is called from Common Proxy
	 * @param tick the input tick.
	 */
	public void onPlayerTick(final TickEvent.PlayerTickEvent tick) {
		if(tick.side == Side.CLIENT && tick.phase == TickEvent.Phase.END){ //I think these are always true?
			for(ExperimentTutorial ex: experiments.values()){
				ex.onClientTickUpdate();
			}
		}
	}
	
	//@SideOnly(Side.SERVER)
	public static void sendTutorialFeatures(int id) {
		try {
			NBTTagCompound nbtFeatures = new NBTTagCompound();
			NBTTagList nbtList = new NBTTagList();
			for(int i =0;i<getExperiment(id).features.size();i++) {
				nbtList.appendTag(getExperiment(id).features.get(i).save());
			}
			nbtFeatures.setTag("features", nbtList);
			
			final ByteArrayOutputStream experimentUpdatesTemp = new ByteArrayOutputStream();	//must convert into ByteArray becuase converting with just Gson fails on reveiving end
			CompressedStreamTools.writeCompressed(nbtFeatures, experimentUpdatesTemp);
			
			Gson gson = new Gson();
			Type gsonType = new TypeToken<ByteArrayOutputStream>(){}.getType();
			final String experimentUpdates = gson.toJson(experimentUpdatesTemp, gsonType);
			
			for(EntityPlayer player: getExperiment(id).scoreboard.getPlayersAsEntity()) {
				ServerEnforcer.INSTANCE.sendTutorialUpdatePackets(experimentUpdates,PacketMeta.Features.ordinal(), (EntityPlayerMP)player);
		}
		System.out.println("Sending Update...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@SideOnly(Side.SERVER)
	public static void sendTutorialActiveFeatures(int id) {
		try {
			NBTTagCompound nbtFeatures = new NBTTagCompound();
			NBTTagList nbtList = new NBTTagList();
			for(int i =0;i<getExperiment(id).activeFeatures.size();i++) {
				nbtList.appendTag(getExperiment(id).activeFeatures.get(i).save());
			}
			nbtFeatures.setTag("activeFeatures", nbtList);
			
			final ByteArrayOutputStream experimentUpdatesTemp = new ByteArrayOutputStream();	//must convert into ByteArray becuase converting with just Gson fails on reveiving end
			CompressedStreamTools.writeCompressed(nbtFeatures, experimentUpdatesTemp);
			
			Gson gson = new Gson();
			Type gsonType = new TypeToken<ByteArrayOutputStream>(){}.getType();
			final String experimentUpdates = gson.toJson(experimentUpdatesTemp, gsonType);
			
			for(EntityPlayer player: getExperiment(id).scoreboard.getPlayersAsEntity()) {
				ServerEnforcer.INSTANCE.sendTutorialUpdatePackets(experimentUpdates,PacketMeta.ActiveFeatures.ordinal(), (EntityPlayerMP)player);
		}
		System.out.println("Sending Update...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void sendFeatureUpdate(int id, int index, TutorialFeature feature, boolean isClient) {
		try {
			Gson gson = new Gson();
			Type gsonType = new TypeToken<ByteArrayOutputStream>(){}.getType();
			NBTTagCompound tempNBT = feature.save();
			tempNBT.setInteger("index", index);
			String experimentUpdates;
			
			final ByteArrayOutputStream experimentUpdatesTemp = new ByteArrayOutputStream();	//must convert into ByteArray becuase converting with just Gson fails on reveiving end
			
			if(isClient) {
				tempNBT.setString("player", Minecraft.getMinecraft().thePlayer.getDisplayNameString());
				CompressedStreamTools.writeCompressed(tempNBT, experimentUpdatesTemp);
				experimentUpdates = gson.toJson(experimentUpdatesTemp, gsonType);
				ClientEnforcer.INSTANCE.sendTutorialUpdatePackets(experimentUpdates,PacketMeta.Feature.ordinal());
			}else {
				CompressedStreamTools.writeCompressed(tempNBT, experimentUpdatesTemp);
				experimentUpdates = gson.toJson(experimentUpdatesTemp, gsonType);
				for(EntityPlayer player: getExperiment(id).scoreboard.getPlayersAsEntity()) {
					ServerEnforcer.INSTANCE.sendTutorialUpdatePackets(experimentUpdates,PacketMeta.Feature.ordinal(), (EntityPlayerMP)player);
				}
			}
			
		}catch(Exception e) {
			PolycraftMod.logger.debug("Cannot send Feature Update: " + e.toString() );
		}
	}


	//@SideOnly(Side.CLIENT)
	public void resetClientExperimentManager() {
		//clientCurrentExperiment = -1;
	}
	
	private NBTTagCompound load() {
		try {
        	features.clear();
        	
        	File file = new File(this.outputFileName + this.outputFileExt);//TODO CHANGE THIS FILE LOCATION
        	InputStream is = new FileInputStream(file);

            NBTTagCompound nbtFeats = CompressedStreamTools.readCompressed(is);
            NBTTagList nbtFeatList = (NBTTagList) nbtFeats.getTag("features");
			for(int i =0;i<nbtFeatList.tagCount();i++) {
				NBTTagCompound nbtFeat=nbtFeatList.getCompoundTagAt(i);
				TutorialFeature test = (TutorialFeature)Class.forName(TutorialFeatureType.valueOf(nbtFeat.getString("type")).className).newInstance();
				test.load(nbtFeat);
				features.add(test);
			}
			
			tutOptions.load(nbtFeats.getCompoundTag("options"));
            is.close();
            return nbtFeats;

        } catch (Exception e) {
            System.out.println("I can't load schematic, because " + e.getStackTrace()[0]);
        }
		return null;
	}
	
	public int createExperiment() {
		NBTTagCompound nbtData = load();
		tutOptions.name = "test name";
		tutOptions.numTeams = 1;
		tutOptions.teamSize = 1;
		
		int id = this.INSTANCE.addExperiment(tutOptions, features, true);
		this.INSTANCE.getExperiment(id).setAreaData(nbtData.getCompoundTag("AreaData").getIntArray("Blocks"), nbtData.getCompoundTag("AreaData").getByteArray("Data"));

		return id;
	}
	
	public int addExperiment(TutorialOptions options, ArrayList<TutorialFeature> features, boolean genInDim8) {
		ExperimentTutorial experiment = new ExperimentTutorial(TutorialManager.INSTANCE.getNextID(), options, features, genInDim8);
		this.experiments.put(nextAvailableExperimentID++, experiment);
		return nextAvailableExperimentID - 1;
	}
	
	public int addExperiment(TutorialOptions options, ArrayList<TutorialFeature> features) {
		return addExperiment(options, features, false);
	}
	
	public boolean addPlayerToExperiment(int expID, EntityPlayerMP player){
		boolean value = experiments.get(expID).addPlayer(player);
		return value;
	}
	
	public void startExperiment(int expID) {
		experiments.get(expID).currentState = ExperimentTutorial.State.Starting;
	}
	
	public EntityPlayer getPlayerEntity(String playerName) {
		try {
			if(this.globalPlayerList.isEmpty()) {
				this.globalPlayerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			} else if(this.globalPlayerList == null) {
				System.out.println("List is null, help pls");
			}
			for (EntityPlayer player : this.globalPlayerList) {
				if(player.getDisplayName().equals(playerName)) {
					return player;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return null;
	}

	
	public ExperimentTutorial.State getExperimentStatus(int id){
		return experiments.get(id).currentState;
	}
	
	public static ExperimentTutorial getExperiment(int id){
		return experiments.get(id);
	}
	
	public void updateExperimentFeature(int experimentID, ByteArrayOutputStream featuresStream, boolean isRemote) {
		try {
			
			NBTTagCompound featureNBT = CompressedStreamTools.readCompressed(new ByteArrayInputStream(featuresStream.toByteArray()));
			
			int index = featureNBT.getInteger("index");
			String playerName = featureNBT.getString("player");
			TutorialFeature test;
		
			test = (TutorialFeature)Class.forName(TutorialFeatureType.valueOf(featureNBT.getString("type")).className).newInstance();
		
			test.load(featureNBT);
			if(isRemote) {	//client side
				this.INSTANCE.experiments.get(experimentID).activeFeatures.set(index, test);
			}else {
				for(ExperimentTutorial exp : this.INSTANCE.experiments.values()) {
					if(exp.isPlayerInExperiment(playerName))
						exp.activeFeatures.set(index, test);
				}
			}
		} catch (Exception e) {
			System.out.println("Cannot load Feature: " + e.getMessage());
		}
	}


	@SideOnly(Side.CLIENT)
	public void updateExperimentFeatures(int experimentID, ByteArrayOutputStream featuresStream) {
		//This should only be sent once
		try {
			NBTTagCompound nbtFeats = CompressedStreamTools.readCompressed(new ByteArrayInputStream(featuresStream.toByteArray()));
            NBTTagList nbtFeatList = (NBTTagList) nbtFeats.getTag("features");
			ArrayList<TutorialFeature> features = new ArrayList<TutorialFeature>();
			
			for(int i =0;i<nbtFeatList.tagCount();i++) {
				NBTTagCompound nbtFeat=nbtFeatList.getCompoundTagAt(i);
				TutorialFeature test = (TutorialFeature)Class.forName(TutorialFeatureType.valueOf(nbtFeat.getString("type")).className).newInstance();
				test.load(nbtFeat);
				features.add(test);
			}
			
			this.INSTANCE.experiments.put(experimentID, new ExperimentTutorial(experimentID, Minecraft.getMinecraft().theWorld, features));
			this.INSTANCE.clientCurrentExperiment = experimentID;
		} catch (Exception e) {
            System.out.println("I can't load initial Experiment Features, because: " + e.getStackTrace()[0]);
        }
	}
	
	public void updateExperimentActiveFeatures(int experimentID, ByteArrayOutputStream activeFeaturesStream) {
		//This should be sent whenever active features change
		try {
			NBTTagCompound nbtFeats = CompressedStreamTools.readCompressed(new ByteArrayInputStream(activeFeaturesStream.toByteArray()));
            NBTTagList nbtFeatList = (NBTTagList) nbtFeats.getTag("activeFeatures");
			ArrayList<TutorialFeature> activeFeatures = new ArrayList<TutorialFeature>();
			
			for(int i =0;i<nbtFeatList.tagCount();i++) {
				NBTTagCompound nbtFeat=nbtFeatList.getCompoundTagAt(i);
				TutorialFeature test = (TutorialFeature)Class.forName(TutorialFeatureType.valueOf(nbtFeat.getString("type")).className).newInstance();
				test.load(nbtFeat);
				activeFeatures.add(test);
			}
			
			//TODO: NEED TO VERIFY THAT ALL EXPERIMENT IDs ARE consistant on client side
			this.INSTANCE.experiments.get(experimentID).updateActiveFeatures(activeFeatures);
		} catch (Exception e) {
            System.out.println("Active Features update failed. Requesting new packet because: " + e.getMessage());
            ClientEnforcer.INSTANCE.sendActiveFeaturesRequest();	//TODO: This shouldn't happen every tick, might overload server
        }
	}
	
	
	/**
	 * For getting experiment id of running experiment
	 * @return First currently running experiment or -1 if there are no running experiments
	 */
	public static int getRunningExperiment() {
		for(int expID: experiments.keySet()) {
			if(experiments.get(expID).currentState == ExperimentTutorial.State.Starting || experiments.get(expID).currentState == ExperimentTutorial.State.Running)
			{
				return expID;
			}
		}
		return -1;
	}

	
	public static int getNextID(){
		return nextAvailableExperimentID;
	}
	
	public static void registerExperimentTypes()
	{
		//experimentTypes.put(1, ExperimentCTB.class);
	}
	
	public static void render(Entity entity) {
//		if (entity instanceof EntityPlayer && entity.worldObj.isRemote) {
//			EntityPlayer player = (EntityPlayer) entity;
//			for(ExperimentTutorial ex: experiments.values()){
//				if(ex.isPlayerInExperiment(player.getDisplayName())){
//					ex.render(entity);
//				}
//			}
//		}
		if(!INSTANCE.experiments.isEmpty())
			if(INSTANCE.experiments.containsKey(INSTANCE.clientCurrentExperiment))
				INSTANCE.experiments.get(INSTANCE.clientCurrentExperiment).render(entity);
	}
	
	public static void renderScreen(Entity entity) {
//		if (entity instanceof EntityPlayer && entity.worldObj.isRemote) {
//			EntityPlayer player = (EntityPlayer) entity;
//			for(ExperimentTutorial ex: experiments.values()){
//				if(ex.isPlayerInExperiment(player.getDisplayName())){
//					ex.render(entity);
//				}
//			}
//		}
		if(!INSTANCE.experiments.isEmpty())
			if(INSTANCE.experiments.containsKey(INSTANCE.clientCurrentExperiment))
				INSTANCE.experiments.get(INSTANCE.clientCurrentExperiment).renderScreen(entity);
	}


	public static int isPlayerinExperiment(String playerName) {
		for(int expID: experiments.keySet()) {
			if(experiments.get(expID).isPlayerInExperiment(playerName))
				return expID;
		}
		return -1;
	}

}
