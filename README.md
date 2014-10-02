yyDroid

Setup debug over usb

1. connect android phone through usb, and in developer option, opt in allow debugging
2. look up host machine's ip address
3. update SERVER_URL in Globals to be host machine's IP
4. do "adb forward tcp:8080 tcp:8080" in terminal, and replace 8080 using the real port number that localhost server is on
