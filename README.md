
mvn clean package -DskipTests

docker-compose up --build

curl http://localhost:9191/producer-app/publish/hola