from ubuntu:latest as build

run apt-get update
run apt-get install openjdk-17-jdk -y

copy . .

run apt-get install maven -y
run mvn clean install

expose 8080

copy --from=build /target/todolist-1.0.0.jar app.jar

entrypoint ["java", "-jar", "app.jar"]