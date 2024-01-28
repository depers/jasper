#!/bin/bash
nohup java -jar $0 > nohup.log 2>&1 &
nohup java -Djasypt.encryptor.password=password -jar jasper.jar > nohup.log 2>&1 &
