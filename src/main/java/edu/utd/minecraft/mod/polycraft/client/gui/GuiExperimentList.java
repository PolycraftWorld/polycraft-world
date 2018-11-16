package edu.utd.minecraft.mod.polycraft.client.gui;

import java.awt.Color;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.GuiSlider;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager.ExperimentListMetaData;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentParameters;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.util.Format;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;

public class GuiExperimentList extends GuiScreen {
	private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation background_image = new ResourceLocation(PolycraftMod.getAssetName("textures/gui/consent_background.png"));
    private static final ResourceLocation SCROLL_TAB = new ResourceLocation(
			"textures/gui/container/creative_inventory/tabs.png");
    
    private static final String __OBFID = "CL_00000691";
    private EntityPlayer player;
    private int x, y, z;
    private int screenID; //current screen
    private static final String TITLE = "Experiments List: Select An Experiment!";
    private ArrayList<GuiButton> experimentsListButton = new ArrayList<GuiButton>();
    private ArrayList<GuiButton> configButtons = new ArrayList<GuiButton>();
    private final int screenContainerWidth = 230;
    private final int screenContainerHeight = 130;
    private boolean wasClicking; 
    private int buttonCount = 2;
    private int buttonheight = 20;
    private int button_padding_y = 4;
    private int currentExperimentDetailOnScreenID = -1;
    private float scroll = 0.0F; // Amount of scroll, from 0.0 to 1.0 inclusive.
	private boolean scrolling; // True if the scroll bar is being dragged.
	private int ylines; // The number of buttons/lines the text space can accommodate.
	private int extraLines; // How many buttons/lines are overflowing from the alloted text space.
	
	private final int SCROLL_HEIGHT = 151;
	private final int X_PAD = 10;
	// private static final int X_WIDTH = 248 - 10 * 2;
	// private static final int X_WIDTH_SCROLL = X_WIDTH - 22;
	private final int X_WIDTH = 206; // X_WIDTH_SCROLL
	private final int Y_HEIGHT = 126;
	private final int Y_PAD = 8;
	private int titleHeight;
	private List expInstructions;
	ExperimentParameters currentParameters;
	
	private GuiExperimentConfig guiConfig;
	
    
    private enum WhichScreen {
    		ExperimentList,
    		ExperimentDetail,
    		ExperimentConfig
    }
    
    private String userFeedbackText = "";
    private WhichScreen screenSwitcher = WhichScreen.ExperimentList;
    
    public GuiExperimentList(EntityPlayer player, int x, int y, int z) {
        System.out.print("gui ExperimentList constructor.\n x, y, z: " + x + " " + y + " " + z);
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.screenID = 0;
        this.titleHeight = 20;
        
    }
   
    /**
     * Simpler constructor. Not sure if we care about the x,y,z of the player.
     * @param player the player who called up this screen
     */
    public GuiExperimentList(EntityPlayer player) {
        this.player = player;
        this.screenID = 0;
        this.titleHeight = 20;
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question. This is run every time the screen 
     * needs to be rendered.
     */
    public void initGui()
    {
    	if(ExperimentManager.INSTANCE.clientCurrentExperiment>0) {
    		this.currentExperimentDetailOnScreenID = ExperimentManager.INSTANCE.clientCurrentExperiment; //open to this screen!
    		screenSwitcher = this.screenChange(WhichScreen.ExperimentDetail);
    		
    		//return;
    	}else {
	        this.buttonList.clear();
	       // System.out.println(ExperimentManager.INSTANCE.clientCurrentExperiment);
	        buildExperimentButtonList();
	        this.buttonList.addAll(this.experimentsListButton);
    	}
   }
    
    /**
     * Handle Key Presses by the user
     * Superclass handles the escape key
     * the keys are ordered from top left to bottom on the keyboard (1 = esc, 2 = 1, 3=2, etc... backspace=14, tab=15, q = 16, etc...)
     * Capital keys (Shift/capslock) will change the char, but not the int
     * for now, if user presses r when inside that window, it will close.
     */
    @Override
    public void keyTyped(char abc, int one) {
    	super.keyTyped(abc, one);
    	if(abc == 'x' || abc == 'X') {
    		 this.mc.displayGuiScreen((GuiScreen)null);
             this.mc.setIngameFocus();
    	}
    }
    

    /**
     * Override functionality on base class
     * This is what is triggered if any interaction happens on the Gui. 
     * This handles the various cases where a user wants to select an experiment and join it or withdraw
     * from that queue.
     * All user requests are currently relayed to the server. We can place a cooldown (in the future) that
     * prevents a single user from continually switching between experiments. Right now, button is disabled after a user requests to join an experiment.
     * we don't have enough users for this to be a problem
     * TODO: Make this more idiot proof and prevent click-spamming. 
     */
    protected void actionPerformed(GuiButton button) {
    	//int x_pos = (this.width - 248) / 2 + 10;
    	//player.addChatMessage(new ChatComponentText("Selected Experiment: " + button.displayString));
    	//userFeedbackText = "You are in queue for: " + button.displayString;
    	int btnID = button.id;
    	WhichScreen newScreen = null;
    	if(button.id > 10000) {
    		//Open the Experiment Config Screen:
    		btnID = button.id-10000;
    		newScreen = WhichScreen.ExperimentConfig;
    		for(GuiButton gbtn : experimentsListButton) {
    			if(gbtn.id == btnID) {
    	    		String expID = gbtn.displayString;
    				String[] expList = expID.split("\\s");
    				try {
    					this.currentExperimentDetailOnScreenID = Integer.parseInt(expList[expList.length - 1]);
    					this.screenSwitcher = this.screenChange(WhichScreen.ExperimentConfig);
    				}catch(NumberFormatException e) {
    					e.printStackTrace();
    					System.out.println("unable to parse string - did we change how we render buttons?");
    				}
    				return;
    			}
    		}

    	}
    	
    	
    	switch(btnID) {
    	case 1000:
    		//user selected "back"
    		this.screenSwitcher = this.screenChange(WhichScreen.ExperimentList);
    		this.currentExperimentDetailOnScreenID = -1; //no experiment selected
    		break;
    	case 2000:
    		//user selected join experiment
    		button.enabled = false;
    		button.displayString = "In Queue";
    		//TODO: disable this button when a user comes back
    		if(ExperimentManager.INSTANCE.clientCurrentExperiment > 0) {
    			if(	ExperimentManager.INSTANCE.clientCurrentExperiment != this.currentExperimentDetailOnScreenID) {
    				//send server a "withdraw" request
    				//TODO: set it up to only send join requests and let the server auto-withdraw a player if they're in another scoreboard.
    				//TODO: Would that be in scoreboard manager?
    		
    				this.sendExperimentUpdateToServer(ExperimentManager.INSTANCE.clientCurrentExperiment, false);
    			
    			}else {
    				//User has already joined!
    				//TODO: display user feedback text on Experiment Detail screen
    				break; //don't send an update.
    			}
    			
    		}
    		//Tell server player wants to join a new experiment.
    		ExperimentManager.INSTANCE.clientCurrentExperiment = this.currentExperimentDetailOnScreenID;
			this.sendExperimentUpdateToServer(ExperimentManager.INSTANCE.clientCurrentExperiment, true);
			userFeedbackText = "You are in queue for: Experiment " + this.currentExperimentDetailOnScreenID; //let the user know what experiment they're in
    			
    		break;
    		
    		
    		
    	case 1:
    		userFeedbackText = "";
    		
    		if(ExperimentManager.INSTANCE.clientCurrentExperiment>0) {
    			this.sendExperimentUpdateToServer(ExperimentManager.INSTANCE.clientCurrentExperiment, false);
    			
    			ExperimentManager.INSTANCE.clientCurrentExperiment = -1; //set this to -1
    		}
    	
    		//store the current joined experiment in clientCurrentExperiment
    		//reset all experiment buttons
    		for(GuiButton gbtn : experimentsListButton) {
    			if(!gbtn.enabled) {
        			gbtn.enabled=true;
    			}
    		}
    		button.enabled=false;
    		break;
    	default:
    		//Open the Experiment Detail Screen:
    		String expID = button.displayString;
			String[] expList = expID.split("\\s");
			try {
				this.currentExperimentDetailOnScreenID = Integer.parseInt(expList[expList.length - 1]);
				this.screenSwitcher = this.screenChange(WhichScreen.ExperimentDetail);
			}catch(NumberFormatException e) {
				e.printStackTrace();
				System.out.println("unable to parse string - did we change how we render buttons?");
			}
			break;
    	}
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
    }

    /**
     * Draws either a gradient over the background screen (when it exists) or a flat gradient over background.png
     */
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
    
    /**
	 * Handles mouse wheel scrolling.
	 */
	public void handleMouseInput() {
		
		int i = Mouse.getEventDWheel();
		if (i != 0 && extraLines > 0) {
			scroll -= Math.signum(i) / (float) extraLines;
			if (this.scroll < 0.0F) {
				this.scroll = 0.0F;
			} else if (this.scroll > 1.0F) {
				this.scroll = 1.0F;
			}
		}
		super.handleMouseInput();
	}

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float otherValue)
    {
    	if(screenSwitcher.equals(WhichScreen.ExperimentConfig)) {
    		drawExperimentConfigScreen();
    		this.guiConfig.drawScreen(mouseX, mouseY, otherValue);
    		//this.drawDefaultBackground();
    		super.drawScreen(mouseX, mouseY, otherValue);
    		return;
    	}
		// Get the position of the top left pixel.
		int x_start = (this.width - 248) / 2;
		int y_start = (this.height - 184) / 2;
		// Operate the scroll bar.
		boolean flag = Mouse.isButtonDown(0);
		int i1 = x_start + 226; // x and y of scroll bar.
		int j1 = y_start + 8;
		int k1 = i1 + 14;
		int l1 = j1 + SCROLL_HEIGHT;
		
		
		// Start scrolling check.
		if (!this.wasClicking && flag && mouseX >= i1 && mouseY >= j1 && mouseX < k1 && mouseY < l1 + 17)
			this.scrolling = extraLines > 0;
		// Stop scrolling check.
		if (!flag)
			this.scrolling = false;
		// Set history of mouse clicking.
		this.wasClicking = flag;
		// Set scroll in progress.
		if (this.scrolling) {
			this.scroll = ((float) (mouseY - j1) - 7.5F) / ((float) (l1 - j1));
			if (this.scroll < 0.0F) {
				this.scroll = 0.0F;
			} else if (this.scroll > 1.0F) {
				this.scroll = 1.0F;
			}
		}

    	
        this.drawDefaultBackground();
        switch(screenSwitcher) {
        	case ExperimentList:
        		drawExperimentListScreen();
        		break;
        	case ExperimentDetail:
        		drawExperimentInstructionScreen();
        		break;
        	case ExperimentConfig:
//        		drawExperimentConfigScreen();
//        		this.guiConfig.drawScreen(mouseX, mouseY, otherValue);
//        		break;
        	default:
        		//Do Nothing
        }
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(SCROLL_TAB);
		// The scroll bar sits at (226, 8) but the border is 1 wide so the scroll
		// indicator really starts at (227, 9).
		this.drawTexturedModalRect(x_start + 227, y_start + 9 + (int) (this.scroll * SCROLL_HEIGHT),
				232 + (extraLines > 0 ? 0 : 12), 0, 12, 15);
        
        super.drawScreen(mouseX, mouseY, otherValue);
    }
    
    private void drawExperimentConfigScreen() {
    	//get top left of screen with padding.
    	int x_pos = (this.width - 248) / 2 + 10;
        int y_pos = (this.height - 190) / 2 + 8;
        this.fontRendererObj.drawString(I18n.format("Experiment Configuration: Experiment " + this.currentExperimentDetailOnScreenID, new Object[0]), x_pos, y_pos, 0xFFFFFFFF);
        y_pos += 12;
        //this.guiConfig.drawScreen(p_148128_1_, p_148128_2_, p_148128_3_);
        
		
	}

	public void drawExperimentListScreen() {
    	//the below "magic numbers" come from Minecraft's Demo files.
        //I'm using them unless there's better ideas in the future
        //TODO: understand why these are the correct magic numbers
        int x_pos = (this.width - 248) / 2 + 10;
        int y_pos = (this.height - 190) / 2 + 8;
        this.fontRendererObj.drawString(I18n.format(TITLE, new Object[0]), x_pos, y_pos, 0xFFFFFFFF);
        y_pos += 12;
        //GameSettings gamesettings = this.mc.gameSettings;
        this.drawRect(x_pos - 2, y_pos - 2, x_pos + this.X_WIDTH, y_pos + this.Y_HEIGHT, 0x50A0A0A0);
        this.fontRendererObj.drawString(I18n.format(this.userFeedbackText, new Object[0]), x_pos, y_pos + this.screenContainerHeight - 12, 0xFFFFFFFF);
        y_pos += buttonheight - button_padding_y - this.fontRendererObj.FONT_HEIGHT;
        //draw the Number of Players in Each experiment:
        
        //TODO: Draw this list based on the scroll position.
        
        for (ExperimentManager.ExperimentListMetaData emd : ExperimentManager.metadata) {
        	if(emd.isAvailable()) {
	        	this.fontRendererObj.drawString(I18n.format("" + emd.currentPlayers + "/" + emd.playersNeeded, new Object[0]), x_pos+ X_WIDTH -2 * X_PAD, y_pos, 0xFFFFFFFF);
	        	//GuiButton temp = new GuiButton(buttonCount++, x_pos+10, y_pos, screenContainerWidth-50, buttonheight, emd.expName);
	        	y_pos+=((int)(buttonheight*1) + button_padding_y);
	        	//experimentsButtonList.add(temp);
        	}
        }
    }
    
    public void drawExperimentInstructionScreen() {
    	//this.buttonList.clear();
    	int x_pos = (this.width - 248) / 2 + 10;
        int y_pos = (this.height - 190) / 2 + 8;
        this.fontRendererObj.drawString(I18n.format("Experiment Instructions: Experiment " + this.currentExperimentDetailOnScreenID, new Object[0]), x_pos, y_pos, 0xFFFFFFFF);
        y_pos += 12;
        //draw background rectangle
        int offset = 2;
        int offset2 = 1;
        this.drawRect(x_pos - 2, y_pos - 2, x_pos + this.X_WIDTH + 2, y_pos + this.Y_HEIGHT + 2 - 3*Y_PAD, Format.getIntegerFromColor(new Color(128, 128, 128)));
        this.drawRect(x_pos - offset2, y_pos - offset2, x_pos + this.X_WIDTH + offset2, y_pos + this.Y_HEIGHT + offset2 - 3*Y_PAD, Format.getIntegerFromColor(new Color(200, 200, 200)));
        //IMPORTANT: user feedback text goes here
        this.fontRendererObj.drawString(I18n.format(this.userFeedbackText, new Object[0]), x_pos, y_pos + this.screenContainerHeight - 12, 0xFFFFFFFF);

        //Draw List based on scroll position:
        int linestart = Math.round(this.scroll * extraLines);
        for (int i = 0; i < ylines; i++, y_pos += this.fontRendererObj.FONT_HEIGHT) {
        	this.fontRendererObj.drawString((String) this.expInstructions.get(linestart + i), x_pos, y_pos, 0);
        }
        
//        this.fontRendererObj.drawStringWithShadow(I18n.format("Objective:", new Object[0]), x_pos, y_pos, 0xFFFFFFFF);
//        y_pos += 12;
//        String objectiveString = "Work with your team to score the most points possible within 5 minutes. It takes 5 seconds to capture or revert a base.";
//        String scoringString = "50 points for each neutral (gray) base. 200 points for reverting an enemy base to neutral. Captured bases generate 5 points per second.";
//        this.fontRendererObj.drawSplitString(I18n.format(objectiveString, new Object[0]), x_pos, y_pos, 230, 0xFFFFFFFF);
//        y_pos += 12;
//        y_pos += 8;
//        y_pos += 12;
//        this.fontRendererObj.drawStringWithShadow(I18n.format("Scoring:", new Object[0]), x_pos, y_pos, 0xFFFFFFFF);
//        y_pos += 12;
//        this.fontRendererObj.drawSplitString(I18n.format(scoringString, new Object[0]), x_pos, y_pos, 230, 0xFFFFFFFF);
//        y_pos += 12;
//        y_pos += 12;
        //this.fontRendererObj.drawString(I18n.format("Click Join Below!", new Object[0]), x_pos, y_pos, 0xFFFFFFFF);
    }
    
    /**
     * Get the list of experiments from the client-side experiments manager and display for the user
     * This class builds the button list and the initGui() function takes these values, 
     * adds the buttons to the GUI's button list, and renders them
     * This always contains builds an updated, synced view, but requires the player to close & re-open to see any updates.
     * TODO: add on the GUI the number of players waiting in each experiment room. 
     */
    private void buildExperimentButtonList() {
    	//GuiButton Constructor: id, xPos, yPos, width, height, displayString
    	//GuiButton Constructor with default Width/Height of 200/20: id, xPos, yPos, displayString
    	int x_pos = (this.width - 248) / 2 + 10; //magic numbers from Minecraft. 
        int y_pos = (this.height - 190) / 2 + 8 + 12; //magic numbers from minecraft
        //+12 to account for the Title Text!
        GuiButton btnCancel = new GuiButton(1, x_pos, y_pos + Y_HEIGHT + Y_PAD, X_WIDTH, buttonheight, "Withdraw From Queue");
    	experimentsListButton.add(btnCancel);
        
        for (ExperimentManager.ExperimentListMetaData emd : ExperimentManager.metadata) {
        	if(emd.isAvailable()) {
        		//Add config button:
        		GuiButton tempConfig = new GuiButton(10000+buttonCount, x_pos+10, y_pos, (int) (X_WIDTH * .1), buttonheight, "?");
	        	GuiButton temp = new GuiButton(buttonCount++, x_pos + 5*X_PAD / 4 + tempConfig.width, y_pos, (int) (X_WIDTH * .65), buttonheight, emd.expName);
	        	y_pos+=(buttonheight + button_padding_y);
	        	experimentsListButton.add(tempConfig);
	        	experimentsListButton.add(temp);
        	}
        }
        
      //if user has already registered for an experiment, show feedback on the screen.
        if(ExperimentManager.INSTANCE.clientCurrentExperiment>0) {
        	//is the user currently in the experiment??
        	//remember, the metadata list is 0-indexed.
        	if(!ExperimentManager.metadata.get(ExperimentManager.INSTANCE.clientCurrentExperiment - 1).isAvailable()) {
        		//if it's not available, then it won't be added to the list! 
        		userFeedbackText = "You are currently in: Experiment " + ExperimentManager.INSTANCE.clientCurrentExperiment;
        	}else {
	        	//GuiButton button = experimentsListButton.get(ExperimentManager.INSTANCE.clientCurrentExperiment);
	        	//button.enabled=false; //disable the "previously selected experiment"
	        	//button.displayString = "Joined: " + button.displayString;
	        	//alert the user of this:
	        	userFeedbackText = "You are in queue for: Experiment " + ExperimentManager.INSTANCE.clientCurrentExperiment;
        	}
    	}
        
        if(experimentsListButton.size() < 2) { //Only button that exists is the cancel button
        	userFeedbackText = "Log in to our experiments server to join!";
        	btnCancel.enabled=false;
        }else if(userFeedbackText.equals("")) { //No experiment has been selected
        	btnCancel.enabled=false;
        }else {
        	btnCancel.enabled=true; //One experiment has already been registered (buttonList > 2)
        	
        	
        }
    }
    
    private WhichScreen screenChange(WhichScreen newScreen) {
    	//On screen change, we need to update the button list and have it re-drawn.
    	switch(newScreen) {
    		case ExperimentList:
    			//this.buttonList.clear();
    			this.experimentsListButton.clear();
    			this.buildExperimentButtonList();
    			this.buttonList.clear();
    			this.buttonList.addAll(this.experimentsListButton);
    			ylines = Math.min(this.experimentsListButton.size(), (Y_HEIGHT - titleHeight) / (this.button_padding_y + this.buttonheight));
    			extraLines = this.experimentsListButton.size() - ylines;
    			break;
    		case ExperimentDetail:
    			//this.experimentsListButton.clear();
    			this.buttonList.clear();
    			this.buttonList.addAll(getExperimentsDetailButtons());
    			this.expInstructions = this.getInstructionsAsList();
    			ylines = Math.min(this.expInstructions.size(), (Y_HEIGHT - titleHeight) / this.fontRendererObj.FONT_HEIGHT);
    			extraLines = this.expInstructions.size() - ylines;
    			break;
    		case ExperimentConfig:
    			this.buttonList.clear();
    			this.currentParameters = ExperimentManager.metadata.get(this.currentExperimentDetailOnScreenID - 1).getParams();
    			guiConfig = new GuiExperimentConfig(this, this.mc);
    			
    			
    		default:
    			break;
    	}
    	this.scroll = 0F;
    	return newScreen;
    }
    
    private ArrayList<GuiButton> getExperimentsDetailButtons(){
    	ArrayList<GuiButton> buttons = new ArrayList<GuiButton>();
    	int x_pos = (this.width - 248) / 2 + X_PAD; //magic numbers from Minecraft. 
        int y_pos = (this.height - 190) / 2 + this.titleHeight; //magic numbers from minecraft
        //+12 to account for the Title Text!
        GuiButton back = new GuiButton(1000, x_pos, y_pos + Y_HEIGHT + Y_PAD, X_WIDTH/2 - X_PAD/2, buttonheight, "< Back");
        GuiButton join = new GuiButton(2000, x_pos + X_WIDTH/2 + X_PAD/2, y_pos + Y_HEIGHT + Y_PAD, X_WIDTH/2 - X_PAD/2, buttonheight, "Join Experiment");
        if(this.currentExperimentDetailOnScreenID == ExperimentManager.INSTANCE.clientCurrentExperiment) {
        	join.enabled = false; //disable joining the same experiment twice.
        	join.displayString = "In Queue";
        }
        if(this.player.dimension == 8) {
        	back.enabled = false;
        }
        buttons.add(back);
        buttons.add(join);
        
        return buttons;
    
    }
    
    private void getConfigButtons() {
    	ArrayList<GuiButton> buttons = new ArrayList<GuiButton>();
    	int x_pos = (this.width - 248) / 2 + X_PAD; //magic numbers from Minecraft. 
    	int y_pos = 0; //this will be updated before the buttons are drawn.
    	
        //int y_pos = (this.height - 190) / 2 + this.titleHeight; //magic numbers from minecraft
        //y_pos += Math.round(this.scroll*this.configButtons.size());
    	
    	ExperimentParameters param = ExperimentManager.metadata.get(this.currentExperimentDetailOnScreenID - 1).getParams();
    	
    	int paramID = 20000;
    	   	
    	for(String key : param.timingParameters.keySet()) {
    		Integer[] vals = param.timingParameters.get(key);
    		GuiSlider temp = new GuiSlider(paramID++, x_pos, y_pos, (int) (this.X_WIDTH*0.75), buttonheight, 
    				key, "", vals[1], vals[2], vals[0], true, true, null);
    		y_pos += buttonheight + button_padding_y;
    		buttons.add(temp);
    	}
    	
    	for(String key : param.scoringParameters.keySet()) {
    		Number[] vals = param.timingParameters.get(key);
    		GuiSlider temp = new GuiSlider(paramID++, x_pos, y_pos, (int) (this.X_WIDTH*0.75), buttonheight, 
    				key, "", (double)vals[1], (double)vals[2], (double)vals[0], true, true, null);
    		y_pos += buttonheight + button_padding_y;
    		buttons.add(temp);
    	}
    	
    	//add Knockback Stick Param:
    	buttons.add(new GuiCheckBox(paramID++, x_pos, y_pos, "Give Knockback?", (boolean) param.itemParameters.get("Give Knockback Stick")));
    		
       }
    

    private List<String> getInstructionsAsList(){
    	
    	String instructions = ExperimentManager.metadata.get(this.currentExperimentDetailOnScreenID - 1).instructions;
    	instructions = instructions.replaceAll("[â€‹]", "");
    	instructions = instructions.replaceAll("[™]", "'");
    	System.out.println(instructions);
    	return this.fontRendererObj.listFormattedStringToWidth(instructions, X_WIDTH);
    	
    	//return null;
    }
    
    
    
    /**
     * Send a packet to the server requesting for a player to either Join or Withdraw from the queue for 
     * a particular experiment
     * @param experimentID The experiment in question
     * @param wantToJoin True if player wants to join, False if they want to withdraw
     */
    private void sendExperimentUpdateToServer(int experimentID, boolean wantToJoin) {
    	ExperimentManager.ExperimentParticipantMetaData part = ExperimentManager.INSTANCE.new ExperimentParticipantMetaData(player.getDisplayName(), experimentID, wantToJoin);
    	Gson gson = new Gson();
		Type gsonType = new TypeToken<ExperimentManager.ExperimentParticipantMetaData>(){}.getType();
		final String experimentUpdates = gson.toJson(part, gsonType);
		ClientEnforcer.INSTANCE.sendExperimentSelectionUpdate(experimentUpdates);
    }
    
}
