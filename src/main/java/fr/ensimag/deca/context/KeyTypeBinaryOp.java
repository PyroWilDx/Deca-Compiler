package fr.ensimag.deca.context;

public class KeyTypeBinaryOp {
    private final String op;
    private final Type type1;
    private final Type type2;
    public KeyTypeBinaryOp(String op, Type t1, Type t2) {
        this.op = op;
        this.type1 = t1;
        this.type2 = t2;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof KeyTypeBinaryOp)) return false;
        KeyTypeBinaryOp key = (KeyTypeBinaryOp) obj;
        return this.op.equals(key.op) && this.type1.equals(key.type1)
                && this.type2.equals(key.type2);
    }

    @Override
    public int hashCode() {
        return op.hashCode() + type1.getName().hashCode() + type2.getName().hashCode();
    }
}
