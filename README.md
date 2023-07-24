# ticket-booking-app

## Requirements
PostgreSQL 15, Java 17, Maven 3.8.6

## Database
Database was made using PostgreSQL. The diagram represents 6 main tables (Movie, Screening, Customer, Ticket, Seat, Reservation) and 3 association tables to reduce many-to-many relations (Reservation_ticket, Reservation_seat, Screening_seat). 

![DB_model](https://github.com/Control11/ticket-booking-app/assets/84398641/91317b5a-6781-49f1-b139-972142d87d16)

DISCLAIMER:

The data included in this file is purely fictional and does not pertain to any specific individual. Any resemblance to real persons is purely coincidental. The names, surnames and any other information presented within this file are entirely random. This data is provided for illustrative purposes only and should not be used or interpreted as representative of any person or entity.

## Usage
First of all, You need to create database. Script to build database structure and fill it with sample data is located at src/main/resources/ directory - all_DB_scripts.sql file. After that, it is necessary to add appropiate configuration to application.properties file. If needed change only those parameters values:

```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/ticket-booking-app
spring.datasource.username=postgres
spring.datasource.password=admin
```

To run this app You should use git clone command or download zip file with this project and extract it. After that open Command Line at project directory and run application using this command:

```bash
mvn spring-boot:run
```

## Endpoints

### Get movies by date request
```bash
localhost:8080/api/movies/2023-10-15
```
This endpoint returns all movies at given date - 2023-10-15.



### Get movies by date and time request
```bash
localhost:8080/api/movies/2023-10-15/16:00
```
This endpoint returns all movies at given date - 2023-10-15 - and after given time - 16:00.


### Get screening info request
```bash
localhost:8080/api/booking/info/4
```
This endpoint returns screening information at given id - 4. 



### Create reservation request
```bash
localhost:8080/api/booking/reservation/guest
```
This endpoint creates reservation for given information in request body. 


### Testing request
To test requests You can use curl_request.bat that contains all 4 request endpoints.

## Business scenario (use case)
1. The user selects the day and the time when he/she would like to see the movie.
2. The system lists movies available in the given time interval - title and screening times.
3. The user chooses a particular screening.
4. The system gives information regarding screening room and available seats.
5. The user chooses seats, and gives the name of the person doing the reservation (name and surname).
6. The system gives back the total amount to pay and reservation expiration time.

## Assumptions
1. The system covers a single cinema with multiple rooms (multiplex).
2. Seats can be booked at latest 15 minutes before the screening begins.
3. Screenings given in point 2. of the scenario should be sorted by title and screening time.
4. There are three ticket types: ADULT (25 PLN), STUDENT (18 PLN), CHILD (12.50 PLN).

## Business requirements
1. name and surname should each be at least three characters long, starting with a capital letter. The surname could consist of two parts separated with a single dash, in this case the second part should also start with a capital letter.
2. reservation applies to at least one seat.
3. There cannot be a single place left over in a row between two already reserved places.
4. The system should properly handle Polish characters

## Additional asumptions
1. Customer's name and surname should only have one capital letter (apart from capital letter after '_' in surname).

