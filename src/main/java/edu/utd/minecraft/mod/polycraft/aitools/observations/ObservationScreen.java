package edu.utd.minecraft.mod.polycraft.aitools.observations;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.nbt.NBTTagCompound;

public class ObservationScreen implements IObservation{
	
	public final static int BYTES_PER_PIXEL = 4;

	@Override
	public void init(ExperimentTutorial exp) {
	}

	@Override
	public JsonElement getObservation(ExperimentTutorial exp, String args) {
		Gson gson = new Gson();
		int width = Minecraft.getMinecraft().getFramebuffer().framebufferTextureWidth;
        int height = Minecraft.getMinecraft().getFramebuffer().framebufferTextureHeight;
		
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * BYTES_PER_PIXEL);
		if (OpenGlHelper.isFramebufferEnabled())
        {
            GlStateManager.bindTexture(Minecraft.getMinecraft().getFramebuffer().framebufferTexture);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
        }
        else
        {
            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);
        }
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int i = (x + (width * y)) * BYTES_PER_PIXEL;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			}
		}
		try {
			// ImageIO.write(image, "PNG", new File("./observation_screen.png"));
			ImageIO.write(image, "PNG", bos);
			byte[] imgData = bos.toByteArray();
			JsonObject retJObject = new JsonObject();
			retJObject.addProperty("width", width);
			retJObject.addProperty("height", height);
			String encStr = Base64.getEncoder().encodeToString(imgData);
			retJObject.addProperty("data", encStr);
			return retJObject;
		} catch (IOException e) {
			System.err.println("Error saving out screen");
			e.printStackTrace();
		}

		JsonObject jobject = new JsonObject();
		jobject.addProperty("err", "INVALID");
		return jobject;
	}

	@Override
	public NBTTagCompound save() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void load(NBTTagCompound nbtObs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "screen";
	}
}
