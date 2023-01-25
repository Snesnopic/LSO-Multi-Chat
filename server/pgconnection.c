#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "pgconnection.h"



PGconn* dbConnection(PGconn *conn)
{
    //ATTENZIONE: i dati del db sono relativi, modificateli in base al vostro pc
    conn = PQconnectdb("dbname=ErmesChatDB host=localhost user=postgres password=gheovgos");
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

int update(char table [], char attribute [], char condition [], PGconn *conn)
{
    PGresult *res;
    char sql[500];
    strcpy(sql, "");
    if(conn == NULL)
        exit(0);
    strcat(sql, table);
    strcat(sql, " SET ");
    strcat(sql, attribute);
    strcat(sql, " WHERE ");
    strcat(sql, condition);
    if(strcmp(condition, "") != 0)
    {
        strcat(sql, " WHERE ");
        strcat(sql, condition);
    }
    res = PQexec(conn, sql);
    if(PQresultStatus(res) != PGRES_COMMAND_OK)
    {
        printf("Errore UPDATE\n");
        return 0;
    }
    else
    {
        printf("UPDATE riuscito\n");
        return 1;
    }
}

char ** selectdb(char attributes [], char table [], char condition [], PGconn *conn, int *rowsRet, int numberOfTableColumns)
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
    if(strcmp(condition, "") != 0)
    {
        strcat(sql, " WHERE ");
        strcat(sql, condition);
    }
    printf("COMANDO SQL: %s\n", sql);
    res = PQexec(conn, sql);
    if(PQresultStatus(res) != PGRES_TUPLES_OK)
    {
        printf("Errore, Nessun dato preso\n");
        exit(0);
    }
    if(PQresultStatus(res) == PGRES_EMPTY_QUERY)
        return NULL;
    int res_count = PQntuples(res);
    int col;
    printf("Numero di record: %d\n", res_count);

    char **selectResult = (char**)malloc(1000 * sizeof(char*));

    for(int i = 0; i < 100; i++)
        selectResult[i] = (char*)malloc(1000 * sizeof(char));

    int cont = 0;
    for(int row = 0; row < res_count; row++)
    {
        for(col = 0; col <= numberOfTableColumns - 1; col++)
        {
            strcpy(selectResult[cont], PQgetvalue(res, row, col));
            cont++;
        }
    }
    PQclear(res);
    *rowsRet = res_count;
    return selectResult;
}

int insert(char tableAndColumn [], char data [], PGconn *conn)
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
    printf("COMANDO SQL: %s\n", sql);
    res = PQexec(conn, sql);
    if(PQresultStatus(res) != PGRES_COMMAND_OK)
    {
        printf("INSERT non andata a buon fine\n");
        return 0;
    }
    else
    {
        printf("INSERT eseguita con successo\n");
        return 1;
    }
}

int delete(char table_name [], char condition [], PGconn *conn)
{
    //condition dev'essere = "" se non si vuole nessuna condizione nella where
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
    if(strcmp(condition, "") == 0)
    {
        strcat(sql, " WHERE ");
        strcat(sql, condition);
    }
    strcat(sql, ";");
    printf("COMANDO SQL: %s\n", sql);
    res = PQexec(conn, sql);
    if(PQresultStatus(res) != PGRES_COMMAND_OK)
    {
        printf("DELETE non andata a buon fine\n");
        return 0;
    }
    else
    {
        printf("DELETE eseguita con successo\n");
        return 1;
    }
}
