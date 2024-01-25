package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;
import java.util.Iterator;

import org.apache.commons.lang.Validate;

/**
 * Print statement (print, println, ...).
 *
 * @author gl47
 * @date 01/01/2024
 */
public abstract class AbstractPrint extends AbstractInst {

    private final boolean printHex;
    private ListExpr arguments = new ListExpr();
    
    abstract String getSuffix();

    public AbstractPrint(boolean printHex, ListExpr arguments) {
        Validate.notNull(arguments);
        this.arguments = arguments;
        this.printHex = printHex;
    }

    public ListExpr getArguments() {
        return arguments;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType) throws ContextualError {
        this.arguments.verifyListExprPrint(compiler, localEnv, currentClass);
        // Done
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        for (AbstractExpr a : getArguments().getList()) {
            a.setPrintHex(getPrintHex());
            a.codeGenPrint(compiler);
        }
        // Done
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) throws ContextualError {
        // Non
    }

    private boolean getPrintHex() {
        return printHex;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("print" + getSuffix());
        if (printHex) s.print("x");
        s.print("(");
        Iterator<AbstractExpr> iterator = arguments.iterator();
        if (iterator.hasNext()) {
            AbstractExpr arg = iterator.next();
            arg.decompile(s);
        }
        while (iterator.hasNext()) {
            AbstractExpr arg = iterator.next();
            s.print(", ");
            arg.decompile(s);
        }
        s.print(");");
        // Done
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        arguments.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        arguments.prettyPrint(s, prefix, true);
    }

}
