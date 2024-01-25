package fr.ensimag.ima.pseudocode;

/**
 * Operand representing a register indirection with offset, e.g. 42(R3).
 *
 * @author Ensimag
 * @date 01/01/2024
 */
public class RegisterOffset extends DAddr {
    
    @Override
    public int getOffset() {
        return offset;
    }

    public Register getRegister() {
        return register;
    }

    private final int offset;
    private final Register register;

    public RegisterOffset(int offset, Register register) {
        super();
        this.offset = offset;
        this.register = register;
    }

    @Override
    public String toString() {
//        if (GameBoy.doCp) {
//            String res = "ld hl, SP";
//            res += "\n\tadd hl, " + GameBoy.getImmToken() + offset;
//            return res;
//        }
        return offset + "(" + register + ")";
    }
}
