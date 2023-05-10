/*
package com.yeshen.appcenter.config.web;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.valves.AccessLogValve;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

*/
/**
 * 自定义tomcat
 *//*

@Component
public class EmbeddedTomcatConfig implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        ((TomcatServletWebServerFactory) factory).setBaseDirectory(new File("/bignox/log/WebAppcenter"));
        ((TomcatServletWebServerFactory) factory).addEngineValves(getAccessLogValue());
        ((TomcatServletWebServerFactory) factory).addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
                protocol.setMinSpareThreads(400);
                protocol.setMaxThreads(400);
                protocol.setMaxConnections(6500);
                protocol.setAcceptCount(100);
                // 设置长连接保持时间
                protocol.setKeepAliveTimeout(5000);
                // 设置keepalive的最大请求次数 超过则断开连接
                protocol.setMaxKeepAliveRequests(5);
                // 本条连接等待多久没数据关闭连接
                protocol.setConnectionTimeout(1000);
                protocol.setSelectorTimeout(1000);
            }
        });
    }

    private AccessLogValve getAccessLogValue(){
        AccessLogValve accessLogValve = new AccessLogValve();
        accessLogValve.setEnabled(true);
        accessLogValve.setDirectory("accesslog");
        accessLogValve.setPrefix("access_log");
        accessLogValve.setSuffix(".log");
        accessLogValve.setPattern("'%D %t %a \"%r\" %I %s'");
        return accessLogValve;
    }
}*/