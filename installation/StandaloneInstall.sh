#!/bin/bash

# - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - #
#    This is the installation script that will automatically setup all the neccessery environment to run SODA on CentOS environment.    #
#                  Please note that this installation script will not set up correctly on any other Linux Distribution                  #
#                      (other than CentOs and its siblings) due to the different environment of each distribution.                      #
#                   But it requires that you must log in as a root user and you must also has installed httpd server.                   #
#                                 Generally, CentOS always comes with built in httpd and iptables server.                               #
#                                    So, you don't need to care for it if you are running on Centos.                                    #
#                                                                                                                                       #
#                                             This script has been tested on CentOS 6.*.                                                #
#                                                                                                                                       #
#                     This script is used to setup a Standalone-SODA server (All logs are not distributed) only.                        #
# - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - #



# Searching for Java (logstash 1.4 can run only on Java 1.7 or later)
if type -p java; then
   echo found java executable in PATH
   _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
   echo found java executable in JAVA_HOME     
   _java="$JAVA_HOME/bin/java"
else
   echo "There is no Java found. Please install Java or setup Path and JAVA_HOME"
   exit 1
fi
if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo version "$version"
    if [[ "$version" < "1.7" ]]; then
        echo "Please update to Java 1.7 or later. SODA requires at least Java 1.7."
	exit 1
    fi
fi


# Make sure only root can run our script
if [ "$(id -u)" != "0" ]; then
   echo "Please log in as root user and try again." 1>&2
   exit 1
fi



echo "Setting up iptables and httpd server ..."
# setup iptables (remove existing one and replace with the new one)
rm -f /etc/sysconfig/iptables
cp -f ./httpd/iptables /etc/sysconfig/iptables
service restart iptables
echo "Setup iptables successfully."


# Make sure there's httpd (apache) server
command -v httpd >/dev/null 2>&1 || { 
   echo >&2 "There's no httpd service, please install httpd service first." 
   exit 1
}
# running httpd server
service httpd restart
chkconfig httpd on
echo "Setup apache server successfully."



echo "Installing Kibana ..."
# setup Kibana to the hosting directory
cp -fR ./kibana /var/www/html/
echo "Setup kibana successfully."



echo "Installing Logger ..."
# setup SODA-logger
   # copy SODA-logger and start it
cp -fR ./sodalogger /usr/share/sodalogger
   #start soda-logger
pushd /usr/share/sodalogger
nohup java -jar -Djava.library.path=./lib/ soda.jar &
popd
echo "Setup logger successfully."



echo "Installing Elasticsearch ..."
# setup elasticsearch
   # copy elasticsearch and start it
cp -fR ./elasticsearch /usr/share/elasticsearch
   # start elasticsearch
/usr/share/elasticsearch/bin/elasticsearch -d
   #install useful plugins
/usr/share/elasticsearch/bin/plugin --install lmenezes/elasticsearch-kopf
/usr/share/elasticsearch/bin/plugin --install mobz/elasticsearch-head
   # tell elasticsearch to set all indices to be expired after 7d (means those indices will be deleted automatically after 7d)
curl -XPUT localhost:9200/_template/indexExpiry -d '{"template" : "*", "mappings" : {"_default_" : {"_ttl" : { "enabled" : true, "default" : "7d" }}}}'
echo "Setup elasticsearch successfully"



echo "Installing Logstash ..."
# setup logstash
   # copy logstash and start it
cp -fR ./logstash /usr/share/logstash
   # start logstash
nohup /usr/share/logstash/bin/logstash agent -f /usr/share/logstash/conf/standalone.conf --log /usr/share/logstash/logstashlogs/logstash.log &
echo "Setup logstash successfully."