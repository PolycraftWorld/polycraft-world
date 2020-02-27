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
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.RenderBox;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
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
	ArrayList<TutorialFeature> features = new ArrayList<TutorialFeature>();
	public TutorialOptions tutOptions = new TutorialOptions();
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
							save();
						else
							saveJson();
						player.addChatComponentMessage(new ChatComponentText("Saved"));
					}
					break;
				case Load:
					if(outputFileName.endsWith(".psm"))
						load(world);
					else
						loadJson(world);
					break;
				case AreaSelection:
					player.addChatMessage(new ChatComponentText("pos2 selected: " + player.getPosition().getX() + "::" + player.getPosition().getY() + "::" + player.getPosition().getZ()));
					tutOptions.pos2 = player.getPosition();
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
					tutOptions.pos2 = pos;
					if(player.worldObj.isRemote)
						updateRenderBoxes();
					break;
				case FeatureTool:
					if(Keyboard.isKeyDown(56)) {	//if holding alt, remove feature at location
						for(int i =0;i<features.size();i++) {
							
							if(features.get(i).getPos().distanceSq(new Vec3i(blockPos.xCoord, blockPos.yCoord, blockPos.zCoord)) < 0.05) {
								features.remove(i);
								player.addChatMessage(new ChatComponentText("removed feature at: " + pos.getX() + "::" + pos.getY() + "::" + pos.getZ()));
								updateRenderBoxes();
							}
						}
					}else{
						features.add(new TutorialFeature("Feature " + features.size(), pos, Color.green));
						
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
				tutOptions.pos = blockPos;
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
		return features;
	}
	
	public void addFeature(TutorialFeature feature){
		features.add(feature);
	}
	
	public void swapFeatures(int i, int j){
		Collections.swap(features, i, j);
	}
	
	public void removeFeatures(int i){
		features.remove(i);
		updateRenderBoxes();
	}
	
	public void updateRenderBoxes() {
		try {
			renderboxes.clear();
			
			if(!features.isEmpty()) {
				int counter = 0;
				for(TutorialFeature v: features) {
					counter++;
					RenderBox box = new RenderBox(v.getPos().getX(), v.getPos().getZ(), v.getPos2().getX(), v.getPos2().getZ(),
							Math.min(v.getPos().getY(), v.getPos2().getY()), Math.max(Math.abs(v.getPos().getY()- v.getPos2().getY()), 1), 1, v.getName());
					box.setColor(v.getColor());
					renderboxes.add(box);
				}
			}
			if(tutOptions.pos.getY() != 0 || tutOptions.pos2.getY() != 0) {
				renderboxes.add(new RenderBox(new Vec3(tutOptions.pos), new Vec3(tutOptions.pos2), 1));
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
	
	private void save() {
		NBTTagCompound nbtFeatures = new NBTTagCompound();
		NBTTagList nbtList = new NBTTagList();
		if (tutOptions.pos == null)
			tutOptions.pos = new BlockPos(0, 0, 0);
		if (tutOptions.pos2 == null)
			tutOptions.pos2 = new BlockPos(0, 0, 0);
		for(int i =0;i<features.size();i++) {
//				NBTTagCompound nbt = new NBTTagCompound();
//				int pos[] = {(int)features.get(i).getPos().xCoord, (int)features.get(i).getPos().yCoord, (int)features.get(i).getPos().zCoord};
//				nbt.setIntArray("Pos",pos);
//				nbt.setString("name", features.get(i).getName());
			nbtList.appendTag(features.get(i).save());
		}
		nbtFeatures.setTag("features", nbtList);
		nbtFeatures.setTag("options", tutOptions.save());
		nbtFeatures.setTag("AreaData", saveArea());
		FileOutputStream fout = null;
		
		try {
			File dir = new File(this.outputFileDir);
			if(!dir.exists())
				dir.mkdir();
			File file = new File(this.outputFileDir + this.outputFileName);
			fout = new FileOutputStream(file);
			
			if (!file.exists()) {
				file.createNewFile();
			}
			CompressedStreamTools.writeCompressed(nbtFeatures, fout);
			fout.flush();
			fout.close();
			
		}catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}catch (IOException e) {
				e.printStackTrace();
		}catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	private void saveJson() {
		JsonObject experimentJson = new JsonObject();
		JsonArray featureListJson = new JsonArray();
		if (tutOptions.pos == null)
			tutOptions.pos = new BlockPos(0, 0, 0);
		if (tutOptions.pos2 == null)
			tutOptions.pos2 = new BlockPos(0, 0, 0);
		for(int i =0;i<features.size();i++) {
			featureListJson.add(features.get(i).saveJson());
		}
		experimentJson.add("features", featureListJson);
		experimentJson.add("options", tutOptions.saveJson());
		FileOutputStream fout = null, foutArea = null;
		
		try {
			File dir = new File(this.outputFileDir);
			if(!dir.exists())
				dir.mkdir();
			File file = new File(this.outputFileDir + this.outputFileName);
			File fileArea = new File(this.outputFileDir + this.outputFileName + 2);
			fout = new FileOutputStream(file);
			foutArea = new FileOutputStream(fileArea);
			if (!file.exists()) {
				file.createNewFile();
			}
			if (!fileArea.exists()) {
				fileArea.createNewFile();
			}
			
			//format the Json to be readable
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(experimentJson.toString());
			String prettyJsonString = gson.toJson(je);
			//write pretty print json to file
			fout.write(prettyJsonString.getBytes());
			fout.flush();
			fout.close();
			
			//output area data to separate file
			CompressedStreamTools.writeCompressed(saveArea(), foutArea);
			foutArea.flush();
			foutArea.close();
			
		}catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}catch (IOException e) {
				e.printStackTrace();
		}catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	
	private void loadJson(World world) {
		boolean oldVersion = true;
		try {
        	features.clear();

        	JsonParser parser = new JsonParser();
            JsonObject expJson = (JsonObject) parser.parse(new FileReader(this.outputFileDir + this.outputFileName));
            JsonArray featListJson = expJson.get("features").getAsJsonArray();
			for(int i =0;i<featListJson.size();i++) {
				JsonObject featJobj=featListJson.get(i).getAsJsonObject();
				TutorialFeature test = (TutorialFeature)Class.forName(TutorialFeatureType.valueOf(featJobj.get("type").getAsString()).className).newInstance();
				System.out.println(TutorialFeatureType.valueOf(featJobj.get("type").getAsString()).className);
				test.loadJson(featJobj);
				features.add(test);
			}

			tutOptions.loadJson(expJson.get("options").getAsJsonObject());
			
			//load area data from additional file
			File file = new File(this.outputFileDir + this.outputFileName + 2);
        	InputStream is = new FileInputStream(file);

            NBTTagCompound nbtArea = CompressedStreamTools.readCompressed(is);
        	int chunkXMax = nbtArea.getInteger("ChunkXSize");
        	int chunkZMax = nbtArea.getInteger("ChunkZSize");
        	
        	for(int chunkX = 0; chunkX <= chunkXMax; chunkX++) {
        		for(int chunkZ = 0; chunkZ <= chunkZMax; chunkZ++) {
            		Chunk chunk = PolycraftChunkProvider.readChunkFromNBT(world, nbtArea.getCompoundTag("chunk," + chunkX + "," + chunkZ));
            		world.getChunkFromChunkCoords(chunkX, chunkZ).setStorageArrays(chunk.getBlockStorageArray());
            		world.getChunkFromChunkCoords(chunkX, chunkZ).setHeightMap(chunk.getHeightMap());
            		world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
        		}
        	}
			
        	
            is.close();

        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println("I can't load schematic, because " + e.getStackTrace()[0]);
        }
        if(world.isRemote)
			updateRenderBoxes();
	}
	
	private void load(World world) {
		boolean oldVersion = true;
		try {
        	features.clear();
        	
        	File file = new File(this.outputFileDir + this.outputFileName);
        	InputStream is = new FileInputStream(file);

            NBTTagCompound nbtFeats = CompressedStreamTools.readCompressed(is);
            NBTTagList nbtFeatList = (NBTTagList) nbtFeats.getTag("features");
			for(int i =0;i<nbtFeatList.tagCount();i++) {
				NBTTagCompound nbtFeat=nbtFeatList.getCompoundTagAt(i);
				TutorialFeature test = (TutorialFeature)Class.forName(TutorialFeatureType.valueOf(nbtFeat.getString("type")).className).newInstance();
				System.out.println(TutorialFeatureType.valueOf(nbtFeat.getString("type")).className);
				test.load(nbtFeat);
				features.add(test);
			}
			oldVersion = nbtFeats.getCompoundTag("AreaData").getString("version").isEmpty();
			if(!oldVersion) {
	        	int chunkXMax = nbtFeats.getCompoundTag("AreaData").getInteger("ChunkXSize");
	        	int chunkZMax = nbtFeats.getCompoundTag("AreaData").getInteger("ChunkZSize");
	        	
	        	for(int chunkX = 0; chunkX <= chunkXMax; chunkX++) {
	        		for(int chunkZ = 0; chunkZ <= chunkZMax; chunkZ++) {
	            		Chunk chunk = PolycraftChunkProvider.readChunkFromNBT(world, nbtFeats.getCompoundTag("AreaData").getCompoundTag("chunk," + chunkX + "," + chunkZ));
	            		world.getChunkFromChunkCoords(chunkX, chunkZ).setStorageArrays(chunk.getBlockStorageArray());
	            		world.getChunkFromChunkCoords(chunkX, chunkZ).setHeightMap(chunk.getHeightMap());
	            		world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
	        		}
	        	}
			}else {
				blocks = nbtFeats.getCompoundTag("AreaData").getIntArray("Blocks");
	        	data = nbtFeats.getCompoundTag("AreaData").getByteArray("Data");
			}
        	
			tutOptions.load(nbtFeats.getCompoundTag("options"));
            is.close();

        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println("I can't load schematic, because " + e.getStackTrace()[0]);
        }
        if(world.isRemote)
			updateRenderBoxes();
        else if(oldVersion){
        	int count = 0;
        	BlockPos pos = new BlockPos(Math.min(tutOptions.pos.getX(), tutOptions.pos2.getX()),
					Math.min(tutOptions.pos.getY(), tutOptions.pos2.getY()),
					Math.min(tutOptions.pos.getZ(), tutOptions.pos2.getZ()));
        	BlockPos size = new BlockPos(Math.abs(tutOptions.pos.getX() - tutOptions.pos2.getX()),
					Math.abs(tutOptions.pos.getY() - tutOptions.pos2.getY()),
					Math.abs(tutOptions.pos.getZ() - tutOptions.pos2.getZ()));
    		for(int x = 0; x < size.getX(); x++){
    			for(int y = 0; y<=size.getY(); y++){
    				for(int z = 0; z<=size.getZ(); z++){
//	    					if(count>=blocks.length) { //in case the array isn't perfectly square (i.e. rectangular area was selected)
//	    						return false;
//	    					}
    					int curblock = (int)blocks[count];

    					if(curblock == 0 || curblock == 76) {
    						if(!world.isAirBlock(new BlockPos(x + pos.getX(), y + pos.getY() ,z + pos.getZ())))
    							world.setBlockToAir(new BlockPos(x + pos.getX(), y + pos.getY() ,z + pos.getZ()));
    						count++;
    						continue;
    					}
    					else if(curblock == 759) {
    						count++;
    						continue; //these are Gas Lamps - we don't care for these.
//	    					}else if(curblock == 849) { //Polycrafting Tables (experiments!)
//	    						world.setBlock(x + (int)pos.xCoord, y + (int)pos.yCoord , z + (int)pos.zCoord, Block.getBlockById(curblock), data[count], 2);
//	    						PolycraftInventoryBlock pbi = (PolycraftInventoryBlock) world.getBlock(x + (int)pos.xCoord, y + (int)pos.yCoord , z + (int)pos.zCoord);
//	    						ItemStack item = new ItemStack(Block.getBlockById((int)blocks[count]));
//	    						pbi.onBlockPlacedBy(world, x + (int)pos.xCoord, y + (int)pos.yCoord, z + (int)pos.zCoord, dummy, new ItemStack(Block.getBlockById((int)blocks[count])));
//	    						count++;
    					}else {
    						world.setBlockState(new BlockPos(x + pos.getX(), y + pos.getY() ,z + pos.getZ()), Block.getBlockById(curblock).getStateFromMeta(data[count]), 3);
    						count++;
    					}
    				}
    			}
    		}
        }
	}
	
//	@SideOnly(Side.SERVER)
//	private void load() {
//		return;
//	}
	
	@SideOnly(Side.CLIENT)
	private NBTTagCompound saveArea() {
		if(tutOptions.pos.getY() != 0 || tutOptions.pos2.getY() != 0)
		{
			int minX = Math.min(tutOptions.pos.getX(), tutOptions.pos2.getX());
			int maxX = Math.max(tutOptions.pos.getX(), tutOptions.pos2.getX());
			int minY = Math.min(tutOptions.pos.getY(), tutOptions.pos2.getY());
			int maxY = Math.max(tutOptions.pos.getY(), tutOptions.pos2.getY());
			int minZ = Math.min(tutOptions.pos.getZ(), tutOptions.pos2.getZ());
			int maxZ = Math.max(tutOptions.pos.getZ(), tutOptions.pos2.getZ());
			int[] intArray;
			short height;
			short length;
			short width;
			
			length=(short)(maxX-minX+1);
			height=(short)(maxY-minY+1);
			width=(short)(maxZ-minZ+1);
			int[] blocks = new int[length*height*width];
			byte[] data = new byte[length*height*width];
			int count=0;
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagList tiles = new NBTTagList();

			int chunkXMin = minX >> 4;
			int chunkZMin = minZ >> 4;
            int chunkXMax = maxX >> 4;
			int chunkZMax = maxZ >> 4;	
			nbt.setString("version", "1.01");
			nbt.setInteger("ChunkXSize", chunkXMax - chunkXMin);
			nbt.setInteger("ChunkZSize", chunkZMax - chunkZMin);
			TileEntity tile;
			
			for(int chunkX = chunkXMin; chunkX <= chunkXMax; chunkX++) {
				for(int chunkZ = chunkZMin; chunkZ <= chunkZMax; chunkZ++) {
					NBTTagCompound nbtChunk = new NBTTagCompound();
					Chunk chunk = Minecraft.getMinecraft().theWorld.getChunkFromChunkCoords(chunkX, chunkZ);
					PolycraftChunkProvider.writeChunkToNBT(chunk, Minecraft.getMinecraft().theWorld, nbtChunk);
					nbtChunk.setInteger("xPos", chunkX - chunkXMin);
					nbtChunk.setInteger("zPos", chunkZ - chunkZMin);
					nbt.setTag("chunk," + (chunkX - chunkXMin) + "," + (chunkZ - chunkZMin), nbtChunk);
				}
			}
			
			//nbt.setTag("TileEntity", tiles);
//			nbt.setIntArray("Blocks", blocks);
//			nbt.setByteArray("Data", data);
			return nbt;
		}else {
			return null;
		}
	}
	
//	@SideOnly(Side.SERVER)
//	private NBTTagCompound saveArea() {
//		return;
//	}
}
