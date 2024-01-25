package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractIdentifier;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.util.HashMap;
import java.util.LinkedList;

public class GameBoyManager {

    public static final int nRegs = 4;
    public static final int dynamicFieldsCptAddr = 57343;
    public static final int Addr0 = 57342;
    public static final int TrueAddrMax = 49152; // Oui c'est inversé en GameBoy sinon ce serait trop facile
    public static final int AddrMax = TrueAddrMax + 48;
    // 48 Pour les variables de la librarie

    public static boolean doCp = false;
    public static boolean doCpRgbds = false;
    public static boolean doIncludeUtils = false;
    public static boolean debugMode = false;

    public static String getImmToken() {
        return (doCp) ? "" : "#";
    }

    public static String getLabelSeparator() {
        return (doCp) ? "_" : ".";
    }

    private int printId;
    private int fieldId;
    private final HashMap<String, Integer> globalVars;
    //    private int dynamicFieldsCount;
    private final HashMap<String, HashMap<String, Integer>> methodsVars;
    // HashMap<ClassName.MethodName, HashMap<VarName, VarOffset>
    private String currDeclaringIdentName;
    private final LinkedList<Integer> currMethodSpOffset; // OH LA PURGE PUTAIN ++

    public GameBoyManager() {
        this.printId = 0;
        this.fieldId = 0;
        this.globalVars = new HashMap<>();
//        this.dynamicFieldsCount = 0;
        this.methodsVars = new HashMap<>();
        this.currDeclaringIdentName = null;
        this.currMethodSpOffset = new LinkedList<>();
    }

    public int getAndIncrPrintId() {
        return printId++;
    }

    public void addGlobalVar(String varName) {
        globalVars.put(varName, globalVars.size());
    }

    public Integer getGlobalVarAddr(String varName) {
        if (!globalVars.containsKey(varName)) return null;
        return (Addr0 - 1) - (globalVars.get(varName) * 2);
    }

//    public void addDynamicFields(int value) {
//        dynamicFieldsCount += value;
//    }
//
//    public int getNextDynamicFieldAddr() {
//        return AddrMax + dynamicFieldsCount * 2;
//    }

    public String getMethodKey(String className, String methodName) {
        return className + "." + methodName;
    }

    public String getCurrMethodKey(VTableManager vTM) {
        return getMethodKey(vTM.getCurrClassName(), vTM.getCurrMethodName());
    }

    public void createCurrMethodVarsMap(VTableManager vTM) {
        methodsVars.put(getCurrMethodKey(vTM), new HashMap<>());
        addCurrMethodFieldVar(vTM); // Pour les push de BC et DE dans la méthode
        addCurrMethodFieldVar(vTM);
    }

    public void addCurrMethodVar(VTableManager vTM, String varName) {
        HashMap<String, Integer> methodVars = methodsVars.get(getCurrMethodKey(vTM));
        methodVars.put(varName, getCurrMethodVarCount(vTM));
    }

    public void addCurrMethodFieldVar(VTableManager vTM) {
        addCurrMethodVar(vTM, fieldId + "F");
        fieldId++;
    }

    public Integer getCurrMethodVarCount(VTableManager vTM) {
        for (String className : vTM.getCurrClassNameStack()) {
            String currKey = getMethodKey(className, vTM.getCurrMethodName());
            if (!methodsVars.containsKey(currKey)) continue;
            return methodsVars.get(currKey).size();
        }
        return null;
    }

    public Integer getCurrMethodVarOffset(VTableManager vTM, String varName) {
        for (String className : vTM.getCurrClassNameStack()) {
            String mKey = getMethodKey(className, vTM.getCurrMethodName());
            if (!methodsVars.containsKey(mKey)) continue;

            return methodsVars.get(mKey).get(varName);
        }
        return null;
    }

    public void setCurrDeclaringIdentName(String value) {
        currDeclaringIdentName = value;
    }

    public void pushCurrMethodSpOffset(int initialValue) {
        currMethodSpOffset.addFirst(initialValue);
    }

    public void popCurrMethodSpOffset() {
        currMethodSpOffset.removeFirst();
    }

    public void incr2CurrMethodSpOffset() {
        if (!currMethodSpOffset.isEmpty()) {
            currMethodSpOffset.set(0, currMethodSpOffset.peekFirst() + 2);
        }
    }

//    public Integer getCurrMethodSpOffset() {
//        return currMethodSpOffset.peekFirst();
//    }

    public Integer getCurrMethodSpOffset() {
//        if (currMethodSpOffset.isEmpty()) return 0;
        int totalSpOffset = 0;
        for (Integer spOffset : currMethodSpOffset) {
            totalSpOffset += spOffset;
        }
        return totalSpOffset;
//        return currMethodSpOffset.peekFirst();
    }

    public static void incHLByValue(DecacCompiler compiler, int value) {
        RegManager rM = compiler.getRegManager();
        int maxLine = (rM.isUsingAllRegs()) ? 10 : 4;

        if (value < maxLine) {
            for (int i = 0; i < value; i++) {
                compiler.addInstruction(new INC_REG(Register.HL));
            }
        } else {
            if (rM.isUsingAllRegs()) {
                compiler.addInstruction(new LOAD_REG(GPRegister.L, Register.A));
                compiler.addInstruction(new LOAD_INT(value, GPRegister.L));
                compiler.addInstruction(new ADD_A(GPRegister.L, Register.A));
                compiler.addInstruction(new LOAD_REG(Register.A, GPRegister.L));
                compiler.addInstruction(new LOAD_REG(GPRegister.H, Register.A));
                compiler.addInstruction(new LOAD_INT(0, GPRegister.H));
                compiler.addInstruction(new ADC_A(GPRegister.H, Register.A));
                compiler.addInstruction(new LOAD_REG(Register.A, GPRegister.H));
            } else {
                GPRegister gpReg = rM.getFreeReg();
                compiler.addInstruction(new LOAD_INT(value, gpReg));
                compiler.addInstruction(new ADD_HL(gpReg, Register.HL));
                rM.freeReg(gpReg);
            }
        }
    }

    public static void incReg16ByValue(DecacCompiler compiler, GPRegister gpReg, int value) {
        int maxLine = 8;

        if (value < maxLine) {
            for (int i = 0; i < value; i++) {
                compiler.addInstruction(new INC_REG(gpReg));
            }
        } else {
            compiler.addInstruction(new LOAD_REG(gpReg.getHighReg(), Register.HL.getHighReg()));
            compiler.addInstruction(new LOAD_REG(gpReg.getLowReg(), Register.HL.getLowReg()));
            compiler.addInstruction(new LOAD_INT(value, gpReg));
            compiler.addInstruction(new ADD_HL(gpReg, Register.HL));
            compiler.addInstruction(new LOAD_REG(Register.HL.getHighReg(), gpReg.getHighReg()));
            compiler.addInstruction(new LOAD_REG(Register.HL.getLowReg(), gpReg.getLowReg()));
        }
    }

    public void loadIdentAddrIntoHL(DecacCompiler compiler, AbstractIdentifier ident) {
        VTableManager vTM = compiler.getVTableManager();
        GameBoyManager gbM = compiler.getGameBoyManager();

        String identName = ident.getName().getName();

        Integer varAddr;
        if (!vTM.isInMethod()) {
            varAddr = getGlobalVarAddr(identName);
            if (varAddr != null) {
                compiler.addInstruction(new LOAD_INT(varAddr, Register.HL));
            }
        } else {
            if (identName.equals(currDeclaringIdentName)) {
                varAddr = null;
            } else {
                varAddr = getCurrMethodVarOffset(vTM, identName);
                if (varAddr != null) {
                    varAddr = (-varAddr * 2 - 1) + getCurrMethodSpOffset();
                    compiler.addInstruction(new LOAD_SP(Register.SP, Register.HL, varAddr));
                }
            }
        }

        if (varAddr != null) return;

        Integer paramOffset = vTM.getCurrParamOffsetOfMethod(identName);
        Integer spOffset = gbM.getCurrMethodSpOffset();
        if (spOffset == null) spOffset = 0;
        if (paramOffset != null) { // It's a Method Param
            compiler.addInstruction(new LOAD_SP(Register.SP, Register.HL,
                    3 + spOffset + (-paramOffset - 2) * 2));
        } else { // It's a Class Field
            compiler.addInstruction(new LOAD_SP(Register.SP, Register.HL, 3 + spOffset));
            compiler.addInstruction(new LOAD_VAL(Register.HL, Register.A));
            compiler.addInstruction(new DEC_REG(Register.HL));
            compiler.addInstruction(new LOAD_VAL(Register.HL, GPRegister.L));
            compiler.addInstruction(new LOAD_REG(Register.A, GPRegister.H));
            int fieldOffset = vTM.getCurrFieldOffset(identName);
            GameBoyManager.incHLByValue(compiler, fieldOffset * 2 + 1);
        }
    }
}
