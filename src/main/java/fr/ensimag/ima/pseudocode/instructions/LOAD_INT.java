package fr.ensimag.ima.pseudocode.instructions;

import fr.ensimag.ima.pseudocode.BinaryInstructionDValToReg;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;

public class LOAD_INT extends BinaryInstructionDValToReg {

    public LOAD_INT(DVal dVal, GPRegister r) {
        super(dVal, r);
    }

    public LOAD_INT(int i, GPRegister r) {
        this(new ImmediateInteger(i), r);
    }

    @Override
    public String getGameBoyAsm() {
        return "ld";
    }
}
