package fr.ensimag.deca.tree;


import fr.ensimag.deca.codegen.CondManager;
import fr.ensimag.ima.pseudocode.Label;

/**
 * @author gl47
 * @date 01/01/2024
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Label setOperandCondVals(CondManager cM) {
        Label fastEndLabel = null;

        if (isInTrue) {
            getLeftOperand().isInTrue = true;
            getLeftOperand().branchLabel = branchLabel;
            getRightOperand().isInTrue = true;
            getRightOperand().branchLabel = branchLabel;
        } else {
            getLeftOperand().isInTrue = true;
            fastEndLabel = cM.getUniqueLabel();
            getLeftOperand().branchLabel = fastEndLabel;
            getRightOperand().isInTrue = false;
            getRightOperand().branchLabel = branchLabel;
        }

        return fastEndLabel;
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }


}
