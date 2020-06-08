package org.yu;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.server.undertow.UndertowConfig;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;

public class WebConfig extends JFinalConfig {

	public static void main(String[] args) {
		UndertowServer.create(new UndertowConfig(WebConfig.class)).configWeb(builder -> {
			builder.addWebSocketEndpoint(LogWebSocketHandle.class);
		}).start();
	}

	@Override
	public void configConstant(Constants me) {
		// 设置编码格式
		me.setEncoding("UTF-8");
		// 设置为开发模式（如果为fals，jfinal会缓存页面，导致开发时修改页面后不能立即呈现）
		me.setDevMode(true);
	}

	@Override
	public void configRoute(Routes routes) {
		// 统一设置映射访问路径 类似于spring mvc的@RequestMapping
	}

	@Override
	public void configPlugin(Plugins me) {
	}

	@Override
	public void configInterceptor(Interceptors me) {

	}

	@Override
	public void configHandler(Handlers me) {
	}

	@Override
	public void configEngine(Engine me) {
	}
}
