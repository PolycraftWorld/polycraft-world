package edu.utd.minecraft.mod.polycraft.inventory.computer;

import static org.lwjgl.opengl.GL11.glColor4f;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ComputerGui extends /*GuiContainer */ PolycraftInventoryGui<ComputerInventory>{

	public static final ResourceLocation computerBaseGui = new ResourceLocation(PolycraftMod.MODID,"textures/gui/newer_computer_with_active_tabs_separated_darkened.png");
	private ComputerInventory computer;
	private final GuiRectangle[] tabs;
	private GuiRectangle activeTab;
	
	public ComputerGui(ComputerInventory inventory, InventoryPlayer playerInventory, int xSize, int ySize) {
//	public ComputerGui(InventoryPlayer invPlayer, ComputerInventory computer) {
	
		
		super(inventory, playerInventory);
		this.xSize=xSize;
		this.ySize=ySize;
		//super(new ComputerContainer( invPlayer, computer));
//		this.xSize=342;
//		this.ySize=330;
//		this.computer=computer;
		tabs = new GuiRectangle[4];
		tabs[0] = new GuiRectangle(336,23,52,60);
		tabs[1] = new GuiRectangle(336,84,52,60);
		tabs[2] = new GuiRectangle(336,145,52,60);
		tabs[3] = new GuiRectangle(336,206,52,60);
		activeTab = tabs[0];
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY){
		glColor4f(1,1,1,1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(computerBaseGui);
		drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
		int srcX,srcY;
		for (int i = 0; i<tabs.length; i++){
			int side = 2;
//			if (i>2)
//				side = 2;
			GuiRectangle rectangle = tabs[i];
			srcX = 342/2;
			srcY = 145/2;
			if ((rectangle.inRect(this, mouseX, mouseY, side) && tabs[i] != activeTab) || false)
				srcY = 84/2;
			if (tabs[i] == activeTab)
				srcY = 23/2;
			rectangle.draw(this, srcX, srcY, side);
		}
	}
	
	@Override
	public void initGui () {
		
		super.initGui();
	}

	public int getLeft(){
		return guiLeft;
	}
	
	public int getTop(){
		return guiTop;
	}
	
	public int getXSize(){
		return xSize;
	}
	
	public int getYSize(){
		return ySize;
	}
}
