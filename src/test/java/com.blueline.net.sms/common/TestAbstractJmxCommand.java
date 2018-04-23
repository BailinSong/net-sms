package com.blueline.net.sms.common;

import com.blueline.net.sms.mbean.AbstractJmxCommand;

import javax.management.*;
import java.io.IOException;

public class TestAbstractJmxCommand extends AbstractJmxCommand {

	protected void invoke(MBeanServerConnection mconn,String[] args)
	{
		 try {
			ObjectName stat = new ObjectName("com.zx.sms:name=ConnState");
			System.out.println(mconn.invoke(stat, "print",args, new String[]{"java.lang.String"}));
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException{
		TestAbstractJmxCommand c = new TestAbstractJmxCommand();
		 c.main0(args);
	 }
}
