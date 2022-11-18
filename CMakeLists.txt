cmake_minimum_required(VERSION 3.23)
project(LSO_Multi_Chat C)

set(CMAKE_C_STANDARD 11)

include_directories(server)

add_executable(LSO_Multi_Chat server/server.c)
