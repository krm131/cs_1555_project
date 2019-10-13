DROP TABLE if exists PASSENGER CASCADE;
DROP TABLE if exists TRAIN CASCADE;
DROP TABLE if exists STATION CASCADE;
DROP TABLE if exists ROUTE CASCADE;
DROP TABLE if exists RAIL_LINE CASCADE;
DROP TABLE if exists SCHEDULE;
DROP TABLE if exists RESERVATION;

CREATE TABLE PASSENGER
(
    cid    INT     NOT NULL,
    fname  VARCHAR NOT NULL,
    lname  VARCHAR NOT NULL,
    street VARCHAR,
    city   VARCHAR,
    st_zip VARCHAR,
    email  VARCHAR,
    pno    CHAR(10),
    PRIMARY KEY (cid),
    CONSTRAINT uni UNIQUE (fname, lname, street, city, st_zip, email, pno)
);
--
CREATE TABLE TRAIN
(
    tno      INT,
    tid      VARCHAR NOT NULL,
    type     VARCHAR,
    top_spd  INT,
    no_seats INT,
    ppm      DECIMAL(5, 2),
    PRIMARY KEY (tno)
);

CREATE TABLE STATION
(
    sno         INT     NOT NULL,
    sid         VARCHAR NOT NULL,
    open_hours  TIME,
    close_hours TIME,
    stop_time   INT, --stop time, can be ignored
    street      VARCHAR,
    city        VARCHAR,
    zip         VARCHAR,
    PRIMARY KEY (sno)
);

CREATE TABLE ROUTE
(
    rid   INT NOT NULL,
    snos  int[],
    stops int[],
    PRIMARY KEY (rid)
);

CREATE TABLE SCHEDULE
(
    rid   INT     NOT NULL,
    day   VARCHAR NOT NULL,
    time  TIME    NOT NULL,
    tno   INT,
    PRIMARY KEY (rid, day, time),
    FOREIGN KEY (rid) REFERENCES ROUTE (rid),
    FOREIGN KEY (tno) REFERENCES TRAIN (tno)
);

CREATE TABLE RAIL_LINE
(
    rlid   INT NOT NULL,
    spdlim INT,
    snos   INT[],
    dist   INT[],
    PRIMARY KEY (rlid)
);

CREATE TABLE RESERVATION
(
    cid   int   not null,
    "day" varchar,
    rid   int,
    "time" time,
    tno     int,
    primary key (cid, time, day, rid),
    foreign key (rid) references route (rid),
    foreign key (cid) references passenger (cid),
    foreign key (tno) references TRAIN (tno),
    constraint ca unique (cid, day, rid, time, tno)
);

COPY PASSENGER (cid, fname, lname, street, city, st_zip)
    FROM 'Customers.txt' DELIMITER ';';

COPY TRAIN (tno, tid, type, top_spd, no_seats, ppm)
    FROM 'Trains.txt' DELIMITER ';';

COPY STATION (sno, sid, open_hours, close_hours, stop_time, street, city, zip)
    FROM 'Stations.txt' DELIMITER ';';

COPY ROUTE (rid, snos, stops)
    FROM 'Routes.csv' DELIMITER ';';

COPY SCHEDULE (rid, day, time, tno)
    FROM 'RouteSched.txt' DELIMITER ';';

COPY RAIL_LINE (rlid, spdlim, snos, dist)
    FROM 'RailLines.csv' DELIMITER ';';