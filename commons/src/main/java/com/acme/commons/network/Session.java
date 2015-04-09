package com.acme.commons.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

public abstract class Session {

    protected final ChannelHandlerContext ctx;
    protected volatile SessionListener listener;

    public Session(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.ctx.pipeline().addLast(getHandler());
    }

    protected abstract ChannelHandler getHandler();

    public void setListener(SessionListener listener) {
        this.listener = listener;
    }

    public abstract void write(String message);

    public abstract void update();

    public abstract void close();
}
