package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;

/**
 * Absence of initialization (e.g. "int x;" as opposed to "int x =
 * 42;").
 *
 * @author gl47
 * @date 01/01/2024
 */
public class NoInitialization extends AbstractInitialization {

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t,
                                        EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // nothing
        // Done
    }

    protected void codeGenInit(DecacCompiler compiler) {
//        if (getVarTypeCode() == null) return;
//
//        RegManager rM = compiler.getRegManager();
//
//        if (getVarTypeCode() == AbstractDeclField.TypeCode.INT_OR_BOOL) {
//            rM.setLastImm(new ImmediateInteger(0));
//        } else if (getVarTypeCode() == AbstractDeclField.TypeCode.FLOAT) {
//            rM.setLastImm(new ImmediateFloat(0.f));
//        } else {
//            rM.setLastImm(new NullOperand());
//        }
//        // Done
    }

    @Override
    protected void codeGenInitGb(DecacCompiler compiler) {
//        if (getVarTypeCode() == null) return;
//
//        RegManager rM = compiler.getRegManager();
//
//        rM.setLastImm(new ImmediateInteger(0));
    }

    /**
     * Node contains no real information, nothing to check.
     */
    @Override
    protected void checkLocation() {
        // nothing
    }

    @Override
    public void decompile(IndentPrintStream s) {
        // nothing
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
