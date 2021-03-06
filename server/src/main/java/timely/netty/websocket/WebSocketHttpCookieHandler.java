package timely.netty.websocket;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import timely.configuration.Security;
import timely.netty.http.HttpRequestDecoder;
import timely.subscription.SubscriptionRegistry;

public class WebSocketHttpCookieHandler extends MessageToMessageCodec<FullHttpRequest, FullHttpRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketHttpCookieHandler.class);
    private boolean anonymousAccessAllowed;

    public WebSocketHttpCookieHandler(Security security) {
        super();
        this.anonymousAccessAllowed = security.isAllowAnonymousAccess();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest msg, List<Object> out) throws Exception {
        msg.retain();
        out.add(msg);
        // If the session cookie exists, set its value on the ctx.
        final String sessionId = HttpRequestDecoder.getSessionId(msg, this.anonymousAccessAllowed);
        ctx.channel().attr(SubscriptionRegistry.SESSION_ID_ATTR).set(sessionId);
        LOG.info("Found session id in WebSocket channel, setting sessionId {} on context", sessionId);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, FullHttpRequest msg, List<Object> out) throws Exception {
        msg.retain();
        out.add(msg);
    }

}
