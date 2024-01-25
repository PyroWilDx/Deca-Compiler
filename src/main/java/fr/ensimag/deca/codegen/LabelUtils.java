package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.Label;

public class LabelUtils {

    public static SymbolTable.Symbol OBJECT_CLASS_SYMBOL = null;
    public static final String OBJECT_CLASS_NAME = "Object";
    public static final String EQUALS_METHOD_NAME = "equals";

    private LabelUtils() {

    }

    public static void setObjectClassSymbol(SymbolTable.Symbol value) {
        OBJECT_CLASS_SYMBOL = value;
    }

    public static Label getClassInitLabel(String className) {
        return new Label("init" + GameBoyManager.getLabelSeparator() + className);
    }

    public static Label getMethodLabel(String className, String methodName) {
        return new Label("code" + GameBoyManager.getLabelSeparator() +
                className + GameBoyManager.getLabelSeparator() + methodName);
    }

    public static Label getMethodEndLabel(String className, String methodName) {
        return new Label("end" + GameBoyManager.getLabelSeparator() +
                className + GameBoyManager.getLabelSeparator() + methodName);
    }

}
