#include <postgresql/libpq-fe.h>



/*
rowsRet serve per restituire al chiamante il numero di righe della select
condition (qual ora ci fosse tra i parametri) DEVE essere = "" (stringa costante vuota) se non si vuole niente nella WHERE
*/


PGconn* dbConnection(PGconn *conn);
void dbDeconnection(PGconn *connection);
void update(char table [], char attribute [], char condition [], PGconn *conn);
char ** selectdb(char attributes [], char table [], char condition [], PGconn *conn, int *rowsRet);
void insert(char tableAndColumn [], char data[], PGconn *conn);
void delete(char table_name [], char condition [], PGconn *conn);