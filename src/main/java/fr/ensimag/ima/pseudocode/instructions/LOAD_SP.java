package fr.ensimag.ima.pseudocode.instructions;

import fr.ensimag.ima.pseudocode.*;

import java.io.PrintStream;

public class LOAD_SP extends BinaryInstructionDValToReg {

    private final int spOffset;

    public LOAD_SP(DVal op1, GPRegister op2, int spOffset) {
        super(op1, op2);

        this.spOffset = spOffset;

        assert (op1 == Register.SP);
        assert (op2 == Register.HL);
    }

    @Override
    public void displayOperandsGameBoy(PrintStream s) {
        s.print(" ");
        s.print(getOperand2());
        s.print(", ");
        s.print(getOperand1());
        if (spOffset >= 0) {
            s.print("+");
            s.print(spOffset);
        } else {
            s.print("-");
            s.print(-spOffset);
        }
    }

    @Override
    public String getGameBoyAsm() {
        return "ld";
    }
}
