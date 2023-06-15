DROP ALL OBJECTS;

create table IF NOT EXISTS MPA
(
    MPA_ID   INTEGER auto_increment,
    MPA_NAME CHARACTER VARYING(50) not null,
    constraint MPA_PK
        primary key (MPA_ID)
);

create table IF NOT EXISTS FILMS
(
    FILM_ID       INTEGER auto_increment,
    FILM_NAME     CHARACTER VARYING(50)  not null,
    DESCRIPTION   CHARACTER VARYING(200) not null,
    RELEASE_DATE  DATE                   not null,
    DURATION      INTEGER                not null,
    RATING        INTEGER default 0      not null,
    MPA_RATING_ID INTEGER                not null,
    constraint FILMS_PK
        primary key (FILM_ID),
    constraint FILMS_MPA_MPA_ID_FK
        foreign key (MPA_RATING_ID) references MPA
);

create table IF NOT EXISTS GENRE
(
    GENRE_ID   INTEGER auto_increment,
    GENRE_NAME CHARACTER VARYING(50) not null,
    constraint GENRE_PK
        primary key (GENRE_ID)
);

create table IF NOT EXISTS FILM_GENRE
(
    GENRE_ID INTEGER not null,
    FILM_ID  INTEGER not null,
    constraint FILM_GENRE_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS,
    constraint FILM_GENRE_GENRE_GENRE_ID_FK
        foreign key (GENRE_ID) references GENRE
);

create table IF NOT EXISTS USERS
(
    USER_ID   INTEGER auto_increment,
    USER_NAME CHARACTER VARYING(50),
    LOGIN     CHARACTER VARYING(50) not null,
    EMAIL     CHARACTER VARYING(50) not null,
    BIRTHDAY  DATE                  not null,
    constraint USERS_PK
        primary key (USER_ID)
);

create table IF NOT EXISTS LIKES
(
    LIKE_ID INTEGER auto_increment,
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint LIKES_PK
        primary key (LIKE_ID),
    constraint LIKES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS,
    constraint LIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS
);

create table IF NOT EXISTS FRIENDS
(
    RELATIONSHIP_ID INTEGER auto_increment,
    USER_ID         INTEGER               not null,
    FRIEND_ID       INTEGER               not null,
    STATUS          BOOLEAN default FALSE not null,
    constraint FRIENDS_PK
        primary key (RELATIONSHIP_ID),
    constraint FRIENDS_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS,
    constraint FRIENDS_USERS_USER_ID_FK_2
        foreign key (FRIEND_ID) references USERS
);