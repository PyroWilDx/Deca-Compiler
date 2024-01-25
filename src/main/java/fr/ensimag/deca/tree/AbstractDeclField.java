package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.VTable;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.SymbolTable;

public abstract class AbstractDeclField extends Tree {

    public enum TypeCode {
        INT_OR_BOOL,
        FLOAT,
        OBJECT
    }

    public abstract void codeGenVTable(DecacCompiler compiler, VTable vTable, int fieldOffset);

    public abstract void codeGenVTableGb(DecacCompiler compiler, VTable vTable, int fieldOffset);

    public abstract void codeGenSetFieldTo0(DecacCompiler compiler, boolean doLoad);

    public abstract void codeGenSetFieldTo0Gb(DecacCompiler compiler);

    public abstract void codeGenDeclField(DecacCompiler compiler);

    public abstract void codeGenDeclFieldGb(DecacCompiler compiler);

    public abstract TypeCode getInitTypeCode();

    public abstract EnvironmentExp verifyDeclFieldMembers(DecacCompiler compiler,
                                                          SymbolTable.Symbol superClass,
                                                          SymbolTable.Symbol classDef,
                                                          int index) throws ContextualError;

    public abstract void verifyDeclFieldBody(DecacCompiler compiler,
                                             EnvironmentExp localEnv,
                                             ClassDefinition classDef) throws ContextualError;

    public abstract SymbolTable.Symbol getName();
}
