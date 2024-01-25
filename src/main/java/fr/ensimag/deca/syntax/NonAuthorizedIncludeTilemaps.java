package fr.ensimag.deca.syntax;

import org.antlr.v4.runtime.IntStream;

/**
 * Exception raised when a #includeTilemaps is used but the -g flag is not passed.
 *
 * @author gl47
 * @date 01/01/2024
 */
public class NonAuthorizedIncludeTilemaps extends DecaRecognitionException {
    public NonAuthorizedIncludeTilemaps(AbstractDecaLexer recognizer, IntStream input) {
        super(recognizer, input);
    }
    @Override
    public String getMessage() {
        return "Usage of #includeTilemaps not authorized if -g flag corresponding to Gameboy mode is not passed";
    }

    private static final long serialVersionUID = -8541996988279297766L;

}
