#!/bin/bash
echo "Taking down composition"
docker compose down
echo "Removing images..."
docker image rm 'fileindexer-fileindexerapp'
docker image rm 'fileindexer-fileindexerreverseproxy'
docker image rm 'fileindexer-fileindexerindex'

echo "Running script that builds the fileindexer-app"
CUR_DIR=$(pwd)
pushd fileindexer-app > /dev/null
./mvnw package
echo "Mvn packaging complete ..."

popd > /dev/null

echo "Rebuild composition..."
docker compose --verbose up --detach 
