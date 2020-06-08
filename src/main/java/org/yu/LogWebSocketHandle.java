package org.yu;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
/**websocket访问url*/
@ServerEndpoint("/websocket/log.ws")
public class LogWebSocketHandle {
	/**logMap是为了安全考虑，不能任由客户端想要读哪个文件就读哪个文件*/
	private static Map<String, String> logMap = new HashMap<String, String>();
	static {
		logMap.put("catalina.out","/opt/dir/d/apache-tomcat-9.0.31/logs/catalina.out");
		logMap.put("manager-log","/opt/dir/d/apache-tomcat-9.0.31/logs/manager.2020-06-08.log");
	}
	private TailLogThread thread ;
	
	public LogWebSocketHandle() {
		System.out.println("LogWebSocketHandle start");
	}
	@OnOpen
	public void onOpen(Session session) {
			InetAddress ia = null;
			try {
				ia = InetAddress.getLocalHost();
				String localname = ia.getHostName();
				String localip = ia.getHostAddress();
				System.out.println("本机名称是：" + localname+",ip:"+ localip);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	@OnMessage
	public void onMessage(String message,Session session) {
		System.out.println("message:"+message);
		String log = logMap.get(message);
		try {
			if (log != null) {
				if(log.contains("close")) {
					//如果前端发送的消息是close,则关闭当前的thread，并返回
					thread.close();
					thread = null;
					return;
				}
				System.out.println(message + "  - >对应的实际log path:" + log);
			} else {
				System.out.println(message + "找不到对应的log日志");
				return;
			}
			// 一定要启动新的线程，防止InputStream阻塞处理WebSocket的线程
			if(thread!=null) {
				//当前页面切换查看不同日志的时候会出现thread!=null，这时候把老的thread进行关闭下
				System.out.println("thread is not null");
				thread.close();
				thread = new TailLogThread(session,log);
				thread.start();
			}else {
				System.out.println("thread is null");
				thread = new TailLogThread(session,log);
				thread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@OnClose
	public void onClose() {
		System.out.println("onclose");
	}
	@OnError
	public void onError(Throwable thr) {
		thr.printStackTrace();
	}
}
