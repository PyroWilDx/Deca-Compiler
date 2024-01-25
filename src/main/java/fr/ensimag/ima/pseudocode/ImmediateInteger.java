package fr.ensimag.ima.pseudocode;

import fr.ensimag.deca.codegen.GameBoyManager;

/**
 * Immediate operand representing an integer.
 * 
 * @author Ensimag
 * @date 01/01/2024
 */
public class ImmediateInteger extends DVal {
    private int value;

    public ImmediateInteger(int value) {
        super();
        this.value = value;
    }

    public void oppValue() {
        value = -value;
    }

    public void addValue(int value) {
        this.value += value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return GameBoyManager.getImmToken() + value;
    }
}
