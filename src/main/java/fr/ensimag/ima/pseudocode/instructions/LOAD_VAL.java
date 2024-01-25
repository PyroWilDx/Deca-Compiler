package fr.ensimag.ima.pseudocode.instructions;

import fr.ensimag.ima.pseudocode.BinaryInstructionDValToReg;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;

import java.io.PrintStream;

public class LOAD_VAL extends BinaryInstructionDValToReg {
    public LOAD_VAL(DVal op1, GPRegister op2) {
        super(op1, op2);
    }

    @Override
    public void displayOperandsGameBoy(PrintStream s) {
        s.print(" ");
        s.print(getOperand2());
        s.print(", [");
        s.print(getOperand1());
        s.print("]");
    }

    @Override
    public String getGameBoyAsm() {
        return "ld";
    }
}
