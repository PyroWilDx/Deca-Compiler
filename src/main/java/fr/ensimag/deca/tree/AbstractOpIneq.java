package fr.ensimag.deca.tree;


/**
 * @author gl47
 * @date 01/01/2024
 */
public abstract class AbstractOpIneq extends AbstractOpCmp {

    public AbstractOpIneq(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

}
