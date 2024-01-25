package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

public class StackManager {

    private final boolean methodStack;
    private boolean doingDeclarations;
    private int maxStackSize;
    private int stackSize;
    private int tmpCpt;
    private int varCpt;
    private int vTableCpt;

    public StackManager(boolean methodStack) {
        this.methodStack = methodStack;
        this.doingDeclarations = false;
        this.maxStackSize = 0;
        this.stackSize = 0;
        this.tmpCpt = 0;
        this.varCpt = 0;
        this.vTableCpt = 0;
    }

    public void doDeclarations() {
        doingDeclarations = true;
    }

    public void finishDoingDeclrations() {
        doingDeclarations = false;
        tmpCpt = 0;
    }

    public void updateMaxStackSize() {
        if (stackSize + tmpCpt > maxStackSize) {
            maxStackSize = stackSize + tmpCpt;
        }
    }

    public void incrStackSize() {
        stackSize++;
        updateMaxStackSize();
    }

    public void incrTmpVar() {
        tmpCpt++;
        updateMaxStackSize();
    }

    public int getTmpVar() {
        return tmpCpt;
    }

    public void decrTmpVar() {
        if (!doingDeclarations) tmpCpt--;
    }

    public void decrTmpVarGb() {
        tmpCpt--;
    }

    public void incrGbVarCpt() {
        incrStackSize();
        varCpt++;
    }

    public void incrVTableCpt() {
        incrStackSize();
        vTableCpt++;
    }

    public RegisterOffset getOffsetAddr() {
        Register register = (methodStack) ? Register.LB : Register.GB;
        return new RegisterOffset(stackSize + 1, register);
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public int getAddSp() {
        return varCpt + vTableCpt;
    }

}
