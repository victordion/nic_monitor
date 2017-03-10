# NIC monitor 

## Motivation 
It is often very useful to monitor network traffic on your server's NIC (network interface controller). If you run a VPS on Digital Ocean, the NIC traffic information can be found on the dashboard page. However there are some drawbacks. For example

* Time resolution is not adjustable.
* Data is not persisted and you cannot get history data.

This project aims at creating a service running on your VPS, monitoring NIC traffic and persisting data into MySQL database. It also maintains a RESTful HTTP service for requests of such data. The data can be displayed in very flexible ways by the frontend. The project can also be extended to monitor and persist other critical server information, such as CPU usage, memory usage, you name it.

# Example
The code is deployed to jcui.info and a visualization of NIC traffic on this host is at http://jcui.info/nicmonitor/
![Alt text](https://cloud.githubusercontent.com/assets/7118674/23784197/73308510-0515-11e7-875e-4315b47f0ecf.png "Example Chart")

## Usage
### Dependency management and build tool 
We use pants to build the project. For installation guide please refer to http://www.pantsbuild.org/install.html

To build the project into a fat jar
```
$ ./pants binary untitled/nic_monitor/server:nic-monitor-server
```

Run the jar on your server
```
$ java -jar nic-monitor-server.jar -username <your-mysql-username> -password <your-mysql-password>
```
