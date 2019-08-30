#!/usr/bin/env python
 
import socket, random, time
 
HOST = "127.0.0.1"
PORT = 9000

movement = ['movenorth', 'movesouth', 'moveeast', 'movewest']

run = True
while run:	# main loop
	sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	sock.connect((HOST, PORT))
	userInput = raw_input()
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
		sock.send(userInput + "\n")
	
	if userInput == 'DATA':	# if we need to receive something, we have to listen for it. Maybe this should be a separate thread?
		data = ''
		data = sock.recv(10240).decode()
		print (data)
	sock.close()


print ("Socket closed")
