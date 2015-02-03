package com.acme.core.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ServiceLoader;

public abstract class NetworkServer {

    protected final ChannelGroup channelGroup = new DefaultChannelGroup("ALL CHANNELS", GlobalEventExecutor.INSTANCE);
    private final ServerBootstrap bootstrap = new ServerBootstrap();
    private final EventLoopGroup eventLoop = new NioEventLoopGroup();

    protected volatile SessionListener listener;

    public static NetworkServer create() {
        ServiceLoader<NetworkServer> servers = ServiceLoader.load(NetworkServer.class);
        return servers.iterator().next();
    }

    protected NetworkServer() {
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.group(eventLoop);
        bootstrap.childHandler(createTransport());
    }

    protected abstract ChannelInitializer<NioSocketChannel> createTransport();

    public void setListener(SessionListener listener) {
        this.listener = listener;
    }

    public void bind(int port) {
        channelGroup.add(bootstrap.bind(port).syncUninterruptibly().channel());
    }

    public abstract void update();

    public void dispose() {
        channelGroup.close().syncUninterruptibly();
        eventLoop.shutdownGracefully().syncUninterruptibly();
    }
}
