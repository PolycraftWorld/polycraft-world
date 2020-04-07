package edu.utd.minecraft.mod.polycraft.privateproperty.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.Packet;

public class PolycraftNetworkManager extends SimpleChannelInboundHandler<Packet>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("I received a packet");
		
	}

}
