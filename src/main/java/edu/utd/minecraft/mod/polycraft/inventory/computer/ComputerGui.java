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

	public static final ResourceLocation computerBaseGui = new ResourceLocation(PolycraftMod.MODID,"textures/gui/new_computer_with_active_tabs_connected.png");
	private ComputerInventory computer;
	
	public ComputerGui(ComputerInventory inventory, InventoryPlayer playerInventory) {
//	public ComputerGui(InventoryPlayer invPlayer, ComputerInventory computer) {
	
		
		super(inventory, playerInventory, 379, false);
		//super(new ComputerContainer( invPlayer, computer));
		this.xSize=342;
		this.ySize=330;
		this.computer=computer;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY){
		glColor4f(1,1,1,1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(computerBaseGui);
		drawTexturedModalRect(0,0,0,0,xSize/2,ySize/2);
		/*Logger logger =  LogManager.getFormatterLogger("polycraft");
		logger.info("ok");
		logger.info(guiLeft);
		logger.info(guiTop);*/
	}
	
	@Override
	public void initGui () {
		
		super.initGui();
	}

	
}
