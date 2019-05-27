package edu.utd.minecraft.mod.polycraft.proxy;

import net.minecraftforge.common.ForgeChunkManager;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.scoreboards.ServerScoreboard;
import edu.utd.minecraft.mod.polycraft.util.Analytics;
import edu.utd.minecraft.mod.polycraft.util.SystemUtil;


public class ServerProxy extends CommonProxy {

	public void postInit() {
		super.postInit();
		if (SystemUtil.getPropertyBoolean("analytics.enabled", false)) {
			FMLCommonHandler.instance().bus().register(Analytics.INSTANCE);
			MinecraftForge.EVENT_BUS.register(Analytics.INSTANCE);
		}
		FMLCommonHandler.instance().bus().register(ServerEnforcer.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ServerEnforcer.INSTANCE);
		FMLCommonHandler.instance().bus().register(ServerScoreboard.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ServerScoreboard.INSTANCE);
		ForgeChunkManager.setForcedChunkLoadingCallback(PolycraftMod.instance, null);
		
		
	}
}
