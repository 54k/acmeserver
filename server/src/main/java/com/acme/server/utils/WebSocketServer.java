package com.acme.server.utils;

import com.acme.commons.network.NetworkServer;
import com.acme.commons.network.Session;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class WebSocketServer extends NetworkServer {

    private final Queue<Runnable> incomingEvents = new ConcurrentLinkedQueue<>();

    @Override
    protected ChannelInitializer<NioSocketChannel> createTransport() {
        return new WebSocketTransport();
    }

    @Override
    public void update() {
        while (!incomingEvents.isEmpty()) {
            incomingEvents.poll().run();
        }
    }

    private final class WebSocketTransport extends ChannelInitializer<NioSocketChannel> {

        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(65536));
            pipeline.addLast(new WebSocketServerProtocolHandler("/"));
            pipeline.addLast(new WebSocketAcceptor());
        }
    }

    private final class WebSocketAcceptor extends ChannelInboundHandlerAdapter {

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
                channelGroup.add(ctx.channel());
                incomingEvents.add(() -> {
                    WebSocketSession session = new WebSocketSession(ctx);
                    session.setListener(listener);
                    listener.connected(session);
                });
            }
        }
    }

    private static final class WebSocketSession extends Session {

        private final Queue<Runnable> incomingEvents = new ConcurrentLinkedQueue<>();

        public WebSocketSession(ChannelHandlerContext ctx) {
            super(ctx);
        }

        protected ChannelHandler getHandler() {
            return new WebSocketHandler();
        }

        @Override
        public void update() {
            while (!incomingEvents.isEmpty()) {
                incomingEvents.poll().run();
            }
        }

        public void write(String message) {
            ctx.writeAndFlush(new TextWebSocketFrame(message));
        }

        public void close() {
            ctx.writeAndFlush(new CloseWebSocketFrame()).addListener(f -> ctx.close());
        }

        private final class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

            @Override
            public boolean acceptInboundMessage(Object msg) throws Exception {
                return msg instanceof TextWebSocketFrame;
            }

            @Override
            protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
                String text = msg.text();
                incomingEvents.add(() -> listener.messageReceived(WebSocketSession.this, text));
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                incomingEvents.add(() -> listener.disconnected(WebSocketSession.this));
            }
        }
    }
}
