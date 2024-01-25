package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable;

/**
 * @author Ensimag
 * @date 01/01/2024
 */
public class NullType extends Type {

    public NullType(SymbolTable.Symbol name) {
        super(name);
    }

    @Override
    public boolean sameType(Type otherType) {
        return (otherType instanceof NullType);
        // Done
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public boolean isClassOrNull() {
        return true;
    }


}
