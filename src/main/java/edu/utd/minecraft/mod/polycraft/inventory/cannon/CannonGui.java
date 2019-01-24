package edu.utd.minecraft.mod.polycraft.inventory.cannon;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.DoubleEntry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockPasswordDoor;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CannonGui  extends PolycraftInventoryGui<CannonInventory>{
	private final int ImageHeight = 250;
    private final int ImageWidth = 200;
    ResourceLocation Texture;
    private GuiButton buttonDone;
    private GuiTextField velocityText;
    private GuiTextField thetaText;
    private GuiTextField massText;
    private GuiConfigEntries guiConfig;
    private DoubleEntry test2;
    private World world;
	
	
	
	public CannonGui(CannonInventory inventory, InventoryPlayer playerInventory, World world) {
		super(inventory, playerInventory, 225, false);
		Texture = new ResourceLocation(PolycraftMod.MODID+":textures/gui/blank.png");
		this.world=world;
		// TODO Auto-generated constructor stub
	}

	@Override
    public void initGui() {
		super.initGui();
        buttonList.clear();
        //this.test2= new DoubleEntry(null, guiConfig, null);
        this.velocityText = new GuiTextField(this.fontRendererObj, this.width / 2 - 68, this.height/2-46, 130, 20);
        this.velocityText.width/=2;
        velocityText.setMaxStringLength(23);
       
        World world = this.inventory.getWorldObj();
        World w2=this.world;
        int x=this.inventory.xCoord;
        int y=this.inventory.yCoord;
        int z=this.inventory.zCoord;
        CannonBlock block=(CannonBlock)world.getBlock(x, y, z);
        //velocityText.setText(Double.toString(block.INSTANCE.velocity));
        this.velocityText.setFocused(true);
        
        this.thetaText = new GuiTextField(this.fontRendererObj, this.width / 2 - 68, this.height/2-16, 130, 20);
        this.thetaText.width/=2;
        thetaText.setMaxStringLength(23);
        //thetaText.setText(Double.toString(block.INSTANCE.theta));
        //this.thetaText.setFocused(true);
        
        this.massText = new GuiTextField(this.fontRendererObj, this.width / 2 + 8, this.height/2-46, 130, 20);
        this.massText.width/=2;
        massText.setMaxStringLength(10);
        //massText.setText(Double.toString(block.INSTANCE.mass));
        //this.massText.setFocused(true);
    	
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int i, int j, float f) {
    	super.drawScreen(i, j, f);
    	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
       
        //mc.getTextureManager().bindTexture(Texture);

        int offsetFromScreenLeft = (width - ImageWidth ) / 2;
        //drawTexturedModalRect(offsetFromScreenLeft, 2, 0, 0, ImageWidth, ImageHeight);
        int widthOfString;
        
        fontRendererObj.drawSplitString("Velocity", 
              offsetFromScreenLeft + 36, 34, 116, 0);
        fontRendererObj.drawSplitString("Angle", 
                offsetFromScreenLeft + 36, 34, 96, 0);
        fontRendererObj.drawSplitString("Mass", 
                offsetFromScreenLeft + 36, 34, 76, 0);
        this.velocityText.drawTextBox();
        this.thetaText.drawTextBox();
        this.massText.drawTextBox();
       
    }
    
    @Override
    public void onGuiClosed()
    {
    	super.onGuiClosed();
    	double v=Double.parseDouble(velocityText.getText());
    	double t=Double.parseDouble(thetaText.getText());
    	double m=Double.parseDouble(massText.getText());
        World world = this.inventory.getWorldObj();
        int x=this.inventory.xCoord;
        int y=this.inventory.yCoord;
        int z=this.inventory.zCoord;
        CannonBlock block=(CannonBlock)world.getBlock(x, y, z);
        //block.INSTANCE.velocity=Double.parseDouble(velocityText.getText());
        //block.INSTANCE.theta=Double.parseDouble(thetaText.getText());
        //block.INSTANCE.mass=Double.parseDouble(massText.getText());
        PolycraftMod.proxy.sendMessageToServerCannon(true);     
    }

   
    
    protected void keyTyped(char par1, int par2)
    {
    	if(!( par2== Keyboard.KEY_E  &&  this.velocityText.isFocused())) super.keyTyped(par1, par2);
        this.velocityText.textboxKeyTyped(par1, par2);
        
        if(!( par2== Keyboard.KEY_E  &&  this.thetaText.isFocused())) super.keyTyped(par1, par2);
        this.thetaText.textboxKeyTyped(par1, par2);
        
        if(!( par2== Keyboard.KEY_E  &&  this.massText.isFocused())) super.keyTyped(par1, par2);
        this.massText.textboxKeyTyped(par1, par2);
        
    }
    
    public void updateScreen()
    {
        super.updateScreen();
        this.velocityText.updateCursorCounter();
        this.thetaText.updateCursorCounter();
        this.massText.updateCursorCounter();
    }
    
    @Override
    protected void mouseClicked(int x, int y, int btn) {
        super.mouseClicked(x, y, btn);
        this.velocityText.mouseClicked(x, y, btn);
        this.thetaText.mouseClicked(x, y, btn);
        this.massText.mouseClicked(x, y, btn);
    }

	private static final int[][] tappedCoordOffsets = new int[][] { new int[] { 1, 0 }, new int[] { 0, 1 },
			new int[] { 0, -1 }, new int[] { -1, 0 } };
	private static final String CANNON = "Cannon";
	


	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
		//int color=0x000000;;
		//this.fontRendererObj.drawString(CANNON, 55, 1, color);
	}
	
	

}
