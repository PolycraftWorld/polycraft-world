package edu.utd.minecraft.mod.polycraft.inventory.textwall;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiConsent;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TextWallRenderHandler //extends PolycraftInventoryBlock.BasicRenderingHandler
{
	//private static final ResourceLocation img = new ResourceLocation("textures/entity/sign.png");
	public TextWallRenderHandler(Inventory config) {
		//super(config);
		//this.inventoryModel.
		// TODO Auto-generated constructor stub
	}
	/* TODO: update to 1.8
	@Override
	@SideOnly(Side.CLIENT)
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick) {
		super.renderTileEntityAt(tileEntity, x, y, z, tick);

		GL11.glPushMatrix();
		//From {@Link TileEntitySignRenderer}
		FontRenderer fontrenderer = this.func_147498_b();
		float f1 = 0.6666667F;
		float f3;
		f3 = 0.016666668F * f1; 
		f3 *=3; //scale Text here

		//does nothing??
		//GL11.glNormal3f(1.0F, 0.0F, -1.0F * f3);

		
		//get block metadata
		//move text as necessary.
		int rotation = tileEntity.getBlockMetadata();
		float rotScale = 0;
		//System.out.println("rotation: " + rotation);
		switch(rotation) {
			case 2: //faces SOUTH - TEXT IS PROPERLY DISPLAYED
				rotScale = 180F;
				//GL11.glTranslatef((float)x - 1.01F, (float)0,(float)z + 5F);
				GL11.glTranslatef((float)x - 3.25F, (float)y + 4F * f1,(float)z - .13F);
				GL11.glScalef(f3, -f3, f3);
				GL11.glNormal3f(1.0F, 0.0F, -1.0F * f3);
				GL11.glRotatef(-rotScale, 0.0F, 1.0F, 0.0F);
				break;
			case 4: //faces EAST - TEXT IS PROPERLY DISPLAYED
				rotScale = 90F;
				//GL11.glTranslatef((float)x - 0.13F, (float)y + 4F * f1,(float)z + 3.75F);
				GL11.glTranslatef((float)x - 0.13F, (float)y + 4F * f1,(float)z + 4.25F);
				GL11.glScalef(f3, -f3, f3);
				GL11.glRotatef(-rotScale, 0.0F, 1.0F, 0.0F);
				break;
			case 5: //faces WEST - TEXT IS PROPERLY DISPLAYED
				rotScale = -90F;
				//GL11.glTranslatef((float)x + 0F,(float)y + 4F,(float)z - 2F);
				//GL11.glTranslatef((float)x + 1.13F, (float)y + 3F,(float)z - 3.25F);
				GL11.glTranslatef((float)x + 1.13F, (float)y + 2.6F,(float)z - 3.25F);
				GL11.glScalef(f3, -f3, f3);
				GL11.glNormal3f(1.0F, 0.0F, -1.0F * f3);
				GL11.glRotatef(-rotScale, 0.0F, 1.0F, 0.0F);
				break;
			default: //faces NORTH - TEXT IS PROPERLY DISPLAYED
				rotScale = 0F;
				//attach text to the object
				//GL11.glTranslatef((float)x + 3.75F, (float)y + 3F,(float)z + 1.13F);
				GL11.glTranslatef((float)x + 4.25F, (float)y + 2.6F,(float)z + 1.13F);
				GL11.glScalef(f3, -f3, f3);
				GL11.glRotatef(-rotScale, 0.0F, 1.0F, 0.0F);
				break;
		}

		//For whatever reason, this has to go AFTER the above is set up.
		//I think the above sets up the "cursor" and then the fontrenderer begins 
		//drawing from that point?
		GL11.glDepthMask(false);
		byte b0 = 0;
		
		List<String> signText = new ArrayList<String>();
		signText.add("Data Sharing Policies: UT Dallas IRB");
		signText.add("");
		
		if(GuiConsent.consent) {
			signText.addAll(fontrenderer.listFormattedStringToWidth(I18n.format("gui.consent.finished"), (int) Math.floor((8/f3))));
		}
		else {
			signText.addAll(fontrenderer.listFormattedStringToWidth(I18n.format("gui.consent.nonegiven"), (int) Math.floor((8/f3))));
		}
		//String[] signText = new String[] {"UT Dallas IRB", "Right Click Here", "to review our terms", "and change your data preferences"};
		
		
		for (int i = 0; i < signText.size(); ++i)
		{
		    String s = (String) signText.get(i);
		    if(i == 0) {
		    	fontrenderer.drawString(s, (int) (-fontrenderer.getStringWidth(s) / 2 - .25/f3), i * 10 - signText.size() * 6, Format.getIntegerFromColor(Color.GREEN));
		    } else {
		    	fontrenderer.drawString(s, (int) (-fontrenderer.getStringWidth(s) / 2 - .25/f3), i * 10 - signText.size() * 6, b0);
		    }
		}
		
		GL11.glDepthMask(true);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		//For OpenGL, we must first PushMatrix(), do things to it, then PopMatrix().
		GL11.glPopMatrix();
		
	}*/
	
}
