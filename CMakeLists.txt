cmake_minimum_required(VERSION 3.23)
project(LSO_Multi_Chat C)

set(CMAKE_THREAD_PREFER_PTHREAD TRUE)
set(THREADS_PREFER_PTHREAD_FLAG TRUE)
find_package(Threads REQUIRED)

set(CMAKE_C_STANDARD 11)

include_directories(server)

add_executable(LSO_Multi_Chat server/server.c server/group.c server/pgconnection.c server/user.c)
find_package(PostgreSQL REQUIRED)

# This line adds both include paths and libraries for Postgres
target_link_libraries(LSO_Multi_Chat PostgreSQL::PostgreSQL)
target_link_libraries(LSO_Multi_Chat Threads::Threads)
target_compile_options(${PROJECT_NAME} PRIVATE
        -Wall -Wextra -Wpedantic
        )
