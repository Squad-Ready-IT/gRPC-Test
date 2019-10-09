# gRPC-Test
Test client-server console app using gRPC

# Build
1. Clone or download this repo 
2. Maven 3 (skip this step if maven 3 is already installed):
 - Download: http://maven.apache.org/download.cgi
 - Installation: http://maven.apache.org/install.html

3. Open console, move to the project directory and run the following command:
> &nbsp; mvn package

# Run

Open console, move to project directory and run the following commands: 
1. Server (Port: 9091; configuration file - Server/src/main/resources/application.properties):
> &nbsp; java -jar Server/target/server-1.0.0.jar
2. Client (Port: 9092; configuration file - Client/src/main/resources/application.properties)
> &nbsp; java -jar Client/target/client-1.0.0.jar
