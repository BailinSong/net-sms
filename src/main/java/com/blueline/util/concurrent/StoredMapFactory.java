package com.blueline.util.concurrent;

import java.util.Map;

public interface StoredMapFactory<K,T> {
	
	/**
	 * @param storedpath
	 * 数据文件保存的路径
	 * @param name 
	 * Map的名字
	 */
	Map<K,T> buildMap(String storedpath, String name);
	
}
