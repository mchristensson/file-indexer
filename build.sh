#!/bin/bash
echo "Taking down composition"
docker compose down
echo "Removing images..."
docker image rm urax-uraxapp
docker image rm urax-uraxdb
docker image rm urax-reverseproxy

echo "Running script"
CUR_DIR=$(pwd)
pushd urax-app > /dev/null
./gradlew assemble
echo "Gradle assemble complete ..."

popd > /dev/null

echo "Rebuild composition..."
docker compose up --detach
