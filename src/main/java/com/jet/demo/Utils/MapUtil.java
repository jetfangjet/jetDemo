package com.jet.demo.Utils;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * map工具类
 */
public final class MapUtil {
    private static final ConcurrentHashMap<String, Object> toolMap = new ConcurrentHashMap<>();

    private static final boolean isBelongBasicTypeClass(Class clazz){
        return (String.class == clazz)||(Boolean.class == clazz)||(Integer.class == clazz)||(Double.class == clazz)||(Long.class == clazz)||(Float.class == clazz)||(Character.class == clazz);
    }


    private static final Object getObjectByCascadeKey(String keyName){
        return getObjectByCascadeKey(toolMap, keyName);
    }
    /**
     * 可以取级联属性对象
     * 如hhh.xxx.xxz
     * 不传入原始对象的话，则用toolMap作为原始对象
     * @param keyName
     * @return
     */
    private static final Object getObjectByCascadeKey(Object sourceObject,String keyName){
        if(null == keyName || "".equals(keyName)){
            return null;
        }
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

    private static Object getObjectByKey(Object object,String key){
        if(object instanceof AbstractMap){
            AbstractMap map = (AbstractMap)object;
            return map.get(key);
        }else{
            Class clazz = object.getClass();
            if(isBelongBasicTypeClass(clazz)){
                return null;
            }
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
                // 反射调用getter方法
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
//        System.out.println("nima.testId="+MapUtil.getObjectByCascadeKey("nima.testId"));

        System.out.println("nima.testClass2.testNumber="+ MapUtil.getObjectByCascadeKey("nima.testClass2.testNumber"));
        System.out.println("testClass2.testNumber="+ MapUtil.getObjectByCascadeKey(testClass, "testClass2.testNumber"));
//        System.out.println("nima.testClass2.testBool="+MapUtil.getObjectByCascadeKey("nima.testClass2.testBool"));
//        MapUtil hasagiUtil = new MapUtil();
//        Class clazz = hasagiUtil.getClass();
//        Class superClazz = clazz.getSuperclass();
//        System.out.println(clazz.getName());
//        System.out.println(superClazz.getName());
//        System.out.println(Object.class == superClazz);
    }
}
