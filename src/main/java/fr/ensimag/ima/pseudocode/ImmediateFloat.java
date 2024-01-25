package fr.ensimag.ima.pseudocode;

import fr.ensimag.deca.codegen.GameBoyManager;

/**
 * Immediate operand containing a float value.
 * 
 * @author Ensimag
 * @date 01/01/2024
 */
public class ImmediateFloat extends DVal {
    private float value;

    public ImmediateFloat(float value) {
        super();
        this.value = value;
    }

    public void addValue(int value) {
        this.value += value;
    }

    public int getIntValue() {
        return (int) value;
    }

    @Override
    public String toString() {
        if (GameBoyManager.doCp) return GameBoyManager.getImmToken() + ((int) value);
        return GameBoyManager.getImmToken() + Float.toHexString(value);
    }
}
