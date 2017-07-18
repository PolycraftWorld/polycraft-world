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

		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

		//region Draw Basic Tabs
		int srcX,srcY;
		for (int i = 0; i<tabs.length; i++){
			int side = 2;
//			if (i>2)
//				side = 2;
			GuiRectangle rectangle = tabs[i];
			srcX = 342/2;
			srcY = 145/2;
			if ((rectangle.inRect(this, mouseX, mouseY, side) && tabs[i] != activeTab))
				srcY = 84/2; //darken tab when hovering over it
			if (tabs[i] == activeTab)
				srcY = 23/2;
			rectangle.draw(this, srcX, srcY, side);
		}
		//endregion Draw Basic Tabs
		
		//region icons on tabs 
			//41:00 on youtube
		//endregion icons on tabs
		
		//region Tab Contents
		if (activeTab == tabs[0]){
			System.out.println("first tab");
			//draw this tab's stuff
//			Minecraft.getMinecraft().getTextureManager().bindTexture(appleIcon);
//			drawTexturedModalRect(guiLeft + 3, guiTop + 3,0,0,170,90);
		}
		if (activeTab == tabs[1]){
			System.out.println("second tab");
			//draw this tab's stuff
		}
		if (activeTab == tabs[2]){
			System.out.println("third tab");
			//draw this tab's stuff		
		}
		if (activeTab == tabs[3]){
			System.out.println("fourth tab");
			//draw this tab's stuff		
		}
		//endregion Tab Contents

	}
	
	@Override
	protected void mouseClicked ( int mouseX, int mouseY, int mouseButton){
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
		GuiRectangle rectangle;
		for (int i = 0; i<tabs.length; i++){
			rectangle = tabs[i];
			int side = 2;
//			if (i>2)
//				side =2;
			if(rectangle.inRect(this, mouseX, mouseY, side)){
				activeTab = tabs[i];
				break;
			}
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
