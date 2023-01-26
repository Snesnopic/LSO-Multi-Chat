#include <postgresql/libpq-fe.h>


typedef struct Group
{
    int groupId;
    int creatorUserId;
    char groupName[200];
}Group;

typedef struct GroupMessage
{
    int userId;
    int groupId;
    char message[1000];
    char timestamp[250];
    char username[250];
}GroupMessage;


typedef struct GroupRequest
{
    char* username;
    int userId;
    int groupId;
}GroupRequest;

Group* getGroupsOfUsers(int user_id, PGconn *conn, int *row);

Group* getGroupsNotOfUsers(int user_id, PGconn *conn, int *row);

Group* getAllGroups(PGconn *conn, int *row);

void itoa(int n, char s[]);

void reverse(char s[]);

GroupMessage* getGroupMessages(int group_id, PGconn *conn, int *row);

int deleteGroup(char groupname[], PGconn* conn);

GroupRequest* getGroupRequests(int group_id, int user_id, PGconn *conn, int *row);

int richiestaGruppo(char group_name[], int user_id, PGconn *conn, int *row);

int messaggioGruppo(char message[], int user_id, int group_id, char *timestamp, PGconn *conn, int *row);

int creaGruppo(char group_name[], int creatorUserId, PGconn *conn);

int modificaNomeGruppo(char newGroupName[], int group_id, PGconn *conn);
