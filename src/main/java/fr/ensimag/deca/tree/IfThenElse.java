package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.CondManager;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import org.apache.commons.lang.Validate;

/**
 * Full if/else if/else statement.
 *
 * @author gl47
 * @date 01/01/2024
 */
public class IfThenElse extends AbstractInst {

    private final AbstractExpr condition;
    private final ListInst thenBranch;
    private final ListInst elseBranch;

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
                              ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        this.condition.verifyCondition(compiler, localEnv, currentClass);
        this.thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        this.elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        // Done
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        CondManager cM = compiler.getCondManager();

        Label startElseLabel = cM.getUniqueLabel();
        Label endIfThenElseLabel = cM.getUniqueLabel();

        condition.isInTrue = false;
        condition.branchLabel = startElseLabel;
        cM.doCond();
        condition.codeGenInst(compiler);
        cM.exitCond();

        thenBranch.codeGenListInst(compiler);
        compiler.addInstruction(new BRA(endIfThenElseLabel));

        compiler.addLabel(startElseLabel);
        elseBranch.codeGenListInst(compiler);

        compiler.addLabel(endIfThenElseLabel);
        // Done
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) throws ContextualError {
        CondManager cM = compiler.getCondManager();

        Label startElseLabel = cM.getUniqueLabel();
        Label endIfThenElseLabel = cM.getUniqueLabel();

        condition.isInTrue = false;
        condition.branchLabel = startElseLabel;
        cM.doCond();
        condition.codeGenInstGb(compiler);
        cM.exitCond();

        thenBranch.codeGenListInstGb(compiler);
        compiler.addInstruction(new BRA(endIfThenElseLabel));

        compiler.addLabel(startElseLabel);
        elseBranch.codeGenListInstGb(compiler);

        compiler.addLabel(endIfThenElseLabel);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("if (");
        condition.decompile(s);
        s.println(") {");
        s.indent();
        thenBranch.decompile(s);
        s.unindent();
        s.println("} else {");
        s.indent();
        elseBranch.decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }
}
