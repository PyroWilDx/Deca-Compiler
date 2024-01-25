package fr.ensimag.deca.tree;


import fr.ensimag.deca.codegen.CondManager;
import fr.ensimag.ima.pseudocode.Label;

/**
 * @author gl47
 * @date 01/01/2024
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Label setOperandCondVals(CondManager cM) {
        Label fastEndLabel = null;

        if (isInTrue) {
            getLeftOperand().isInTrue = false;
            fastEndLabel = cM.getUniqueLabel();
            getLeftOperand().branchLabel = fastEndLabel;
            getRightOperand().isInTrue = true;
            getRightOperand().branchLabel = branchLabel;
        } else {
            getLeftOperand().isInTrue = false;
            getLeftOperand().branchLabel = branchLabel;
            getRightOperand().isInTrue = false;
            getRightOperand().branchLabel = branchLabel;
        }

        return fastEndLabel;
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }


}
