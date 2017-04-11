To use program you need to start main.java first.It will ask you host name,portnumber(21 as default can be editable),username,and password to start connection.After connect button clicked it will start new thread from clientmode.java.This thread will login with user and pass commands then start reading from server.Also it listens inputactions for sending message to server.It checks ftp code of what server says.When you type "pasv" and enter code will return 227 so this thread will start another thread for passive mode.Passive mode takes references from clientmode(which port to use passive mode that given by server).And passive thread starts listening server on spesific port that given by server.

As bonus

I implemented active mode but it only works on localserver because when i send my real ip and opened port to real server i couldnt make port forwarding with java.To use active mode just typing "port" and pressing enter is enough.After this step, program will create new serversocket on your computer with any port that available(to do it i just assigned port number to zero) and send this informations to the ftp server. Then clientmode will start new thread of activemode.This thread will accept ftp server connection as client to created server on computer.Then will listen ftpserver.

Sabri Akkaş
