package edu.utd.minecraft.mod.polycraft.proxy;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import edu.utd.minecraft.mod.polycraft.util.Analytics;

public class ServerProxy extends CommonProxy {

	public void postInit() {
		super.postInit();
		if (System.getProperty("analyticsEnabled") != null && Boolean.parseBoolean(System.getProperty("analyticsEnabled"))) {
			final Analytics analytics = new Analytics(
					Boolean.parseBoolean(System.getProperty("analyticsDebug")),
					System.getProperty("analyticsIntervalSpatial") == null ? null : Integer.parseInt(System.getProperty("analyticsIntervalSpatial")),
					System.getProperty("analyticsIntervalStatus") == null ? null : Integer.parseInt(System.getProperty("analyticsIntervalStatus")));
			FMLCommonHandler.instance().bus().register(analytics);
			MinecraftForge.EVENT_BUS.register(analytics);
		}
	}
}
