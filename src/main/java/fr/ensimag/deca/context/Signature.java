package fr.ensimag.deca.context;

import java.util.ArrayList;
import java.util.List;

/**
 * Signature of a method (i.e. list of arguments)
 *
 * @author gl47
 * @date 01/01/2024
 */
public class Signature {
    List<Type> args = new ArrayList<Type>();

    public void add(Type t) {
        args.add(t);
    }
    
    public Type paramNumber(int n) {
        return args.get(n);
    }
    
    public int size() {
        return args.size();
    }
    public Signature() {
        super();
    }
    public Signature(List<Type> args) {
        this();
        this.args = args;
    }
    public Signature copyWithoutFirst() {
        if (args.size() == 1) return new Signature();
        List<Type> newArgs = args.subList(1, args.size());
        return new Signature(newArgs);
    }
    public Type getFirst() {
        if (args.isEmpty()) return null;
        return args.get(0);
    }
    public boolean isEmpty() {
        return args.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Signature)) return false;
        Signature sig2 = (Signature) obj;
        return this.args.equals(sig2.args);
    }

    @Override
    public int hashCode() {
        return this.args.hashCode();
    }

    @Override
    public String toString() {
        return args.toString();
    }
}
