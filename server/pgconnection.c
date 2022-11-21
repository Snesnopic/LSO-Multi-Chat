#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "pgconnection.h"




PGconn* dbConnection(PGconn *conn)
{
    //ATTENZIONE: i dati del db sono relativi, modificateli in base al vostro pc
    conn = PQconnectdb("dbname= Ermes host=localhost user=postgres password=admin");
    if(PQstatus(conn) == CONNECTION_BAD)
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

void update(char table [], char attribute [], char condition [], PGconn *conn)
{
    PGresult *res;
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

char ** selectdb(char attributes [], char table [], char condition [], PGconn *conn)
{
    if(conn == NULL)
    {
        printf("Tentata SELECT ma connessione con db assente");
        exit(0);
    }
    PGresult *res;
    char sql[500];
    strcpy(sql, "");
    strcat(sql, "SELECT ");
    strcat(sql, attributes);
    strcat(sql, " FROM ");
    strcat(sql, table);
    /*
    strcat(sql, " WHERE ");
    strcat(sql, condition);
     */
    res = PQexec(conn, sql);
    if(PQresultStatus(res) != PGRES_TUPLES_OK)
    {
        printf("Nessun dato preso\n");
        exit(0);
    }
    int res_count = PQntuples(res);
    int col;
    printf("Numero di record: %d\n", res_count);
    char **selectResult = (char**)malloc(1000 * sizeof(char*));
    for(int i = 0; i < 50; i++)
        selectResult[i] = (char*)malloc(1000 * sizeof(char));
    int cont = 0;
    for(int row = 0; row < res_count; row++)
    {
        for(col = 1; col < 3; col++)
        {
            strcpy(selectResult[cont], PQgetvalue(res, row, col));
            cont++;
        }
        strcpy(selectResult[cont + 1], "-"); //carattere separatore tra una riga della tabella a quella dopo
    }
    PQclear(res);
    return selectResult;
}

void insert(char tableAndColumn [], char data [], PGconn *conn)
{
    // tableAndColumn = table_name(column_1, column_2, ..., column_n)
    // data = "X1, X2, X3, .... , Xn"
    if(conn == NULL)
    {
        printf("Tentata INSERT ma connessione con db assente");
        exit(0);
    }
    char sql[500];
    PGresult *res;
    strcpy(sql, "");
    strcat(sql, "INSERT INTO ");
    strcat(sql, tableAndColumn);
    strcat(sql, " VALUES (");
    strcat(sql, data);
    strcat(sql, ");");
    res = PQexec(conn, sql);
}

void delete(char table_name [], char condition [], PGconn *conn)
{
    // condition = ad esempio: CustomerName = "GiorgioUni"
    if(conn == NULL)
    {
        printf("Tentata DELETE ma connessione con db assente");
        exit(0);
    }
    PGresult *res;
    char sql[500];
    strcpy(sql, "");
    strcat(sql, "DELETE FROM ");
    strcat(sql, table_name);
    strcat(sql, " WHERE ");
    strcat(sql, condition);
    strcat(sql, ";");
    res = PQexec(conn, sql);
}