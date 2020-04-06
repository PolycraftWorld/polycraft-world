package edu.utd.minecraft.mod.polycraft.item;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentDefinition;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.util.RenderBox;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialOptions;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftChunkProvider;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDevTool extends ItemCustom  {
	
	int[] lastBlock = new int[3]; //used to store last clicked block so we can schedule a block update if it breaks on the client side
	boolean updateLastBlock = false;
	public ExperimentDefinition expDef = new ExperimentDefinition();
	TutorialFeature selectedFeature;
	int blocks[];
	byte data[];
	private long lastEventNanoseconds = 0;
	String tool;
	boolean setting;
	private String outputFileDir = "experiments/";
	public String outputFileName = "output.psm";
	
	private StateEnum currentState;
	public static enum StateEnum {
		AreaSelection,
		FeatureTool,
		GuideTool,
		Save,
		Load,
		Test;
		
		public StateEnum next() {
		    if (ordinal() == values().length - 1)
		    	return values()[0];
		    return values()[ordinal() + 1];
		}
		
		public StateEnum previous() {
		    if (ordinal() == 0)
		    	return values()[values().length - 1];
		    return values()[ordinal() - 1];
		}
	}
	public static ArrayList renderboxes= new ArrayList();
	
	
	public ItemDevTool(CustomObject config) {
		super(config);
		//this.setTextureName(PolycraftMod.getAssetName("gripped_engineered_diamond_axe"));
		//this.setCreativeTab(CreativeTabs.tabTools); //TODO: Take this out of CreativeTab and Make Command to access.
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		this.setting =false;
		currentState = StateEnum.AreaSelection;
	}
	
	public void setState(String state, EntityPlayer player) {
		currentState = StateEnum.valueOf(state);
		player.addChatMessage(new ChatComponentText("State: " + state));
	}
	
	public void setState(String state) {
		currentState = StateEnum.valueOf(state);
	}
	
	public void setState(StateEnum state) {
		currentState = state;
	}
	
	public StateEnum getState() {
		return currentState;
	}
		
	@Override
	// Doing this override means that there is no localization for language
	// unless you specifically check for localization here and convert
	public String getItemStackDisplayName(ItemStack par1ItemStack)
	{
		return "Dev Tool";
	}  
	@Override
	public boolean  hitEntity(ItemStack p_77644_1_, EntityLivingBase p_77644_2_, EntityLivingBase p_77644_3_)
	{
		p_77644_2_.setHealth(0);
		return true;
	}

	
	
	
	@Override
	public ItemStack onItemRightClick(ItemStack p_77659_1_, World world, EntityPlayer player) {
			
		if(player.isSneaking()) {
			//currentState = currentState.next();
			//player.addChatMessage(new ChatComponentText(currentState.toString() + "Mode"));
			if(world.isRemote)
				PolycraftMod.proxy.openDevToolGui(player);
		}else {
			switch(currentState) {
				case Save:
					if(world.isRemote) {
						//save();
						if(outputFileName.endsWith(".psm"))
							expDef.save(this.outputFileDir, this.outputFileName);
						else
							expDef.saveJson(this.outputFileDir, this.outputFileName);
						player.addChatComponentMessage(new ChatComponentText("Saved"));
					}
					break;
				case Load:
					if(outputFileName.endsWith(".psm"))
						expDef.load(world, this.outputFileDir, this.outputFileName);
					else
						expDef.loadJson(world, this.outputFileDir + this.outputFileName);
			        if(world.isRemote)
						updateRenderBoxes();
					break;
				case AreaSelection:
					player.addChatMessage(new ChatComponentText("pos2 selected: " + player.getPosition().getX() + "::" + player.getPosition().getY() + "::" + player.getPosition().getZ()));
					expDef.getOptions().pos2 = player.getPosition();
					if(player.worldObj.isRemote)
						updateRenderBoxes();
					break;
				default:
					break;
			}
		}
		return super.onItemRightClick(p_77659_1_, world, player);
	}
	
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
							 float hitX, float hitY, float hitZ) {
		Vec3 blockPos = new Vec3(pos.getX(), pos.getY(), pos.getZ());
		Vec3 hitPos = new Vec3(hitX, hitY, hitZ);
		if(!world.isRemote) {
			return super.onItemUse(stack, player, world, pos, side, hitX, hitY, hitZ);
		}
		if(Keyboard.isKeyDown(29)) {	//if holding ctrl select block in front of face clicked
			blockPos = getBlockAtFace(blockPos, hitPos);
		}
		if(Mouse.getEventNanoseconds()==lastEventNanoseconds) {
    		return super.onItemUse(stack, player, world, pos, side, hitX, hitY, hitZ);
    	}else {
    		lastEventNanoseconds = Mouse.getEventNanoseconds();
    	}
		if(!player.isSneaking()) {		
			switch(currentState) {
				case AreaSelection:
					player.addChatMessage(new ChatComponentText("pos2 selected: " + pos.getX() + "::" + pos.getY() + "::" + pos.getZ()));
					expDef.getOptions().pos2 = pos;
					if(player.worldObj.isRemote)
						updateRenderBoxes();
					break;
				case FeatureTool:
					if(Keyboard.isKeyDown(56)) {	//if holding alt, remove feature at location
						for(int i =0;i<expDef.getFeatures().size();i++) {
							
							if(expDef.getFeatures().get(i).getPos().distanceSq(new Vec3i(blockPos.xCoord, blockPos.yCoord, blockPos.zCoord)) < 0.05) {
								expDef.getFeatures().remove(i);
								player.addChatMessage(new ChatComponentText("removed feature at: " + pos.getX() + "::" + pos.getY() + "::" + pos.getZ()));
								updateRenderBoxes();
							}
						}
					}else{
						expDef.getFeatures().add(new TutorialFeature("Feature " + expDef.getFeatures().size(), pos, Color.green));
						
						player.addChatMessage(new ChatComponentText("Added feature at: " + blockPos.xCoord + "::" + blockPos.yCoord + "::" + blockPos.zCoord));
						updateRenderBoxes();
					}
					break;
				default:
					break;
			}
		}

		return super.onItemUse(stack, player, world, pos, side, hitX, hitY, hitZ);
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos blockPos, EntityPlayer player) {
		
		if(!player.worldObj.isRemote) {
			//player.worldObj.scheduleBlockUpdate(X, Y, Z, player.worldObj.getBlock(X, Y, Z), 20);
			return true;
		}
		
		switch(currentState) {
			case AreaSelection:
				player.addChatMessage(new ChatComponentText("pos1 selected: " + blockPos.getX() + "::" + blockPos.getY() + "::" + blockPos.getZ()));
				updateLastBlock = true;
				expDef.getOptions().pos = blockPos;
				lastBlock[0] = blockPos.getX();
				lastBlock[1] = blockPos.getY();
				lastBlock[2] = blockPos.getZ();
				if(player.worldObj.isRemote)
					updateRenderBoxes();
				break;
			case FeatureTool:
				break;
			default:
				break;
		}
		
		return true;
	}
	
	public ArrayList<TutorialFeature> getFeatures(){
		return expDef.getFeatures();
	}
	
	public void addFeature(TutorialFeature feature){
		expDef.getFeatures().add(feature);
	}
	
	public void swapFeatures(int i, int j){
		Collections.swap(expDef.getFeatures(), i, j);
	}
	
	public void removeFeatures(int i){
		expDef.getFeatures().remove(i);
		updateRenderBoxes();
	}
	
	public void updateRenderBoxes() {
		try {
			renderboxes.clear();
			
			if(!expDef.getFeatures().isEmpty()) {
				int counter = 0;
				for(TutorialFeature v: expDef.getFeatures()) {
					counter++;
					RenderBox box = new RenderBox(v.getPos().getX(), v.getPos().getZ(), v.getPos2().getX(), v.getPos2().getZ(),
							Math.min(v.getPos().getY(), v.getPos2().getY()), Math.max(Math.abs(v.getPos().getY()- v.getPos2().getY()), 1), 1, v.getName());
					box.setColor(v.getColor());
					renderboxes.add(box);
				}
			}
			if(expDef.getOptions().pos.getY() != 0 || expDef.getOptions().pos2.getY() != 0) {
				renderboxes.add(new RenderBox(new Vec3(expDef.getOptions().pos), new Vec3(expDef.getOptions().pos2), 1));
			}
		}catch(ConcurrentModificationException e) {
			
		}
	}
	
	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if(updateLastBlock) {
				updateLastBlock = false;
				//player.worldObj.scheduleBlockUpdate(lastBlock[0], lastBlock[1], lastBlock[2], player.worldObj.getBlock(lastBlock[0], lastBlock[1], lastBlock[2]), 5);
				player.worldObj.markBlockRangeForRenderUpdate(lastBlock[0], lastBlock[1], lastBlock[2], lastBlock[0], lastBlock[1], lastBlock[2]);
				player.addChatMessage(new ChatComponentText("Update block"));
			}
			
		}
		super.onUpdate(itemstack, world, entity, par4, par5);
	}
	
	public static void render(Entity entity) {
		synchronized (renderboxes) {
			Iterator<RenderBox> boxes = renderboxes.iterator();
			while (boxes.hasNext()) {
				RenderBox box = boxes.next();
				box.render(entity);
			}
		}
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemDevTool) {
				//Minecraft.getMinecraft().mcProfiler.startSection("overlayMessage");
				ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
		        int width = res.getScaledWidth();
		        int height = res.getScaledHeight();
	            int opacity = 200;
	            
//	            FMLClientHandler.instance().getClient().fontRendererObj.drawString("texts", 50, 50, 0xFF0000FF);
//	            if (opacity > 0)
//	            {
//	                GlStateManager.pushMatrix();
//	                GL11.glRotatef(180, 1, 0, 0);
//	                GL11.glRotatef(-60, 1, 0, 0);
//	                GlStateManager.translate(0F, -(player.ticksExisted % 400) / 2.0, 0.0F);
//	                //GlStateManager.enableBlend();
//	                //GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
//	                int color = 0xFFFFFF;
//	                Minecraft.getMinecraft().fontRendererObj.drawString("Hello There!", Minecraft.getMinecraft().fontRendererObj.getStringWidth(((ItemDevTool)player.getHeldItem().getItem()).currentState.name()) / 2, -4, color | (opacity << 24));
//	                Minecraft.getMinecraft().fontRendererObj.drawString("General Kenobi!", Minecraft.getMinecraft().fontRendererObj.getStringWidth(((ItemDevTool)player.getHeldItem().getItem()).currentState.name()) / 2, 5, color | (opacity << 24));
//	                //GlStateManager.disableBlend();
//	                GlStateManager.popMatrix();
//	            }
	
	            //Minecraft.getMinecraft().mcProfiler.endSection();
			}
		}
	}
	
	private Vec3 getBlockAtFace(Vec3 blockPos, Vec3 hitPos) {
		blockPos = new Vec3((int) (blockPos.xCoord + (hitPos.xCoord==0.0?-1:hitPos.xCoord==1.0?1:0))
		 				,(int) (blockPos.yCoord + (hitPos.yCoord==0.0?-1:hitPos.yCoord==1.0?1:0))
		 				,(int) (blockPos.zCoord + (hitPos.zCoord==0.0?-1:hitPos.zCoord==1.0?1:0)));
		
		return blockPos;
	}
	
//	@SideOnly(Side.SERVER)
//	private NBTTagCompound saveArea() {
//		return;
//	}
}
