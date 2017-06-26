package com.huangyiming.disjob.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
 
/**
 * 深度拷贝
 * @author Disjob
 *
 */
public class DeepCopy {  
    /** 
     * 深层拷贝 ,obj对象务必要实现Serializable接口,否则拷贝失败
     *  
     * @param <T> 
     * @param obj 
     * @return 
     * @throws Exception 
     */  
    public static <T> T copy(T obj) throws Exception {  
    	if(obj == null){
    		return null;
    	}
        //是否实现了序列化接口，即使该类实现了，他拥有的对象未必也有...  
        if(Serializable.class.isAssignableFrom(obj.getClass())){  
            //如果子类没有继承该接口，这一步会报错  
            try {  
                return copyImplSerializable(obj);  
            } catch (Exception e) {  
                //这里不处理，会运行到下面的尝试json  
            	e.printStackTrace();
            }  
        }  
        
        return null;  
    }  
  
    /** 
     * 深层拷贝 - 需要类继承序列化接口 
     * @param <T> 
     * @param obj 
     * @return 
     * @throws Exception 
     */  
    @SuppressWarnings("unchecked")  
    public static <T> T copyImplSerializable(T obj) throws Exception {  
        ByteArrayOutputStream baos = null;  
        ObjectOutputStream oos = null;  
          
        ByteArrayInputStream bais = null;  
        ObjectInputStream ois = null;  
          
        Object o = null;  
        //如果子类没有继承该接口，这一步会报错  
        try {  
            baos = new ByteArrayOutputStream();  
            oos = new ObjectOutputStream(baos);  
            oos.writeObject(obj);  
            bais = new ByteArrayInputStream(baos.toByteArray());  
            ois = new ObjectInputStream(bais);  
  
            o = ois.readObject();  
            return (T) o;  
        } catch (Exception e) {  
            throw new Exception("对象中包含没有继承序列化的对象");  
        } finally{  
            try {  
                baos.close();  
                oos.close();  
                bais.close();  
                ois.close();  
            } catch (Exception e2) {  
                //这里报错不需要处理  
            }  
        }  
    }  
      
  public static void main(String[] args) {

		 Map<String, String> map = new HashMap<String, String>();
		 map.put("1", "123");
 		 Map<String, String> map1=new HashMap<String, String>();
		try {
			map1 = DeepCopy.copy(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 map.remove("1");
		 map.put("123", "12344");
		 for (Map.Entry<String, String> entry:map.entrySet()) {
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
		 System.out.println("==================");
		 for (Map.Entry<String, String> entry:map1.entrySet()) {
				System.out.println(entry.getKey()+":"+entry.getValue());
			}
	
}
    
}  