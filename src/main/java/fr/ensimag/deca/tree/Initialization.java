package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.RegManager;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD_REG;
import org.apache.commons.lang.Validate;

/**
 * @author gl47
 * @date 01/01/2024
 */
public class Initialization extends AbstractInitialization {

    public AbstractExpr getExpression() {
        return expression;
    }

    private AbstractExpr expression;

    public void setExpression(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    public Initialization(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError { // regles 3.8 et 3.9
        this.setExpression(expression.verifyRValue(compiler, localEnv, currentClass, t));
        // Done
    }

    @Override
    protected void codeGenInit(DecacCompiler compiler) {
        expression.codeGenInst(compiler);
        // Done
    }

    @Override
    protected void codeGenInitGb(DecacCompiler compiler) {
        RegManager rM = compiler.getRegManager();

        expression.codeGenInstGb(compiler);
        GPRegister gpReg = rM.getLastReg();
        if (gpReg == Register.HL) {
            gpReg = rM.getFreeReg();
            compiler.addInstruction(new LOAD_REG(Register.HL.getHighReg(), gpReg.getHighReg()));
            compiler.addInstruction(new LOAD_REG(Register.HL.getLowReg(), gpReg.getLowReg()));
        }
        rM.freeReg(gpReg);

    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(" = ");
        expression.decompile(s);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expression.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, true);
    }
}
