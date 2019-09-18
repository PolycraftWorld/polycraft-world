package edu.utd.minecraft.mod.polycraft.client.gui.api;

import java.util.List;

import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.client.gui.GuiAITrainingRoom;
import edu.utd.minecraft.mod.polycraft.item.ItemAITool.BlockType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPolyButtonDropDown<E extends Enum<E>> extends GuiButton{
	
	public static final ResourceLocation DropDownTab = new ResourceLocation(PolycraftMod.MODID,
			"textures/gui/Button.png");
	
	//private E[] options; //current opt set to options[0]
	//private String currentOpt; 
	private E currentOpt;
	public boolean open;
	
	public int x, y, w, h;
	public GuiButton[] buttonList;
	
	public GuiPolyButtonDropDown(int id, int xStart, int yStart, int width, int height, final E option) {
		super(id, xStart, yStart, width, height, option.name());
		this.x=xStart;
		this.y=yStart;
		this.w=width;
		this.h=height;
		this.currentOpt=option;
		this.buttonList = new GuiButton[ option.getDeclaringClass().getEnumConstants().length];
	
		//this.options = options;
		open=false;
	}
	
	public void setCurrentOpt(E option)
	{
		this.currentOpt= option;
		this.displayString=option.name();
	}
	

	public E getCurrentOpt() {
		return currentOpt;
	}
	
	public void addButtons(List<GuiButton> buttonList)
	{
		int yOffset=0;
		for(int c=0;c< currentOpt.getDeclaringClass().getEnumConstants().length;c++)
		{
			if(currentOpt.getDeclaringClass().getEnumConstants()[c].name()==this.currentOpt.name())
				continue;
			this.buttonList[c]= new GuiButton(c+77, this.x, this.y+((yOffset+1)*20), width, height, currentOpt.getDeclaringClass().getEnumConstants()[c].name());
			buttonList.add(this.buttonList[c]);
			yOffset++;
		}
	}
	
	public void removeButtons(List<GuiButton> buttonList)
	{
		for(GuiButton btn:this.buttonList)
		{
			buttonList.remove(btn);
		}
	}
	
	 public boolean actionPerformed(GuiButton button) {
		 for(GuiButton btn: buttonList)
		 {
			 if(btn==button)
			 {
				 this.currentOpt= Enum.valueOf(currentOpt.getDeclaringClass(), button.displayString);
				 this.displayString=button.displayString;
				 return true;
			 }
		 }
		 return false;
		 
	 }
	
//	public void nextOption() {
//		if (option.ordinal() == option.getDeclaringClass().getEnumConstants().length - 1)
//	    	option = option.getDeclaringClass().getEnumConstants()[0];
//		else
//			option = option.getDeclaringClass().getEnumConstants()[option.ordinal() + 1];
//		this.displayString = prefix + ": " + option.name();
//	}
	
	public void openMenu()
	{
		open=true;
	}
	
	public void closeMenu()
	{
		open=false;
	}
	
	public GuiButton[] getButtons()
	{
		return buttonList;
	}
	
//	public void drawMenu(GuiAITrainingRoom gui)
//	{
//		if(open)
//		{
//			 int yOffset=0;
//		    for(String text:options)
//		    {
//		    	GL11.glEnable(GL11.GL_BLEND);
//			    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//			    gui.mc.getTextureManager().bindTexture(DropDownTab);
//			    FontRenderer fontRender=gui.mc.fontRendererObj;
//			    //PolycraftMod.logger.debug("Screen width & Height: " + this.width + " " + this.height);
//			    //System.out.println("Screen width & Height: " + this.width + " " + this.height);
//			   // this.drawTexturedModalRect(i, j, 0, 0, 248, screenContainerHeight + 30);
//			   
//			   // gui.drawTexturedModalRect(x, y+yOffset, 0, 0, w, h);
//			    this.drawCenteredString(fontRender, text, x + w / 2, y+yOffset + (h - 8) / 2, 14737632);
//			   // gui.mc.fontRendererObj.drawString(text, x, y+yOffset, 0xFFFFFFFF);
//		        yOffset+=20;
//		    }
//		}
//	}
}
