### Docker Environment Setup
@(TW)

#### Update Hosts
```
sudo echo "127.0.0.1     mysql consul" >> /etc/hosts
```

#### Create Network
``` bash
docker network create todo
```

#### Consul
``` bash
docker run -d --name consul --network-alias consul --network todo -p8500:8500 consul
```

#### MySQL
``` bash
docker run --name mysql -e MYSQL_ROOT_PASSWORD=password --network todo --network-alias mysql -d mysql:5
docker exec -it mysql mysql -ppassword -e "create schema todo"
docker exec -it mysql mysql -ppassword -e "create schema user"
```

#### Gateway
``` bash
APP=$(basename `pwd`); JAR=`pwd`/build/libs/$APP-0.0.1-SNAPSHOT.jar docker run -d -p8080:8080 --name $APP-`mktemp -d XXXX`  --network todo -v $JAR:/$APP.jar coney/serverjre:8 java -jar /$APP.jar
```

#### TodoService
```
APP=$(basename `pwd`); JAR=`pwd`/build/libs/$APP-0.0.1-SNAPSHOT.jar docker run -d --name $APP-`mktemp -d XXXX`  --network todo -v $JAR:/$APP.jar coney/serverjre:8 java -jar /$APP.jar
```

#### UserService
``` bash
APP=$(basename `pwd`); JAR=`pwd`/build/libs/$APP-0.0.1-SNAPSHOT.jar docker run -d --name $APP-`mktemp -d XXXX`  --network todo -v $JAR:/$APP.jar coney/serverjre:8 java -jar /$APP.jar
```