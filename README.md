# Device Manager Application

Device Manager Application is a Spring Web application to manage TELECOM devices through a Web Service where the user will be able to perform the following CRUD operations:
- READ
- CREATE
- UPDATE (PUT / PATCH)
- DELETE

## Installation

1. Download the source code [github](https://github.com/GomesRui/global1).
2. Under the main folder, create the .jar file using maven:

```bash
mvn clean package
```

3. Access the web service through the url [http://localhost:8080/devicemanager/](http://localhost:8080/devicemanager/)

## Usage

When the application starts, the database (H2 memory) will be empty so it is important it is filled up properly, using the POST method:
REQUEST: `POST http://localhost:8080/api/v1/devicemanager/
BODY: {
    "name": "S8",
    "brand": "Samsung"
}`

Afterwards, multiple operations can be done:
- READ:
  REQUEST ALL: `GET http://localhost:8080/api/v1/devicemanager/`
  REQUEST ID: `GET http://localhost:8080/api/v1/devicemanager/{id}`
  REQUEST BRAND: `GET http://localhost:8080/api/v1/devicemanager/search?brand={brand}`
- UPDATE:
  REQUEST FULL: `PUT http://localhost:8080/api/v1/devicemanager/{id}
           BODY: {
             "name": "S8",
             "brand": "Samsung"
         }`
  REQUEST PARTIAL: `PATCH http://localhost:8080/api/v1/devicemanager/{id}
           BODY: {
             "name": "S8",
         }`
- DELETE
  REQUEST: `DELETE http://localhost:8080/api/v1/devicemanager/{id}`

To further explore the application functionalities, you can access the swagger UI [http://localhost:8080/api-ui](link) 

## Roadmap

Further unit and integration tests are required;\
Further exception handling;\
UI could be implemented with MVC architecture;

## License

The purpose of this application was to resolve an exercise defined by 1GLOBAL.
