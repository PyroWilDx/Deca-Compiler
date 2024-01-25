lexer grammar DecaLexer;

options {
   language=Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@members {
}

// Deca lexer rules.

SEMI : ';';

COMMA : ',';

OBRACE : '{';
CBRACE : '}';

OPARENT : '(';
CPARENT : ')';

EQUALS : '=';

OR : '||';
AND : '&&';
EQEQ : '==';
NEQ : '!=';
LEQ : '<=';
GEQ : '>=';
LT : '<';
GT : '>';

INSTANCEOF : 'instanceof';

PLUS : '+';
MINUS : '-';
TIMES : '*';
SLASH : '/';
PERCENT : '%';

EXCLAM : '!';

DOT : '.';

READINT : 'readInt';
READFLOAT : 'readFloat';
NEW : 'new';

TRUE : 'true';
FALSE : 'false';
THIS : 'this';
NULL : 'null';

IF : 'if';
ELSE : 'else';

WHILE : 'while';

RETURN : 'return';

PRINT : 'print';
PRINTLN : 'println';
PRINTX : 'printx';
PRINTLNX : 'printlnx';

CLASS : 'class';
EXTENDS : 'extends';
PROTECTED : 'protected';
ASM : 'asm';

COMMENT : ('//'.*? ('\n'|EOF)|'/*' .*? '*/') {skip();};

STRING : '"'(STRING_CAR | '\\' | '\\\\' | '\\"')*'"';
INT : '0' | (POSITIVE_DIGIT DIGIT*);
FLOAT : FLOATDEC | FLOATHEX;
INCLUDE : '#include' (' ')* ' "'(LETTER | DIGIT | '.' | '-' | '_')+ '"'{doInclude(getText());};
INCLUDE_TILE : '#includeTiles' (' ')* ' "'(LETTER | DIGIT | '.' | '-' | '_')+ '"'{doIncludeTiles(getText());};
INCLUDE_TILE_MAP : '#includeTilemaps' (' ')* ' "'(LETTER | DIGIT | '.' | '-' | '_')+ '"'{doIncludeTilemaps(getText());};
IDENT : (LETTER | '$' | '_')(LETTER | DIGIT |'_')*;


MULTI_LINE_STRING : '"'(STRING_CAR | '\\' | '\\\\' | '\n' | '\\"')*'"';

fragment POSITIVE_DIGIT : '1'..'9';
fragment DIGIT : '0'..'9';
fragment LETTER : ('a'..'z'|'A'..'Z');


fragment NUM : DIGIT+;
fragment EXP : ('E' | 'e') ('+' | '-')? NUM;
fragment DEC : NUM '.' NUM;
fragment FLOATDEC : (DEC | DEC EXP) ('F' | 'f')?;
fragment DIGITHEX : DIGIT | ('A'..'F') | ('a'..'f');
fragment NUMHEX : DIGITHEX+;
fragment FLOATHEX : ('0x' | '0X') NUMHEX '.' NUMHEX ('P' | 'p') ('+' | '-')? NUM ('F' | 'f')?;

fragment STRING_CAR : ~ ('"' | '\\' | '\n');

RTL : '\n' {skip();};
TAB : '\t' {skip();};
SPACE : ' ' {skip();};