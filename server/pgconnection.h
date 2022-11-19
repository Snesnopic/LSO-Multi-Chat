#include <postgresql/libpq-fe.h>


PGconn *conn;
PGresult *res;
int res_count;


PGconn* dbConnection();
void dbDeconnection(PGconn *connection);
void update(char table [], char attribute [], char condition []);
char ** selectdb(char attributes [], char table [], char condition []);
void insert(char tableAndColumn [], char data[]);
void delete(char table_name [], char condition []);