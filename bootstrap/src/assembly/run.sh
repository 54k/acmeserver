#!/bin/sh

PRG="$0"

while [ -h "$PRG" ]; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`/"$link"
    fi
done
BQ_HOME=`dirname "$PRG"`

# Absolute path
BQ_HOME=`cd "$BQ_HOME/.." ; pwd`

# echo Resolved BQ_HOME: $BQ_HOME
# echo "JAVA_HOME $JAVA_HOME"

cygwin=false;
case "`uname`" in
    CYGWIN*)
        cygwin=true
        ;;
esac

# Build a classpath containing our two magical startup JARs
BQ_CP=`echo "$BQ_HOME"/bin/*.jar | sed 's/ \//:\//g'`
# echo BQ_CP: $BQ_CP

# Store file locations in variables to facilitate Cygwin conversion if needed

BQ_OSGI_FRAMEWORK_STORAGE="$BQ_HOME/cache"
# echo "BQ_OSGI_FRAMEWORK_STORAGE: $BQ_OSGI_FRAMEWORK_STORAGE"

BQ_AUTO_DEPLOY_DIRECTORY="$BQ_HOME/bundle"
# echo "BQ_AUTO_DEPLOY_DIRECTORY: $BQ_AUTO_DEPLOY_DIRECTORY"

BQ_CONFIG_FILE_PROPERTIES="$BQ_HOME/conf/config.properties"
# echo "BQ_CONFIG_FILE_PROPERTIES: $BQ_CONFIG_FILE_PROPERTIES"

cygwin=false;
case "`uname`" in
    CYGWIN*)
        cygwin=true
        ;;
esac

if [ "$cygwin" = "true" ]; then
    export BQ_HOME=`cygpath -wp "$BQ_HOME"`
    export BQ_CP=`cygpath -wp "$BQ_CP"`
    export BQ_OSGI_FRAMEWORK_STORAGE=`cygpath -wp "$BQ_OSGI_FRAMEWORK_STORAGE"`
    export BQ_AUTO_DEPLOY_DIRECTORY=`cygpath -wp "$BQ_AUTO_DEPLOY_DIRECTORY"`
    export BQ_CONFIG_FILE_PROPERTIES=`cygpath -wp "$BQ_CONFIG_FILE_PROPERTIES"`
    # echo "Modified BQ_HOME: $BQ_HOME"
    # echo "Modified BQ_CP: $BQ_CP"
    # echo "Modified BQ_OSGI_FRAMEWORK_STORAGE: $BQ_OSGI_FRAMEWORK_STORAGE"
    # echo "Modified BQ_AUTO_DEPLOY_DIRECTORY: $BQ_AUTO_DEPLOY_DIRECTORY"
    # echo "Modified BQ_CONFIG_FILE_PROPERTIES: $BQ_CONFIG_FILE_PROPERTIES"
fi

java $BQ_OPTS -Djava.util.logging.config.file=$BQ_HOME/conf/logging.properties -Dgosh.home=$BQ_HOME/conf -Dorg.osgi.framework.storage="$BQ_OSGI_FRAMEWORK_STORAGE" -Dfelix.auto.deploy.dir="$BQ_AUTO_DEPLOY_DIRECTORY" -Dfelix.config.properties="file:$BQ_CONFIG_FILE_PROPERTIES" -cp "$BQ_CP" com.acme.bootstrap.Main