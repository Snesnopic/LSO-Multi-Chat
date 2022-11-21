#include <postgresql/libpq-fe.h>


PGconn* dbConnection(PGconn *conn);
void dbDeconnection(PGconn *connection);
void update(char table [], char attribute [], char condition [], PGconn *conn);
char ** selectdb(char attributes [], char table [], char condition [], PGconn *conn);
void insert(char tableAndColumn [], char data[], PGconn *conn);
void delete(char table_name [], char condition [], PGconn *conn);