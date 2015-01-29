package com.acme.server;

import com.acme.commons.network.NetworkServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;

public class FakeNetworkServer extends NetworkServer {

    @Override
    protected ChannelInitializer<NioSocketChannel> createTransport() {
        return new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
            }
        };
    }

    @Override
    public void bind(int port) {
    }

    @Override
    public void update() {
    }

    @Override
    public void dispose() {
    }
}
