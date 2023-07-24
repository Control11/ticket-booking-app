curl localhost:8080/api/movies/2023-10-15
curl localhost:8080/api/movies/2023-10-15/16:00
curl localhost:8080/api/booking/info/4
curl -X POST localhost:8080/api/booking/reservation/guest -H "Content-Type: application/json" -d "{\"screeningId\":1,\"customer\":{\"name\":\"Lucja\",\"surname\":\"Kudrys_Wolska\"},\"seats\":[{\"row\":\"B\",\"number\":1},{\"row\":\"B\",\"number\":2}],\"ticketTypes\":[\"CHILD\",\"ADULT\"]}"