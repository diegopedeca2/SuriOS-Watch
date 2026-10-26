import time
import sys

import serial


sys.stdout.reconfigure(encoding="utf-8", errors="replace")


with serial.Serial("COM4", 115200, timeout=0.2) as port:
    port.dtr = False
    port.rts = False
    deadline = time.time() + 10
    while time.time() < deadline:
        data = port.read(256)
        if data:
            print(data.decode("utf-8", errors="replace"), end="", flush=True)
