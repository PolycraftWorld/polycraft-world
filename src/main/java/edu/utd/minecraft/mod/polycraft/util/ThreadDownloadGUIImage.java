package edu.utd.minecraft.mod.polycraft.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ThreadDownloadGUIImage extends SimpleTexture {

	private static final AtomicInteger threadDownloadCounter = new AtomicInteger(0);
	
	private String url;
	private File resourceFile;
	private boolean isDownloaded;
	private BufferedImage bufferedImage;
    private Thread imageThread;
	
	public ThreadDownloadGUIImage(String url, File file, ResourceLocation resourceLocation) {
		super(resourceLocation);
		this.url = url;
		this.resourceFile = null;//new File(resourceLocation.getResourcePath());
		
	}
	
	 public void loadTexture(IResourceManager resourceManager) throws IOException
	    {
	        if (this.bufferedImage == null && this.textureLocation != null)
	        {
	        	try {
	        		super.loadTexture(resourceManager);
	        	} catch(FileNotFoundException ex) {
	        		System.out.println("NO file found!");
	        	}
	        }
	        else {
	        	TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), this.bufferedImage, false, false);
	        }

	        if (this.imageThread == null)
	        {
	            if (this.resourceFile != null && this.resourceFile.isFile())
	            {
	               // logger.debug("Loading http texture from local cache ({})", new Object[] {this.resourceFile});

	                try
	                {
	                    this.bufferedImage = ImageIO.read(this.resourceFile);

	                }
	                catch (IOException ioexception)
	                {
	                   this.downloadTexture();
	                }
	            }
	            else
	            {
	                this.downloadTexture();
	            }
	        }
	    }
	 
	 protected void downloadTexture() {
		 this.imageThread = new Thread("GUI Texture Downloader #" + threadDownloadCounter.incrementAndGet())
	        {
	            private static final String __OBFID = "CL_00001050";
	            public void run()
	            {
	                HttpURLConnection httpurlconnection = null;
	               // ThreadDownloadImageData.logger.debug("Downloading http texture from {} to {}", new Object[] {ThreadDownloadImageData.this.imageUrl, ThreadDownloadImageData.this.field_152434_e});

	                try
	                {
	                    httpurlconnection = (HttpURLConnection)(new URL(ThreadDownloadGUIImage.this.url)).openConnection(Minecraft.getMinecraft().getProxy());
	                    httpurlconnection.setDoInput(true);
	                    httpurlconnection.setDoOutput(false);
	                    httpurlconnection.connect();

	                    if (httpurlconnection.getResponseCode() / 100 == 2)
	                    {
	                        BufferedImage bufferedimage;

	                        if (ThreadDownloadGUIImage.this.resourceFile != null)
	                        {
	                            FileUtils.copyInputStreamToFile(httpurlconnection.getInputStream(), ThreadDownloadGUIImage.this.resourceFile);
	                            bufferedimage = ImageIO.read(ThreadDownloadGUIImage.this.resourceFile);
	                        }
	                        else
	                        {
	                        	ThreadDownloadGUIImage.this.resourceFile = new File("test123.png");
	                        	FileUtils.copyInputStreamToFile(httpurlconnection.getInputStream(), ThreadDownloadGUIImage.this.resourceFile);
	                            bufferedimage = ImageIO.read(ThreadDownloadGUIImage.this.resourceFile);
	                        }

	                        ThreadDownloadGUIImage.this.setBufferedImage(bufferedimage); //do we need the image buffer?
	                        return;
	                    }
	                }
	                catch (Exception exception)
	                {
	                    //ThreadDownloadImageData.logger.error("Couldn\'t download http texture", exception);
	                    return;
	                }
	                finally
	                {
	                    if (httpurlconnection != null)
	                    {
	                        httpurlconnection.disconnect();
	                    }
	                }
	            }
	        };
	        this.imageThread.setDaemon(true);
	        this.imageThread.start();
	    }

	protected void setBufferedImage(BufferedImage bufferedimage2) {
		// TODO Auto-generated method stub
		this.bufferedImage = bufferedimage2;
		//ImageIO.
		
	}

}
