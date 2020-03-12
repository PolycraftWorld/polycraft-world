package edu.utd.minecraft.mod.polycraft.aitools;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraftforge.fml.client.FMLClientHandler;

public class APIHelper {
	public enum CommandResult{
		SUCCESS,
		FAIL,
		E_ACTION_PREVENTED
	}
	

    static final String tempMark = "TEMP_";
	
	public static void start(String args[]){	
		long seed = getWorldSeedFromString("Polycraft AI Gym");
        WorldSettings worldsettings = new WorldSettings(seed, GameType.SURVIVAL, false, false, WorldType.FLAT);
        // This call to setWorldName allows us to specify the layers of our world, and also the features that will be created.
        // This website provides a handy way to generate these strings: http://chunkbase.com/apps/superflat-generator
        worldsettings.setWorldName("Polycraft AI Gym");
        worldsettings.enableCommands(); // Enables cheat commands.
        // Create a filename for this map - we use the time stamp to make sure it is different from other worlds, otherwise no new world
        // will be created, it will simply load the old one.
        createAndLaunchWorld(worldsettings, false);
        Minecraft.getMinecraft().getIntegratedServer().setDifficultyForAllWorlds(EnumDifficulty.PEACEFUL);
        Minecraft.getMinecraft().getIntegratedServer().setAllowFlight(true);;
        for(WorldServer server: Minecraft.getMinecraft().getIntegratedServer().worldServers) {
        	server.getGameRules().setOrCreateGameRule("doMobSpawning", "false");
        	server.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
//        	server.getGameRules().setOrCreateGameRule("doEntityDrops", "false");
//        	server.getGameRules().setOrCreateGameRule("doFireTick", "false");
        	server.getGameRules().setOrCreateGameRule("doImmediateRespawn", "true");
//        	server.getGameRules().setOrCreateGameRule("doMobLoot", "false");
        	server.getGameRules().setOrCreateGameRule("doTraderSpawning", "false");
        	server.getGameRules().setOrCreateGameRule("doWeatherCycle", "false");
        	server.setWorldTime(1000);
        }
        for(BiomeGenBase biome: BiomeGenBase.BIOME_ID_MAP.values()) {
        	biome.setDisableRain();
        }
	}
	
	/** Get a filename to use for creating a new Minecraft save map.<br>
     * Ensure no duplicates.
     * @param isTemporary mark the filename such that the file management code knows to delete this later
     * @return a unique filename (relative to the saves folder)
     */
    public static String getNewSaveFileLocation(boolean isTemporary) {
        File dst;
        File savesDir = FMLClientHandler.instance().getSavesDir();
        do {
            // We used to create filenames based on the current date/time, but this can cause problems when
            // multiple clients might be writing to the same save location. Instead, use a GUID:
            String s = UUID.randomUUID().toString();

            // Add our port number, to help with file management:
            s = 9000 + "_" + s;

            // If this is a temp file, mark it as such:
            if (isTemporary) {
                s = tempMark + s;
            }

            dst = new File(savesDir, s);
        } while (dst.exists());

        return dst.getName();
    }
    /**
     * Creates and launches a unique world according to the settings. 
     * @param worldsettings the world's settings
     * @param isTemporary if true, the world will be deleted whenever newer worlds are created
     * @return
     */
    public static boolean createAndLaunchWorld(WorldSettings worldsettings, boolean isTemporary)
    {
        String s = getNewSaveFileLocation(isTemporary);
        Minecraft.getMinecraft().launchIntegratedServer(s, s, worldsettings);
        cleanupTemporaryWorlds(s);
        return true;
    }
	
	public static long getWorldSeedFromString(String seedString)
    {
        // This seed logic mirrors the Minecraft code in GuiCreateWorld.actionPerformed:
        long seed = (new Random()).nextLong();
        if (seedString != null && !seedString.isEmpty())
        {
            try
            {
                long i = Long.parseLong(seedString);
                if (i != 0L)
                    seed = i;
            }
            catch (NumberFormatException numberformatexception)
            {
                seed = (long)seedString.hashCode();
            }
        }
        return seed;
    }
	
	/**
     * Attempts to delete all Minecraft Worlds with "TEMP_" in front of the name
     * @param currentWorld excludes this world from deletion, can be null
     */
    public static void cleanupTemporaryWorlds(String currentWorld){
        List<SaveFormatComparator> saveList;
        ISaveFormat isaveformat = Minecraft.getMinecraft().getSaveLoader();
        isaveformat.flushCache();

        try{
            saveList = isaveformat.getSaveList();
        } catch (AnvilConverterException e){
            e.printStackTrace();
            return;
        }

        String searchString = tempMark + 9000 + "_";

        for (SaveFormatComparator s: saveList){
            String folderName = s.getFileName();
            if (folderName.startsWith(searchString) && !folderName.equals(currentWorld)){
                isaveformat.deleteWorldDirectory(folderName);
            }
        }
    }
}
