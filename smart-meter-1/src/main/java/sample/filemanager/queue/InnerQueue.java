package sample.filemanager.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class InnerQueue<T> {
	private BlockingQueue<T> mRespMsgQ;
	
	public InnerQueue() {
		
		mRespMsgQ = new LinkedBlockingQueue<T>();
	}
	
	public void putData(T respMsg) throws InterruptedException {
		mRespMsgQ.put(respMsg);
	}
	
	public T pollData() {
		T data = mRespMsgQ.poll();
		return data;
	}
	
	public T pollData(int timeoutSec) throws InterruptedException {
		T data = mRespMsgQ.poll(timeoutSec, TimeUnit.SECONDS);
		return data;
	}
	
	public T takeData() throws InterruptedException {
		T data = mRespMsgQ.take();
		return data;
	}
	
}