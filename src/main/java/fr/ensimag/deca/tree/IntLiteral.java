package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.RegManager;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateInteger;

import java.io.PrintStream;

/**
 * Integer literal
 *
 * @author gl47
 * @date 01/01/2024
 */
public class IntLiteral extends AbstractLiteral {

    private int value;

    @Override
    public IntLiteral asIntLiteral(String errorMessage, Location location) {
        return this;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean isZero() {
        return value == 0;
    }

    public boolean isPowerOf2() {
        return (value != 0) && ((value & (value - 1)) == 0);
    }

    public int getExponentOf2() {
        int n = value;
        int power = 0;
        while ((n & 1) == 0) {
            n >>= 1;
            power++;
        }
        return power;
    }

    public IntLiteral(int value) {
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        Type exprType = compiler.environmentType.INT;
        setType(exprType);
        return exprType;
        // Done
    }

    @Override
    String prettyPrintNode() {
        return "Int (" + getValue() + ")";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();

        rM.setLastImm(new ImmediateInteger(value));
        // Done
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();

        rM.setLastImm(new ImmediateInteger(value));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Integer.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

}
