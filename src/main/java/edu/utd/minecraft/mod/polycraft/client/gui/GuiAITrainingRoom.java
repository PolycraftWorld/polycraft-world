package edu.utd.minecraft.mod.polycraft.client.gui;

import static org.lwjgl.opengl.GL11.glColor4f;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiExperimentConfig.ConfigSlider;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonCycle;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonDropDown;
import edu.utd.minecraft.mod.polycraft.client.gui.api.PolycraftGuiScreenBase;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import edu.utd.minecraft.mod.polycraft.item.ItemAITool;
import edu.utd.minecraft.mod.polycraft.item.ItemAITool.BlockType;
import edu.utd.minecraft.mod.polycraft.item.ItemDevTool;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.CollectMessage;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.aitool.GenerateMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiAITrainingRoom extends PolycraftGuiScreenBase {
	
	private EntityPlayer player;
//	private int x, y, z;
	GuiSlider widthSlider;
	GuiSlider lengthSlider;
	GuiSlider heightSlider;
	GuiButton genBtn;
	GuiButton saveBtn;
	GuiButton loadBtn;
	GuiPolyButtonDropDown blockTypeDropDown;
	GuiCheckBox wallCheck;
    private static final ResourceLocation background_image = new ResourceLocation(PolycraftMod.getAssetNameString("textures/gui/hospital_old.png"));
	private ItemAITool AITool;
	
	public GuiAITrainingRoom(EntityPlayer player)
	{
        this.player = player;
        if(player.getHeldItem().getItem() instanceof ItemAITool)
       		this.AITool = (ItemAITool) player.getHeldItem().getItem();
//        this.x = x;
//        this.y = y;
//        this.z = z;
	}
	
	public void addBtn(GuiButton btn) {
    	this.buttonList.add(btn);
    }
	
	public void removeBtn(GuiButton btn) {
		this.buttonList.remove(btn);
	}

	
	public void initGui()
    {
	    int i = (this.width) / 2;
	    int j = (this.height) / 2; //old was 200
				
		widthSlider = new GuiSlider(0,i-90,j-75,120,20,"Width: "," Block(s)",1,64,AITool.getWidth(),false,true,null);
		lengthSlider = new GuiSlider(1,i-90,j-45,120,20,"Length: "," Block(s)",1,64,AITool.getLength(),false,true,null);
		heightSlider = new GuiSlider(2,i-90,j+35,120,20,"Height: "," Block(s)",1,16,AITool.getHeight(),false,true,null);
		blockTypeDropDown = new GuiPolyButtonDropDown(5, i-90, j-15, 120, 20,AITool.getBlockType());
		wallCheck = new GuiCheckBox(3, i-90, j+15, "Walls?", AITool.getWalls());
		heightSlider.enabled=wallCheck.isChecked();
		
		genBtn = new GuiButton(4, i-20, j+65, 90, 20, "Generate");
		saveBtn = new GuiButton(4, i-120, j+65, 45, 20, "Save");
		loadBtn = new GuiButton(4, i-70, j+65, 45, 20, "Load");
		
		addBtn(widthSlider);
		addBtn(lengthSlider);
		addBtn(genBtn);
		addBtn(wallCheck);
		addBtn(heightSlider);
		addBtn(blockTypeDropDown);
		addBtn(saveBtn);
		addBtn(loadBtn);
		//int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr)
    }
	
	 protected void actionPerformed(GuiButton button) {
		 super.actionPerformed(button);
		 if(button==genBtn)
		 {
			 
			 	AITool.save();
				List<Object> params = new ArrayList<Object>();
				params.add(AITool.getWalls());
				params.add(AITool.getWidth());
				params.add(AITool.getLength());
				params.add(AITool.getHeight());
				params.add(AITool.getBlockType());
				PolycraftMod.SChannel.sendToServer(new GenerateMessage(params));
				this.exitGuiScreen();
//			 ClientEnforcer.INSTANCE.sendAIToolGeneration();
		 }
		 if(button==loadBtn)
		 {
			   
			 	AITool.load();
			 	
			 	int i = (this.width) / 2;
			    int j = (this.height) / 2; //old was 200
			    
			    widthSlider.setValue(AITool.getWidth());
			    widthSlider.updateSlider();
			    lengthSlider.setValue(AITool.getLength());
			    lengthSlider.updateSlider();
			    heightSlider.setValue(AITool.getHeight());
			    heightSlider.updateSlider();
			    blockTypeDropDown.setCurrentOpt(AITool.getBlockType());
			    wallCheck.setIsChecked(AITool.getWalls());
//			 ClientEnforcer.INSTANCE.sendAIToolGeneration();
		 }
		 if(button==saveBtn)
		 {
			 
			 	AITool.save();
//			 ClientEnforcer.INSTANCE.sendAIToolGeneration();
		 }
		 if(!blockTypeDropDown.open)
		 {
			 if(button==blockTypeDropDown)
			 {
				 for(GuiButton btn: this.buttonList)
				 {
					 if(btn!=blockTypeDropDown)
						 btn.enabled=false;
				 }
				 
				 blockTypeDropDown.addButtons(this.buttonList);
				 blockTypeDropDown.open=true;
				// buttons =blockTypeDropDown.getButtons();

//				 for(int c=0;c<buttons.length;c++)
//				 {
//					
//					
//					 addBtn(buttons[c]);
//					 
//					 
//				 }
			 }
		 }
		 else if(blockTypeDropDown.open)
		 {
			 if(blockTypeDropDown.actionPerformed(button))
			 {
				 
				 blockTypeDropDown.removeButtons(this.buttonList);
				 this.AITool.setBlockType((BlockType) this.blockTypeDropDown.getCurrentOpt());
				 blockTypeDropDown.open=false;
				 
				 for(GuiButton btn: this.buttonList)
				 {
					 btn.enabled=true;
				 }

				 
//					 for(int c=0;c<buttons.length;c++)
//					 {
//						 //if(buttons[c].displayString!=blockTypeDropDown.displayString)
//						 removeBtn(buttons[c]);
//						 
//					 }
			 }
			 if(button==blockTypeDropDown)
			 {

				 blockTypeDropDown.removeButtons(this.buttonList);
				 this.AITool.setBlockType((BlockType) this.blockTypeDropDown.getCurrentOpt());
				 blockTypeDropDown.open=false;
				 
				 for(GuiButton btn: this.buttonList)
				 {
					 btn.enabled=true;
				 }
			 }
			 
		 }
		
	 }
	

	@Override
	protected void exitGuiScreen() {
		this.mc.displayGuiScreen((GuiScreen)null);
        this.mc.setIngameFocus();
		
	}
	
	 public void drawScreen(int mouseX, int mouseY, float otherValue)
    {
        this.drawDefaultBackground();	        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        super.drawScreen(mouseX, mouseY, otherValue);
    }
	
	public void drawDefaultBackground()
	{
	    super.drawDefaultBackground();
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    this.mc.getTextureManager().bindTexture(background_image);
	    //PolycraftMod.logger.debug("Screen width & Height: " + this.width + " " + this.height);
	    //System.out.println("Screen width & Height: " + this.width + " " + this.height);
	    int i = (this.width - 248) / 2;
	    int j = (this.height - 184) / 2; //old was 200
	   // this.drawTexturedModalRect(i, j, 0, 0, 248, screenContainerHeight + 30);
	    this.drawTexturedModalRect(i, j, 0, 0, 248, 184);
	}
	
	@Override
	 protected void mouseReleased(int mouseX, int mouseY, int state)
	 {
		super.mouseReleased(mouseX, mouseY, state);
		this.AITool.setLength(this.lengthSlider.getValueInt());
		this.AITool.setWidth(this.widthSlider.getValueInt());
		this.AITool.setHeight(this.heightSlider.getValueInt());
		this.AITool.setWalls(this.wallCheck.isChecked());
		this.AITool.setBlockType((BlockType) this.blockTypeDropDown.getCurrentOpt());
		heightSlider.enabled=wallCheck.isChecked();
	       
	 }
	
	@Override
    protected void mouseClicked(int x, int y, int mouseEvent) throws IOException {
    	super.mouseClicked(x, y, mouseEvent);
//    	if(blockTypeDropDown.open)
//		{
//	    	boolean menuPressed=false;
//	        for (int i = 0; i < this.buttonList.size(); ++i)
//	        {
//	        	
//	            GuiButton guibutton = (GuiButton)this.buttonList.get(i);
//	            if(!guibutton.enabled)
//	            	continue;
//	
//	            if (guibutton.mousePressed(this.mc, x, y))
//	            {
//	            	menuPressed=true;
//	            }
//	        }
//	        if(!menuPressed)
//	        {
//	        	 blockTypeDropDown.removeButtons(this.buttonList);
//				 this.AITool.setBlockType((BlockType) this.blockTypeDropDown.getCurrentOpt());
//				 blockTypeDropDown.open=false;
//				 
//				 for(GuiButton btn: this.buttonList)
//				 {
//					 btn.enabled=true;
//				 }
//	        }
//		}
    	
    }
	
}
