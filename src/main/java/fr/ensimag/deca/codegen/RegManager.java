package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.util.Arrays;
import java.util.LinkedList;

public class RegManager {
    public static final int MAX_REG = 16;

    private static int startReg;
    public final int nRegs;
    private final LinkedList<GPRegister> freeRegs;
    private final LinkedList<boolean[]> usedRegsStack;
    private DVal lastImm;

    public RegManager(int nRegs) {
//        startReg = (GameBoyManager.doCp) ? 1 : 2;
        startReg = 2;
        this.nRegs = nRegs;
        this.freeRegs = new LinkedList<>();
        for (int i = startReg; i < nRegs; i++) {
            freeRegs.addLast(Register.getR(i));
        }
        this.usedRegsStack = new LinkedList<>();
        lastImm = null;
    }

    public GPRegister getFreeReg() {
        if (!freeRegs.isEmpty()) {
            GPRegister freeReg = freeRegs.removeFirst();
            if (!usedRegsStack.isEmpty() && freeReg.getNumber() > (startReg - 1)) {
                boolean[] usedRegs = usedRegsStack.getFirst();
                usedRegs[freeReg.getNumber()] = true;
            }
            return freeReg;
        }
        return null;
    }

    public GPRegister getLastReg() {
        return freeRegs.removeFirst();
    }

    public void freeReg(GPRegister gpReg) {
        if (gpReg != null && gpReg.getNumber() > (startReg - 1)) {
            freeRegs.addFirst(gpReg);
        }
    }

    public void freeRegForce(GPRegister gpReg) {
        if (gpReg != null) {
            freeRegs.addFirst(gpReg);
        }
    }

    public void freeAllRegs() {
        freeRegs.clear();
        for (int i = startReg; i < nRegs; i++) {
            freeRegs.addLast(Register.getR(i));
        }
    }

    public boolean isUsingAllRegs() {
        return freeRegs.isEmpty();
    }

    public void setLastImm(DVal dAddr) {
        lastImm = dAddr;
    }

    public DVal getLastImm() {
        DVal dVal = lastImm;
        lastImm = null;
        return dVal;
    }

    public GPRegister getLastRegOrImm(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();

        DVal lastImm = getLastImm();
        GPRegister gpReg;
        if (lastImm == null) {
            gpReg = rM.getLastReg();
        } else {
            gpReg = rM.getFreeReg();
            compiler.addInstruction(new LOAD(lastImm, gpReg));
        }
        return gpReg;
    }

    public void saveUsedRegs() {
        boolean[] usedRegs = new boolean[nRegs];
        Arrays.fill(usedRegs, false);
        usedRegsStack.addFirst(usedRegs);
    }

    public boolean[] popUsedRegs() {
        return usedRegsStack.removeFirst();
    }

    public static void addSaveRegsInsts(DecacCompiler compiler, int index,
                                        boolean[] usedRegs) {
        ErrorManager eM = compiler.getErrorManager();
        StackManager sM = compiler.getStackManager();

        LinkedList<AbstractLine> startLines = new LinkedList<>();
        int usedCount = 0;
        for (int i = startReg; i < usedRegs.length; i++) {
            if (usedRegs[i]) {
                startLines.addLast(new Line(new PUSH(Register.getR(i))));
                usedCount++;
            }
        }
        int maxStackSize = sM.getMaxStackSize() + usedCount;
        if (maxStackSize > 0) {
//            if (sM.getAddSp() + usedCount > 0) {
            if (sM.getAddSp() > 0) {
//                startLines.addFirst(new Line(new ADDSP(sM.getAddSp() + usedCount)));
                startLines.addFirst(new Line(new ADDSP(sM.getAddSp())));
            }
            if (compiler.getCompilerOptions().doCheck()) {
                startLines.addFirst(new Line(new BOV(eM.getStackOverflowLabel())));
                startLines.addFirst(new Line(new TSTO(maxStackSize)));
            }
        }
        compiler.addAllLine(index, startLines);
    }

    public static void addRestoreRegsInsts(DecacCompiler compiler, boolean[] usedRegs) {
        for (int i = usedRegs.length - 1; i > (startReg - 1); i--) {
            if (usedRegs[i]) {
                compiler.addInstruction(new POP(Register.getR(i)));
            }
        }
    }

    public static void addSaveRegsInstsGb(DecacCompiler compiler, int index,
                                          boolean[] usedRegs) {
        LinkedList<AbstractLine> startLines = new LinkedList<>();
        int usedCount = 0;
        for (int i = startReg; i < usedRegs.length; i++) {
            if (usedRegs[i]) {
                startLines.addLast(new Line(new PUSH(Register.getR(i))));
                usedCount++;
            }
        }
        if (usedCount > 0) {
            startLines.addLast(new Line(new ADDSP(usedCount * 2)));
        }
        compiler.addAllLine(index, startLines);
    }

    public static void addRestoreRegsInstsGb(DecacCompiler compiler, boolean[] usedRegs) {
        int usedCount = 0;
        for (int i = usedRegs.length - 1; i > (startReg - 1); i--) {
            if (usedRegs[i]) {
                usedCount++;
            }
        }
        if (usedCount > 0) {
            compiler.addInstruction(new SUBSP(usedCount * 2));
        }
        for (int i = usedRegs.length - 1; i > (startReg - 1); i--) {
            if (usedRegs[i]) {
                compiler.addInstruction(new POP(Register.getR(i)));
            }
        }
    }

}
