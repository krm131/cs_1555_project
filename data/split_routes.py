routes = []
rout = open("Routes.csv", "w+")
tsp = ""
fis = 0
t = ""
ot = ""
with open("Routes.txt") as routef:
    for line in routef:
        if fis == 0:
            fis = 1
        else:
            rout.write('\n')
        tmp: str = line.split(': ')[1].strip()
        tmp = tmp.split(' Stations')[0].strip()
        stat = line.split(': ')[2].strip()
        stat = stat.split(' Stop')[0].strip()
        stop = line.split(': ')[3].strip()
        first = 0
        rout.write(tmp + ';{' + stat + '};{' + stop + '}')
        tsp = tmp
        t = ot
rout.close()
