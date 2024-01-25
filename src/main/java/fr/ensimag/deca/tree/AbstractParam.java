package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.SymbolTable;

public abstract class AbstractParam extends Tree {
    public abstract SymbolTable.Symbol getName();

    public abstract Type verifyDeclParamMembers(DecacCompiler compiler) throws ContextualError;
    // Done

    public abstract EnvironmentExp verifyDeclParamBody(DecacCompiler compiler) throws ContextualError;
    // Done

    
}
