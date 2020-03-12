package edu.utd.minecraft.mod.polycraft.aitools.commands;

import java.nio.charset.StandardCharsets;

import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;

public abstract class APICommandBase {
	
	protected float stepCost;
	protected Side side;
	
	public APICommandBase() {};
	
	public APICommandBase(float cost){
		stepCost = cost;
		side = Side.CLIENT;
	}
	
	/**
	 * Server side actions to perform when this command is issued/called
	 * @return
	 */
	public abstract APICommandResult serverExecute(String[] args, EntityPlayerMP player);
	
	/**
	 * Server side actions to perform when this command is issued/called
	 * @return
	 */
	public abstract APICommandResult clientExecute(String[] args);
	
	public float getStepCost() {
		return stepCost;
	}
	
	public Side getSide() {
		return side;
	}
	
	/**
     * When Overriding, call super fromBytes first for consistency 
     * @param buf
     */
    public void fromBytes(ByteBuf buf)
    {
    	stepCost = buf.readFloat();
    	side = buf.readBoolean() ? Side.CLIENT: Side.SERVER;	// Read isClient. if True set to client, else Server
    }

    /**
     * When Overriding, call super toBytes first for consistency
     * @param buf
     */
    public void toBytes(ByteBuf buf)
    {
    	buf.writeFloat(stepCost);
    	buf.writeBoolean(side.isClient());
    }
	
}
