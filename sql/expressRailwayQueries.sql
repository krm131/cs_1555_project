CREATE OR REPLACE FUNCTION add_customer(fname VARCHAR, lname VARCHAR, street VARCHAR, city VARCHAR, st_zip VARCHAR,
                                        email VARCHAR, pno CHAR(10))
    RETURNS int AS
$$
DECLARE
    J INT;
BEGIN
    J := (select max(cid)
          FROM passenger) + 1;
    INSERT INTO passenger VALUES (J, fname, lname, street, city, st_zip, email, pno);
    RETURN J;
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION getcid(fnamei varchar, lnamei varchar)
    RETURNS table(
        cid int,
        fname varchar,
        lname varchar,
        street varchar,
        city varchar,
        st_zip varchar,
        email varchar,
        pno char(10)
                 ) AS
$$
BEGIN
    if fnamei isnull or fnamei = '' then
        return query select * from passenger as p where p.lname = lnamei;
    end if;
    if lnamei isnull or lnamei = '' then
        return query select * from passenger as p where p.fname = fnamei;
    end if;
    return query select * from passenger as p where p.lname = lnamei and p.fname = fnamei;
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION edit_customer(cide INT, field VARCHAR, val VARCHAR)
    RETURNS void AS
$$
DECLARE
    J VARCHAR;
BEGIN
    J := concat('UPDATE passenger SET ', field, '=''', val, ''' WHERE cid = ', cide);
    EXECUTE (J);
end;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION view_customerc(cidi INT)
    RETURNS TABLE
            (
                cust_id    int,
                fname  varchar,
                lname  varchar,
                street varchar,
                city   varchar,
                st_zip varchar,
                email  varchar,
                pno    char(10)
            ) AS
$$
BEGIN
    RETURN QUERY SELECT *
                 FROM passenger
                 where cid = cidi;
END;
$$ LANGUAGE 'plpgsql';

--CAN IGNORE TIME IT TAKES TO STOP AT A STATION

--Finds route number and time (time at start of route, not at station) given stations and a day
CREATE OR REPLACE FUNCTION find_route(station1 INT, station2 INT, dayi VARCHAR)
    RETURNS table
            (
                route_id int,
                "time"   TIME
            ) AS
$$
BEGIN
    RETURN QUERY SELECT rid, time_at_station(rid, station1, I."time")
                 from (SELECT schedule.rid, schedule."time"
                       FROM schedule
                                NATURAL JOIN (
                           select rid
                           from route) as r
                       WHERE dayi = "day") as I
                 WHERE stop_after_on_route(rid, station1, station2) and seats_available(rid, dayi, I."time")>0;
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION stop_after_on_route(route_no INT, station1 INT, station2 INT)
    RETURNS boolean AS
$$
DECLARE
    J INT[];
BEGIN
    --raise notice '% % %', route_no, station1, station2;
    J := (SELECT stops
          FROM route
          where rid = route_no);
    if not ARRAY [station1] <@ J or not ARRAY [station2] <@ J then
        return false;
    end if;
    if array_position(J, station1) < array_position(J, station2) then
        return true;
    end if;
    return false;
END;
$$ LANGUAGE 'plpgsql';

--counts stops on a route between 2 stations, excluding one (assuming the embarking station doesn't count)
CREATE OR REPLACE FUNCTION count_stops_between_on_route(route_no INT, station1 INT, station2 INT)
    RETURNS INT AS
$$
DECLARE
    J     INT[];
BEGIN
    J := (select r.stops from route as r where rid = route_no);
    return array_position(J, station2) - array_position(J, station1);
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION count_stations_between_on_route(route_no INT, station1 INT, station2 INT)
    RETURNS INT AS
$$
DECLARE
    J     INT[];
BEGIN
    J := (select r.snos from route as r where rid = route_no);
    return array_position(J, station2) - array_position(J, station1);
END;
$$ LANGUAGE 'plpgsql';

create or replace function time_at_station(routei int, station int, start_time time)
    returns time as
$$
DECLARE
    stations int[];
    stationt int[];
    J        float;
    stationd int[];
    spd      int;
    o        int;
    tmp      float;
    spdt     int;
BEGIN
    o := 1;
    if station = (select snos from route where rid = routei)[1] then
        return start_time;
    end if;
    J := 0;
    stations := (select snos from route where rid = routei);
    loop
        exit when o = array_position(stations, station);
        --raise notice 'o: % %', o, array_position(stations, station);
        --raise notice '% %', stations[o], stations[o + 1];
        stationt := (select snos
                     from (select * from rail_line where array_position(snos, stations[o]) is not null) as r
                     where array_position(snos, stations[o + 1]) is not null);
        stationd := (select dist from rail_line where snos @> stationt);
        spd := (select spdlim from rail_line where snos @> stationt);
        spdt := (select top_spd from train natural join schedule where rid = routei and start_time = "time");
        if spdt < spd then
          spd := spdt;
        else
          --raise notice '%>%',spdt, spd;
        end if;
        if array_position(stationt, stations[o]) < array_position(stationt, stations[o + 1]) then
            tmp := stationd[array_position(stationt, stations[o + 1])];
        else
            tmp := stationd[array_position(stationt, stations[o])];
        end if;
        --raise notice 's1: % s2: % dist: % time: %', stations[o], stations[o + 1], tmp, to_char(to_timestamp((tmp / spd * 3600)), 'HH24:MI:SS');
        tmp := tmp / spd;
        J := J + tmp;
        o := o + 1;
    end loop;
    return to_char(to_timestamp((J * 3600)) + start_time, 'HH24:MI:SS');
end;
$$ language 'plpgsql';

CREATE OR REPLACE FUNCTION trains_at_station(station INT, dayi VARCHAR, timei time)
    RETURNS TABLE
            (
                tno INT
            ) AS
$$
BEGIN
    RETURN QUERY (SELECT tno
                  FROM schedule
                           NATURAL JOIN route
                  WHERE not array_position(stops, station) isnull
                    AND "day" = dayi
                    AND time_at_station(rid, station, "time") = timei
    );
END;
$$ LANGUAGE 'plpgsql';

--returns all routes that use more than one rail line
CREATE OR REPLACE FUNCTION multi_rail_routes()
    RETURNS TABLE
            (
                rid        INT,
                rail_count INT
            ) AS
$$
BEGIN
    RETURN QUERY (SELECT r.rid, cast (COUNT(distinct rlid) as int) as rail_count
                  FROM route as r, rail_line
                  WHERE array_length(array(select unnest(r.snos) intersect
						select unnest(rail_line.snos)), 1)>0
                  GROUP BY r.rid
    );
END;
$$ LANGUAGE 'plpgsql';

--returns all stops on specified route
CREATE OR REPLACE FUNCTION route_all_stops(ridi INT)
    RETURNS INT[] AS
$$
BEGIN
    RETURN (SELECT stops
            FROM route
            WHERE ridi = rid
    );
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION crossing_routes()
    RETURNS TABLE
            (
                route1 INT,
                route2 INT
            ) AS
$$
BEGIN
    RETURN QUERY (SELECT r1.rid, r2.rid
                  FROM route AS r1,
                       route AS r2
                  WHERE r1.snos = r2.snos
                    and r1.stops <> r2.stops
    );
END;
$$ LANGUAGE 'plpgsql';

--returns table with stations that all trains pass through at some point
--TODO modify for arrays
CREATE OR REPLACE FUNCTION station_all_trains()
    RETURNS TABLE
            (
                sno INT,
				        sid VARCHAR
            ) AS
$$
BEGIN
    RETURN QUERY (SELECT s1.sno, s1.sid
                  FROM station AS s1
                  WHERE NOT EXISTS((SELECT tno
                                    FROM train)
                                   EXCEPT
                                   (SELECT tno
                                    FROM route
                                             NATURAL JOIN schedule AS s2
                                    WHERE s1.sno = any(stops)))
    );
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION trains_not_using_station(stationi INT)
    RETURNS TABLE
            (
                tno INT
            ) AS
$$
BEGIN
    RETURN QUERY (SELECT test.tno
                  FROM ((SELECT t.tno
                         FROM train as t)
                        EXCEPT
                        (SELECT sc.tno
                         FROM route
                                  NATURAL JOIN schedule as sc
                         WHERE stationi = any(stops))) as test
                  order by test.tno
    );
END;
$$ LANGUAGE 'plpgsql';

create or replace function dist_station(routei int, station int)
    returns int as
$$
DECLARE
    stations int[];
    stationt int[];
    J        float;
    stationd int[];
    spd      int;
    o        int;
    tmp      float;
BEGIN
    o := 1;
    if station = (select snos from route where rid = routei)[0] then
        return 0;
    end if;
    J := 0;
    stations := (select snos from route where rid = routei);
    loop
        exit when o = array_position(stations, station);
        --raise notice 'o: % %', o, array_position(stations, station);
        --raise notice '% %', stations[o], stations[o + 1];
        stationt := (select snos
                     from (select * from rail_line where array_position(snos, stations[o]) is not null) as r
                     where array_position(snos, stations[o + 1]) is not null);
        stationd := (select dist from rail_line where snos @> stationt);
        spd := (select spdlim from rail_line where snos @> stationt);
        if array_position(stationt, stations[o]) < array_position(stationt, stations[o + 1]) then
            tmp := stationd[array_position(stationt, stations[o + 1])];
        else
            tmp := stationd[array_position(stationt, stations[o])];
        end if;
        --raise notice 's1: % s2: % dist: %', stations[o], stations[o + 1], tmp;
        J := J + tmp;
        o := o + 1;
    end loop;
    return J;
end;
$$ language 'plpgsql';

CREATE OR REPLACE FUNCTION price(route int, dayi varchar, timei time, station1 int, station2 int)
    RETURNS int AS
$$
declare
    p int;
BEGIN
    p := (select ppm
          from train
                   natural join schedule
          where route = rid
            and "day" = dayi
            and time_at_station(route, station1, "time") = timei);
    return p * (dist_station(route, station2) - dist_station(route, station1));
END;
$$ LANGUAGE 'plpgsql';


CREATE OR REPLACE FUNCTION get_sched(route int)
    RETURNS table
            (
                train_no int,
                "day"    varchar,
                "time"   time
            ) AS
$$
BEGIN
    return query select tno, sc.day, sc.time from schedule as sc where rid = route;
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION routes_on_day(dayi varchar)
    RETURNS table
            (
                rid   int,
                snos  int[],
                stops int[]
            ) AS
$$
BEGIN
    return query select r.rid, r.snos, r.stops
                 from (select * from schedule where "day" = dayi) as "s"
                          natural join route as r;
END;
$$ LANGUAGE 'plpgsql';
--
--not 100% sure this is right
CREATE OR REPLACE FUNCTION seats_available(ridi int, dayi VARCHAR, timei time)
	RETURNS int AS
$$
DECLARE
	capacity int;
	taken int;
BEGIN
	capacity := (select no_seats from train natural join schedule where rid = ridi and dayi = "day" and timei = "time");
	taken := (select count(distinct cid)
			 from reservation
			 where ridi = rid and dayi="day" and timei = "time");
	return (capacity - taken);
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION time_between_stations(ridi int, station1 int, station2 int, st1_time time, dayi varchar)
  RETURNS time AS
$$
BEGIN
  return time_at_station(ridi, station2, (select time from schedule where st1_time = time_at_station(ridi, station1, "time") and "day" = dayi)) - st1_time;
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION dist_between_stations(ridi int, station1 int, station2 int)
  RETURNS int AS
$$
BEGIN
  return dist_station(ridi, station2) - dist_station(ridi, station1);
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION reserve_seat(cidi int, routei int, dayi varchar, timei time)
  RETURNS void AS
$$
BEGIN
  if seats_available(routei, dayi, timei) <=0 then
    raise exception 'No seats available';
  end if;
  insert into reservation values (cidi, dayi, routei, timei, (select tno from train natural join schedule where rid = routei and dayi = "day" and timei = "time"));
  return;
END;
$$ LANGUAGE 'plpgsql';

--only returns lists of routes, not sure how to return lists of corresponding stations as well
CREATE OR REPLACE FUNCTION find_route_mult(sid1 int, sid2 int, dayi varchar,
								routes OUT int[], route_count OUT int) AS
$$
DECLARE
	pos int;
    temprow RECORD;
BEGIN	
	FOR temprow IN
			select * from find_route(sid1, sid2, dayi)
		LOOP
			pos := array_position((select stops from route where rid = temprow.route_id), sid1);
			routes := array_append(routes, temprow.route_id);
			route_count := route_count + 1;
			routes := find_route_recursive(routes, (select stops from route where rid = temprow.route_id)[pos+1], sid2, dayi);
			route_count := array_length(routes, 1);
		END LOOP;
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION find_route_recursive(routes int[], sid1 int, sid2 int, dayi varchar)
	RETURNS int[] AS
$$
DECLARE
	pos int;
    temprow RECORD;
BEGIN
	if sid1 = sid2 then
	  return routes;
	end if;
	FOR temprow IN
			select * from find_route(sid1, sid2, dayi)
		LOOP
			pos := array_position((select stops from route where rid = temprow.route_id), sid1);
			if temprow.route_id <> routes[array_length(routes, 1)] then
			  routes := array_append(routes, temprow.route_id);
			END IF; 
			routes := find_route_recursive(routes, (select stops from route where rid = temprow.route_id)[pos+1], sid2, dayi);
		END LOOP;
		return null;
END;
$$ LANGUAGE 'plpgsql';

CREATE OR REPLACE FUNCTION stop_percent(perc float)
	RETURNS table(
	rid int
	) AS
$$
BEGIN
	return query (select r.rid from route as r where (cardinality(stops) / cardinality(snos)*100) >= perc);
END;
$$ LANGUAGE 'plpgsql';

--only finds route combos of 2, not really sure what else to do in time
CREATE OR REPLACE FUNCTION multi_route(sid1 int, sid2 int, dayi varchar)
	returns table
			(
				rid1 int,
				rid2 int,
				num_inter int
			) AS
$$
BEGIN
	RETURN QUERY (select r1.route_id, r2.route_id, array_length(array_intersect((select stops from route where rid = r1.route_id),
				(select stops from route where rid = r2.route_id)),1)
				 FROM find_route(sid1,sid2,dayi) as r1, find_route(sid1,sid2,dayi) as r2
				 WHERE r1.route_id != r2.route_id AND array_length(array_intersect((select stops from route where rid = r1.route_id),
				(select stops from route where rid = r2.route_id)),1)>1);
END;
$$ LANGUAGE	'plpgsql';
	
CREATE OR REPLACE FUNCTION array_intersect(a1 int array, a2 int array)
  RETURNS int[] as
$$
declare
    J int[];
BEGIN
    J := (SELECT ARRAY(
        SELECT UNNEST(a1)
        INTERSECT
        SELECT UNNEST(a2)
    ));
    return J;
END;
$$ LANGUAGE 'plpgsql';







