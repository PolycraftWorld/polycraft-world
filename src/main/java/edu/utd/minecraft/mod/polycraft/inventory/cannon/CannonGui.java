package edu.utd.minecraft.mod.polycraft.inventory.cannon;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

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

import java.io.IOException;

public class CannonGui  extends PolycraftInventoryGui<CannonInventory>{
	private final int ImageHeight = 200;
    private final int ImageWidth = 200;
    ResourceLocation Texture;
    private GuiButton buttonVPlus;
    private GuiButton buttonVMinus;
    private GuiButton buttonTPlus;
    private GuiButton buttonTMinus;
    private GuiButton buttonMPlus;
    private GuiButton buttonMMinus;
    private GuiTextField velocityText;
    private GuiTextField thetaText;
    private GuiTextField massText;
    public double velocity;
    public double theta;
    public double mass;
    //private GuiConfigEntries guiConfig;

	
	
	public CannonGui(CannonInventory inventory, InventoryPlayer playerInventory) {
		super(inventory, playerInventory, 210, false);
		Texture = new ResourceLocation(PolycraftMod.MODID+":textures/gui/blank.png");
		//this.velocityText.setText("0.0");
		//this.thetaText.setText("0.0");
		//this.massText.setText("0.0");
		
		
		// TODO Auto-generated constructor stub
	}

	@Override
    public void initGui() {
		super.initGui();
        buttonList.clear();
        int offsetFromScreenLeft = (width - ImageWidth ) / 2;
        int offsetFromScreenTop = (height - ImageHeight ) / 2;
        buttonVPlus = new GuiButton(0, width / 2 -24, offsetFromScreenTop+ImageHeight - 165, 
                20, 20, I18n.format("+", new Object[0]));
        buttonVMinus = new GuiButton(0, width / 2 - 82,offsetFromScreenTop+ImageHeight - 165, 
                20, 20, I18n.format("-", new Object[0]));
        buttonMPlus = new GuiButton(0, width / 2 + 57, offsetFromScreenTop+ImageHeight - 165, 
                20, 20, I18n.format("+", new Object[0]));
        buttonMMinus = new GuiButton(0, width / 2 -1, offsetFromScreenTop+ImageHeight - 165, 
                20, 20, I18n.format("-", new Object[0]));
        buttonTPlus = new GuiButton(0, width / 2 -24, offsetFromScreenTop+ImageHeight - 115, 
                20, 20, I18n.format("+", new Object[0]));
        buttonTMinus = new GuiButton(0, width / 2 -82,offsetFromScreenTop+ImageHeight - 115, 
                20, 20, I18n.format("-", new Object[0]));
        buttonList.add(buttonVPlus);
        buttonList.add(buttonVMinus);
        buttonList.add(buttonTPlus);
        buttonList.add(buttonTMinus);
        buttonList.add(buttonMPlus);
        buttonList.add(buttonMMinus);
        
        //this.test2= new DoubleEntry(null, guiConfig, null);
        this.velocityText = new GuiTextField(40, this.fontRendererObj, this.width / 2 - 58, this.height/2-66, 60, 20);
        this.velocityText.width/=2;
        velocityText.setMaxStringLength(3);

        velocityText.setText(Double.toString(this.inventory.velocity));
        this.velocityText.setFocused(true);
        
        this.thetaText = new GuiTextField(41, this.fontRendererObj, this.width / 2 - 58, this.height/2-16, 60, 20);
        this.thetaText.width/=2;
        thetaText.setMaxStringLength(3);
        thetaText.setText(Double.toString(this.inventory.theta));
        //this.thetaText.setFocused(true);
        
        this.massText = new GuiTextField(42, this.fontRendererObj, this.width / 2 + 23, this.height/2-66, 60, 20);
        this.massText.width/=2;
        massText.setMaxStringLength(3);
        massText.setText(Double.toString(this.inventory.mass));
        //this.massText.setFocused(true);
        this.theta=Double.parseDouble(thetaText.getText());
        GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(6.5F);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(ImageWidth+50, ImageHeight-95);
        GL11.glVertex2d(ImageWidth+50+(20*Math.cos(-this.theta/180*Math.PI)), ImageHeight-95+(20*Math.sin(-this.theta/180*Math.PI)));
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);

    	
    }
	
    @Override
    public void actionPerformed(GuiButton button) {
    	if (button == buttonVPlus)
        {
    		if(hasDouble(velocityText.getText()))
    		{
    			this.velocity=Double.parseDouble(velocityText.getText());
    			this.velocity+=1;
    			if(velocity<0)
    			{
    				velocity=0;
    			}
    			velocityText.setText(Double.toString(this.velocity));
    		}
        }
    	
    	if (button == buttonVMinus)
        {
    		if(hasDouble(velocityText.getText()))
    		{
    			this.velocity=Double.parseDouble(velocityText.getText());
    			this.velocity-=1;
    			if(velocity<0)
    			{
    				velocity=0;
    			}
    			velocityText.setText(Double.toString(this.velocity));
    			
    			
    		}
        }
    	
    	if (button == buttonTPlus)
        {
    		if(hasDouble(thetaText.getText()))
    		{
    			this.theta=Double.parseDouble(thetaText.getText());
    			this.theta+=1;
    			if(theta<0)
    			{
    				theta=0;
    			}
    			thetaText.setText(Double.toString(this.theta));
    		}
        }
    	
    	if (button == buttonTMinus)
        {
    		if(hasDouble(thetaText.getText()))
    		{
    			this.theta=Double.parseDouble(thetaText.getText());
    			this.theta-=1;
    			if(theta<0)
    			{
    				theta=0;
    			}
    			thetaText.setText(Double.toString(this.theta));
    		}
        }
    	
    	if (button == buttonMPlus)
        {
    		if(hasDouble(massText.getText()))
    		{
    			this.mass=Double.parseDouble(massText.getText());
    			this.mass+=1;
    			if(mass<0)
    			{
    				mass=0;
    			}
    			massText.setText(Double.toString(this.mass));
    		}
        }
    	
    	if (button == buttonMMinus)
        {
    		if(hasDouble(massText.getText()))
    		{
    			this.mass=Double.parseDouble(massText.getText());
    			this.mass-=1;
    			if(mass<0)
    			{
    				mass=0;
    			}
    			massText.setText(Double.toString(this.mass));
    		}
        }

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
        int offsetFromScreenTop = (height - ImageHeight ) / 2;
        //drawTexturedModalRect(offsetFromScreenLeft, 2, 0, 0, ImageWidth, ImageHeight);
        int widthOfString;
        
        fontRendererObj.drawSplitString("Velocity", 
              offsetFromScreenLeft + 32, offsetFromScreenTop+20, 116, 0);
        fontRendererObj.drawSplitString("Angle", 
                offsetFromScreenLeft + 32, offsetFromScreenTop+70, 96, 0);
        fontRendererObj.drawSplitString("Mass", 
                offsetFromScreenLeft + 108, offsetFromScreenTop+20, 76, 0);
        this.velocityText.drawTextBox();
        this.thetaText.drawTextBox();
        this.massText.drawTextBox();
        
        fontRendererObj.drawSplitString("E", 
                offsetFromScreenLeft + 158, offsetFromScreenTop+82, 50, 0);
        fontRendererObj.drawSplitString("N", 
                offsetFromScreenLeft + 135, offsetFromScreenTop+57, 50, 0);
        fontRendererObj.drawSplitString("W", 
                offsetFromScreenLeft + 110, offsetFromScreenTop+82, 50, 0);
        fontRendererObj.drawSplitString("S", 
                offsetFromScreenLeft + 134, offsetFromScreenTop+106, 50, 0);
        
	 	//GL11.glEnd();
        GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(6.5F);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(offsetFromScreenLeft+ImageWidth-63, offsetFromScreenTop+ImageHeight-115);
        GL11.glVertex2d(offsetFromScreenLeft+ImageWidth-63+(20*Math.cos(-this.theta/180*Math.PI)), offsetFromScreenTop+ImageHeight-115+(20*Math.sin(-this.theta/180*Math.PI)));
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        

       
    }
    
    public boolean hasDouble(String s)
    {
    	if(s.length()==0)
    	{
    		return false;
    	}
    	else 
    	{
    		for(int c=0;c<s.length();c++)
    		{
    			if(!(Character.isDigit(s.charAt(c)) || s.charAt(c)=='.'))
        		{
        			return false;
        		}
    		}
    	}
    	
    	return true;
    }
    
    @Override
    public void onGuiClosed()
    {
    	super.onGuiClosed();
    	String v=velocityText.getText();
    	String t=thetaText.getText();
    	String m=massText.getText();
    	boolean test=true;
    	if(v.length()==0 || t.length()==0 || m.length()==0)
    	{
    		test=false;
    	}
    	for(int c=0;c<v.length();c++)
    	{
    		if(!(Character.isDigit(v.charAt(c)) || v.charAt(c)=='.'))
    		{
    			test=false;
    		}

    	}
    	for(int c=0;c<t.length();c++)
    	{
    		if(!(Character.isDigit(t.charAt(c)) || t.charAt(c)=='.'))
    		{
    			test=false;
    		}
    	}
    	for(int c=0;c<m.length();c++)
    	{
    		if(!(Character.isDigit(m.charAt(c)) || m.charAt(c)=='.'))
    		{
    			test=false;
    		}
    	}
    	
       if(test)
       {
	    	int x=this.inventory.getPos().getX();
	    	int y=this.inventory.getPos().getY();
	    	int z=this.inventory.getPos().getZ();
	        this.velocity=Double.parseDouble(velocityText.getText());
	        this.theta=Double.parseDouble(thetaText.getText());
	        this.mass=Double.parseDouble(massText.getText());
	    	PolycraftMod.proxy.sendMessageToServerCannon (x , y, z, velocity, theta, mass,0);
	    	
	    		
	        this.inventory.velocity=Double.parseDouble(velocityText.getText());
	        this.inventory.theta=Double.parseDouble(thetaText.getText());
	        this.inventory.mass=Double.parseDouble(massText.getText());
	       
       }
       else
       {
    	   //TODO something?
       }
        

        
             
    }
    
    
   
    
    protected void keyTyped(char par1, int par2) throws IOException {
    	if(!( par2== Keyboard.KEY_E  &&  this.velocityText.isFocused())) super.keyTyped(par1, par2);
        this.velocityText.textboxKeyTyped(par1, par2);
        
        if(!( par2== Keyboard.KEY_E  &&  this.thetaText.isFocused())) super.keyTyped(par1, par2);
        this.thetaText.textboxKeyTyped(par1, par2);
        
        if(!( par2== Keyboard.KEY_E  &&  this.massText.isFocused())) super.keyTyped(par1, par2);
        this.massText.textboxKeyTyped(par1, par2);
        
    }
    
    @Override
    public void updateScreen()
    {
    	
        //super.updateScreen();
        this.velocityText.updateCursorCounter();
        this.thetaText.updateCursorCounter();
        this.massText.updateCursorCounter();
    }
    
    @Override
    protected void mouseClicked(int x, int y, int btn) throws IOException {        super.mouseClicked(x, y, btn);
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
