package com.huangyiming.disjob.monitor.rms;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.huangyiming.disjob.common.util.DateUtil;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.monitor.rms.pojo.RMSMonitorInfo;
import com.huangyiming.disjob.monitor.rms.util.RMSPropertityConfigUtil;

public class BaseRMSMonitor implements Runnable {

	protected boolean available = true;
	 
	private static final int maxWaitingMsgToSend = 500;
	private ResponseHandler responseHandler = new DefaultResponseHandler();
	private static BlockingQueue<Runnable> rmsWorkQueue = new LinkedBlockingQueue<Runnable>(maxWaitingMsgToSend);
	protected static final DiscardPolicy rmsDiscardPolicy = new DiscardPolicy(){
		 public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
			 LoggerUtil.error("[RMS Monitor] workQueue size is:" + rmsWorkQueue.size() + " a rms msgToSend is gonging to be discard when you see this message.");
        }
	};
	private static final Executor executor = new ThreadPoolExecutor(2, 5, 20L, TimeUnit.SECONDS,
			rmsWorkQueue, new ThreadFactory() {
				AtomicInteger atomicInteger = new AtomicInteger(0);
				@Override
				public Thread newThread(Runnable r) {
					return new Thread(r, "thread-rms-monitor-" + atomicInteger.incrementAndGet());
				}
			}, rmsDiscardPolicy);
	private RMSMonitorInfo monitorInfo;
	private String message ;
	public BaseRMSMonitor(RMSMonitorInfo monitorInfo, String message) {
		this.monitorInfo = monitorInfo;
		this.message = message;
	}

	@Override
	public void run() {
		doSend();
	}
	
	public void send(){
		if(RMSPropertityConfigUtil.ifUseRMSMonitor()){
			if(monitorInfo.isAvailable()){
				executor.execute(this);
			}
		}
	}
	
	protected void doSend() {
		if(this.available){
			message = message.replaceAll("/", " ");
			LoggerUtil.info("[ rms monitor doSend]:"+message+"; doSend at time:"+DateUtil.getFormatNow());
			SendInfo sendInfo = new SendInfo(monitorInfo, message);
			String sendString = sendInfo.get();
			LoggerUtil.info("[ start ] rms send at time:"+DateUtil.getFormatNow()+"; send infos："+sendString);
			MonitorClient client = new MonitorClient(sendString);
			MonitorResponse response = client.execute(new MonitorRequest(RMSPropertityConfigUtil.getMonitorUrl(), monitorInfo.getProjectCode(), sendString));
			if(responseHandler != null&&response!=null){
				responseHandler.handle(response);
				LoggerUtil.info("[ end ] rms receive response at time:"+DateUtil.getFormatNow()+"]"+"; response infos："+response.toString());
			}else{
				LoggerUtil.warn(sendString+": response null");
			}
		}
	}
	
	private static class DefaultResponseHandler implements ResponseHandler{

		@Override
		public void handle(MonitorResponse response) {
			if("0".equals(response.getCode())){
				LoggerUtil.info(response.toString());
			}else{
				LoggerUtil.warn(response.toString());
			}
		}
		
	}
	
	private interface ResponseHandler{
		public void handle(MonitorResponse monitorResponse);
	}
	
	public static void main(String[] args) {
		System.out.println("file:/data/www/pms/daemon/provider/auto_update_deals_status.php is not exists!".replaceAll("/", "->"));
	}
}
