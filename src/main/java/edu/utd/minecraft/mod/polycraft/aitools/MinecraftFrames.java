package edu.utd.minecraft.mod.polycraft.aitools;

import java.awt.Image;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.javafx.image.AlphaType;
import com.sun.javafx.image.PixelGetter;
import com.sun.javafx.image.PixelUtils;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import sun.awt.image.PixelConverter;

public class MinecraftFrames {

	
	/** A buffer to hold pixel values returned by OpenGL. */
    public static IntBuffer intBuffer;
    /** The built-up array that contains all the pixel values returned by OpenGL. */
    public static int[] pixelValues;


	public void init(ExperimentTutorial exp) {
	}

	public JsonElement getObservation(ExperimentTutorial exp, String args) {
		Gson gson = new Gson();
		int width = Minecraft.getMinecraft().getFramebuffer().framebufferTextureWidth;
        int height = Minecraft.getMinecraft().getFramebuffer().framebufferTextureHeight;
		
        int i = width * height;

        if (intBuffer == null || intBuffer.capacity() < i)
        {
            intBuffer = BufferUtils.createIntBuffer(i);
            pixelValues = new int[i];
        }
		
		intBuffer.clear();

        if (OpenGlHelper.isFramebufferEnabled())
        {
            GlStateManager.bindTexture(Minecraft.getMinecraft().getFramebuffer().framebufferTexture);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer)intBuffer);
        }
        else
        {
            GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer)intBuffer);
        }

        intBuffer.get(pixelValues);
		System.out.print("pixelValues length: " + pixelValues.length);
		JsonObject jobject = new JsonObject();
		jobject.add("img", gson.toJsonTree(pixelValues));
		return jobject;
	}
}
