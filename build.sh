#!/bin/bash
echo "Taking down composition"
docker compose down
echo "Removing images..."
docker image rm fileindexer-app
docker image rm fileindexer-index
docker image rm fileindexer-reverseproxy

echo "Running script that builds the fileindexer-app"
CUR_DIR=$(pwd)
echo "(1) In directory $(pwd)"
pushd fileindexer-app > /dev/null
echo "(2) In directory $(pwd)"
mvn package
echo "Mvn packaging complete ..."
echo "(3) In directory $(pwd)"

popd > /dev/null

echo "Rebuild composition..."
docker compose --verbose up --detach 
