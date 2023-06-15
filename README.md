![diagram of project](https://github.com/Shjiwa/-java-filmorate/blob/add-database/ER_diagram.png)

# java-filmorate
A service that works with movie ratings from users and top recommendations for viewing.

###### All CRUD-operations with films / users
>UPDATE films <br>
SET FILM_NAME = 'Avatar', <br>
 DESCRIPTION = 'avatar description', <br>
RELEASE_DATE = '2009-10-17', <br>
DURATION = 162, <br>
RATING = 10, <br>
MPA_RATING_ID = 3 <br>
WHERE FILM_ID = 1;
###### Users can add / delete like to films
###### Users can add / delete friends
###### Users can request shared friends
###### Users can request top-rated films
>SELECT FILMS.*, COUNT(l.FILM_ID) AS film_count <br>
FROM FILMS <br>
LEFT JOIN LIKES AS l ON FILMS.FILM_ID = l.FILM_ID <br>
GROUP BY FILMS.FILM_ID <br>
ORDER BY film_count DESC <br>
LIMIT 10;
