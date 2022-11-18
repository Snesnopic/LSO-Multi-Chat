#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "pgconnection.h"



PGconn* dbConnection()
{
    //Attenzione: i dati del db non sono giusti
    conn = PQconnectdb("dbname= ermesChat host=localhost user=gheogvos password=nonloso");
    if(PQstatus(conn) = CONNECTION_BAD)
    {
        printf("Connessione al db non riuscita\n");
        exit(0);
    }
    else{
        printf("Connessione al db riuscita\n");
        return conn;
    }
}

void dbDeconnection(PGconn *connection)
{
    PQfinish(connection);
}

void update(char table [], char attribute [], char condition [])
{
    if(conn == NULL)
    {
        printf("Tentato UPDATE ma connessione con db assente");
        exit(0);
    }
    char sql[500];
    strcpy(sql, "");
    strcat(sql, "UPDATE ");
    strcat(sql, table);
    strcat(sql, " SET ");
    strcat(sql, attribute);
    strcat(sql, " WHERE ");
    strcat(sql, condition);
    res = PQexec(conn, sql);
}

char ** selectdb(char attributes [], char table [], char condition [])
{
    if(conn == NULL)
    {
        printf("Tentata SELECT ma connessione con db assente");
        exit(0);
    }
    char sql[500];
    strcpy(sql, "");
    strcat(sql, "SELECT ");
    strcat(sql, attributes);
    strcat(sql, " FROM ");
    strcat(sql, table);
    strcat(sql, " WHERE ");
    strcat(sql, condition);
    res = PQexec(conn, sql);
    if(PQresultStatus(res) != PGRES_TUPLES_OK)
    {
        printf("Nessun dato preso\n");
        exit(0);
    }
    res_count = PQntuples(res);
    printf("Numero di record: %d\n", res_count);
    char **selectResult = (char**)malloc(100 * sizeof(char*));
    for(int i = 0; i < 50; i++)
        selectResult[i] = (char*)malloc(100*sizeof(char ));
    for(int row = 0; row < res_count; row++)
    {
        for(col = 0; col < 5; col++)
        {
            strcpy(selectResult[row], PQgetvalue(res, row, col));
        }
    }
    PQclear(res);
    return selectResult;
}