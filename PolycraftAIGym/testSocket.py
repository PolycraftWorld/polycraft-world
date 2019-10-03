#!/usr/bin/env python
 
import socket, random, time, json
 
HOST = "127.0.0.1"
PORT = 9000

movement = ['movenorth', 'movesouth', 'moveeast', 'movewest']

run = True
while run:	# main loop
	sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	sock.connect((HOST, PORT))
	userInput = input()
	if userInput == 'exit':	# wait for user input commands
		run = False
	elif userInput == 'wonder':	# testing automatic commands
		count = 200
		while count > 0:
			sock.send(random.choice(movement))
			sock.close()	# socket must be closed between after command
			time.sleep(0.5)
			sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			sock.connect((HOST, PORT))
			count = count - 1
	else:
		sock.sendall(str.encode(userInput))
	
	if userInput.startswith('DATA'):	# if we need to receive something, we have to listen for it. Maybe this should be a separate thread?
		data = ''
		data = sock.recv(10240).decode()
		data_dict = json.loads(data)
		print (data_dict)
	sock.close()


print ("Socket closed")
