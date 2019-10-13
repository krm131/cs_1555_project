import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
//
//
public class expressRailway {
    private static Scanner s = new Scanner(System.in);

    public static void main(String args[]) throws
            SQLException, ClassNotFoundException, IOException {
        Class.forName("org.postgresql.Driver");
        login();
    }

    private static void login() throws IOException, SQLException {
        while (true) {
            System.out.print("Username: ");
            if (s.nextLine().equals("exit"))
                break;
            System.out.print("Password: ");
            s.nextLine();
            mainMenu();
        }
    }

    private static void mainMenu() throws IOException, SQLException {
        boolean cont = true;
        while (cont) {
            System.out.println("1. Custoper operations");
            System.out.println("2. Trip search/Reserve");
            System.out.println("3. Other searches");
            System.out.println("4. Database operations");
            System.out.println("5. Exit");
            System.out.print("Enter option: ");
            switch (s.nextInt()) {
                case 1:
                    cusOps();
                    break;
                case 2:
                    search();
                    break;
                case 3:
                    other();
                    break;
                case 4:
                    dbop();
                    break;
                case 5:
                    cont = false;
                    s.nextLine();
                    break;
                default:
                    System.out.println("Invalid choice\n");
                    break;
            }
        }
        System.out.println("");
    }

    private static void cusOps() throws SQLException, IOException {
        System.out.println("1. Add customer");
        System.out.println("2. Edit customer data");
        System.out.println("3. View customer data");
        System.out.println("4. Get CID by name");
        System.out.println("5. Back");
        System.out.print("Enter option: ");
        String cmd;
        switch (s.nextInt()) {
            case 1:
                s.nextLine();
                cmd = "select * from add_customer(\n" +
                        "\t\tfname := '";
                System.out.print("First name: ");
                cmd += s.nextLine() + "',\n" +
                        "\t\tlname := '";
                System.out.print("Last name: ");
                cmd += s.nextLine() + "',\n" +
                        "\t\tstreet := '";
                System.out.print("Street: ");
                cmd += s.nextLine() + "',\n" +
                        "\t\tcity := '";
                System.out.print("City: ");
                cmd += s.nextLine() + "',\n" +
                        "\t\tst_zip := '";
                System.out.print("Two letter state code + zip (XX xxxxx(-xxxx)): ");
                cmd += s.nextLine() + "',\n" +
                        "\t\temail := '";
                System.out.print("Email: ");
                cmd += s.nextLine() + "',\n" +
                        "\t\tpno := '";
                System.out.print("Phone number: ");
                cmd += s.nextLine() + "'\n);";
                try {
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement();
                    ResultSet res = s1.executeQuery(cmd);
                    while (res.next()) {
                        System.out.println(res.getInt(1));
                    }
                    conn.close();
                } catch (SQLException e) {
                    System.out.flush();
                    StringWriter w = new StringWriter();
                    PrintWriter pw = new PrintWriter(w);
                    e.printStackTrace(pw);
                    pw.flush();

                    String stackTrace = w.toString();
                    String[] lines = stackTrace.split("\n");
                    System.out.println(lines[0]);
                    System.out.println(lines[1]);
                    System.out.println(lines[2]);

                    System.out.flush();
                    w.close();
                    pw.close();
                }
                break;
            case 2:
                System.out.print("Customer ID: ");
                cmd = "select * from edit_customer(cide := " + s.nextInt() + ", field := '";
                System.out.print("Field to edit: ");
                s.nextLine();
                cmd += s.nextLine().toLowerCase() + "', val := '";
                System.out.print("Value: ");
                cmd += s.nextLine() + "');";
                try {
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement();
                    s1.executeQuery(cmd);
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Failed to edit customer");
                }
                break;
            case 3:
                System.out.print("Customer ID: ");
                cmd = "select * from view_customerc(cidi := " + s.nextInt() + ");";
                try {
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement();
                    ResultSet res = s1.executeQuery(cmd);
                    while (res.next()) {
                        System.out.println("First name: " + res.getString("fname"));
                        System.out.println("Last name: " + res.getString("lname"));
                        System.out.println("Street: " + res.getString("street"));
                        System.out.println("City: " + res.getString("city"));
                        System.out.println("State Zip: " + res.getString("st_zip"));
                        System.out.println("Email: " + res.getString("email"));
                        System.out.println("Phone Number: " + res.getString("pno"));
                    }
                    conn.close();
                } catch (SQLException e) {
                    System.out.flush();
                    e.printStackTrace(System.out);
                    System.out.flush();
                }
                break;
            case 4:
                System.out.println("Either first name or last name can be blank, but not both");
                s.nextLine();
                System.out.print("First name: ");
                cmd = "select * from getcid('" + s.nextLine() + "', '";
                System.out.print("Last name: ");
                cmd += s.nextLine() + "');";
                try {
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement();
                    ResultSet res = s1.executeQuery(cmd);
                    while (res.next()) {
                        System.out.println();
                        System.out.println("CID: " + res.getString("cid"));
                        System.out.println("First name: " + res.getString("fname"));
                        System.out.println("Last name: " + res.getString("lname"));
                        System.out.println("Street: " + res.getString("street"));
                        System.out.println("City: " + res.getString("city"));
                        System.out.println("State Zip: " + res.getString("st_zip"));
                        System.out.println("Email: " + res.getString("email"));
                        System.out.println("Phone Number: " + res.getString("pno"));
                    }
                    conn.close();
                } catch (SQLException e) {
                    System.out.flush();
                    e.printStackTrace(System.out);
                    System.out.flush();
                }
                break;
            default:
                break;
        }
        System.out.println("");
    }

    private static void search() throws IOException {
        System.out.println("1. Single route search");
        System.out.println("2. Combination route search");
        System.out.println("3. Reserve seats");
        System.out.println("4. Back");
        System.out.print("Enter option: ");
        int st1, s2;
        String day;
        int choice;
        String cmd;
        switch (s.nextInt()) {
            case 1:
                System.out.println("1. Simple search");
                System.out.println("2. Order by stops asc.");
                System.out.println("3. Order by stations desc. ");
                System.out.println("4. Order by price asc.");
                System.out.println("5. Order by price desc.");
                System.out.println("6. Order by trip time asc.");
                System.out.println("7. Order by trip time desc.");
                System.out.println("8. Order by trip distance asc.");
                System.out.println("9. Order by trip distance desc. ");
                System.out.print("Enter option: ");
                choice = s.nextInt();
                cmd = "select route_id, to_char(to_timestamp(10 * 3600) + \"time\", 'HH24:MI:SS') as timeo from find_route(station1 := ";
                System.out.print("From station: ");
                cmd += (st1 = s.nextInt()) + ", station2 := ";
                System.out.print("To station: ");
                cmd += (s2 = s.nextInt()) + ", dayi := '";
                s.nextLine();
                System.out.print("Day: ");
                cmd += (day = s.nextLine()) + "') where not \"time\" isnull"; //some routes do not follow rail lines and so have null times
                System.out.println("");
                switch (choice) {
                    case 1: //unordered search
                        cmd += ";";
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route ID: " + res.getString("route_id"));
                                System.out.println("Departure time: " + res.getString("timeo"));
                                if (count == 10) {
                                    System.out.print("m for more results, q to exit to menu: ");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
                    case 2: //fewest stops
                        cmd += " order by sb;";
                        cmd = cmd.replace("timeo from", "timeo, count_stops_between_on_route(route_id, " + st1 + ", " + s2 + ") as sb from");
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route ID: " + res.getString("route_id"));
                                System.out.println("Departure time: " + res.getString("timeo"));
                                System.out.println("Stops: " + res.getString("sb"));
                                if (count == 10) {
                                    System.out.println("m for more results, q to exit to menu");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
                    case 3: //most stops
                        cmd += " order by sb desc;";
                        cmd = cmd.replace("timeo from", "timeo, count_stations_between_on_route(route_id, " + st1 + ", " + s2 + ") as sb from");
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route ID: " + res.getString("route_id"));
                                System.out.println("Departure time: " + res.getString("timeo"));
                                System.out.println("Stops: " + res.getString("sb"));
                                if (count == 10) {
                                    System.out.println("m for more results, q to exit to menu");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
                    case 4:
                        cmd += " order by p;";
                        cmd = cmd.replace("timeo from", "timeo, price(route_id, '" + day + "', \"time\", " + st1 + ", " + s2 + ") as p from");
                        //System.out.println(cmd);
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route ID: " + res.getString("route_id"));
                                System.out.println("Departure time: " + res.getString("timeo"));
                                System.out.println("Price: " + res.getString("p"));
                                if (count == 10) {
                                    System.out.println("m for more results, q to exit to menu");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
                    case 5:
                        cmd += " order by p desc;";
                        cmd = cmd.replace("timeo from", "timeo, price(route_id, '" + day + "', \"time\", " + st1 + ", " + s2 + ") as p from");
                        System.out.println(cmd);
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route ID: " + res.getString("route_id"));
                                System.out.println("Departure time: " + res.getString("timeo"));
                                System.out.println("Price: " + res.getString("p"));
                                if (count == 10) {
                                    System.out.println("m for more results, q to exit to menu");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
                    case 6:
                        cmd += " order by t;";
                        cmd = cmd.replace("timeo from", "timeo, time_between_stations(route_id, " + st1 + ", " + s2 + ", \"time\", '" + day + "') as t from");
                        //System.out.println(cmd);
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route ID: " + res.getString("route_id"));
                                System.out.println("Departure time: " + res.getString("timeo"));
                                System.out.println("Travel time: " + res.getString("t"));
                                if (count == 10) {
                                    System.out.println("m for more results, q to exit to menu");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
                    case 7:
                        cmd += " order by t desc;";
                        cmd = cmd.replace("timeo from", "timeo, time_between_stations(route_id, " + st1 + ", " + s2 + ", \"time\", '" + day + "') as t from");
                        //System.out.println(cmd);
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route ID: " + res.getString("route_id"));
                                System.out.println("Departure time: " + res.getString("timeo"));
                                System.out.println("Travel time: " + res.getString("t"));
                                if (count == 10) {
                                    System.out.println("m for more results, q to exit to menu");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
                    case 8:
                        cmd += " order by d;";
                        cmd = cmd.replace("timeo from", "timeo, dist_between_stations(route_id, " + st1 + ", " + s2 + ") as d from");
                        //System.out.println(cmd);
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route ID: " + res.getString("route_id"));
                                System.out.println("Departure time: " + res.getString("timeo"));
                                System.out.println("Distance: " + res.getString("d"));
                                if (count == 10) {
                                    System.out.println("m for more results, q to exit to menu");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
                    case 9:
                        cmd += " order by d desc;";
                        cmd = cmd.replace("timeo from", "timeo, dist_between_stations(route_id, " + st1 + ", " + s2 + ") as d from");
                        //System.out.println(cmd);
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route ID: " + res.getString("route_id"));
                                System.out.println("Departure time: " + res.getString("timeo"));
                                System.out.println("Distance: " + res.getString("d"));
                                if (count == 10) {
                                    System.out.println("m for more results, q to exit to menu");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
                    default:
                        System.out.println("invalid input");
                        break;
                }
                break;
            case 2:
                System.out.println("1. Simple search");
                System.out.println("2. Order by stops asc.");
                System.out.println("3. Order by stops desc. ");
                System.out.println("4. Order by price asc.");
                System.out.println("5. Order by price desc.");
                System.out.println("6. Order by trip time asc.");
                System.out.println("7. Order by trip time desc.");
                System.out.println("8. Order by trip distance asc.");
                System.out.println("9. Order by trip distance desc. ");
                System.out.print("Enter option: ");
                choice = s.nextInt();
                cmd = "select * from find_route_mult(";
                System.out.print("From station: ");
                cmd += (st1 = s.nextInt()) + ", ";
                System.out.print("To station: ");
                cmd += (s2 = s.nextInt()) + ", cast ('";
                s.nextLine();
                System.out.print("Day: ");
                cmd += (day = s.nextLine()) + "' as varchar));"; //some routes do not follow rail lines and so have null times
                System.out.println("");
                switch (choice) {
                    case 1: //unordered search
                        cmd += ";";
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route Count: " + res.getString("route_count"));
                                System.out.println("Routes " + res.getString("routes"));
                                if (count == 10) {
                                    System.out.print("m for more results, q to exit to menu: ");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
					case 2: //fewest stops
                        cmd += " order by sb;";
                        cmd = cmd.replace("timeo from", "timeo, count_stops_between_on_route(route_id, " + st1 + ", " + s2 + ") as sb from");
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route count: " + res.getString("route_count"));
                                System.out.println("Routes " + res.getString("routes"));
                                System.out.println("Stops: " + res.getString("sb"));
                                if (count == 10) {
                                    System.out.println("m for more results, q to exit to menu");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
					case 3: //most stops
                        cmd += " order by sb desc;";
                        cmd = cmd.replace("timeo from", "timeo, count_stations_between_on_route(route_id, " + st1 + ", " + s2 + ") as sb from");
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route count: " + res.getString("route_count"));
                                System.out.println("Routes: " + res.getString("routes"));
                                System.out.println("Stops: " + res.getString("sb"));
                                if (count == 10) {
                                    System.out.println("m for more results, q to exit to menu");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
					case 4:
                        cmd += " order by p;";
                        cmd = cmd.replace("timeo from", "timeo, price(route_id, '" + day + "', \"time\", " + st1 + ", " + s2 + ") as p from");
                        //System.out.println(cmd);
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route count: " + res.getString("route_count"));
                                System.out.println("Routes: " + res.getString("routes"));
                                System.out.println("Price: " + res.getString("p"));
                                if (count == 10) {
                                    System.out.println("m for more results, q to exit to menu");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
                    case 5:
                        cmd += " order by p desc;";
                        cmd = cmd.replace("timeo from", "timeo, price(route_id, '" + day + "', \"time\", " + st1 + ", " + s2 + ") as p from");
                        System.out.println(cmd);
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route count: " + res.getString("route_count"));
                                System.out.println("Routes: " + res.getString("routes"));
                                System.out.println("Price: " + res.getString("p"));
                                if (count == 10) {
                                    System.out.println("m for more results, q to exit to menu");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
                    case 6:
                        cmd += " order by t;";
                        cmd = cmd.replace("timeo from", "timeo, time_between_stations(route_id, " + st1 + ", " + s2 + ", \"time\", '" + day + "') as t from");
                        //System.out.println(cmd);
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route count: " + res.getString("route_count"));
                                System.out.println("Routes: " + res.getString("routes"));
                                System.out.println("Travel time: " + res.getString("t"));
                                if (count == 10) {
                                    System.out.println("m for more results, q to exit to menu");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
                    case 7:
                        cmd += " order by t desc;";
                        cmd = cmd.replace("timeo from", "timeo, time_between_stations(route_id, " + st1 + ", " + s2 + ", \"time\", '" + day + "') as t from");
                        //System.out.println(cmd);
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route count: " + res.getString("route_count"));
                                System.out.println("Routes: " + res.getString("routes"));
                                System.out.println("Travel time: " + res.getString("t"));
                                if (count == 10) {
                                    System.out.println("m for more results, q to exit to menu");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
                    case 8:
                        cmd += " order by d;";
                        cmd = cmd.replace("timeo from", "timeo, dist_between_stations(route_id, " + st1 + ", " + s2 + ") as d from");
                        //System.out.println(cmd);
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route count: " + res.getString("route_count"));
                                System.out.println("Routes: " + res.getString("routes"));
                                System.out.println("Distance: " + res.getString("d"));
                                if (count == 10) {
                                    System.out.println("m for more results, q to exit to menu");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
                    case 9:
                        cmd += " order by d desc;";
                        cmd = cmd.replace("timeo from", "timeo, dist_between_stations(route_id, " + st1 + ", " + s2 + ") as d from");
                        //System.out.println(cmd);
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                            Statement s1 = conn.createStatement();
                            ResultSet res = s1.executeQuery(cmd);
                            int count = 0;
                            while (res.next()) {
                                count++;
                                System.out.println("Route count: " + res.getString("route_count"));
                                System.out.println("Routes: " + res.getString("routes"));
                                System.out.println("Distance: " + res.getString("d"));
                                if (count == 10) {
                                    System.out.println("m for more results, q to exit to menu");
                                    if (s.nextLine().equals("q"))
                                        break;
                                    else count = 0;
                                }
                            }
                            conn.close();
                        } catch (SQLException e) {
                            System.out.flush();
                            e.printStackTrace(System.out);
                            System.out.flush();
                        }
                        break;
                    default:
                        System.out.println("invalid input");
                        break;
                }
                break;
            case 3:
                System.out.print("Customer ID: ");
                int cid = s.nextInt();
                String cont = "y";
                while (cont.equals("y")) {
                    System.out.print("Route ID: ");
                    cmd = "select * from reserve_seat(" + cid + ", " + s.nextInt() + ", '";
                    System.out.print("Day: ");
                    s.nextLine();
                    cmd += s.nextLine() + "', '";
                    System.out.print("Time (HH24:MI:SS): ");
                    cmd += s.nextLine() + "');";
                    try {
                        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                        Statement s1 = conn.createStatement();
                        ResultSet res = s1.executeQuery(cmd);
                        conn.close();
                    } catch (SQLException e) {
                        System.out.flush();
                        StringWriter w = new StringWriter();
                        PrintWriter pw = new PrintWriter(w);
                        e.printStackTrace(pw);
                        pw.flush();

                        String stackTrace = w.toString();
                        String[] lines = stackTrace.split("\n");
                        System.out.println(lines[0]);

                        System.out.flush();
                        w.close();
                        pw.close();
                    }
                    System.out.println("Reserve same cid on another route? (y/n) ");
                    cont = s.nextLine();
                }
                break;
            default:
                break;
        }
    }

    private static void other() {
        System.out.println("1. Find trains at station at time and day");
        System.out.println("2. Find routes that use >1 rail line");
        System.out.println("3. Routes with similar but not same stops");
        System.out.println("4. Find stations visited by all trains");
        System.out.println("5. Find trains that do not visit a station");
        System.out.println("6. Find routes that stop at greater than X% of stations");
        System.out.println("7. Display schedule of route");
        System.out.println("8. Find available seats on route at day and time");
        System.out.println("9. Find origin time from station and departure time");
        System.out.println("10. Back");
        System.out.print("Enter option: ");
        switch (s.nextInt()) {
            case 1: //pretty sure there's only ever going to be one
                System.out.print("Station number: ");
                String cmd = "select * from trains_at_station(" + s.nextInt() + ", '";
                s.nextLine();
                System.out.print("Day: ");
                cmd += s.nextLine() + "', '";
                System.out.print("Time: ");
                cmd += s.nextLine() + "');";
                try {
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement();
                    ResultSet res = s1.executeQuery(cmd);
                    conn.close();
                    while (res.next()) {
                        System.out.println(res.getString("tno"));
                    }
                } catch (SQLException e) {
                    System.out.println("could not fetch tables");
                    break;
                }
                break;
            case 2:
                cmd = "select * from multi_rail_routes();";
				try {
					Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
					Statement s1 = conn.createStatement();
					ResultSet res = s1.executeQuery(cmd);
					int count = 0;
					s.nextLine();
					while (res.next()) {
						count++;
						System.out.println("Route ID: " + res.getString("rid"));
						System.out.println("Number used rail lines: " + res.getString("rail_count"));
						if (count == 10) {
							System.out.println("m for more results, q to exit to menu");
							if (s.nextLine().equals("q"))
								break;
							else count = 0;
						}
					}
					conn.close();
				} catch (SQLException e) {
					System.out.flush();
					e.printStackTrace(System.out);
					System.out.flush();
				}
                break;
            case 3:
                try {
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement();
                    ResultSet res = s1.executeQuery("select * from crossing_routes()");
                    conn.close();
                    while (res.next()) {
                        System.out.println("Route: " + res.getString("route1") + " similar to: " + res.getString("route2"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                    break;
                }
                break;
            case 4:
                cmd = "select * from station_all_trains();";
				try {
					Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
					Statement s1 = conn.createStatement();
					ResultSet res = s1.executeQuery(cmd);
					int count = 0;
					s.nextLine();
					while (res.next()) {
						count++;
						System.out.println("Station: " + res.getString("sid"));
						if (count == 10) {
							System.out.println("m for more results, q to exit to menu");
							if (s.nextLine().equals("q"))
								break;
							else count = 0;
						}
					}
					conn.close();
				} catch (SQLException e) {
					System.out.flush();
					e.printStackTrace(System.out);
					System.out.flush();
				}
                break;
            case 5:
				System.out.println("Station Number: ");
				cmd = "select * from trains_not_using_station(" + s.nextInt() + ");";
				s.nextLine();
				try {
					Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
					Statement s1 = conn.createStatement();
					ResultSet res = s1.executeQuery(cmd);
					int count = 0;
					while (res.next()) {
						count++;
						System.out.println("Train: " + res.getString("tno"));
						if (count == 10) {
							System.out.println("m for more results, q to exit to menu");
							if (s.nextLine().equals("q"))
								break;
							else count = 0;
						}
					}
					conn.close();
				} catch (SQLException e) {
					System.out.flush();
					e.printStackTrace(System.out);
					System.out.flush();
				}
                break;
            case 6:
                try {
                    System.out.print("Percent (0-100): ");
                    float r = s.nextFloat();
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement();
                    ResultSet res = s1.executeQuery("select * from stop_percent(" + r + ")");
                    conn.close();
                    while (res.next()) {
                        System.out.println("Route: " + res.getInt("rid"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                    break;
                }
                break;
            case 7:
                try {
                    System.out.print("Route ID: ");
                    int r = s.nextInt();
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement();
                    ResultSet res = s1.executeQuery("select * from get_sched(" + r + ")");
                    conn.close();
                    while (res.next()) {
                        System.out.println("Train no: " + res.getString("train_no") + " day: " + res.getString("day") + " time: " + res.getString("time"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                    break;
                }
                break;
            case 8:
                try {
                    System.out.print("Route ID: ");
                    String c = "select * from seats_available(" + s.nextInt() + ", '";
                    System.out.print("Day: ");
                    s.nextLine();
                    c += s.nextLine() + "', '";
                    System.out.print("Time (HH24:MI:SS): ");
                    c += s.nextLine() + "');";
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement();
                    ResultSet res = s1.executeQuery(c);
                    conn.close();
                    res.next();
                    System.out.println("Available: " + res.getInt(1));
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                    break;
                }
                break;
            case 9:
                try {
                    System.out.print("Route ID: ");
                    int ri = s.nextInt();
                    System.out.print("Station no: ");
                    int sn = s.nextInt();
                    System.out.print("Time at station: ");
                    s.nextLine();
                    String tim = s.nextLine();
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement();
                    ResultSet res = s1.executeQuery("select \"time\" from schedule where time_at_station("+ri+", "+sn+", \"time\") = '" + tim + "';");
                    conn.close();
                    while(res.next())
                        System.out.println("Time: " + res.getString("time"));
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                    break;
                }
                break;
            default:
                break;
        }
    }

    private static void dbop() throws FileNotFoundException {
        System.out.println("1. Import data to db");
        System.out.println("2. Export data from db");
        System.out.println("3. Delete all data");
        System.out.println("4. Delete all functions");
        System.out.println("5. Reset tables");
        System.out.println("6. Update/reset queries");
        System.out.println("7. Back");
        System.out.print("Enter option: ");
        switch (s.nextInt()) {
            case 1:
                ArrayList<String> tables = new ArrayList<>();
                try {
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement();
                    ResultSet res = s1.executeQuery("select table_name from information_schema.tables as ist where ist.table_schema = 'public';");
                    conn.close();
                    int count = 1;
                    while (res.next()) {
                        String str = res.getString("table_name");
                        tables.add(str);
                        System.out.println(count++ + ". " + str);
                    }
                } catch (SQLException e) {
                    System.out.println("could not fetch tables");
                    break;
                }
                System.out.print("Table to insert into: ");
                int t = s.nextInt();
                String table = tables.get(t - 1);
                String[] columns;
                try {
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet res = s1.executeQuery("select column_name from information_schema.columns where table_name = '" + table + "';");
                    res.last();
                    int count = res.getRow();
                    res.beforeFirst();
                    columns = new String[count];
                    System.out.println("If a column does not appear in the data to be imported, enter -1.  Starts at 0");
                    int max = -1;
                    while (res.next()) {
                        String str = res.getString("column_name");
                        System.out.print("Position of " + str + ": ");
                        boolean val = false;
                        while (!val) {
                            int pos = s.nextInt();
                            if (pos < 0) {
                                val = true;
                            } else if (pos > count - 1 || columns[pos] != null) {
                                System.out.print("Invalid position.  Position of column: ");
                            } else {
                                columns[pos] = res.getString("column_name");
                                if (pos > max)
                                    max = pos;
                                val = true;
                            }
                        }
                    }
                    for (int i = 0; i <= max; i++) {
                        if (columns[i] == null) {
                            System.out.println("ERROR: gap in data");
                            return;
                        }
                    }
                    String collist = " (";
                    for (String col : columns) {
                        collist += col + ",";
                    }
                    collist = collist.substring(0, collist.length() - 1) + ")";
                    //System.out.println(collist);
                    System.out.print("Delimiter: ");
                    s.nextLine();
                    String delim = s.nextLine();
                    System.out.print("Absolute path to data: ");
                    String cmd = "copy " + table + collist + " from '" + s.nextLine() + "' delimiter '" + delim + "';";
                    try {
                        s1.executeUpdate(cmd);
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace(System.out);
                        conn.close();
                        break;
                    }
                } catch (SQLException e) {
                    System.out.println("could not fetch tables");
                    e.printStackTrace(System.out);
                    break;
                }
                break;
            case 2:
                tables = new ArrayList<>();
                try {
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement();
                    ResultSet res = s1.executeQuery("select table_name from information_schema.tables as ist where ist.table_schema = 'public';");
                    conn.close();
                    int count = 1;
                    while (res.next()) {
                        String str = res.getString("table_name");
                        tables.add(str);
                        System.out.println(count++ + ". " + str);
                    }
                } catch (SQLException e) {
                    System.out.println("could not fetch tables");
                    break;
                }
                System.out.print("Table to export: ");
                t = s.nextInt();
                s.nextLine();
                table = tables.get(t - 1);
                System.out.print("Absolute path of destination file: ");
                String cmd = "copy " + table + " to '" + s.nextLine() + "' delimiter '";
                System.out.print("Delimiter: ");
                cmd += s.nextLine() + "' csv";
                System.out.print("Header ? (y/n) ");
                if (s.nextLine().equals("y")) {
                    cmd += " header";
                }
                cmd += ";";
                System.out.println(cmd);
                try {
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement();
                    s1.executeUpdate(cmd);
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                }
                break;
            case 3:
                System.out.print("Are you sure? (y/n) ");
                s.nextLine();
                if (!(s.nextLine().equals("y")))
                    return;
                try {
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement();
                    ResultSet res = s1.executeQuery("select table_name from information_schema.tables as ist where ist.table_schema = 'public';");
                    Statement tmp = conn.createStatement();
                    while (res.next()) {
                        try {
                            tmp.executeUpdate("truncate " + res.getString("table_name") + " cascade;");
                        } catch (SQLException e) {
                            e.printStackTrace(System.out);
                        }
                    }
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                }
                break;
            case 4:
                try {
                    System.out.print("Are you sure? (y/n) ");
                    s.nextLine();
                    if (!(s.nextLine().equals("y")))
                        return;
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement();
                    s1.executeUpdate("DO\n" +
                            "$do$\n" +
                            "DECLARE\n" +
                            "   _sql text;\n" +
                            "BEGIN\n" +
                            "\n" +
                            "SELECT INTO _sql\n" +
                            "       string_agg(format('DROP %s %s;'\n" +
                            "                       , CASE prokind\n" +
                            "                           WHEN 'f' THEN 'FUNCTION'\n" +
                            "                           WHEN 'a' THEN 'AGGREGATE'\n" +
                            "                           WHEN 'p' THEN 'PROCEDURE'\n" +
                            "                           WHEN 'w' THEN 'FUNCTION'  -- window function (rarely applicable)\n" +
                            "                          END\n" +
                            "                       , oid::regprocedure)\n" +
                            "                , E'\\n')\n" +
                            "FROM   pg_proc\n" +
                            "WHERE  pronamespace = 'public'::regnamespace  -- schema name here!\n" +
                            ";\n" +
                            "\n" +
                            "IF _sql IS NOT NULL THEN\n" +
                            "   EXECUTE _sql;         -- uncomment payload once you are sure\n" +
                            "ELSE \n" +
                            "   RAISE NOTICE 'No fuctions found in schema %', quote_ident(_schema);\n" +
                            "END IF;\n" +
                            "\n" +
                            "END\n" +
                            "$do$  LANGUAGE plpgsql;");
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                }
                break;
            case 5:
                try {
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement();
                    Scanner fs = new Scanner(expressRailway.class.getResourceAsStream("sql/expressRailway.sql"));
                    fs.useDelimiter(";\n");
                    while (fs.hasNext()) {
                        s1.executeUpdate(fs.next());
                        fs.findInLine(";\n");
                    }
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                }
                break;
            case 6:
                try {
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    Statement s1 = conn.createStatement();
                    Scanner fs = new Scanner(expressRailway.class.getResourceAsStream("sql/expressRailwayQueries.sql"));
                    fs.useDelimiter("';\n"); //assumes everything is a function ending in plpgsql';
                    while (fs.hasNext()) {
                        s1.executeUpdate(fs.next());
                        fs.findInLine("';\n");
                    }
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                }
                break;
            default:
                break;
        }
    }
}
