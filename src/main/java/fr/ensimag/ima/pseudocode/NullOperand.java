package fr.ensimag.ima.pseudocode;

import fr.ensimag.deca.codegen.GameBoyManager;

/**
 * The #null operand.
 *
 * @author Ensimag
 * @date 01/01/2024
 */
public class NullOperand extends DVal {

    @Override
    public String toString() {
        if (GameBoyManager.doCp) return GameBoyManager.getImmToken() + "0";
        else return GameBoyManager.getImmToken() + "null";
    }

}
