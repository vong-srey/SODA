### What is this MyEditionForToolsUsedInSODA folder?

This MyEditionForToolsUsedInSODA folder contains all the code that I wrote or modified on all open sources that I'm using in SODA.
Below, I described what are those files, what it should be copied and moved to.



##### forkibana folder:

Below files have to be copied to ./kibana
	
	config.js
	jquery.js
	observer.html


Below files have to be copied to ./kibana/app/partials/
	
	dashLoader.html
	dashLoaderObserver.html


This file is the configuration to show the monitoring on kibana. It should be open from kibana
	
	Kibana-OHP_MonitorConfig.json
	



##### forlogstash:

Below files have to be copied to ./logstash/conf/
	
	learn.conf


Below files have to be copied to ./logstash/patterns/
	
	soda-patterns


Below files have to be copied to ./logstash/lib/logstash/filters/
	
	sodaclone.rb