Restaurant Management System - Getting Started
==============================================

This guide provides the necessary steps to configure, build, and run the entire Restaurant Management System microservices application on your local machine using Docker.

Prerequisites
-------------

Before you begin, ensure you have the following installed on your system:

*   **Java 17+**
    
*   **Apache Maven 3.8+**
    
*   **Docker** and **Docker Compose**
    

1\. Configuration Setup
-----------------------

The application uses a .env file to manage all external configurations like database credentials, service URLs, and security secrets.

**a. Create the .env file:** In the root directory of the project, create a file named .env. This file will store all the environment variables.

**b. Populate the .env file:** You will need to add all the required environment variables to this file. This includes database URLs, Kafka connection strings, MinIO credentials, JWT secrets, and email credentials.

2\. Start Infrastructure Services
-------------------------

Before building the applications, start all the background infrastructure services (databases, Kafka, MinIO, etc.). This ensures they are ready for the application services to connect to.

Run this single command from the **root directory** of the project:

```   docker compose up -d *db *cache kafka    ```

3\. Build the Application
-------------------------

Before starting the Docker containers, you must build all the Spring Boot applications using Maven. This compiles the Java code and creates the executable .jar file for each microservice.

Run this single command from the **root directory** of the project:

```   mvn clean install   ```

This command will build all the modules defined in your parent pom.xml file.

4\. Run the Application
-----------------------

Once the Maven build is successful, you can start the entire application stack, including all infrastructure and microservices, with a single Docker Compose command.

Run this command from the **root directory** of the project:

```   docker compose up --build   ```

*   \--build: This flag tells Docker Compose to rebuild the image for each service using the fresh .jar files you created in the previous step.
    

It may take a few seconds for all containers to start up and for the services to register with Eureka. You can view the logs for all services in your terminal. To access the API, send requests to the **API Gateway** at http://localhost:8080.

Shutting Down
-------------

To stop all running containers, press Ctrl + C in the terminal where Docker Compose is running, or run the following command from the project root:

```   docker compose down   ```