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
	GuiPolyButtonCycle blockTypeCycle;
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
	

	
	public void initGui()
    {
				
		widthSlider = new GuiSlider(0,130,40,120,20,"Width: "," Block(s)",1,64,AITool.getWidth(),false,true,null);
		lengthSlider = new GuiSlider(1,130,70,120,20,"Length: "," Block(s)",1,64,AITool.getLength(),false,true,null);
		heightSlider = new GuiSlider(2,130,150,120,20,"Height: "," Block(s)",1,16,AITool.getHeight(),false,true,null);
		blockTypeCycle = new GuiPolyButtonCycle(5, 130, 100, 120, 20,"Block Type", AITool.getBlockType());
		wallCheck = new GuiCheckBox(3, 130, 130, "Walls?", AITool.getWalls());
		heightSlider.enabled=wallCheck.isChecked();
		
		genBtn = new GuiButton(4, 170, 180, 90, 20, "Generate");
		addBtn(widthSlider);
		addBtn(lengthSlider);
		addBtn(genBtn);
		addBtn(wallCheck);
		addBtn(heightSlider);
		addBtn(blockTypeCycle);
		//int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr)
    }
	
	 protected void actionPerformed(GuiButton button) {
		 super.actionPerformed(button);
		 if(button==genBtn)
		 {
			 
			 
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
		 if(button==blockTypeCycle)
		 {
			 blockTypeCycle.nextOption();
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
		this.AITool.setBlockType((BlockType) this.blockTypeCycle.getCurrentOption());
		heightSlider.enabled=wallCheck.isChecked();
	       
	 }
	
}
