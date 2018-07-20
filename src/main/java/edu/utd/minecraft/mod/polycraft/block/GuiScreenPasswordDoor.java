package edu.utd.minecraft.mod.polycraft.block;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;


public class GuiScreenPasswordDoor extends GuiScreen {
	private final int ImageHeight = 256;
    private final int ImageWidth = 256;
    public static final int GUI_ID = 30;
    private static ResourceLocation Texture;
    private GuiButton buttonDone;
    private BlockPasswordDoor door;
    private EntityPlayer player;
    private int x, y, z;
    private GuiTextField text;
    
    public GuiScreenPasswordDoor(BlockPasswordDoor door, EntityPlayer player, int x, int y, int z) {
        System.out.print("gui constructor.\n");
        Texture = new ResourceLocation(PolycraftMod.MODID+":textures/gui/doorGUI.png");
        this.door = door;
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
        
    }

    @Override
    public void initGui() {
        buttonList.clear();
        buttonDone = new GuiButton(0, width / 2 + 2, ImageHeight - 40, 
                98, 20, I18n.format("gui.done", new Object[0]));
        buttonList.add(buttonDone);
        
        this.text = new GuiTextField(this.fontRendererObj, this.width / 2 - 68, this.height/2-46, 137, 20);
        text.setMaxStringLength(23);
        text.setText("sample text");
        this.text.setFocused(true);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int i, int j, float f) {
    	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
       
        mc.getTextureManager().bindTexture(Texture);

        int offsetFromScreenLeft = (width - ImageWidth ) / 2;
        drawTexturedModalRect(offsetFromScreenLeft, 2, 0, 0, ImageWidth, ImageHeight);
        int widthOfString;
        
        fontRendererObj.drawSplitString("This will be a test question", 
              offsetFromScreenLeft + 36, 34, 116, 0);
        this.text.drawTextBox();
        super.drawScreen(i, j, f);
    }

    @Override
    public void actionPerformed(GuiButton button) {
    	if (button == buttonDone)
        {
    		if(this.text.getText().contains("fatuous")) {
        		door.open(this.player.worldObj, x,y,z, this.player);
                mc.displayGuiScreen((GuiScreen)null);
    		}else {
    			if (!player.worldObj.isRemote)
    		    {
    		        PolycraftMod.proxy.sendMessageToServerClientFailedDoorPass(true); 
    		    }
    			mc.displayGuiScreen((GuiScreen)null);
    		}
        }

    }
    
    protected void keyTyped(char par1, int par2)
    {
    	if(!( par2== Keyboard.KEY_E  &&  this.text.isFocused())) super.keyTyped(par1, par2);
        this.text.textboxKeyTyped(par1, par2);
    }
    
    public void updateScreen()
    {
        super.updateScreen();
        this.text.updateCursorCounter();
    }
    
    @Override
    protected void mouseClicked(int x, int y, int btn) {
        super.mouseClicked(x, y, btn);
        this.text.mouseClicked(x, y, btn);
    }
}