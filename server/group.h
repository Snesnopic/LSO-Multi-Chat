typedef struct Group
{
    int groupId;
    char[250] groupName;
    struct Group *next;
}Group;

Group* getGroupsOfUsers(int user);

Group* getGroupsNotOfUsers(int user);

int groupListSize(Group* head);