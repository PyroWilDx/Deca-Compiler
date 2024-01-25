#! /bin/sh

# Auteur : gl47
# Version initiale : 01/01/2024

# Notre script de test de la lexicographie.

RED='\033[0;31m';
GREEN='\033[0;32m';
NC='\033[0m'; # No Color

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"


test_lex_valide () {
    test_synt "$1" > "${1%.deca}".res
    if test_synt "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "$Echec inattendu pour test_lex sur $1 XXXXXXXXXXXXXXXXXXXXXXXXX${NC}"
        exit 1
    else
      echo "${GREEN}Succes attendu de test_lex sur $1${NC}"
    fi
}
echo [ TESTS VALIDES POUR LE LEXEUR]

test_lex_valide "src/test/deca/syntax/valid/helloWorld/hello.deca"
test_lex_valide "src/test/deca/syntax/valid/helloWorld/hello.deca"

for cas_de_test in src/test/deca/syntax/valid/sansObjet/*.deca
do
    test_lex_valide "$cas_de_test"
done

for cas_de_test in src/test/deca/syntax/valid/avecObjet/*.deca
do
    test_lex_valide "$cas_de_test"
done

echo [ TESTS INVALIDES POUR LE LEXEUR]

if test_lex src/test/deca/syntax/invalid/helloWorld/chaine_incomplete.deca 2>&1 \
    | grep -q -e 'chaine_incomplete.deca:10:'
then
    echo "${GREEN}Echec attendu pour test_lex sur chaine_incomplete.deca.${NC}"
else
    echo "Erreur non detectee par test_lex pour chaine_incomplete.deca XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX${NC}"
    exit 1
fi

if test_lex src/test/deca/syntax/invalid/include/include_incorrect.deca 2>&1 \
    | grep -q -e 'include_incorrect.deca:10:'
then
    echo "${GREEN}Echec attendu pour test_lex sur include_incorrect.deca.${NC}"
else
    echo "Erreur non detectee par test_lex pour include_incorrect.deca XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX${NC}"
    exit 1
fi

if test_lex src/test/deca/syntax/invalid/sansObjet/crochets.deca 2>&1 \
    | grep -q -e 'crochets.deca:11:'
then
    echo "${GREEN}Echec attendu pour test_lex sur crochets.deca.${NC}"
else
    echo "Erreur non detectee par test_lex pour crochets.deca XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX${NC}"
    exit 1
fi

if test_lex src/test/deca/syntax/invalid/sansObjet/deuxpoints.deca 2>&1 \
    | grep -q -e 'deuxpoints.deca:11:'
then
    echo "${GREEN}Echec attendu pour test_lex sur deuxpoints.deca.${NC}"
else
    echo "Erreur non detectee par test_lex pour deuxpoints.deca XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX${NC}"
    exit 1
fi

if test_lex src/test/deca/syntax/invalid/sansObjet/interrogation.deca 2>&1 \
    | grep -q -e 'interrogation.deca:12:'
then
    echo "${GREEN}Echec attendu pour test_lex sur interrogation.deca.${NC}"
else
    echo "Erreur non detectee par test_lex pour interrogation.deca XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX${NC}"
    exit 1
fi