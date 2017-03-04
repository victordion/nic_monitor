# NIC monitor 

## Motivation 
It is often very useful to monitor network traffic on your server's NIC (network interface controller). If you run a VPS on Digital Ocean, the NIC traffic information can be found on the dashboard page. However there are some drawbacks. For example

* Time resolution is not adjustable.
* Data is not persisted and you cannot get history data.

This project aims at creating a service running on your VPS, monitoring NIC traffic and persisting data into MySQL database. It also maintains a RESTful HTTP service for requests of such data. The data can be displayed in very flexible ways by the frontend. The project can also be extended to monitor and persist other critical server information, such as CPU usage, memory usage, you name it.

## Usage
### Dependency management and build tool 
We use pants to build the project. For installation guide please refer to http://www.pantsbuild.org/install.html

To build the project into a fat jar
```
./pants binary untitled/nic_monitor/server:nic-monitor-server
```

Run the jar on your server
```
java -jar nic-monitor-server.jar -username <your-mysql-username> -password <your-mysql-password>
```
