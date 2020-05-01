#!/usr/bin/env python

import socket, random, time, json

HOST = "127.0.0.1"
PORT = 9000

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect((HOST, PORT))

command_stream = [
	"reset domain ../available_tests/hg_nonov.json",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move q",
    "smooth_move q",
    "smooth_turn 90",
    "smooth_move w",
    "smooth_move w",
    "smooth_move e",
    "smooth_move e",
    "smooth_move e",
    "smooth_move e",
    "smooth_move e",
    "smooth_move w",
    "smooth_move d",
    "smooth_move d",
    "smooth_move d",
    "smooth_move d",
    "smooth_move d",
    "smooth_move d",
    "smooth_move d",
    "smooth_move d",
    "smooth_move d",
    "smooth_move d",
    "smooth_move d",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move w",
    "smooth_move q",
    "smooth_move w",
    "smooth_move w",
    "smooth_tilt down",
    "smooth_move w",
    "place_macguffin"
]
reset = False

for command in command_stream:
	sock.send(str.encode(command + '\n'))
	print(command )
	BUFF_SIZE = 4096  # 4 KiB
	data = b''
	while True:
		part = sock.recv(BUFF_SIZE)
		data += part
		if len(part) < BUFF_SIZE:
			# either 0 or end of data
			break
	data_dict = json.loads(data)
	print(data_dict)
	if reset:
		time.sleep(0.25)
	else:
		time.sleep(2)
		reset = True

sock.close()

print("Socket closed")
