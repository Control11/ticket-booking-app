SELECT 
	c.name AS customer_name,
	c.surname AS customer_surname,
	s.date AS screening_date,
	s.time AS screening_time,
	STRING_AGG(DISTINCT seat.row || seat.number, ', ') AS reserved_seats,
	r.expiration_date,
	STRING_AGG(DISTINCT t.type, ', ') AS ticket_types
FROM
	Reservation r
JOIN Customer c ON r.customer_id = c.id
JOIN Screening s ON r.screening_id = s.id
JOIN Reservation_seat rs ON r.id = rs.reservation_id
JOIN Seat ON rs.seat_id = seat.id 
JOIN Reservation_ticket rt ON r.id = rt.reservation_id
JOIN Ticket t ON  rt.ticket_id = t.id
GROUP BY
    c.name,
    c.surname,
    s.date,
    s.time,
    r.expiration_date;
