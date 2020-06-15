package com.jet.demo.Utils;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * map工具类
 */
public final class MapUtil {
    private static final ConcurrentHashMap<String, Object> toolMap = new ConcurrentHashMap<>();

    // 判断是否属于基本类型
    private static final boolean isBelongBasicTypeClass(Class clazz){
        return (String.class == clazz)||(Boolean.class == clazz)||(Integer.class == clazz)||(Double.class == clazz)||(Long.class == clazz)||(Float.class == clazz)||(Character.class == clazz);
    }


    private static final Object getObjectByCascadeKey(String keyName){
        return getObjectByCascadeKey(toolMap, keyName);
    }
    /**
     * 根据对象取级联属性的值
     * 如hhh.xxx.xxz
     * 不传入原始对象的话，则用toolMap作为原始对象
     * @param keyName
     * @return
     */
    private static final Object getObjectByCascadeKey(Object sourceObject,String keyName){
        if(null == keyName || "".equals(keyName)){
            return null;
        }
        // 将keyName按.进行分割
        String[] keyNameChainArray = keyName.split("\\.");
        int len = keyNameChainArray.length;
        Object currentObject = sourceObject;
        for(int keyNameChainArrayIndex = 0; keyNameChainArrayIndex < len; keyNameChainArrayIndex++){
            String key = keyNameChainArray[keyNameChainArrayIndex];
            Object object = getObjectByKey(currentObject, key);
            if(object == null){
                return null;
            }
            if(keyNameChainArrayIndex+1  == len){
                return object;
            }else{
                currentObject = object;
            }
        }
        return null;
    }

    //根据key获取对象中的对应属性名的值
    private static Object getObjectByKey(Object object,String key){
        // 如果是AbstractMap的子类，直接调用get(String key)方法返回
        if(object instanceof AbstractMap){
            AbstractMap map = (AbstractMap)object;
            return map.get(key);
        }else{
            // 否则就调用getter方法进行返回
            // 获取对象的类
            Class clazz = object.getClass();
            // 判断是否属于基本对象类型
            if(isBelongBasicTypeClass(clazz)){
                return null;
            }
            // 构造方法名，属性名首字母大写
            StringBuilder methodNameBuilder = new StringBuilder("get");
            int len = key.length();
            for(int i = 0; i < len; i++){
                char c = key.charAt(i);
                if(0 ==  i){
                    // 首字母大写
                    if(c >= 'a' && c <= 'z'){
                        c -= 32;
                    }
                }
                methodNameBuilder.append(c);
            }
            String methodName= methodNameBuilder.toString();
            try{
                // 反射调用getter方法并返回结果
                Method method = clazz.getMethod(methodName);
                Object resultObject = method.invoke(object);
                return resultObject;
            }catch (Exception e){
//                e.printStackTrace();
                return null;
            }

        }
    }

    /**
     * 测试实体类1
     */
    private static final class TestClass{
        private String testName;
        private String testId;
        private TestClass2 testClass2;

        public TestClass2 getTestClass2() {
            return testClass2;
        }

        public void setTestClass2(TestClass2 testClass2) {
            this.testClass2 = testClass2;
        }

        public String getTestName() {
            return testName;
        }

        public void setTestName(String testName) {
            this.testName = testName;
        }

        public String getTestId() {
            return testId;
        }

        public void setTestId(String testId) {
            this.testId = testId;
        }

    }
    /**
     * 测试实体类2
     */
    private static final class TestClass2{
        private int testNumber;
        private boolean testBool;

        public int getTestNumber() {
            return testNumber;
        }

        public void setTestNumber(int testNumber) {
            this.testNumber = testNumber;
        }

        public boolean isTestBool() {
            return testBool;
        }

        public void setTestBool(boolean testBool) {
            this.testBool = testBool;
        }
    }

    public static void main(String[] args) {
        TestClass testClass = new TestClass();
        testClass.setTestId("hhh");
        testClass.setTestName("好好好");
        TestClass2 testClass2 = new TestClass2();
        testClass2.setTestBool(true);
        testClass2.setTestNumber(1045);
        testClass.setTestClass2(testClass2);
        toolMap.put("nima", testClass);
        // 获取toolMap中nima.testId的值
        System.out.println("nima.testId="+MapUtil.getObjectByCascadeKey("nima.testId"));
        // 获取toolMap中nima.testClass2.testNumber的值
        System.out.println("nima.testClass2.testNumber="+ MapUtil.getObjectByCascadeKey("nima.testClass2.testNumber"));
        // 获取testClass中testClass2.testNumber的值
        System.out.println("testClass2.testNumber="+ MapUtil.getObjectByCascadeKey(testClass, "testClass2.testNumber"));
        // 获取toolMap中nima.testClass2.testNumber的值
        System.out.println("nima.testClass2.testBool="+MapUtil.getObjectByCascadeKey("nima.testClass2.testBool"));

    }
}
