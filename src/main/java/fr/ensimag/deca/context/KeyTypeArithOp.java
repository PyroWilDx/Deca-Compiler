package fr.ensimag.deca.context;

public class KeyTypeArithOp {
    private final Type type1;
    private final Type type2;
    public KeyTypeArithOp(Type t1, Type t2) {
        this.type1 = t1;
        this.type2 = t2;
    }

    public Type getType1() {
        return type1;
    }

    public Type getType2() {
        return type2;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof KeyTypeArithOp)) return false;
        KeyTypeArithOp key = (KeyTypeArithOp) obj;
        return this.type1.equals(key.type1) && this.type2.equals(key.type2);
    }

    @Override
    public int hashCode() {
        return this.type1.getName().hashCode() + this.type2.getName().hashCode();
    }
}
