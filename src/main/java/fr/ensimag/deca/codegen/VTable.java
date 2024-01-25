package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.Label;

import java.util.HashMap;
import java.util.LinkedList;

public class VTable {

    private final SymbolTable.Symbol superClassSymbol;
    private final String className;
    private final DAddr classAddr;
    private final OrderedHashMap<String, VMethodInfo> classMethods;
    private final HashMap<String, Integer> classFields;
    private int redefinedFieldsCount;

    public VTable(SymbolTable.Symbol superClassSymbol, SymbolTable.Symbol classSymbol,
                  DAddr classAddr) {
        this.superClassSymbol = superClassSymbol;
        this.className = classSymbol.getName();
        this.classAddr = classAddr;
        this.classMethods = new OrderedHashMap<>();
        this.classFields = new HashMap<>();
        this.redefinedFieldsCount = 0;
    }

    public VTable getVTableOfSuperClass(VTableManager vTM) {
        return vTM.getVTable(superClassSymbol.getName());
    }

    public String getClassName() {
        return className;
    }

    public DAddr getClassAddr() {
        return classAddr;
    }

    public void addMethod(String methodName, int methodOffset) {
        VMethodInfo methodInfo = new VMethodInfo(className, methodName, methodOffset);
        classMethods.addLast(methodName, methodInfo);
    }

    public void addSuperMethod(String superClassName, String methodName, int methodOffset) {
        VMethodInfo methodInfo = new VMethodInfo(superClassName, methodName, methodOffset);
        classMethods.addLast(methodName, methodInfo);
    }

    public void copyMethodParams(VTable otherVTable, String methodName) {
        classMethods.get(methodName).copyParams(otherVTable.classMethods.get(methodName));
    }

    public LinkedList<VMethodInfo> getClassMethods() {
        return classMethods.getOrderedValues();
    }

    public int getMethodOffset(String methodName) {
        return classMethods.get(methodName).getMethodOffset();
    }

    public Label getMethodLabel(String methodName) {
        return classMethods.get(methodName).getMethodLabel();
    }
    public void addParamToMethod(String methodName, String paramName,
                                 int paramOffset) {
        classMethods.get(methodName).addParam(paramName, paramOffset);
    }

    public Integer getParamOffsetOfMethod(String methodName, String paramName) {
        if (!classMethods.containsKey(methodName)) return null;
        return classMethods.get(methodName).getParamOffset(paramName);
    }

    public int getParamCountOfMethod(String methodName) {
        return classMethods.get(methodName).getParamCount();
    }

    public void addField(String fieldName, int fieldOffset) {
        if (classFields.containsKey(fieldName)) {
            redefinedFieldsCount++;
        }
        classFields.put(fieldName, fieldOffset);
    }

    public void addAllFields(VTable otherVTable) {
        classFields.putAll(otherVTable.classFields);
    }

    public Integer getFieldOffset(String fieldName) {
        return classFields.get(fieldName);
    }

    public int getFieldsCount() {
        return classFields.size() + redefinedFieldsCount;
    }

}
