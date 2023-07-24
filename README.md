# ticket-booking-app

## Requirements
PostgreSQL 15, Java 17, Maven 3.8.6

## Database
Database was made using PostgreSQL 15. The diagram represents 6 main tables (Movie, Screening, Customer, Ticket, Seat, Reservation) and 3 association tables to reduce many-to-many relations (Reservation_ticket, Reservation_seat, Screening_seat). 

![DB_model](https://github.com/Control11/ticket-booking-app/assets/84398641/91317b5a-6781-49f1-b139-972142d87d16)

Script to build database structure and fill it with sample data is located at src/main/resources/ directory - DB_create_insert_script.sql file.

DISCLAIMER:

The data included in this file is purely fictional and does not pertain to any specific individual. Any resemblance to real persons is purely coincidental. The names, surnames and any other information presented within this file are entirely random. This data is provided for illustrative purposes only and should not be used or interpreted as representative of any person or entity.


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
