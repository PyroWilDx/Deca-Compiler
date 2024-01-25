package fr.ensimag.ima.pseudocode.instructions;

import fr.ensimag.ima.pseudocode.*;

/**
 * @author Ensimag
 * @date 01/01/2024
 */
public class BSR extends UnaryInstruction {

    public BSR(DVal operand) {
        super(operand);
    }
    
    public BSR(Label target) {
        super(new LabelOperand(target));
    }

    @Override
    public String getGameBoyAsm() {
        return "call";
    }
}
