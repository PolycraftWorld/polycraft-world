import os, subprocess, socket, json
from pathlib import Path

class PolycraftGym(object):

    def __init__(self, host, port):
        self.host = host
        self.port = port

    def start_client(self, scene):
        # this function is currently broken!
        path = Path(os.getcwd())
        # print(str(path.parent) + '\\gradlew RunClient')
        # os.system('\"' + str(path.parent) + '\\gradlew RunClient' + '\"')
        # subprocess.run([str(path.parent) + '\\gradlew', 'setupDecompWorkspace'])

    def setup_scene(self, scene):
        self.send_command('RESET domain ' + scene)

    def send_command(self, command):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect((self.host, self.port))
        sock.send(command)
        sock.close()

    def step_command(self, command):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect((self.host, self.port))
        sock.send(command)
        data = sock.recv(10240).decode()
        data_dict = json.loads(data)
        print(data_dict)
        sock.close()