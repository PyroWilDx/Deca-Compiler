package fr.ensimag.deca.context;

public class KeyTypeUnaryOp {
    private final String op;
    private final Type type;
    public KeyTypeUnaryOp(String op, Type type) {
        this.op = op;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof KeyTypeUnaryOp)) return false;
        KeyTypeUnaryOp key = (KeyTypeUnaryOp) obj;
        return this.op.equals(key.op) && this.type.equals(key.type);
    }

    @Override
    public int hashCode() {
        return this.op.hashCode() + this.type.getName().hashCode();
    }
}
