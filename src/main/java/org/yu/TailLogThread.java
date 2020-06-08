package org.yu;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.websocket.Session;
public class TailLogThread extends Thread {
    private BufferedReader reader;
    private Session session;
    private AtomicBoolean isClose = new AtomicBoolean(false);
    private Process process;
    public TailLogThread( Session session,String log) {  
    	try {
    		//日志实时获取新内容的能力，借用了tail这个命令， mac和linux都默认支持这个命令
			this.process = Runtime.getRuntime().exec("tail -f "+log);
			this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    this.session = session;
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    @Override
    public void run() {
        String line= null;
        try {
            while(  isClose.get() == false &&  ( reader == null || (reader!=null && (line = reader.readLine()) != null )) ) {
            	if(reader == null) {
            		Thread.sleep(1000);
            		System.out.println("sleep 1000");
            	}else {
            		
            		if(isClose.get() == true) {
            			System.out.println(this.getName()+" thread is close");
            			break;
            		}
            		//判断下websocket session是否关闭了
            		if(session == null || !session.isOpen()) {
            			System.out.println("session close");
            			break;
            		}else {
            			//将实时日志通过WebSocket发送给客户端，给每一行添加一个HTML换行
            			session.getBasicRemote().sendText(line + "<br>");
            		}
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        	try {
        		if(reader!=null) {
        			reader.close();
        		}
        		if(process!=null) {
        			process.destroy();
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    public synchronized void close() {
    	System.out.println("close thread..."+this.getName());
    	isClose.set(true);
    }
    public synchronized boolean isSessionOpen() {
    	return session.isOpen();
    }
}