package edu.utd.minecraft.mod.polycraft.client.gui;

import java.net.URI;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
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
    private ArrayList<GuiButton> experimentsButtonList = new ArrayList<GuiButton>();
    private final int screenContainerWidth = 230;
    private final int screenContainerHeight = 130;
    private int buttonCount = 1;
    
    private String userFeedbackText = "This is a test";
    
    public GuiExperimentList(EntityPlayer player, int x, int y, int z) {
        System.out.print("gui ExperimentList constructor.\n x, y, z: " + x + " " + y + " " + z);
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.screenID = 0;
        
    }
    
    public GuiExperimentList(EntityPlayer player) {
        //System.out.print("gui ExperimentList constructor.\n x, y, z: " + x + " " + y + " " + z);
        this.player = player;
//        this.x = x;
//        this.y = y;
//        this.z = z;
        this.screenID = 0;
        
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        
        getExperimentsList();
        this.buttonList.addAll(this.experimentsButtonList);
        //byte b0 = 15;
        
        //this.buttonList.add(new GuiButton(1, this.width / 2 - 116, this.height / 2 + 62 + b0, 114, 20, I18n.format("Back", new Object[0])));
        //this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height / 2 + 62 + b0, 114, 20, I18n.format("Next >", new Object[0])));
    }
    
    /**
     * Get the list of experiments from the client-side experiments manager and display for the user
     * This class builds the button list and the initGui() function takes these values, adds the buttons to the GUI's button list, and renders them
     * TODO: add on the GUI the number of players waiting in each experiment room. 
     * TODO: Make that a live-updating thing (technically, drawScreen is on client tick, but we need a way to refresh the info the experimentsList object provides 
     */
    private void getExperimentsList() {
    	//GuiButton Constructor: id, xPos, yPos, width, height, displayString
    	//GuiButton Constructor with default Width/Height of 200/20: id, xPos, yPos, displayString
    	int x_pos = (this.width - 248) / 2 + 10; //magic numbers from Minecraft. 
        int y_pos = (this.height - 190) / 2 + 8 + 12; //magic numbers from minecraft
        int buttonheight = 20;
        int button_padding_y = 4;
        //+12 to account for the Title Text!
    	
        //TODO: iterate through the experiments list to capture the correct button names and numbers of buttons
        GuiButton temp1 = new GuiButton(buttonCount++, x_pos, y_pos, screenContainerWidth - 20, buttonheight, "Experiment 1");
    	y_pos += (buttonheight + button_padding_y);
    	GuiButton temp2 = new GuiButton(buttonCount++, x_pos, y_pos, screenContainerWidth - 20, buttonheight, "Experiment 2");
    	y_pos += (buttonheight + button_padding_y);
    	GuiButton temp3 = new GuiButton(buttonCount++, x_pos, y_pos, screenContainerWidth - 20, buttonheight, "Experiment 3");
    	y_pos += (buttonheight + button_padding_y);
    	GuiButton temp4 = new GuiButton(buttonCount++, x_pos, y_pos, screenContainerWidth - 20, buttonheight, "Experiment 4");
        
    	experimentsButtonList.add(temp1);
    	experimentsButtonList.add(temp2);
    	experimentsButtonList.add(temp3);
    	experimentsButtonList.add(temp4);
    }

    protected void actionPerformed(GuiButton button) {
    	int x_pos = (this.width - 248) / 2 + 10;
    	player.addChatMessage(new ChatComponentText("Selected Experiment: " + button.displayString));
    	userFeedbackText = "You are in queue for: " + button.displayString;
//    	
//        switch (button.id)
//        {
//            case 1:
//                button.enabled = false;
//
//                try
//                {
//                    Class oclass = Class.forName("java.awt.Desktop");
//                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
//                    oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {new URI("http://www.minecraft.net/store?source=demo")});
//                }
//                catch (Throwable throwable)
//                {
//                    logger.error("Couldn\'t open link", throwable);
//                }
//
//                break;
//            case 2:
//                this.mc.displayGuiScreen((GuiScreen)null);
//                this.mc.setIngameFocus();
//        }
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
        //the below "magic numbers" come from Minecraft's Demo files.
        //I'm using them unless there's better ideas in the future
        //TODO: understand why these are the correct magic numbers
        int x_pos = (this.width - 248) / 2 + 10;
        int y_pos = (this.height - 190) / 2 + 8;
        this.fontRendererObj.drawString(I18n.format(TITLE, new Object[0]), x_pos, y_pos, 0xFFFFFFFF);
        y_pos += 12;
        GameSettings gamesettings = this.mc.gameSettings;
        this.drawRect(x_pos - 2, y_pos - 2, x_pos + this.screenContainerWidth, y_pos + this.screenContainerHeight, 0x50A0A0A0);
        this.fontRendererObj.drawString(I18n.format(this.userFeedbackText, new Object[0]), x_pos, y_pos + this.screenContainerHeight - 12, 0xFFFFFFFF);

        //TODO: create a parser for the following function, so it's easy to set colors!
        /*p_78258_4_ is the integer that minecraft parses to get the color channels.
         * this.red = (float)(p_78258_4_ >> 16 & 255) / 255.0F;
            this.blue = (float)(p_78258_4_ >> 8 & 255) / 255.0F;
            this.green = (float)(p_78258_4_ & 255) / 255.0F;
            this.alpha = (float)(p_78258_4_ >> 24 & 255) / 255.0F;
         */
        
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }
    
}
