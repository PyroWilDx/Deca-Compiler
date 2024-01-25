package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.util.Iterator;

/**
 * List of expressions (eg list of parameters).
 *
 * @author gl47
 * @date 01/01/2024
 */
public class ListExpr extends TreeList<AbstractExpr> {
    public void verifyListExprPrint(DecacCompiler compiler, EnvironmentExp localEnv,
                                    ClassDefinition currentClass) throws ContextualError {
        for (AbstractExpr expr : this.getList()) {
            expr.verifyExprPrint(compiler, localEnv, currentClass);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        Iterator<AbstractExpr> iterator = this.getList().iterator();
        while (iterator.hasNext()) {
            iterator.next().decompile(s);
            if (iterator.hasNext()) {
                s.print(", ");
            }
        }
    }
}
