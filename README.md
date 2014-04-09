SODA
====

####Server Observer and Data Aggregator

###Project Scope:



The SODA project aims to investigate the relationship between server components (which includes only HTTP requests/responses, Garbage Collection, Central Processing Unit, Network Traffics, Storage and Disk Throughput and Database Transaction Times) and server performance. The project will deliver a tool that will be deployed in the server (which run either Windows or Linux) and it will monitor and record the performance of those server components. Whenever that those server components are reaching to a critical period (which will be defined after the investigation), the tool will notify to the System Administrator and/or Support Engineer. Lastly, The tool will offer a web service that can let System Administrator and/or Support Engineer to look at those server components' data on the web browser. 


====

###Use Cases:

######Use Case :1


As a Client System or a Support Engineer, I want to be able to view (visualize, understand) below data during an incident*. So, that I can have an understanding of what is happening in my server.

    Web application throughput (number of responses/second, HTTP requests and responses)
    Exception log
    GC Performance
    CPU performance
    Network traffics
    Storage and disk throughput
    Database transaction times (i.e. are these rising) – Stretch

        
######Use Case 2:

As a Support Engineer, I want to have a record of below data during an incident*. So, that I can visualise and investigate on this data at a later stage (i.e. when the client brings the problem to my attention). Those data are:

Web application throughput (number of responses/second, HTTP requests and responses)

    Exception log
    GC Performance
    CPU performance
    Network traffics
    Storage and disk throughput
    Database transaction times (i.e. are these rising) - Stretch


        

######Use Case 3:

As a Client System or a Support Engineer, I want to get a notification when there is a problem. So, that I can do in-time monitoring of what applications are running during (or causing) the GC storm.


*Incident: means

    Server stop responding, or
    Server response time increased dramatically (i.e. 10 times longer that its normally performance)
