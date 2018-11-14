package edu.utd.minecraft.mod.polycraft.inventory.textwall;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TextWallRenderHandler extends PolycraftInventoryBlock.BasicRenderingHandler {
	private static final ResourceLocation img = new ResourceLocation("textures/entity/sign.png");
	public TextWallRenderHandler(Inventory config) {
		super(config);
		//this.inventoryModel.
		// TODO Auto-generated constructor stub
	}
	
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
		f3 *=7; //scale Text here

		//does nothing??
		//GL11.glNormal3f(1.0F, 0.0F, -1.0F * f3);

		
		//get block metadata
		//move text as necessary.
		int rotation = tileEntity.getBlockMetadata();
		float rotScale = 0;
		//System.out.println("rotation: " + rotation);
		switch(rotation) {
			case 2: //faces WEST
				rotScale = 180F;
				//GL11.glTranslatef((float)x - 1.01F, (float)0,(float)z + 5F);
				GL11.glTranslatef((float)x - 3.25F, (float)y + 4F * f1,(float)z - .13F);
				GL11.glScalef(f3, -f3, f3);
				GL11.glNormal3f(1.0F, 0.0F, -1.0F * f3);
				GL11.glRotatef(-rotScale, 0.0F, 1.0F, 0.0F);
				break;
			case 4:
				rotScale = 90F;
				GL11.glTranslatef((float)x - 0.13F, (float)y + 4F * f1,(float)z + 3.75F);
				GL11.glScalef(f3, -f3, f3);
				GL11.glRotatef(-rotScale, 0.0F, 1.0F, 0.0F);
				break;
			case 5:
				rotScale = -90F;
				//GL11.glTranslatef((float)x + 0F,(float)y + 4F,(float)z - 2F);
				GL11.glTranslatef((float)x + 1.13F, (float)y + 3F,(float)z - 3.25F);
				GL11.glScalef(f3, -f3, f3);
				GL11.glNormal3f(1.0F, 0.0F, -1.0F * f3);
				GL11.glRotatef(-rotScale, 0.0F, 1.0F, 0.0F);
				break;
			default:
				rotScale = 0F;
				//attach text to the object
				
				GL11.glTranslatef((float)x + 3.75F, (float)y + 3F,(float)z + 1.13F);
				GL11.glScalef(f3, -f3, f3);
				GL11.glRotatef(-rotScale, 0.0F, 1.0F, 0.0F);
				break;
		}

		//For whatever reason, this has to go AFTER the above is set up.
		//I think the above sets up the "cursor" and then the fontrenderer begins 
		//drawing from that point?
		GL11.glDepthMask(false);
		byte b0 = 0;
		String[] signText = new String[] {"ABCDEFG", "ABCDE", "ABC"};
		
		for (int i = 0; i < signText.length; ++i)
		{
		    String s = signText[i];
		    fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, i * 10 - signText.length * 5, b0);
		}
		
		GL11.glDepthMask(true);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		//For OpenGL, we must first PushMatrix(), do things to it, then PopMatrix().
		GL11.glPopMatrix();
		
	}

}
