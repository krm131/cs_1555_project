ro = open("RouteSched.csv", "w+")
with open("RouteSched.txt") as rs:
    for line in rs:
        ro.write(line.split('\n')[0] + ";0\n");