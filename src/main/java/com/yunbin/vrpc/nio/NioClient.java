package com.yunbin.vrpc.nio;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by cloud.huang on 17/10/6.
 */
public class NioClient {


    public static void main(String[] args) throws Exception {

        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress("localhost", 8006));
        Selector selector = Selector.open();

        channel.register(selector, SelectionKey.OP_CONNECT);


        while (true) {
            if (selector.select(1000) == 0) {
                System.out.print(".");
                TimeUnit.MILLISECONDS.sleep(1000);
                continue;
            }
            Set<SelectionKey> keys = selector.selectedKeys();
            System.out.println(keys.size());
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                if (key.isConnectable()) {
                    System.out.println("clent isConnectable");

                    channel.finishConnect();
                    byte[] data = "this msg form clent init".getBytes();
                    channel.write(ByteBuffer.wrap(data));
                    channel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    TimeUnit.MILLISECONDS.sleep(2000);
                    System.out.println("clent read start");
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int index = client.read(buffer);
                    String content = new String(buffer.array(), 0, index);
                    System.out.println(content);

                    byte[] data = "this msg form clent".getBytes();
                    channel.write(ByteBuffer.wrap(data));

                    channel.register(selector, SelectionKey.OP_READ);
                }

            }
        }
    }


}
