package com.app.server.debug.entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import javassist.Modifier;

import com.app.server.comm.base.BaseModel;
import com.app.server.comm.util.StringUtil;

@SuppressWarnings("serial")
public class CtBeanEntity extends BaseModel{

	private String fieldName;
	private Object fieldValue;
	private Class<?> fieldType;
	private List<CtAnnotationEntity> annotations;
	private Field sourceField;
	private Boolean isStatic=false;
	private Boolean isFinal=false;
	private String modifier;
	
	public Field getSourceField() {
		return sourceField;
	}
	public Boolean getIsStatic() {
		return isStatic;
	}
	public void setIsStatic(Boolean isStatic) {
		this.isStatic = isStatic;
	}
	public Boolean getIsFinal() {
		return isFinal;
	}
	public void setIsFinal(Boolean isFinal) {
		this.isFinal = isFinal;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(Integer modifier) {
			if(Modifier.isPrivate(modifier)){
				this.modifier = "private";
				return;
			}
			if(Modifier.isPublic(modifier)){
				this.modifier = "public";
				return;
			}
			if(Modifier.isProtected(modifier)){
				this.modifier = "protected";
				return;
			}
			if(Modifier.isNative(modifier)){
				this.modifier = "native";
				return;
			}
	}
	public void setSourceField(Field sourceField) {
		this.sourceField = sourceField;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public Object getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(Object fieldValue) {
		this.fieldValue = fieldValue;
	}
	public Class<?> getFieldType() {
		return fieldType;
	}
	
	public List<CtAnnotationEntity> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(List<CtAnnotationEntity> annotations) {
		this.annotations = annotations;
	}
	public void setFieldType(Class<?> fieldType) {
		this.fieldType = fieldType;
	}
	public static void main(String[] args) {
	}
	
	public Annotation getAnnotation(Class<?> clazz){
		if(StringUtil.isNullOrEmpty(annotations)){
			return null;
		}
		for (CtAnnotationEntity annotation:annotations) {
			if(clazz.isAssignableFrom(annotation.getAnnotation().annotationType())){
				return annotation.getAnnotation();
			}
		}
		return null;
	}
	
}
