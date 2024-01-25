package fr.ensimag.ima.pseudocode.instructions;

import fr.ensimag.ima.pseudocode.BinaryInstruction;
import fr.ensimag.ima.pseudocode.GPRegister;

import java.io.PrintStream;

public class STORE_REG extends BinaryInstruction {
    public STORE_REG(GPRegister op1, GPRegister op2) {
        super(op1, op2);
    }

    @Override
    public void displayOperandsGameBoy(PrintStream s) {
        s.print(" [");
        s.print(getOperand2());
        s.print("], ");
        s.print(getOperand1());
    }

    @Override
    public String getGameBoyAsm() {
        return "ld";
    }
}
