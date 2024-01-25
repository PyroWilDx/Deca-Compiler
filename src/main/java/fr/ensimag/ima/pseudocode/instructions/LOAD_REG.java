package fr.ensimag.ima.pseudocode.instructions;

import fr.ensimag.ima.pseudocode.BinaryInstructionDValToReg;
import fr.ensimag.ima.pseudocode.GPRegister;

public class LOAD_REG extends BinaryInstructionDValToReg {

    public LOAD_REG(GPRegister op1, GPRegister op2) {
        super(op1, op2);
    }

    @Override
    public String getGameBoyAsm() {
        return "ld";
    }
}
