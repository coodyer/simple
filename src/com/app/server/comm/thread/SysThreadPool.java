package com.app.server.comm.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SysThreadPool {
	public static final int CORESIZE_NORMAL=5;
	public static final int MAXCORESIZE = 100;
	public static final int KEEPALIVETIME = 10;  //10s
	public static final ExecutorService  threadPool =  new ThreadPoolExecutor(CORESIZE_NORMAL,MAXCORESIZE,
	          KEEPALIVETIME,TimeUnit.SECONDS,
	          new LinkedBlockingQueue<Runnable>()); 
}
