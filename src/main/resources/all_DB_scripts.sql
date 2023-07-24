CREATE TABLE Movie (
   id SERIAL PRIMARY KEY NOT NULL,
   title varchar(100)  NOT NULL
);

CREATE TABLE Screening (
   id SERIAL PRIMARY KEY NOT NULL,
   room_number int  NOT NULL,
   movie_id int  NOT NULL,
   date date  NOT NULL,
   time time  NOT NULL
);

CREATE TABLE Customer (
   id SERIAL PRIMARY KEY NOT NULL,
   name varchar(100)  NOT NULL,
   surname varchar(100)  NOT NULL
);

CREATE TABLE Ticket (
   id SERIAL PRIMARY KEY NOT NULL,
   type varchar(7)  NOT NULL,
   price decimal(5,2)  NOT NULL
);

CREATE TABLE Reservation (
   id SERIAL PRIMARY KEY NOT NULL,
   customer_id int  NOT NULL,
   screening_id int  NOT NULL,
   expiration_date timestamp  NOT NULL
);

CREATE TABLE Seat (
   id SERIAL PRIMARY KEY NOT NULL,
   number int  NOT NULL,
   row varchar(2)  NOT NULL
);

CREATE TABLE Reservation_ticket (
   id SERIAL PRIMARY KEY NOT NULL,
   ticket_id int  NOT NULL,
   reservation_id int  NOT NULL
);

CREATE TABLE Reservation_seat (
   id SERIAL PRIMARY KEY NOT NULL,
   reservation_id int  NOT NULL,
   seat_id int  NOT NULL
);

CREATE TABLE Screening_seat (
   id SERIAL PRIMARY KEY NOT NULL,
   screening_id int  NOT NULL,
   seat_id int  NOT NULL,
   status varchar(9)  NOT NULL
);




ALTER TABLE Screening ADD CONSTRAINT Screening_Movie
   FOREIGN KEY (movie_id)
   REFERENCES Movie (id)  
   NOT DEFERRABLE 
   INITIALLY IMMEDIATE
;

ALTER TABLE Reservation ADD CONSTRAINT Screening_Reservation
   FOREIGN KEY (screening_id)
   REFERENCES Screening (id)  
   NOT DEFERRABLE 
   INITIALLY IMMEDIATE
;

ALTER TABLE Reservation ADD CONSTRAINT Reservation_Customer
   FOREIGN KEY (customer_id)
   REFERENCES Customer (id)  
   NOT DEFERRABLE 
   INITIALLY IMMEDIATE
;

ALTER TABLE Reservation_ticket ADD CONSTRAINT Reservation_ticket_Ticket
   FOREIGN KEY (ticket_id)
   REFERENCES Ticket (id)  
   NOT DEFERRABLE 
   INITIALLY IMMEDIATE
;

ALTER TABLE Reservation_ticket ADD CONSTRAINT Reservation_ticket_Reservation
   FOREIGN KEY (reservation_id)
   REFERENCES Reservation (id)  
   NOT DEFERRABLE 
   INITIALLY IMMEDIATE
;

ALTER TABLE Reservation_seat ADD CONSTRAINT Reservation_seat_Reservation
   FOREIGN KEY (reservation_id)
   REFERENCES Reservation (id)  
   NOT DEFERRABLE 
   INITIALLY IMMEDIATE
;

ALTER TABLE Reservation_seat ADD CONSTRAINT Reservation_seat_Seat
   FOREIGN KEY (seat_id)
   REFERENCES Seat (id)  
   NOT DEFERRABLE 
   INITIALLY IMMEDIATE
;

ALTER TABLE Screening_seat ADD CONSTRAINT Room_seat_Seat
   FOREIGN KEY (seat_id)
   REFERENCES Seat (id)  
   NOT DEFERRABLE 
   INITIALLY IMMEDIATE
;

ALTER TABLE Screening_seat ADD CONSTRAINT Screening_seat_Screening
   FOREIGN KEY (screening_id)
   REFERENCES Screening (id)  
   NOT DEFERRABLE 
   INITIALLY IMMEDIATE
;


INSERT INTO Movie (title) VALUES
   ('Avengers: Endgame'),
   ('The Shawshank Redemption'),
   ('The Godfather'),
   ('Pulp Fiction'),
   ('The Dark Knight'),
   ('Fight Club'),
   ('Inception'),
   ('Goodfellas'),
   ('Interstellar'),
   ('The Matrix');

INSERT INTO Customer (name, surname) VALUES
   ('John', 'Doe'),
   ('Jane', 'Smith'),
   ('Michael', 'Johnson'),
   ('Emily', 'Williams'),
   ('David', 'Brown'),
   ('Olivia', 'Jones'),
   ('James', 'Davis'),
   ('Emma', 'Miller'),
   ('Daniel', 'Wilson'),
   ('Sophia', 'Anderson');

INSERT INTO Ticket (type, price) VALUES
   ('ADULT', 25.00),
   ('CHILD', 12.50),
   ('STUDENT', 18.00);
   
INSERT INTO Seat (number, row) VALUES
   (1, 'A'),
   (1, 'B'),
   (1, 'C'),
   
   (2, 'A'),
   (2, 'B'),
   (2, 'C'),
   
   (3, 'A'),
   (3, 'B'),
   (3, 'C'),
   
   (4, 'A'),
   (4, 'B'),
   (4, 'C'),
   
   (5, 'A'),
   (5, 'B'),
   (5, 'C');
   
INSERT INTO Screening (room_number, movie_id, date, time) VALUES
	(1, 1, '2023-10-15', '18:00:00'),
	(2, 3, '2023-10-15', '20:30:00'),
	(3, 2, '2023-10-15', '15:45:00'),
	(1, 5, '2023-10-16', '19:15:00'),
	(2, 4, '2023-10-16', '21:00:00'),
	(1, 1, '2023-10-15', '20:00:00'),
	(2, 3, '2023-10-15', '18:30:00');
   
INSERT INTO Reservation (customer_id, screening_id, expiration_date) VALUES
   (1, 1, '2023-07-15 17:30:00'),
   (2, 2, '2023-07-15 19:30:00'),
   (3, 3, '2023-07-15 14:45:00'),
   (4, 4, '2023-07-16 18:30:00'),
   (5, 5, '2023-07-16 20:30:00'),
   (6, 5, '2023-07-16 16:30:00'),
   (7, 4, '2023-07-15 18:00:00'),
   (8, 3, '2023-07-15 13:15:00'),
   (9, 2, '2023-07-14 15:45:00'),
   (10, 1, '2023-07-12 18:30:00');
   
INSERT INTO Reservation_ticket (reservation_id, ticket_id) VALUES
   (1, 1),
   (2, 2),
   (3, 3),
   (4, 3),
   (5, 2),
   (6, 1),
   (7, 2),
   (8, 1),
   (9, 3),
   (10, 1);

INSERT INTO Reservation_seat (reservation_id, seat_id) VALUES
   (1, 1),
   (2, 2),
   (3, 3),
   (4, 4),
   (5, 5),
   (6, 3),
   (7, 1),
   (8, 2),
   (9, 3),
   (10, 4);
   
INSERT INTO Screening_seat (screening_id, seat_id, status) VALUES
   (1, 1, 'RESERVED'),
   (1, 2, 'AVAILABLE'),
   (1, 3, 'AVAILABLE'),
   (1, 4, 'RESERVED'),
   (1, 5, 'AVAILABLE'),
   
   (2, 1, 'AVAILABLE'),
   (2, 2, 'RESERVED'),
   (2, 3, 'RESERVED'),
   (2, 4, 'AVAILABLE'),
   (2, 5, 'AVAILABLE'),
   
   (3, 1, 'AVAILABLE'),
   (3, 2, 'RESERVED'),
   (3, 3, 'RESERVED'),
   (3, 4, 'AVAILABLE'),
   (3, 5, 'AVAILABLE'),
   
   (4, 1, 'BOUGHT'),
   (4, 2, 'AVAILABLE'),
   (4, 3, 'AVAILABLE'),
   (4, 4, 'RESERVED'),
   (4, 5, 'AVAILABLE'),
   
   (5, 1, 'AVAILABLE'),
   (5, 2, 'AVAILABLE'),
   (5, 3, 'RESERVED'),
   (5, 4, 'AVAILABLE'),
   (5, 5, 'RESERVED'),
   
   (6, 1, 'AVAILABLE'),
   (6, 2, 'AVAILABLE'),
   (6, 3, 'AVAILABLE'),
   (6, 4, 'AVAILABLE'),
   (6, 5, 'AVAILABLE'),

   (7, 1, 'AVAILABLE'),
   (7, 2, 'AVAILABLE'),
   (7, 3, 'AVAILABLE'),
   (7, 4, 'AVAILABLE'),
   (7, 5, 'AVAILABLE');
   

