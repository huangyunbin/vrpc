package com.yunbin.vrpc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * Created by cloud.huang on 17/10/5.
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    private final ByteBuf firstMessage ;

    public EchoClientHandler() {
        firstMessage = Unpooled.buffer();
        firstMessage.writeInt(1);
        firstMessage.writeCharSequence("test", Charset.forName("utf8"));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
        ctx.writeAndFlush(firstMessage);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TimeUnit.SECONDS.sleep(1);
        ByteBuf copy =((ByteBuf) msg).copy();
        System.out.println("channelRead   " + copy.readInt());
        System.out.println("channelRead   " + copy.toString(Charset.forName("utf8")));
        copy.clear();
        ctx.writeAndFlush(msg);

    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught");
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
