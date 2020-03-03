package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager.PacketMeta;
import edu.utd.minecraft.mod.polycraft.privateproperty.ClientEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ExpFeatureMessage implements IMessage{

	public PacketType type;
	public int expID;
	public ArrayList<TutorialFeature> featureList;
	public int featureIndex;
	
	public enum PacketType{
		All,
		SINGLE
	}

    public ExpFeatureMessage()
    {
    }

    public ExpFeatureMessage(PacketType type, int expID, ArrayList<TutorialFeature> features)
    {
        this.type = type;
		this.expID = expID;
		this.featureList = new ArrayList<TutorialFeature>(features);
    }
    
    public ExpFeatureMessage(List<Object> params)
    {
        this.type = (PacketType)params.get(0);
		this.expID = (int)params.get(1);
        switch(type) {
		case All:
			this.featureList = new ArrayList<TutorialFeature>(TutorialManager.getExperiment(expID).getFeatures());
			break;
		case SINGLE:
			this.featureIndex = (int)params.get(2);
			this.featureList = new ArrayList<TutorialFeature>();
			this.featureList.add((TutorialFeature)params.get(3));
			break;
		default:
			break;
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    	//This code can probably be simplified, but I copied it from the old packet system - Stephen G.
		try {
			this.type = PacketType.values()[buf.readInt()];
	        this.expID = buf.readInt();
	        int tempLength = buf.readInt();
	        String jsonObj = StandardCharsets.UTF_8.decode(buf.readBytes(tempLength).nioBuffer()).toString();
	        Gson gson = new Gson();
			ByteArrayOutputStream tempByteArray = (ByteArrayOutputStream) gson.fromJson(jsonObj, new TypeToken<ByteArrayOutputStream>() {}.getType());
			NBTTagCompound nbtFeats;
			nbtFeats = CompressedStreamTools.readCompressed(new ByteArrayInputStream(tempByteArray.toByteArray()));
			NBTTagList nbtFeatList = (NBTTagList) nbtFeats.getTag("features");
			featureList = new ArrayList<TutorialFeature>();
			
			for(int i =0;i<nbtFeatList.tagCount();i++) {
				NBTTagCompound nbtFeat=nbtFeatList.getCompoundTagAt(i);
				TutorialFeature test = (TutorialFeature)Class.forName(TutorialFeatureType.valueOf(nbtFeat.getString("type")).className).newInstance();
				test.load(nbtFeat);
				featureList.add(test);
			}
			if(this.type == PacketType.SINGLE)
				this.featureIndex = buf.readInt();
			else
				this.featureIndex = 0;
		} catch (IOException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
       
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	//This code can probably be simplified, but I copied it from the old packet system - Stephen G.
        buf.writeInt(this.type.ordinal());
        buf.writeInt(expID);
        NBTTagCompound nbtFeatures = new NBTTagCompound();
		NBTTagList nbtList = new NBTTagList();
		for(int i =0;i<featureList.size();i++) {
			nbtList.appendTag(featureList.get(i).save());
		}
		nbtFeatures.setTag("features", nbtList);
		
		final ByteArrayOutputStream experimentUpdatesTemp = new ByteArrayOutputStream();	//must convert into ByteArray becuase converting with just Gson fails on reveiving end
		try {
			CompressedStreamTools.writeCompressed(nbtFeatures, experimentUpdatesTemp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Gson gson = new Gson();
		Type gsonType = new TypeToken<ByteArrayOutputStream>(){}.getType();
		final String experimentUpdates = gson.toJson(experimentUpdatesTemp, gsonType);
		buf.writeInt(experimentUpdates.length());
        buf.writeBytes(experimentUpdates.getBytes());
        
        if(type == PacketType.SINGLE)
        	buf.writeInt(featureIndex);
    }

}
