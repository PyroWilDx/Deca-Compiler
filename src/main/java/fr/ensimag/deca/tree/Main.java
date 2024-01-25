package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * @author gl47
 * @date 01/01/2024
 */
public class Main extends AbstractMain {
    private static final Logger LOG = Logger.getLogger(Main.class);

    private final ListDeclVar declVariables;
    private final ListInst insts;

    public Main(ListDeclVar declVariables,
                ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }
    
    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify Main: start");
        EnvironmentExp env = new EnvironmentExp(null);
        EnvironmentExp envExpSup = new EnvironmentExp(null);
        EnvironmentExp envReturn = declVariables.verifyListDeclVariable(compiler, envExpSup, env, compiler.environmentType.OBJECT.getDefinition());
        insts.verifyListInst(compiler, envReturn, compiler.environmentType.OBJECT.getDefinition(),
                compiler.environmentType.VOID);

        LOG.debug("verify Main: end");
        // Done
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        compiler.addComment("Beginning of Main Declarations:");
        declVariables.codeGenListDeclVar(compiler);
        compiler.addComment("Beginning of Main Instructions:");
        insts.codeGenListInst(compiler);
        // Done
    }

    @Override
    protected void codeGenMainGb(DecacCompiler compiler) throws ContextualError {
        compiler.addComment("Beginning of Main Declarations:");
        declVariables.codeGenListDeclVarGb(compiler);
        compiler.addComment("Beginning of Main Instructions:");
        insts.codeGenListInstGb(compiler);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }
}
