package fr.ensimag.ima.pseudocode.instructions;

import fr.ensimag.ima.pseudocode.*;

public class LOAD_GEN extends BinaryInstructionDValToReg {

    public LOAD_GEN(DVal op1, GPRegister op2) {
        super(op1, op2);
    }

    @Override
    public String getGameBoyAsm() {
        return "ld";
    }
}