cd "`dirname $BASH_SOURCE`"
DN="`pwd`"
cd - >/dev/null


declare -r MODULES="berkeleyje cassandra hbase persistit lucene es"
export MODULES

declare -r DEP_DIR="$DN"/../tmp
declare -r PAYLOAD_DIR="$DN"/../payload
export DEP_DIR PAYLOAD_DIR

declare -r RPM_TOPDIR=~/rpmbuild
export  RPM_TOPDIR

declare -r M2_REPO=/tmp/janusgraph-pkgbuild-repo
declare -r MVN_OPTS="-Dmaven.repo.local=$M2_REPO"
export M2_REPO MVN_OPTS


if [ ! -e "$M2_REPO" ]; then
    echo "$M2_REPO does not exist.  Symlinking to ~/.m2/repository."
    ln -s ~/.m2/repository "$M2_REPO"
elif [ -L "$M2_REPO" ]; then
    echo "$M2_REPO symlinks to `readlink $M2_REPO`"
else
    echo "$M2_REPO exists but is not a symlink: `ls -l $M2_REPO`"
fi
