#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "user.h"


int usernameAndPasswordCheck(char username[], char password[], PGconn* conn)
{
    int placeholder = 0;
    char **queryResult = NULL;
    char whereCondition[100] = " username = '";
    strcat(whereCondition, username);
    strcat(whereCondition, "' AND userpassword = '");
    strcat(whereCondition, password);
    strcat(whereCondition, "'");
    queryResult = selectdb("userid", "userdata", whereCondition, conn, &placeholder, 1);
    return atoi(queryResult[0]);
}


int userRegistration(char username[], char password[], PGconn* conn)
{
    char usernameTmp[250] = "'";
    strcat(usernameTmp, username);
    strcat(usernameTmp, "'");
    char passwordTmp[250] = "'";
    strcat(passwordTmp, password);
    strcat(passwordTmp, "'");
    char insertValues[250] = "";
    strcat(insertValues, usernameTmp);
    strcat(insertValues, ",");
    strcat(insertValues, passwordTmp);
    if(insert("userdata (username, userpassword) ", insertValues ,conn) == 0)
    {
        printf("Username già esistente\n");
        return 0;
    }
    else
    {
        printf("Registrazione effettuata!\n");
        return 1;
    }
}

int usernameCheck(char username[], PGconn *conn)
{
    if(conn == NULL)
    {
        printf("connessione al DB persa o assente\n");
        exit(0);
    }
    int placeholder = 0;
    char whereCondition[200] = " username = '";
    strcat(whereCondition, username);
    strcat(whereCondition, "'");
    char** queryResult = selectdb("userid", "userdata", whereCondition, conn, &placeholder, 1);
    return placeholder;
}

int modificaUsername(char newUsername[], int user_id, PGconn *conn)
{
    if(conn == NULL)
    {
        printf("connessione con DB persa o assente\n");
        exit(0);
    }
    if(usernameCheck(newUsername, conn) != 0)
    {
        printf("Username esistente\n");
        return -1;
    }
    char whereCondition[250];
    strcpy(whereCondition, "");
    strcat(whereCondition, "userid = ");
    char userid[250];
    strcpy(userid, "");
    itoa(user_id, userid);
    strcat(whereCondition, userid);
    char *attributes = (char*)malloc(250*sizeof(char));
    strcat(attributes, "username = '");
    strcat(attributes, newUsername);
    strcat(attributes, "'");
    return update("userdata", attributes, whereCondition, conn);
}

int modificaPassword(char *username, int user_id, PGconn *conn)
{
    if(conn == NULL)
    {
        printf("connessione con DB persa o assente\n");
        exit(0);
    }
    char whereCondition[250];
    strcpy(whereCondition, "");
    strcat(whereCondition, "userid = ");
    char userid[250];
    strcpy(userid, "");
    itoa(user_id, userid);
    strcat(whereCondition, userid);
    char *attributes = (char*)malloc(250*sizeof(char));
    memset(attributes, 0, 250);
    strcat(attributes, "username = '");
    puts(username);
    strcat(attributes, username);
    strcat(attributes, "'");
    puts(attributes);
    return update("userdata", attributes, whereCondition, conn);
}
