package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.Label;

public class CondManager {

    private long idCpt;
    private int doingCondCount;
    private boolean doingOpCmp;

    public CondManager() {
        this.idCpt = 0;
        this.doingCondCount = 0;
        this.doingOpCmp = false;
    }

    public Label getUniqueLabel() {
        Label uniqueLabel = new Label("L" + idCpt);
        idCpt++;
        return uniqueLabel;
    }

    public long getUniqueId() {
        return idCpt++;
    }

    public void doCond() {
        doingCondCount++;
    }

    public void exitCond() {
        doingCondCount--;
    }

    public boolean isDoingCond() {
        return doingCondCount > 0;
    }

    public void doOpCmp() {
        doingOpCmp = true;
    }

    public void exitOpCmp() {
        doingOpCmp = false;
    }

    public boolean isNotDoingOpCmp() {
        return !doingOpCmp;
    }

}
