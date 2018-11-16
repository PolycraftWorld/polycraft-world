package edu.utd.minecraft.mod.polycraft.client.gui;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiConfigEntries.IConfigEntry;
import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentParameters;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;

public class GuiExperimentConfig extends GuiListExtended {
	private final GuiScreen gui;
	private final Minecraft minecraft;
	
	private final ArrayList<GuiListExtended.IGuiListEntry> configList;
	private int maxStringLength = 0; //used during rendering to "left-justify" all parameter names
	
	private ExperimentParameters params;
	public static Color HEADER_TEXT_COLOR = new Color(155, 155, 155);
	private static int SLOT_HEIGHT = 22;
	
    /**
     * What to multiply the amount you moved your mouse by (used for slowing down scrolling when over the items and not
     * on the scroll bar)
     */
    private float scrollMultiplier;
	
	public GuiExperimentConfig(GuiScreen gui, Minecraft mc) {
		//see below for the names of those variables.
		super(mc, gui.width - 248, gui.height, 40, gui.height - 40,SLOT_HEIGHT);
	//	super(mc, ((GuiExperimentList)gui).X_WIDTH, ((GuiExperimentList)gui).SCROLL_HEIGHT, 50, 200, SLOT_HEIGHT);
		
		int x_start = (gui.width - 248) / 2;
		int y_start = (gui.height - 184) / 2;
		
		this.left = x_start;
		this.right = gui.width - this.left;
		System.out.println("Width: , Height, Top, Bottom: " + this.width + " " + this.height + " " + this.top + " " + this.bottom);
		System.out.println("Left: Right: " + this.left + " " + this.right);
		this.minecraft = mc;
		this.gui = gui;
		this.configList = new ArrayList<>();
		//this.headerPadding = 0;
		
		if(gui instanceof GuiExperimentList) {
			params = ((GuiExperimentList)gui).currentParameters;
			
			//TODO: Make a more sustainable params list.
			this.configList.add(new ConfigHeader("Timing"));
			
			for(String key : params.timingParameters.keySet()) {
	    		Integer[] vals = params.timingParameters.get(key);
	    		
	    		int l = mc.fontRenderer.getStringWidth(key);
	    		if(l > this.maxStringLength)
	    			maxStringLength = l;
	    		
	    		this.configList.add(new ConfigSlider(key, vals[0], vals[1], vals[2]));
	    		
	    	}
			
			this.configList.add(new ConfigHeader("Scoring"));
			
			for(String key : params.scoringParameters.keySet()) {
	    		Number[] vals = params.scoringParameters.get(key);
	    		int l = mc.fontRenderer.getStringWidth(key);
	    		if(l > this.maxStringLength)
	    			maxStringLength = l;
	    		this.configList.add(new ConfigSlider(key, Double.parseDouble(vals[0].toString()), Double.parseDouble(vals[1].toString()), Double.parseDouble(vals[2].toString())));
	    	}
			
			this.configList.add(new ConfigHeader("Items"));
		}
		
		
	}
	
	public ArrayList<? extends IGuiListEntry> getChangedItems(){
		ArrayList<GuiListExtended.IGuiListEntry> newItems = new ArrayList<>();
		for(GuiListExtended.IGuiListEntry entry : this.configList) {
			if(entry instanceof ConfigSlider) {
				ConfigSlider temp = (ConfigSlider) entry;
				if(temp.hasChanged) {
					newItems.add(new ConfigSlider(temp.parameterName, temp.slider.getValue(), 0, 0));
				}
			}
		}
		return newItems;
	}

	/**
	 * Don't delete this - it's a good reference for future you!
	 * @param minecraft
	 * @param width
	 * @param height
	 * @param top
	 * @param bottomOffset
	 * @param slotHeight
	 */
	@Deprecated
	public GuiExperimentConfig(Minecraft minecraft, int width, int height, int top, int bottomOffset, int slotHeight) {
		super(minecraft, width, height, top, bottomOffset, slotHeight);
		this.minecraft = minecraft;
		this.gui = null;
		this.configList = new ArrayList<>();
	}

	@Override
	public IGuiListEntry getListEntry(int index) {
		
		return this.configList.get(index);
	}

	@Override
	protected int getSize() {
	
		return this.configList.size();
	}
	
	/**
	 * The default one in GuiSlot uses CONSTANTS!! WHYYYY
	 */
	@Override
	protected int getScrollBarX() {
		// TODO Auto-generated method stub
		return this.right - 10;
	}
	
	/**
	 * The Default one just returns 220... WHY??
	 */
	@Override
	public int getListWidth() {
		// TODO Auto-generated method stub
		return this.right;
	}
	
	@Override
	protected void drawSelectionBox(int xPos, int yPos, int otherVar1, int otherVar2) {
		int totalRows = this.getSize();
		Tessellator tessellator = Tessellator.instance;
		
		for (int rowIterator = 0; rowIterator < totalRows; ++rowIterator) {
			int curRow = yPos + rowIterator * this.slotHeight + this.headerPadding;
			
			if(curRow < this.bottom - this.slotHeight && curRow > this.top) {
				this.drawSlot(rowIterator, xPos, curRow, 0, tessellator, otherVar1, otherVar2);
			}
		}
	}
	
	
	@SideOnly(Side.CLIENT)
	public class ConfigHeader implements GuiListExtended.IGuiListEntry {
		
		private final String headerName;
		private final int width;
		
		public ConfigHeader(String name) {
			this.headerName = I18n.format(name, new Object[0]);
			this.width = GuiExperimentConfig.this.minecraft.fontRenderer.getStringWidth(headerName);
		}

		@Override
		public void drawEntry(int p_148279_1_, int p_148279_2_, int yStart, int p_148279_4_, int p_148279_5_,
				Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_) {
			//Your guess is as good as mine - I am not patient in figuring out those variables.
			//I think p_.._3 and p_.._5 have to do with the y height and scroll position.
			GuiExperimentConfig.this.minecraft.fontRenderer.drawString(this.headerName,
					GuiExperimentConfig.this.minecraft.currentScreen.width / 2 - this.width / 2,  
					yStart + GuiExperimentConfig.this.SLOT_HEIGHT/4 + p_148279_5_ / 2 - GuiExperimentConfig.this.minecraft.fontRenderer.FONT_HEIGHT / 2, 
					Format.getIntegerFromColor(GuiExperimentConfig.HEADER_TEXT_COLOR));
			
		}

		@Override
		public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_,
				int p_148278_6_) {
			//don't do anything on mouse press.
			return false;
		}

		@Override
		public void mouseReleased(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_,
				int p_148277_6_) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	@SideOnly(Side.CLIENT)
	public class ConfigSlider implements GuiListExtended.IGuiListEntry {
		
		public boolean hasChanged = false;
		
		private int SLIDER_WIDTH = 100;
		private int RESET_WIDTH = 20;
		private int HEIGHT = GuiExperimentConfig.SLOT_HEIGHT - 2;
		
		private String parameterName;
		private GuiSlider slider;
		private GuiButton reset;
		private double defaultValue;
		
		/**
		 * Create a Config Slider GuiSlot
		 * @param name 			Name of Parameter to be varied
		 * @param defaultValue 	default (server-saved) value of the parameter
		 * @param minVal		minimum Slider Value
		 * @param maxVal		maximum Slider Value
		 */
		public ConfigSlider(String name, double defaultValue, double minVal, double maxVal) {
			this.parameterName = name;
			//new GuiSlider(paramID++, x_pos, y_pos, (int) (this.X_WIDTH*0.75), buttonheight, 
			//key, "", vals[1], vals[2], vals[0], true, true, null); //I18n.format(name, new Object[0])
			this.defaultValue = defaultValue;
			this.slider = new GuiSlider(0, 0, 0, SLIDER_WIDTH, HEIGHT, "", "", minVal, maxVal, defaultValue, false, true, null);
			this.reset = new GuiButton(0, 0, 0, RESET_WIDTH, HEIGHT, "X");
			this.parameterName = GuiExperimentConfig.this.minecraft.fontRenderer.trimStringToWidth(this.parameterName, GuiExperimentConfig.this.width/2 + 5);
		}

		@Override
		public void drawEntry(int p_148279_1_, int what, int yStart, int p_148279_4_, int p_148279_5_,
				Tessellator p_148279_6_, int mouseX, int mouseY, boolean p_148279_9_) {
			// draw each ConfigSlider entity on a row.
			int xStart = GuiExperimentConfig.this.left;
			GuiExperimentConfig.this.minecraft.fontRenderer.drawString(this.parameterName, xStart + 2,
					yStart + p_148279_5_ / 2 - GuiExperimentConfig.this.minecraft.fontRenderer.FONT_HEIGHT / 2, Format.getIntegerFromColor(GuiExperimentConfig.HEADER_TEXT_COLOR));
			
			
			//GuiExperimentConfig.this.minecraft.fontRenderer.drawString(this.parameterName, xStart + 120 - GuiExperimentConfig.this.maxStringLength,
			//		yStart + p_148279_5_ / 2 - GuiExperimentConfig.this.minecraft.fontRenderer.FONT_HEIGHT / 2, Format.getIntegerFromColor(GuiExperimentConfig.HEADER_TEXT_COLOR));
			
			this.slider.xPosition = xStart + 105;
			this.slider.yPosition = yStart - GuiExperimentConfig.this.SLOT_HEIGHT/4 + p_148279_5_ / 2 - GuiExperimentConfig.this.minecraft.fontRenderer.FONT_HEIGHT / 2;
			this.slider.drawButton(GuiExperimentConfig.this.minecraft, mouseX, mouseY);
			this.reset.enabled = this.slider.getValue() != this.defaultValue; //disable reset if the slider is at its default.
			this.reset.xPosition = xStart + 105 + SLIDER_WIDTH + 5;
			this.reset.yPosition = yStart - GuiExperimentConfig.this.SLOT_HEIGHT/4 + p_148279_5_ / 2 - GuiExperimentConfig.this.minecraft.fontRenderer.FONT_HEIGHT / 2;
			this.reset.drawButton(GuiExperimentConfig.this.minecraft, mouseX, mouseY);
			
		}

		@Override
		public boolean mousePressed(int startingSlotID, int mouseX, int mouseY, int b, int c,
				int d) {
			System.out.println("Slider Changed: " + this.parameterName);
			if(this.reset.mousePressed(GuiExperimentConfig.this.minecraft, mouseX, mouseY)) {
				this.slider.setValue(this.defaultValue);
				//this.slider.dispString = String.format("%d", (int)this.defaultValue);
				this.slider.displayString = String.format("%d", (int)this.defaultValue);
				this.reset.enabled = false;
				this.hasChanged = false;
				//reset will be disabled on drawEntry()
				return true;
			} else if(this.slider.mousePressed(GuiExperimentConfig.this.minecraft, mouseX, mouseY)) {
				//don't need to do any additional functionaity, I don't think?
				this.hasChanged = true;
				return true;
				//return this.slider.mousePressed(GuiExperimentConfig.this.mc, mouseX, mouseY);
			} else {
				return false;
			}
		}

		@Override
		public void mouseReleased(int p_148277_1_, int mouseX, int mouseY, int p_148277_4_, int p_148277_5_,
				int p_148277_6_) {
			this.slider.mouseReleased(mouseX, mouseY);
			this.reset.mouseReleased(mouseX, mouseY);
			
		}
		
		public String getName() {
			return this.parameterName;
		}
		
		public double getSelectedValue() {
			return this.defaultValue;
		}
		
	}
		
}
