# ticket-booking-app

## Database
Database was made using PostgreSQL 15. The diagram represents 6 main tables (Movie, Screening, Customer, Ticket, Seat, Reservation) and 3 association tables to reduce many-to-many relations (Reservation_ticket, Reservation_seat, Screening_seat). 

![DB_model](https://github.com/Control11/ticket-booking-app/assets/84398641/e29b0e44-159f-4831-b206-20fd03d23d76)

Script to build database structure and fill it with sample data is located at src/main/resources/ directory - DB_create_insert_script.sql file.

DISCLAIMER:

The data included in this file is purely fictional and does not pertain to any specific individual. Any resemblance to real persons is purely coincidental. The names, surnames and any other information presented within this file are entirely random. This data is provided for illustrative purposes only and should not be used or interpreted as representative of any person or entity.
