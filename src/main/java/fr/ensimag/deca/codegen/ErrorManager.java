package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

import java.util.HashMap;

public class ErrorManager {

    private static final Label divBy0Label = new Label("divisionBy0Error");
    private static final Label floatOverflowLabel = new Label("floatOverflowError");
    private static final Label ioErrLabel = new Label("ioError");
    private static final Label stackOverflowLabel = new Label("stackOverflowError");
    private static final Label nullPointerLabel = new Label("nullPointerError");
    private static final Label heapOverflowLabel = new Label("heapOverflowError");

    private static final HashMap<Label, String> errMsgs = initErrMsgs();

    private final OrderedHashMap<Label, Boolean> errMap;

    private static HashMap<Label, String> initErrMsgs() {
        HashMap<Label, String> errMsgs = new HashMap<>();

        errMsgs.put(divBy0Label, "Error: Division by 0");
        errMsgs.put(floatOverflowLabel, "Error: Float Operation Overflow");
        errMsgs.put(ioErrLabel, "Error: Input/Output Error");
        errMsgs.put(stackOverflowLabel, "Error: Stack Overflow");
        errMsgs.put(nullPointerLabel, "Error: Dereferencing Null Pointer");
        errMsgs.put(heapOverflowLabel, "Error: Heap Overflow");

        return errMsgs;
    }

    public ErrorManager() {
        errMap = new OrderedHashMap<>();

        errMap.addLast(divBy0Label, false);
        errMap.addLast(floatOverflowLabel, false);
        errMap.addLast(ioErrLabel, false);
        errMap.addLast(stackOverflowLabel, false);
        errMap.addLast(nullPointerLabel, false);
        errMap.addLast(heapOverflowLabel, false);
    }

    public void codeGenAllErrors(DecacCompiler compiler) {
        for (Label bLabel : errMap.getOrderedKeys()) {
            if (errMap.get(bLabel).equals(true)) {
                compiler.addLabel(bLabel);
                compiler.addInstruction(new WSTR(errMsgs.get(bLabel)));
                compiler.addInstruction(new WNL());
                compiler.addInstruction(new ERROR());
            }
        }
    }

    public Label useErr(Label errLabel) {
        errMap.updateValue(errLabel, true);
        return errLabel;
    }

    public Label getDivBy0Label() {
        return useErr(divBy0Label);
    }

    public Label getFloatOverflowLabel() {
        return useErr(floatOverflowLabel);
    }

    public Label getIoErrLabel() {
        return useErr(ioErrLabel);
    }

    public Label getStackOverflowLabel() {
        return useErr(stackOverflowLabel);
    }

    public Label getNullPointerLabel() {
        return useErr(nullPointerLabel);
    }

    public Label getHeapOverflowLabel() {
        return useErr(heapOverflowLabel);
    }

}
