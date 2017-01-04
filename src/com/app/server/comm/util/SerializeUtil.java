package com.app.server.comm.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil {
	public static byte[] serialize(Object object) throws IOException {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		// 序列化
		baos = new ByteArrayOutputStream();
		oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		byte[] bytes = baos.toByteArray();
		oos.close();
		baos.close();
		return bytes;
	}

	public static Object unserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = null;
			// 反序列化
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			bais.close();
			Object obj=ois.readObject();
			ois.close();
			return obj;
	}
}