package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 *
 * @author gl47
 * @date 01/01/2024
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        this.getOperand().verifyExpr(compiler, localEnv, currentClass);
        this.setType(compiler.environmentType.FLOAT);
        return compiler.environmentType.FLOAT;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        if (getOperand() instanceof IntLiteral) {
            IntLiteral op = (IntLiteral) getOperand();
            setOperand(new FloatLiteral((float) op.getValue()));
            getOperand().codeGenInst(compiler);
        } else {
            super.codeGenInst(compiler);
        }
    }

    @Override
    protected void codeGenOpUnary(DecacCompiler compiler, GPRegister gpReg) {
        compiler.addInstruction(new FLOAT(gpReg, gpReg));
        // Done
    }

    @Override
    protected void codeGenOpUnaryGb(DecacCompiler compiler, GPRegister gpReg) {
        // Non
    }

    @Override
    protected String getOperatorName() {
        return "/* convFloat */";
    }

}
