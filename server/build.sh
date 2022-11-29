#!/bin/bash

gcc -Wall -pthread server.c group.c pgconnection.c user.c -o server -Iinclude_dir -Llib_dir -lpq