package fr.ensimag.ima.pseudocode.instructions;

import fr.ensimag.ima.pseudocode.BinaryInstructionDValToReg;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;

public class ADC_A extends BinaryInstructionDValToReg {

    public ADC_A(DVal op1, GPRegister op2) {
        super(op1, op2);
    }

    @Override
    public String getGameBoyAsm() {
        return "adc";
    }
}
