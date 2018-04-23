package com.blueline.net.sms.common;

import com.blueline.net.sms.manager.EventLoopGroupFactory;
import com.blueline.net.sms.manager.ExitUnlimitCirclePolicy;
import io.netty.util.concurrent.Future;
import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class TestCirculeFutureTask {
	int cnt = 0;
	
	private void throwsexp(){
		System.out.println("==" + System.currentTimeMillis());
		int s = 3/1;
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	@Test
	public void testexp(){
		EventLoopGroupFactory.INS.getBusiWork().scheduleWithFixedDelay(new Runnable(){

			@Override
			public void run() {
				
				throwsexp();
				
			}
		}, 2,1, TimeUnit.SECONDS);
		
		EventLoopGroupFactory.INS.getBusiWork().scheduleWithFixedDelay(new Runnable(){

			@Override
			public void run() {
				
				System.out.println(DateFormatUtils.format(Calendar.getInstance(), "MMdd"));

			}
		}, 2,2, TimeUnit.SECONDS);

		try {
			Thread.sleep(5*1000);
			EventLoopGroupFactory.INS.closeAll();
			System.out.println(
					"关闭EventLoopGroupFactory"
			);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LockSupport.park();
	}
	
	@Test
	public void test() {
		final Thread th = Thread.currentThread();
		EventLoopGroupFactory.INS.submitUnlimitCircleTask( new Callable<Integer>() {
			private long lastime = 0;
			@Override
			public Integer call() throws Exception {
				cnt++;
				long now = System.nanoTime();
				System.out.println(now - lastime);
				lastime = now;
				return cnt;
			}
		}, new ExitUnlimitCirclePolicy() {

			@Override
			public boolean notOver(Future future) {
				try {
					boolean ret =  (Integer)future.get() < 100;
					if(!ret){
						LockSupport.unpark(th);
					}
					return ret;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				LockSupport.unpark(th);
				return false;
			}
		},0);
		System.out.println(ManagementFactory.getRuntimeMXBean().getName());
		LockSupport.park();
		

	}

}
