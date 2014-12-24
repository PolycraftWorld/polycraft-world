package edu.utd.minecraft.mod.polycraft.proxy;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import edu.utd.minecraft.mod.polycraft.util.Analytics;

public class ServerProxy extends CommonProxy {

	public void postInit() {
		super.postInit();
		//TODO comment this back out when we figure out how to pass parameters in BeastNode
		//if (System.getProperty("analyticsEnabled") != null && Boolean.parseBoolean(System.getProperty("analyticsEnabled"))) {
			FMLCommonHandler.instance().bus().register(Analytics.INSTANCE);
			MinecraftForge.EVENT_BUS.register(Analytics.INSTANCE);
		//}
	}
}
