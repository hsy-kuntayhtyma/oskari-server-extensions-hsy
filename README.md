## HSY Oskari server extensions

## Installation

* run ```mvn clean install```
* copy `oskari-map.war` to jetty9\webapps
* copy `transport.war` to jetty9\webapps

OR development you can use following:
```
mvn clean install && cp webapp-map/target/oskari-map.war ../../../Apps/jetty-9.4.12-oskari-hsy/oskari-server/webapps/ && cp webapp-transport/target/transport.war ../../../Apps/jetty-9.4.12-oskari-hsy/oskari-server/webapps/
``` 


## Flyway modules

| Module | Description |
| ------ | ----------: | 
| `ammassuo` | use only ammassuo server: `hsyoskaritest3` | 
| `dev`  | use when you copy database from HSY and want convert layer urls to work you enviranment |
| `hsy`| common hsy module, use it for all servers |
| `pipe` | pipe module, use only for servers: `hsyoskaritest3` and `hsyoskari3` |
| `seutumaisa` | use only seutumaisa servers: `hsyoskaritest2` and `hsyoskari2` |



 