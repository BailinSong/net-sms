package com.blueline.net.sms.common;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 由于System.currentTimeMillis()性能问题，缓存当前时间，每1s更新一次
 */
public enum CachedMillisecondClock {
	INS(1000);
	private final Timer timer;
	private final AtomicLong now = new AtomicLong(0);// 当前时间
	private final long accuracy;

	private CachedMillisecondClock(int accuracy) {
		this.accuracy=accuracy;
		this.now.set(System.currentTimeMillis());
		this.timer=PAFTimer.getTimer();
		start();
	}

	private void start() {
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				now.set(System.currentTimeMillis());
			}
		}, 0,accuracy);
	}

	public long now() {
		return now.get();
	}

	static class PAFTimer{
		static Timer timer;
		static{
			timer=new Timer(CachedMillisecondClock.class.getSimpleName(),true);
		}

		public static Timer getTimer() {
			return timer;
		}
	}
}
