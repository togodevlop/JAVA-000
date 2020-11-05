package io.github.kimmking.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface HttpRequestFilter {

    /**
     * change inbound request
     * @param fullRequest
     * @param ctx
     */
    void preFilter(FullHttpRequest fullRequest, ChannelHandlerContext ctx);

    /**
     * change outbound response
     * @param fullHttpResponse
     * @param ctx
     */
    void postFilter(FullHttpResponse fullHttpResponse, ChannelHandlerContext ctx);

}
