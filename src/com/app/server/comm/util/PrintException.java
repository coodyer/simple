package com.app.server.comm.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;

import com.app.server.comm.base.BaseLogger;

public class PrintException {

    /**
     * 获取Exception的堆栈新息。用于显示出错来源时使用。
     * 
     * @param e Exception对象
     * @param length 需要的信息长度，如果 <=0,表示全部信息
     * @return String 返回该Exception的堆栈新息
     * @author AIXIANG
     */
    public static String getErrorStack(Throwable e, int length) {
        String error = null;
        if (e != null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                e.printStackTrace(ps);
                error = baos.toString();
                if (length > 0) {
                    if (length > error.length()) {
                        length = error.length();
                    }
                    error = error.substring(0, length);
                }
                baos.close();
                ps.close();
            } catch (Exception e1) {
                error = e.toString();
            }
        }
        return error;
    }
    
    public static void printException(BaseLogger logger,Throwable e){
    	e.printStackTrace();
    	String error=getErrorStack(e, 0);
    	logger.info(error);
    	logger.error(error);
    }
}
