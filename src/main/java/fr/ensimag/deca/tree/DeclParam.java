package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;

import java.io.PrintStream;

public class DeclParam extends AbstractParam {
    private final AbstractIdentifier type;
    private final AbstractIdentifier name;

    public DeclParam(AbstractIdentifier type, AbstractIdentifier name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        name.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s,prefix,false);
        name.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iterChildren(f);
        name.iterChildren(f);
    }

    @Override
    public SymbolTable.Symbol getName() {
        return this.name.getName();
    }

    @Override
    public Type verifyDeclParamMembers(DecacCompiler compiler) throws ContextualError {
        Type type = this.type.verifyType(compiler);
        if (type.equals(compiler.environmentType.VOID)) {
            throw new ContextualError("Parameter type cannot be void.", getLocation());
        }
        return type;
        // Done
    }

    @Override
    public EnvironmentExp verifyDeclParamBody(DecacCompiler compiler) throws ContextualError {
        Type t = this.type.verifyType(compiler);
        EnvironmentExp env = new EnvironmentExp(null);
        ExpDefinition def = new ParamDefinition(t, getLocation());
        try {
            env.declare(this.name.getName(), def);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new DecacInternalError("Symbol cannot have been declared twice.");
        }
        return env;
        // Done
    }
}
