package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.Label;

import java.util.HashMap;

public class VMethodInfo {

    private final String className;
    private final String methodName;
    private final int methodOffset;
    private final HashMap<String, Integer> methodParams;

    public VMethodInfo(String className, String methodName, int methodOffset) {
        this.className = className;
        this.methodName = methodName;
        this.methodOffset = methodOffset;
        this.methodParams = new HashMap<>();
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getMethodOffset() {
        return methodOffset;
    }

    public Label getMethodLabel() {
        return LabelUtils.getMethodLabel(className, methodName);
    }

    public void addParam(String paramName, int paramOffset) {
        methodParams.put(paramName, paramOffset);
    }

    public Integer getParamOffset(String paramName) {
        return methodParams.get(paramName);
    }

    public void copyParams(VMethodInfo otherMethod) {
        methodParams.putAll(otherMethod.methodParams);
    }

    public int getParamCount() {
        return methodParams.size();
    }

}
