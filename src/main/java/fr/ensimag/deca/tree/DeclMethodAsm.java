package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.*;

import java.io.PrintStream;

public class DeclMethodAsm extends AbstractDeclMethod {
    private final String code;
    private final AbstractIdentifier type;
    private final AbstractIdentifier name;
    private final ListDeclParam params;

    public DeclMethodAsm(String code, AbstractIdentifier type, AbstractIdentifier name,
                         ListDeclParam params) {
        this.code = code.replace("\\\"", "\"");
        this.type = type;
        this.name = name;
        this.params = params;
    }

    @Override
    public boolean isOverride() {
        return this.override;
    }

    @Override
    public int getMethodIndex() {
        return methodIndex;
    }

    public void codeGenDeclMethod(DecacCompiler compiler) {
        compiler.addLabel(mStartLabel);
        compiler.add(new LineAsm(code.substring(1, code.length() - 1)));
    }

    @Override
    public void codeGenDeclMethodGb(DecacCompiler compiler) {
        compiler.addLabel(mStartLabel);
        compiler.add(new LineAsm(code.substring(1, code.length() - 1)));
    }

    public SymbolTable.Symbol getName() {
        return name.getName();
    }

    @Override
    public AbstractIdentifier getTypeIdent() {
        return this.type;
    }

    @Override
    public AbstractIdentifier getNameIdent() {
        return name;
    }

    @Override
    public ListDeclParam getParams() {
        return params;
    }


    public void verifyDeclMethodBody(DecacCompiler compiler,
                                     EnvironmentExp localEnv,
                                     ClassDefinition currentClass) throws ContextualError{
        this.type.verifyType(compiler);
        this.params.verifyListDeclParamBody(compiler);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        this.type.decompile(s);
        s.print(" ");
        this.name.decompile(s);
        s.print("(");
        this.params.decompile(s);
        s.println(")");
        s.println("asm(" + code + ");");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        name.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, false);
        System.out.println(prefix + "AsmCode (" + code + ")");

    }

}

