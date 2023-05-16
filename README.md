# HSY Oskari server extensions

## Installation

* run ```mvn clean install```
* copy `oskari-map.war` to jetty9\webapps
* copy `transport.war` to jetty9\webapps

OR development you can use following:

```
mvn clean install && cp webapp-map/target/oskari-map.war ../../../Apps/jetty-9.4.12-oskari-hsy/oskari-server/webapps/ && cp webapp-transport/target/transport.war ../../../Apps/jetty-9.4.12-oskari-hsy/oskari-server/webapps/
```

## Installation on HSY servers

### First run all these

* oskari-server-extensions-karttasovellus:
  * new version:  run ```mvn -N versions:set -DnewVersion=<version>```
  * commit changes: ```git add . && git commit -m "Version upgrade"```
  * add new git tag: ```git tag -a <version> -m "Version <version>"```
  * push changes: ```git push && git push --tags```
  * create wars : ```mvn clean install```
* oskari-frontend-contrib:
  * add new git tag: ```git tag -a <version> -m "Version <version>"```
  * push changes: ```git push && git push --tags```
* oskari-frontend-karttasovellus:
  * add new git tag: ```git tag -a <version> -m "Version <version>"```
  * push changes: ```git push && git push --tags```
* servers:
  * add new git tag: ```git tag -a <version> -m "Version <version>"```
  * push changes: ```git push && git push --tags```
* oskari-frontend-hsy:
  * add new git tag: ```git tag -a <version> -m "Version <version>"```
  * push changes: ```git push && git push --tags```
  * create minifyed package: ```npm run build -- --env.appdef=1.00:applications/hsy```

### Create installation zip package

* create new folder: `oskari-<version>`
* copy oskari-server-extensions-karttasovellus/webapp-map/target/oskari-map.war to created folder
* zip oskari-frontend-hsy/dist -folder and copy dist.zip to created folder
* zip `oskari-<version>` files (not folder) to `oskari-<version>.zip`

### Transfer installation zip to HSY server

Transfer `oskari-<version>.zip` to HSY server in `/home/<username>/update_versions` -folder

### Install update to HSY server

Run following code in `/home/<username>/update_versions` -folder:

```
sudo sh deploy.sh oskari-<version>.zip
```

Now all are installed and you can test it when jetty starts (if not then try to start again).

## Flyway modules

| Module | Description |
| ------ | ----------: |
| `ammassuo` | use only ammassuo server: `hsyoskaritest3` |
| `dev`  | use when you copy database from HSY and want convert layer urls to work you enviranment |
| `hsy`| common hsy module, use it for all servers |
| `pipe` | pipe module, use only for servers: `hsyoskaritest3` and `hsyoskari3` |
| `seutumaisa` | use only seutumaisa servers: `hsyoskaritest2` and `hsyoskari2` |
