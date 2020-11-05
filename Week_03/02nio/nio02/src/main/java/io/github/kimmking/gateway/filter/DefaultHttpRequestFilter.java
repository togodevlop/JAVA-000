package io.github.kimmking.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author lixiaobing
 * @date 2020-11-02 15:45
 * @Description:
 */
public class DefaultHttpRequestFilter implements HttpRequestFilter {

    @Override
    public void preFilter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
        fullRequest.headers().set("My_header", "test");

    }

    @Override
    public void postFilter(FullHttpResponse fullHttpResponse, ChannelHandlerContext ctx) {

    }

}
