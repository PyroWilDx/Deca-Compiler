package fr.ensimag.ima.pseudocode.instructions;

import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.UnaryInstructionImmInt;

import java.io.PrintStream;

/**
 *
 * @author Ensimag
 * @date 01/01/2024
 */
public class SUBSP extends UnaryInstructionImmInt {

    public SUBSP(ImmediateInteger operand) {
        super(operand);
    }

    public SUBSP(int i) {
        this(new ImmediateInteger(i));
    }

    @Override
    public void displayOperandsGameBoy(PrintStream s) {
        s.print(" SP, -");
        s.print(getOperand());
    }

    @Override
    public String getGameBoyAsm() {
        return "add";
    }
}
