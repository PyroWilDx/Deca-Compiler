package fr.ensimag.deca.syntax;

import org.antlr.v4.runtime.IntStream;

/**
 * Exception raised when a #includeTiles is used but the -g flag is not passed.
 *
 * @author gl47
 * @date 01/01/2024
 */
public class NonAuthorizedIncludeTiles extends DecaRecognitionException {

    public NonAuthorizedIncludeTiles(AbstractDecaLexer recognizer, IntStream input) {
        super(recognizer, input);
    }
    @Override
    public String getMessage() {
        return "Usage of #includeTiles not authorized if -g flag corresponding to Gameboy mode is not passed";
    }

    private static final long serialVersionUID = -8541996988279897766L;

}
