#!/bin/bash

# - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - #
#    This is the installation script that will automatically setup all the neccessery environment to run SODA on CentOS environment.    #
#                  Please note that this installation script might not set up correctly on any other Linux Distribution                 #
#                      (other than CentOs and its siblings) due to the different environment of each distribution.                      #
#                   But it requires that you must log in as a root user and you must also has installed httpd server.                   #
#                                 Generally, CentOS always comes with built in httpd and iptables server.                               #
#                                    So, you don't need to care for it if you are running on Centos.                                    #
#                                                                                                                                       #
#                                             This script has been tested on CentOS 6.*.                                                #
#                                                                                                                                       #
#                     This script is used to setup a Standalone-SODA server (All logs are not distributed) only.                        #
# - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - # - #



# Checking if the machine running has java 1.7 or greater.
# If it has java lower than 1.7, it will inform the user and terminate this installation
function search_for_java_1_7(){
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
}


# Check if the user is running as a root.
# the installation requires root permission.
# If it is not running as a root, it will let user know and terminates installation.
function is_running_as_root(){
	if [ "$(id -u)" != "0" ]; then
	   echo "Please log in as root user and try again." 1>&2
	   exit 1
	fi

	echo "running as root...";
}


# Setup http server
# If it can't setup programmatically, it will let user know to setup manually, and terminates the installation
function setup_httpd_server(){
	command -v httpd >/dev/null 2>&1 || { 
	   echo >&2 "There's no httpd service, please manually install httpd service first." 
	   exit 1
	}
	# running httpd server
	service httpd restart
	chkconfig httpd on
	echo "Setup apache server successful."
}


# Setup iptables and the open port 10:9900 to listen for tcp
function setup_iptables(){
	echo "Setting up iptables and httpd server ..."
	# setup iptables (remove existing one and replace with the new one)
	rm -f /etc/sysconfig/iptables
	cp -f ./httpd/iptables /etc/sysconfig/iptables
	service restart iptables
	echo "Setup iptables successful."
}


# setup soda as a logger that log the system performances (CPU, memory, Disk, DF, Process)
function setup_soda_logger(){
	echo "Installing Logger ..."
	# setup SODA-logger
	   # copy SODA-logger and start it
	cp -fR ./sodalogger_observer /usr/share/sodalogger
	   #start soda-logger
	pushd /usr/share/sodalogger
	nohup java -jar -Djava.library.path=./lib/ soda.jar -a &
	popd
	echo "Setup Logger successful."
}


# setup soda as the observer that observing the system performance.
# the criterias being observed are defined through kibana web interface.
function setup_soda_observer(){
	echo "Installing Observer ..."
	# setup SODA-observer
	   # copy SODA-observer and start it
	cp -fR ./sodalogger_observer /usr/share/sodaobserver
	   #start soda-observer
	pushd /usr/share/sodaobserver
	nohup java -jar -Djava.library.path=./lib/ soda.jar -o &
	popd
	echo "Setup Observer successful."
}


# Setup soda as bother a logger and observer (this method is used by standalone server only)
function setup_soda_logger_observer(){
	echo "Installing SODA (Logger and Observer) ..."
	# setup SODA
	   # copy SODA
	cp -fR ./sodalogger_observer /usr/share/soda
	   #start soda
	pushd /usr/share/soda
	nohup java -jar -Djava.library.path=./lib/ soda.jar &
	popd
	echo "Setup SODA successful."
}


# Setup redis and its required environemnts
function setup_redis(){
	echo "Installing Redis Node Server ...";
	cp -fR ./redis /usr/share/redis
	# Install redis
	pushd /usr/share/redis
	make
	# start redis
	nohup /usr/share/redis/src/redis-server &
	popd
	echo "Setup Redis Node Server successful."
}



# This algortihm is copied from: http://www.linuxjournal.com/content/validating-ip-address-bash-script
# All these works are credited to Mitch Frazier (Jun 26, 2008)
#
# Test an IP address for validity:
# Usage:
#      valid_ip IP_ADDRESS
#      if [[ $? -eq 0 ]]; then echo good; else echo bad; fi
#   OR
#      if valid_ip IP_ADDRESS; then echo good; else echo bad; fi
#
function valid_ip()
{
    local  ip=$1
    local  stat=1

    if [[ $ip =~ ^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$ ]]; then
        OIFS=$IFS
        IFS='.'
        ip=($ip)
        IFS=$OIFS
        [[ ${ip[0]} -le 255 && ${ip[1]} -le 255 \
            && ${ip[2]} -le 255 && ${ip[3]} -le 255 ]]
        stat=$?
    fi
    return $stat
}


# Generate the node.conf file for logstash that running on a node of distributed system
function generate_node_conf(){
	while true; do
		read -p "Please type in the IP address of your Central Observer: " ip

		if valid_ip $ip; then
			var="	
			\n #for detail of the patterns, please find them in logstash/patterns/grok-patterns or logstash/patterns/soda-patterns
			\n # input the logs into the SODA log management system
			\n
			\n input { file { path => [\"/usr/share/sodalogger/Logs/*/*\", \"/var/log/httpd/access_log\", \"/var/www/html/pydio/dblogs/dblog\"] } }
			\n
			\n # log manager push the log event into redis server as a channel datastructure.
			\n # channel data structure means that the data is just pushed onto the stack and if there's no thread is working, that stack will be empty.
			\n # list data structure means that once the data is pushed onto the stack, it will blocked until there's a thread process this data. it can be memory intense
			\n output { redis {
			\n 		host => \"$ip\"		#ip address the redis server
			\n		data_type => \"channel\"
			\n		key => \"logstash\"
			\n	} }
			";
			echo -e $var > ./logstash/conf/node.conf;
			break;

		else
			echo "The given IP Adress is not valid"
		fi
	done	
}

 
# Setup logstash on a distributed node (In a distributed system, there can be many of this node)
function setup_logstash_on_distributed_node(){
	echo "Installing Logstash ..."
	# setup configuration file for logstash
	generate_node_conf;

	# copy logstash and start it
	cp -fR ./logstash /usr/share/logstash
	# start logstash
	nohup /usr/share/logstash/bin/logstash agent -f /usr/share/logstash/conf/node.conf --log /usr/share/logstash/logstashlogs/logstash.log &
	echo "Setup logstash successfully."
}


# Setup logstash on the central observer (In general, there will be only one central observer in a distributed system)
function setup_logstash_on_central_observer(){
	echo "Installing Logstash ..."
	# setup logstash
	   # copy logstash and start it
	cp -fR ./logstash /usr/share/logstash
	   # start logstash
	nohup /usr/share/logstash/bin/logstash agent -f /usr/share/logstash/conf/server.conf --log /usr/share/logstash/logstashlogs/logstash.log &
	echo "Setup logstash successfully."
}


# Setup logstash as a standalone server
function setup_logstash_on_standalone(){
	echo "Installing Logstash ..."
	# setup logstash
	   # copy logstash and start it
	cp -fR ./logstash /usr/share/logstash
	   # start logstash
	nohup /usr/share/logstash/bin/logstash agent -f /usr/share/logstash/conf/standalone.conf --log /usr/share/logstash/logstashlogs/logstash.log &
	echo "Setup logstash successfully."
}


# Setup elasticsearch, its required plugin and set all the data to be expired (deleted) within 2d
function setup_elasticsearch(){
	echo "Installing Elasticsearch ..."
	# setup elasticsearch
	   # copy elasticsearch and start it
	cp -fR ./elasticsearch /usr/share/elasticsearch
	   # start elasticsearch
	nohup /usr/share/elasticsearch/bin/elasticsearch &
	   #install useful plugins
	/usr/share/elasticsearch/bin/plugin --install lmenezes/elasticsearch-kopf
	/usr/share/elasticsearch/bin/plugin --install mobz/elasticsearch-head
	   # tell elasticsearch to set all indices to be expired after 7d (means those indices will be deleted automatically after 7d)
	curl -XPUT localhost:9200/_template/indexexpiry -d '{"template":"*","mappings":{"_default_":{"_ttl":{"enabled":true,"default":"2d"}}}}'
	echo "Setup elasticsearch successfully"
}


# Setup kibana (kibana requires a servlet or http server such as httpd)
function setup_kibana(){
	echo "Installing Kibana ..."
	# setup Kibana to the hosting directory
	cp -fR ./kibana /var/www/html/
	echo "Setup kibana successfully."
}





echo "Preparing environment...";

# Make sure there's java 1.7 or greater on this machine			
search_for_java_1_7;

# Make sure only root can run our script
is_running_as_root;

# Make sure there's httpd (apache) server
setup_httpd_server;

setup_iptables;



while true; do
	read -p "
How would you like to install SODA?
  1.Install SODA on a distributed node (a node of distributed servers system). 
  2.Install SODA on the Central Server (a central server of distributed servers system. 
  3.Install SODA on a standalone server.
Please choose 1,2 or 3:" answer
	case $answer in
		[1a]* ) 
			echo "Preparing to install as a distributed node...";

			setup_soda_logger;
			setup_redis;
			setup_logstash_on_distributed_node;

			echo "Installation as a distributed node is successfully done."; 
			break;;

		[2b]* ) 
			echo "Preparing to install as the Centeral Server...";
			
			setup_redis;
			setup_elasticsearch;
			setup_kibana;
			setup_logstash_on_central_observer;
			setup_soda_observer;

			echo "Installation as a distributed node is successfully done."; 
			break;;

		[3c]* ) 
			echo "Preparing to install as a standalone server...";
			
			setup_kibana;
			setup_soda_logger_observer;
			setup_elasticsearch;
			setup_logstash_on_standalone;

			echo "Installation as a distributed node is successfully done."; 
			break;;
	esac
done




