#! /bin/sh

cd "$(dirname "$0")"/../../.. || exit 1

for fichier in src/test/deca/context/valid/langage_sans_objet/*.deca
do
  ./src/test/script/launchers/test_context $fichier
done