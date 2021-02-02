from random import randint

print(400)  # объём бочки
print(100)  # воды в бочке

y = 2020
mon = 0
d = 0
h = 0
m = 0
s = 0
z = 0


def ts2str():
    return f"{y:04d}-{(mon+1):02d}-{(d+1):02d}T{h:02d}:{m:02d}:{s:02d}.{z:03d}Z"


def add2ts(i):
    global y, mon, d, h, m, s, z
    z += i
    s += z // 1000
    z %= 1000
    m += s // 60
    s %= 60
    h += m // 60
    m %= 60
    d += h // 24
    h %= 24
    mon += d // 31
    d %= 31
    y += mon // 12
    mon %= 12


for _ in range(100_000):
    D = randint(0, 400) - 200
    add2ts(randint(1, 100_000_000))
    print(f"{ts2str()} - [username] - wanna {('top up' if D >= 0 else 'scoop')} {abs(D)}l")
