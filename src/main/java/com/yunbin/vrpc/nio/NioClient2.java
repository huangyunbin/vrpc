package com.yunbin.vrpc.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * Created by cloud.huang on 17/10/6.
 */
public class NioClient2 {

    public static void main(String[] args) throws Exception {
        SocketChannel channel = SocketChannel.open(new InetSocketAddress("localhost", 8006));
        channel.configureBlocking(false);

        byte[] data = "this msg form clent".getBytes();
        channel.write(ByteBuffer.wrap(data));

        TimeUnit.SECONDS.sleep(10);

        channel.close();
    }
}
