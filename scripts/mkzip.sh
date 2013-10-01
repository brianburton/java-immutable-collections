#!/bin/bash

mvn clean package
cd target || exit 1
[ -f javimmutable-collections-*-sources.jar ] || exit 1

base=`basename javimmutable-collections-*-sources.jar -sources.jar`
echo $base

[ -d $base ] && /bin/rm -rf $base
mkdir $base
cp *.jar $base
cp ../LICENSE.txt $base
zip -r ${base}.zip $base
