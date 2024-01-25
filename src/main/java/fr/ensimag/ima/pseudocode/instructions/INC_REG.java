package fr.ensimag.ima.pseudocode.instructions;

import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.UnaryInstruction;

public class INC_REG extends UnaryInstruction {
    public INC_REG(GPRegister op) {
        super(op);
    }

    @Override
    public String getGameBoyAsm() {
        return "inc";
    }
}
