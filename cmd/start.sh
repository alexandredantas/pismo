#!/bin/bash

GREEN='\033[0;32m'
BASEDIR=$(dirname "$0")
EXECDIR=$PWD

cd $BASEDIR
BASEPATH=$PWD

cd $BASEPATH/../accounts
./gradlew build

if [ $? != "0" ]
then
    echo "Error compiling accounts project"
    exit $?
fi

cd $BASEPATH/../transactions
./gradlew build

if [ $? != "0" ]
then
    echo "Error compiling transactions project"
    exit $?
fi


cd $EXECDIR

docker pull java:8-alpine
docker pull alpine
docker pull postgres:10.4-alpine

docker swarm init > /dev/null 2>&1
docker stack deploy --compose-file $BASEPATH/../docker-compose.yml pismo-dev

SERVICESUP=0
echo "Starting environment"
while [ $SERVICESUP -lt 2 ]
do
    echo -n "."
    SERVICESUP=$(docker ps | grep -w "healthy" | grep pismo | wc -l)
done

printf "\n${GREEN}Dev up"