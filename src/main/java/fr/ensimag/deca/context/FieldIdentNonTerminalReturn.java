package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.Visibility;

public class FieldIdentNonTerminalReturn {
    private final Visibility visibility;
    private final ClassDefinition containingClass;
    private final Type type;

    public FieldIdentNonTerminalReturn(Visibility visibility,
                                       ClassDefinition containingClass,
                                       Type type) {
        this.visibility = visibility;
        this.containingClass = containingClass;
        this.type = type;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public ClassDefinition getContainingClass() {
        return containingClass;
    }

    public Type getType() {
        return type;
    }
}
