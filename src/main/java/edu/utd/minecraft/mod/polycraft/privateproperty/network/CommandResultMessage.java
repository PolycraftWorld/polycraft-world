package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CommandResultMessage implements IMessage{

	public APICommandResult result;

    public CommandResultMessage()
    {
    }

    public CommandResultMessage(List<Object> params)
    {
        if (params.size() == 1)
            this.result = (APICommandResult)params.get(0);
    }
    
    public CommandResultMessage(APICommandResult result)
    {
        this.result = result;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    	JsonParser parser = new JsonParser();
    	result = APICommandResult.fromJson((JsonObject)parser.parse(StandardCharsets.UTF_8.decode(buf.readBytes(buf.readInt()).nioBuffer()).toString()));
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	String tempJson = result.toJson().toString();
    	buf.writeInt(tempJson.length());
        buf.writeBytes(tempJson.getBytes());
    }

}
