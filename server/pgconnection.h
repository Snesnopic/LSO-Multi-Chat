#include <libpq-fe.h>


PGconn *conn;
PGresult *res;
int res_count;


void dbConnection();
void update(char table [], char attribute [], char condition []);
char * selectdb(char attributes [], char table [], char condition []);