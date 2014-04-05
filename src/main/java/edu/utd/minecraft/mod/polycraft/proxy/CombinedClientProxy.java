package edu.utd.minecraft.mod.polycraft.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import edu.utd.minecraft.mod.polycraft.handler.PolycraftTickHandler;

public class CombinedClientProxy extends CommonProxy
{
	@Override
	public void preInit() {
		super.preInit();
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void postInit() {
		super.postInit();
		FMLCommonHandler.instance().bus().register(new PolycraftTickHandler());
	}
}
