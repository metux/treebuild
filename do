#!/bin/bash

if [ ! "$JLIB_METUX" ]; then
    echo "Missing JLIB_METUX variable"
fi

export TARGET_SYSTEM=jail
export UNITOOL_CONF=/install/config/systems/$TARGET_SYSTEM/etc/unitool/tools.cf
export PI_CONFIG=/install/config/systems/$TARGET_SYSTEM/etc/unitool/pi-build.conf

PROJECTS="$PACKAGES x11-util-macros"
PROJECTS="$PACKAGES x11-xtrans"
PROJECTS="$PACKAGES x11-xproto"
PROJECTS="$PACKAGES x11-bigreqsproto"
PROJECTS="$PACKAGES libbzip2"
PROJECTS="$PACKAGES bzip2"

for p in $PROJECTS ; do
    BASEDIR=/home/devel/projects/pi-build/
    PROJECTDIR=/home/devel/projects/pi-packages/$p
    export CLASSPATH=$BASEDIR/.build:$JLIB_UNITOOL/.build:$JLIB_METUX/.build
    cd $PROJECTDIR && pwd && java metux.treebuild.main.Main --pi-build recipe pi-build.xml
done
