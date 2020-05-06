import subprocess
import sys


def execute(command):
    pal_client_process = subprocess.Popen(command, shell=True, cwd='../', stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    count = 0
    # Poll process for new output until finished
    while True:
        nextline = pal_client_process.stdout.readline()
        if nextline == '' and pal_client_process.poll() is not None:
            break
        if len(str(nextline)) > 3:
            sys.stdout.write(str(nextline) + '\n')
            sys.stdout.flush()
        # count += 1
        # if count > 25:
        #     break

    output = pal_client_process.communicate()[0]
    exitCode = pal_client_process.returncode

    if (exitCode == 0):
        return output
    else:
        raise subprocess.CalledProcessError(exitCode, command)
    # popen = subprocess.Popen(cmd, cwd='../', stdout=subprocess.PIPE, universal_newlines=True, shell=True)
    # for stdout_line in iter(popen.stdout.readline, ""):
    #     yield stdout_line
    # popen.stdout.close()
    # return_code = popen.wait()
    # if return_code:
    #     raise subprocess.CalledProcessError(return_code, cmd)


def main():
    #run polycraft client
    print("test")
    #execute("cd ..")
    print("test")
    execute("call gradlew runclient")
    print("test")


if __name__ == "__main__":
    main()


