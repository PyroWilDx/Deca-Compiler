package fr.ensimag.ima.pseudocode.instructions;

import fr.ensimag.ima.pseudocode.BinaryInstructionDValToReg;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;

public class ADD_HL extends BinaryInstructionDValToReg {
    
    public ADD_HL(DVal op1, GPRegister op2) {
        super(op1, op2);
    }

    @Override
    public String getGameBoyAsm() {
        return "add";
    }
}
