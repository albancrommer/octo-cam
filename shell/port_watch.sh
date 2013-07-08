#! /bin/bash
port_open=$(netstat -altnp|grep 12345)
#if [[ -z $port_open ]]; 
#	then 
	msg="\n$(date +'%d/%m/%y %H:%M:%S') opening port";
	echo -e "$msg" >> /tmp/.mydaemon.log;
 	> /tmp/.mydaemon.sock
	nc -l 12345 >> /tmp/.mydaemon.sock  
	wait;
#	else
#	echo -ne "."  >> /tmp/.mydaemon.log;
#fi;
sleep 1
exit;

