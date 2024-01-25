package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.CondManager;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;

import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.instructions.BRA;
import org.apache.commons.lang.Validate;

/**
 * @author gl47
 * @date 01/01/2024
 */
public class While extends AbstractInst {
    private final AbstractExpr condition;
    private final ListInst body;

    public AbstractExpr getCondition() {
        return condition;
    }

    public ListInst getBody() {
        return body;
    }

    public While(AbstractExpr condition, ListInst body) {
        Validate.notNull(condition);
        Validate.notNull(body);
        this.condition = condition;
        this.body = body;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        CondManager cM = compiler.getCondManager();
        
        Label startBodyLabel = cM.getUniqueLabel();
        Label condLabel = cM.getUniqueLabel();

        compiler.addInstruction(new BRA(condLabel));

        compiler.addLabel(startBodyLabel);
        body.codeGenListInst(compiler);

        compiler.addLabel(condLabel);
        condition.branchLabel = startBodyLabel;
        cM.doCond();
        condition.codeGenInst(compiler);
        cM.exitCond();
        // Done
    }

    @Override
    protected void codeGenInstGb(DecacCompiler compiler) throws ContextualError {
        CondManager cM = compiler.getCondManager();

        Label startBodyLabel = cM.getUniqueLabel();
        Label condLabel = cM.getUniqueLabel();

        compiler.addInstruction(new BRA(condLabel));

        compiler.addLabel(startBodyLabel);
        body.codeGenListInstGb(compiler);

        compiler.addLabel(condLabel);
        condition.branchLabel = startBodyLabel;
        cM.doCond();
        condition.codeGenInstGb(compiler);
        cM.exitCond();
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
                              ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        this.condition.verifyCondition(compiler, localEnv, currentClass);
        this.body.verifyListInst(compiler, localEnv, currentClass, returnType);
        // Done
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("while (");
        getCondition().decompile(s);
        s.println(") {");
        s.indent();
        getBody().decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        condition.iter(f);
        body.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }

}
