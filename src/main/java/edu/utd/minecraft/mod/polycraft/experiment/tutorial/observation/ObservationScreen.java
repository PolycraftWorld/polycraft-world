package edu.utd.minecraft.mod.polycraft.experiment.tutorial.observation;

import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public class ObservationScreen implements IObservation{

	/** A buffer to hold pixel values returned by OpenGL. */
    public static IntBuffer pixelBuffer;
    /** The built-up array that contains all the pixel values returned by OpenGL. */
    public static int[] pixelValues;

	@Override
	public void init(ExperimentTutorial exp) {
	}

	@Override
	public JsonElement getObservation(ExperimentTutorial exp, String args) {
		Gson gson = new Gson();
//		int width = Minecraft.getMinecraft().getFramebuffer().framebufferTextureWidth;
//        int height = Minecraft.getMinecraft().getFramebuffer().framebufferTextureHeight;
//		
//        int i = width * height;
//
//        if (pixelBuffer == null || pixelBuffer.capacity() < i)
//        {
//            pixelBuffer = BufferUtils.createIntBuffer(i);
//            pixelValues = new int[i];
//        }
		
//		pixelBuffer.clear();
//
//        if (OpenGlHelper.isFramebufferEnabled())
//        {
//            GlStateManager.bindTexture(Minecraft.getMinecraft().getFramebuffer().framebufferTexture);
//            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer)pixelBuffer);
//        }
//        else
//        {
//            GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer)pixelBuffer);
//        }
//
//        pixelBuffer.get(pixelValues);
		//System.out.print("pixelValues length: " + pixelValues.length);
		JsonObject jobject = new JsonObject();
		jobject.add("img", gson.toJsonTree(pixelValues));
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
