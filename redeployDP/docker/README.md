# Docker-based local deployment

There are 3 most common setups used in our tests

1. The whole data platform with keycloak, nginx and non-pms API modules
2. Pure identity module for direct access. Includes IM itself, postgresql backend and activemq
3. Identity module behind nginx proxy. Contains the same elements as the above setup + nginx, redis, keycloak.

There are separate docker-compose files for each of the above setups.

The first step to run the data platform is to build dedicated docker images.
execute ```./build_all.sh``` script to build them all.
The following new images should have appeared after that:
```
docker.snapshot.travel:5000/snapshot/dataplatform_builder 1.37
docker.snapshot.travel:5000/snapshot/dataplatform_builder latest
snapshot/dataplatform_builder latest
docker.snapshot.travel:5000/snapshot/db_migrator 1.37
docker.snapshot.travel:5000/snapshot/db_migrator latest
snapshot/db_migrator latest
docker.snapshot.travel:5000/snapshot/java 8u131
docker.snapshot.travel:5000/snapshot/java latest
snapshot/java latest
docker.snapshot.travel:5000/snapshot/nonpms_integrations 1.37
docker.snapshot.travel:5000/snapshot/nonpms_integrations latest
snapshot/nonpms_integrations latest
docker.snapshot.travel:5000/snapshot/base 2.0
docker.snapshot.travel:5000/snapshot/base latest
snapshot/base latest
docker.io/openjdk 8-jre-alpine
registry.fedoraproject.org/fedora 25
```
The next step is to build individual modules. There is a prepared docker image dedicated for this task: _snapshot/dataplatform_builder_
If you want to build only identity module, run the following commands:
```bash
# export $BASE_FOLDER=$path_to_dataplatformcoreqa
# docker run -e ONLY_IDENTITY=true -v $BASE_FOLDER/redeployDP/docker_shared:/data:rw snapshot/dataplatform_builder
```
if you want to build custom IdentityModule branch other than master, then the second command would look like:
```bash
# docker run -e ONLY_IDENTITY=true -e DP_BRANCH_NAME=$BRANCH_NAME -v $BASE_FOLDER/redeployDP/docker_shared:/data:rw snapshot/dataplatform_builder

```
To build not only identity module but also all non-pms integrations modules, the second command is like this:
```bash
# docker run -v $BASE_FOLDER/redeployDP/docker_shared:/data:rw snapshot/dataplatform_builder

```
Unfortunately, there is so far no way to build custom branch of non-pms integrations

Now, the last prerequisite to run data platform with nginx and keycloak is to login to dockerhub. 
Execute the following command
```bash
# docker login
```
please contact someone from IM QA for credentials ;)
After the login is succeeded you can start the docker-based infrastructure:
```bash
# docker-compose -f docker-compose-im-behind-proxy.yaml up
```
or
```bash
# docker-compose -f docker-compose-pure-im.yaml up
```
or 

```bash
# docker-compose up
```
The latter will start IM together with nginx and keycloak together with all non-pms API modules in separate docker containers.
Beware: this last setup is extremely greedy for system resources, please do not use it unless you have at least 8Gb of **free** RAM.

To be able to use setup with keycloak+ngins the following two additional steps are required.
1. Clone the [identity module repo](https://bitbucket.org/bbox/data-platform-identity). Go to data-platform-identity/IdentityModule and execute:
    ```bash
    $ ../gradlew testData -Ddb.url=jdbc:postgresql://localhost:5432/identity -Ddb.password=secret
    ```
    This will populate the identity database with the default entities
2. Go to dataplatformcoreqa/redeployDP/docker_shared/keycloak and execute:
    ```bash
    $ ./init.sh

    ```
After these steps are done you will have nginx listening on port 81 on your localhost and you can use the following credentials to get the oauth token:
```bash
username: admin@snapshot.travel
password: snapshot
client_id: 04000000-1111-4444-8888-000000000001
client_secret: 898d570b-5889-4821-90af-bd33973a57a0
```
The correct CURL looks like this:
```bash
curl -X POST \
  http://localhost:81/oauth/token \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/x-www-form-urlencoded' \
  -H 'postman-token: 32235ff5-273b-3eee-1391-953b9dfb31b4' \
  -d 'grant_type=password&client_id=04000000-1111-4444-8888-000000000001&client_secret=898d570b-5889-4821-90af-bd33973a57a0&username=admin%40snapshot.travel&password=snapshot'
```
You can also access keycloak web console [here](http://localhost:8081/auth/)
