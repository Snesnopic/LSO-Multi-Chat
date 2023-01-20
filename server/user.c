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
    queryResult = selectdb("username", "userdata", whereCondition, conn, &placeholder, 1);
    if(queryResult)
        return 0; //caso in cui username OPPURE password sono sbagliati
    else
        return 1; //caso in cui username E password sono corretti
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