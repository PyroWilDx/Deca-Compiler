package fr.ensimag.ima.pseudocode;

import fr.ensimag.deca.codegen.GameBoyManager;

import java.util.HashMap;

/**
 * General Purpose Register operand (R0, R1, ... R15).
 *
 * @author Ensimag
 * @date 01/01/2024
 */
public class GPRegister extends Register {
    public static final String[] gameBoyRegs = {"hl", "a", "bc", "de"};

    /**
     * @return the number of the register, e.g. 12 for R12.
     */
    public int getNumber() {
        return number;
    }

    private final int number;

    public GPRegister(String name, int number) {
        super((GameBoyManager.doCp && number < GameBoyManager.nRegs) ? gameBoyRegs[number] : name);
        this.number = number;
    }

    public static final GPRegister B = new GPRegister("b", 42);
    public static final GPRegister C = new GPRegister("c", 42);
    public static final GPRegister D = new GPRegister("d", 42);
    public static final GPRegister E = new GPRegister("e", 42);
    public static final GPRegister H = new GPRegister("h", 42);
    public static final GPRegister L = new GPRegister("l", 42);
    private static final HashMap<String, GPRegister> reg8BitMap = initReg8BitMap();

    private static HashMap<String, GPRegister> initReg8BitMap() {
        HashMap<String, GPRegister> map = new HashMap<>();
        map.put("b", B);
        map.put("c", C);
        map.put("d", D);
        map.put("e", E);
        map.put("h", H);
        map.put("l", L);
        return map;
    }

    public GPRegister getHighReg() {
        String hStr = toStringHigh();
        if (hStr == null) return this;
        return reg8BitMap.get(hStr);
    }

    public GPRegister getLowReg() {
        String lStr = toStringLow();
        if (lStr == null) return this;
        return reg8BitMap.get(lStr);
    }

    public GPRegister getHighRegOfLow() {
        if (name.equals("c")) {
            return reg8BitMap.get("b");
        } else if (name.equals("e")) {
            return reg8BitMap.get("d");
        }
        return null;
    }
}
