package fr.ensimag.ima.pseudocode.instructions;

import fr.ensimag.ima.pseudocode.Operand;
import fr.ensimag.ima.pseudocode.UnaryInstruction;

import java.io.PrintStream;

public class INC_SP extends UnaryInstruction {

    public INC_SP(Operand operand) {
        super(operand);
    }

    @Override
    public void displayOperandsGameBoy(PrintStream s) {
        s.print(" ");
        s.print(getOperand());
    }

    @Override
    public String getGameBoyAsm() {
        return "inc";
    }
}
