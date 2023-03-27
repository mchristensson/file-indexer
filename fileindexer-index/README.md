# Urax mysql db module
This module provides a mysql container based on image `mysql:8`.
> * mysql configuration file
> * baseline sql script ([See also _debezium tutorial_](https://debezium.io/documentation/reference/stable/tutorial.html))
>

The container is typically access from some other container within the same docker network

### Run db container
> The following starts the container from our image
> * Using `-d` for starting in detached mode
> * argument `MYSQL_ROOT_PASSWORD` defines mysql process password 
> * argument `MYSQL_USER` defines jdbc-connection username 
> * argument `MYSQL_PASSWORD` defines jdbc-connection user password 
```
$ docker run -p 3306:3306 -d -e MYSQL_ROOT_PASSWORD=root -e MYSQL_USER=uraxmysqluser -e MYSQL_PASSWORD=uraxmysqlpw urax-uraxdb-1
```


### Connect to the running container using TTY terminal
```
docker exec -it urax-uraxdb-1 bash
```

#### Checking bind address
The bind address have to be 0.0.0.0 (which means "every IP addresses") to work with docker network. 
If you do not have this record in your configuration, you'll have an issue about connecting your docker mysql db container.
```
mysqld --verbose --help | grep bind-address
```

#### Try accessing database
```
sh-4.4# mysql -p {DB_SCHEMA_NAME}
Enter password: {MYSQL_ROOT_PASSWORD}
```


### Add Adminer-image for handshaking and verification
In the `docker-compose.yml` file, you may add the image `Adminer` in order to inspect the MySql container.
```
  adminer:
    image: adminer
    restart: always
    ports:
      - 9090:8080
```

