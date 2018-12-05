package edu.utd.minecraft.mod.polycraft.util;

import java.io.File;

import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.util.ResourceLocation;

public class ThreadDownloadGUIImage extends SimpleTexture {

	private String url;
	private File resourceFile;
	private boolean isDownloaded;
	
	public ThreadDownloadGUIImage(String url, File file, ResourceLocation resourceLocation) {
		super(resourceLocation);
		// TODO Auto-generated constructor stub
	}

}
