package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.RegManager;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.GPRegister;
import org.apache.commons.lang.Validate;

/**
 * Unary expression.
 *
 * @author gl47
 * @date 01/01/2024
 */
public abstract class AbstractUnaryExpr extends AbstractExpr {

    public void setOperand(AbstractExpr value) {
        operand = value;
    }

    public AbstractExpr getOperand() {
        return operand;
    }

    private AbstractExpr operand;

    public AbstractUnaryExpr(AbstractExpr operand) {
        Validate.notNull(operand);
        this.operand = operand;
    }

    protected abstract String getOperatorName();

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        String op = this.getOperatorName();
        Type operandType = this.operand.verifyExpr(compiler, localEnv, currentClass);
        Type type = compiler.environmentType.getTypeUnaryOp(op, operandType);
        if (type == null) {
            throw new ContextualError("Unary operation '" + op
                    + "' cannot have operand of type : '"
                    + operandType.getName() + "'.", this.getLocation());
        }
        setType(type);
        return type;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();

        getOperand().codeGenInst(compiler);
        GPRegister gpReg = rM.getLastRegOrImm(compiler);
        codeGenOpUnary(compiler, gpReg);
        rM.freeReg(gpReg);
        // Done
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();

        getOperand().codeGenInstGb(compiler);
        GPRegister gpReg = rM.getLastRegOrImm(compiler);
        codeGenOpUnaryGb(compiler, gpReg);
        rM.freeReg(gpReg);
    }

    protected abstract void codeGenOpUnary(DecacCompiler compiler, GPRegister gpReg);

    protected abstract void codeGenOpUnaryGb(DecacCompiler compiler, GPRegister gpReg);

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        s.print(getOperatorName());
        getOperand().decompile(s);
        s.print(")");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        operand.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        operand.prettyPrint(s, prefix, true);
    }

}
