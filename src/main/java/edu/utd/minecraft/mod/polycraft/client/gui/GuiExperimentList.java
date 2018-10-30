package edu.utd.minecraft.mod.polycraft.client.gui;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager.ExperimentListMetaData;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
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
    private static final String __OBFID = "CL_00000691";
    private EntityPlayer player;
    private int x, y, z;
    private int screenID; //current screen
    private static final String TITLE = "Experiments List: Select An Experiment!";
    private ArrayList<GuiButton> experimentsListButton = new ArrayList<GuiButton>();
    private final int screenContainerWidth = 230;
    private final int screenContainerHeight = 130;
    private int buttonCount = 2;
    private int buttonheight = 20;
    private int button_padding_y = 4;
    private int currentExperimentDetailOnScreenID = -1;
    
    private enum WhichScreen {
    		ExperimentList,
    		ExperimentDetail
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
        
    }
   
    /**
     * Simpler constructor. Not sure if we care about the x,y,z of the player.
     * @param player the player who called up this screen
     */
    public GuiExperimentList(EntityPlayer player) {
        this.player = player;
        this.screenID = 0;
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
	        getExperimentsList();
	        this.buttonList.addAll(this.experimentsListButton);
    	}
   }
    
    /**
     * Get the list of experiments from the client-side experiments manager and display for the user
     * This class builds the button list and the initGui() function takes these values, 
     * adds the buttons to the GUI's button list, and renders them
     * This always contains builds an updated, synced view, but requires the player to close & re-open to see any updates.
     * TODO: add on the GUI the number of players waiting in each experiment room. 
     */
    private void getExperimentsList() {
    	//GuiButton Constructor: id, xPos, yPos, width, height, displayString
    	//GuiButton Constructor with default Width/Height of 200/20: id, xPos, yPos, displayString
    	int x_pos = (this.width - 248) / 2 + 10; //magic numbers from Minecraft. 
        int y_pos = (this.height - 190) / 2 + 8 + 12; //magic numbers from minecraft
        //+12 to account for the Title Text!
        GuiButton btnCancel = new GuiButton(1, x_pos+10, y_pos + screenContainerHeight - 12 - 24, screenContainerWidth-20, buttonheight, "Withdraw From Queue");
    	experimentsListButton.add(btnCancel);
        
        for (ExperimentManager.ExperimentListMetaData emd : ExperimentManager.metadata) {
        	if(emd.isAvailable()) {
	        	GuiButton temp = new GuiButton(buttonCount++, x_pos+10, y_pos, screenContainerWidth-50, buttonheight, emd.expName);
	        	y_pos+=(buttonheight + button_padding_y);
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
        	userFeedbackText = "Sorry - no experiments are available";
        	btnCancel.enabled=false;
        }else if(userFeedbackText.equals("")) { //No experiment has been selected
        	btnCancel.enabled=false;
        }else {
        	btnCancel.enabled=true; //One experiment has already been registered (buttonList > 2)
        	
        	
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
    	
    	switch(button.id) {
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
        int j = (this.height - 200) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, 248, screenContainerHeight + 30);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
    	
        this.drawDefaultBackground();
        switch(screenSwitcher) {
        	case ExperimentList:
        		drawExperimentListScreen();
        		break;
        	case ExperimentDetail:
        		drawExperimentInstructionScreen();
        		break;
        	default:
        		//Do Nothing
        }
        
        //TODO: create a parser for the following function, so it's easy to set colors!
        /*p_78258_4_ is the integer that minecraft parses to get the color channels.
         * this.red = (float)(p_78258_4_ >> 16 & 255) / 255.0F;
            this.blue = (float)(p_78258_4_ >> 8 & 255) / 255.0F;
            this.green = (float)(p_78258_4_ & 255) / 255.0F;
            this.alpha = (float)(p_78258_4_ >> 24 & 255) / 255.0F;
         */
        
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
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
        this.drawRect(x_pos - 2, y_pos - 2, x_pos + this.screenContainerWidth, y_pos + this.screenContainerHeight, 0x50A0A0A0);
        this.fontRendererObj.drawString(I18n.format(this.userFeedbackText, new Object[0]), x_pos, y_pos + this.screenContainerHeight - 12, 0xFFFFFFFF);
        y_pos += buttonheight/3;
        //draw the Number of Players in Each experiment:
        for (ExperimentManager.ExperimentListMetaData emd : ExperimentManager.metadata) {
        	if(emd.isAvailable()) {
	        	this.fontRendererObj.drawString(I18n.format("" + emd.currentPlayers + "/" + emd.playersNeeded, new Object[0]), x_pos+ this.screenContainerWidth - 30, y_pos, 0xFFFFFFFF);
	        	//GuiButton temp = new GuiButton(buttonCount++, x_pos+10, y_pos, screenContainerWidth-50, buttonheight, emd.expName);
	        	y_pos+=((int)(buttonheight*1) + button_padding_y);
	        	//experimentsButtonList.add(temp);
        	}
        }
    }
    
    private WhichScreen screenChange(WhichScreen newScreen) {
    	//On screen change, we need to update the button list and have it re-drawn.
    	switch(newScreen) {
    		case ExperimentList:
    			//this.buttonList.clear();
    			this.experimentsListButton.clear();
    			this.getExperimentsList();
    			this.buttonList.clear();
    			this.buttonList.addAll(this.experimentsListButton);
    			break;
    		case ExperimentDetail:
    			//this.experimentsListButton.clear();
    			this.buttonList.clear();
    			this.buttonList.addAll(getExperimentsDetailButtons());
    			break;
    		default:
    			break;
    	}
    	
    	return newScreen;
    }
    
    private ArrayList<GuiButton> getExperimentsDetailButtons(){
    	ArrayList<GuiButton> buttons = new ArrayList<GuiButton>();
    	int x_pos = (this.width - 248) / 2 + 10; //magic numbers from Minecraft. 
        int y_pos = (this.height - 190) / 2 + 8 + 12; //magic numbers from minecraft
        //+12 to account for the Title Text!
        GuiButton back = new GuiButton(1000, x_pos, y_pos + screenContainerHeight - 12 - 24, screenContainerWidth/2 - 2, buttonheight, "< Back");
        GuiButton join = new GuiButton(2000, x_pos + screenContainerWidth/2, y_pos + screenContainerHeight - 12 - 24, screenContainerWidth/2 - 2, buttonheight, "Join Experiment");
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
    
    public void drawExperimentInstructionScreen() {
    	//this.buttonList.clear();
    	int x_pos = (this.width - 248) / 2 + 10;
        int y_pos = (this.height - 190) / 2 + 8;
        this.fontRendererObj.drawString(I18n.format("Experiment Instructions: Experiment " + this.currentExperimentDetailOnScreenID, new Object[0]), x_pos, y_pos, 0xFFFFFFFF);
        y_pos += 12;
        //draw background rectangle
        this.drawRect(x_pos - 2, y_pos - 2, x_pos + this.screenContainerWidth, y_pos + this.screenContainerHeight, 0x50A0A0A0);
        //IMPORTANT: user feedback text goes here
        this.fontRendererObj.drawString(I18n.format(this.userFeedbackText, new Object[0]), x_pos, y_pos + this.screenContainerHeight - 12, 0xFFFFFFFF);
        
        this.fontRendererObj.drawString(I18n.format("Objective:", new Object[0]), x_pos, y_pos, 0xFFFFFFFF);
        y_pos += 12;
        String objectiveString = "Work with your team to score the most points possible within 5 minutes.";
        String scoringString = "50 points for each neutral (gray) base. 200 points for each enemy base.";
        this.fontRendererObj.drawSplitString(I18n.format(objectiveString, new Object[0]), x_pos, y_pos, 230, 0xFFFFFFFF);
        y_pos += 12;
        y_pos += 12;
        this.fontRendererObj.drawString(I18n.format("Scoring:", new Object[0]), x_pos, y_pos, 0xFFFFFFFF);
        y_pos += 12;
        this.fontRendererObj.drawSplitString(I18n.format(scoringString, new Object[0]), x_pos, y_pos, 230, 0xFFFFFFFF);
        y_pos += 12;
        y_pos += 12;
        this.fontRendererObj.drawString(I18n.format("Click Join Below!", new Object[0]), x_pos, y_pos, 0xFFFFFFFF);
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
