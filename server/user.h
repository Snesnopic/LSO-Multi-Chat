#include "pgconnection.h"

typedef struct User
{
    int userId;
    char username[1000];
    char userpassword[1000];
}User;


int usernameAndPasswordCheck(char username[], char password[], PGconn* conn);

int userRegistration(char username[], char password[], PGconn* conn);

int modificaUsername(char newUsername[], PGconn *conn);

int usernameCheck(char username[], int user_id, PGconn *conn);

int modificaPassword(char username[], int user_id, PGconn *conn);
