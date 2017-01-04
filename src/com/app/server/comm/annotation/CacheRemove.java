package com.app.server.comm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheRemove {
	
	String[] keys() ;
	
	String pkField() default "";
	
	boolean isModuel() default false;//true为按照模块删除，false是按照简单的key删除

}
