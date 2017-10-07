package com.yunbin.vrpc.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by cloud.huang on 17/10/6.
 */
public class NioSever {

    private static Selector selector;

    public static void main(String[] args) throws Exception {
        selector = Selector.open();

        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        channel.socket().bind(new InetSocketAddress("localhost", 8006));

        channel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            if (selector.select(1000) == 0) {
                System.out.print(".");
                continue;
            }
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                if (!key.isValid()) {
                    System.out.println("key is not Valid");
                    continue;
                }
                if (key.isAcceptable()) {
                    accept(key);
                }

                if (key.isReadable()) {
                    read(key);
                }

//                if (key.isWritable()) {
//                    write(key);
//                }

                it.remove();
            }
        }


    }

    private static void write(SelectionKey key) throws Exception {
        System.out.println("write start...");
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("this msg from server".getBytes());
        channel.write(buffer);
        channel.register(selector, SelectionKey.OP_READ);

    }

    private static void read(SelectionKey key) throws Exception {
        System.out.println("read start...");
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int index = 0;
        try {
            index = channel.read(buffer);
        } catch (Exception e) {
            key.channel().close();
            key.cancel();
            return;
        }

        if (index == -1) {
            key.channel().close();
            key.cancel();
            return;
        }


        String content = new String(buffer.array()).trim();
        System.out.println(content);

        byte[] data = "this msg form server".getBytes();
        channel.write(ByteBuffer.wrap(data));

        System.out.println("write end...");

    }

    private static void accept(SelectionKey key) throws Exception {
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = channel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("new socketChannel create...");
    }
}
