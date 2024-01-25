package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

/**
 * String literal
 *
 * @author gl47
 * @date 01/01/2024
 */
public class StringLiteral extends AbstractStringLiteral {

    private final String value;

    @Override
    public StringLiteral asStringLiteral(String message, Location location) throws ContextualError {
        return this;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean isZero() {
        return value.isEmpty();
    }

    public StringLiteral(String value) {
        Validate.notNull(value);
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        Type exprType = compiler.environmentType.STRING;
        setType(exprType);
        return exprType;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        String printValue = value.substring(1, value.length() - 1);
        printValue = printValue.replace("\\\"", "\"");
        compiler.addInstruction(new WSTR(printValue));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(value);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    String prettyPrintNode() {
        return "StringLiteral (" + getValue() + ")";
    }

}
