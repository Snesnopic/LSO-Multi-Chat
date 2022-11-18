#include <libpq-fe.h>


PGconn *conn;
PGresult *res;
int res_count;


PGconn* dbConnection();
void dbDeconnection(PGconn *connection);
void update(char table [], char attribute [], char condition []);
char ** selectdb(char attributes [], char table [], char condition []);