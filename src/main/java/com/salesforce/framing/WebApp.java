package com.salesforce.framing;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.util.Deque;
import java.util.Map;

public class WebApp {

    public static final String X_FRAME_OPTIONS_PARAM = "x_frame_options", CONTENT_SECURITY_PARAM = "content_security";

    public static void main(String[] args) throws Exception {

        final String html = ByteStreams.toString(WebApp.class.getResourceAsStream("/index.html"));

        final Undertow server = Undertow.builder()
                .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
                .setServerOption(UndertowOptions.ENABLE_SPDY, true)
                .addHttpListener(getPort(), "0.0.0.0")
                .setHandler(exchange -> {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                    final Map<String, Deque<String>> params = exchange.getQueryParameters();
                    if (params.containsKey(X_FRAME_OPTIONS_PARAM))
                        exchange.getResponseHeaders().put(HttpString.tryFromString("X-Frame-Options"), params.get(X_FRAME_OPTIONS_PARAM).element());
                    if (params.containsKey(CONTENT_SECURITY_PARAM))
                        exchange.getResponseHeaders().put(HttpString.tryFromString("Content-Security-Policy"), params.get(CONTENT_SECURITY_PARAM).element());
                    exchange.getResponseSender().send(html);
                })
                .build();

        server.start();

    }

    private static int getPort() {
        final String envPort = System.getenv("PORT");
        if (envPort != null)
            return Integer.parseInt(envPort);
        return 8080;
    }

}
