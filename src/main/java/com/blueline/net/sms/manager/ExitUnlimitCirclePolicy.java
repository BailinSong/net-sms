package com.blueline.net.sms.manager;

import io.netty.util.concurrent.Future;

public interface ExitUnlimitCirclePolicy {
	boolean notOver(Future future);
}
