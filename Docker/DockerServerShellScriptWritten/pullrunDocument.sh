#!/bin/sh

service=documentserverservice
buildn="$1"
if [ $buildn -gt 0 ]
then
   echo " $service $buildn"
   ./pullrunimage.sh $service $buildn
fi
