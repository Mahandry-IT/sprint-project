package mg.itu.prom16;

import java.lang.reflect.Method;

public class VerbAction {
    private String className;
    private Method methodName;
    private String  httpMethod;
    
    public VerbAction(String className, Method methodName, String httpMethod) {
        setClassName(className);
        setMethodName(methodName);
        setHttpMethod(httpMethod);
    }

    public String getClassName() {
        return className;
    }

    public Class<?> getControllerClass() throws Exception {
        return Class.forName(className);
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    public Method getMethodName() {
        return methodName;
    }
    public void setMethodName(Method methodName) {
        this.methodName = methodName;
    }
    public String getHttpMethod() {
        return httpMethod;
    }
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+":{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName.getName() + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                '}';
    }
}
