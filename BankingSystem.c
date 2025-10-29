#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Transaction stack node
typedef struct Transaction {
    char type[16];
    float amount;
    struct Transaction* next;
} Transaction;

// Account node (linked list)
typedef struct Account {
    int acc_no;
    char name[100];
    float balance;
    Transaction *top; // transaction stack
    struct Account* next;
} Account;

Account *head = NULL;

// Stack methods for transaction history per account
void push_trans(Transaction **top, const char *type, float amount) {
    Transaction* t = (Transaction*)malloc(sizeof(Transaction));
    strcpy(t->type, type);
    t->amount = amount;
    t->next = *top;
    *top = t;
}

float pop_trans(Transaction **top, char *type_out) {
    if (*top == NULL)
        return 0;
    Transaction* tmp = *top;
    strcpy(type_out, tmp->type);
    float amt = tmp->amount;
    *top = tmp->next;
    free(tmp);
    return amt;
}

// Linked list methods
Account* find_account(int acc_no) {
    Account* p = head;
    while(p) {
        if(p->acc_no == acc_no) return p;
        p = p->next;
    }
    return NULL;
}

void add_account() {
    int acc_no; char name[100]; float amt;
    printf("Account Number: "); scanf("%d", &acc_no);
    printf("Name: "); scanf("%s", name);
    printf("Initial Deposit: "); scanf("%f", &amt);

    Account *acc = (Account*)malloc(sizeof(Account));
    acc->acc_no = acc_no;
    strcpy(acc->name, name);
    acc->balance = amt;
    acc->next = head;
    acc->top = NULL;
    push_trans(&(acc->top), "Initial", amt);
    head = acc;
    printf("Account added.\n");
}

void deposit() {
    int acc_no; float amt;
    printf("Account Number: "); scanf("%d", &acc_no);
    Account* acc = find_account(acc_no);
    if (!acc) { printf("No such account.\n"); return; }
    printf("Deposit Amount: "); scanf("%f", &amt);
    acc->balance += amt;
    push_trans(&(acc->top), "Deposit", amt);
    printf("Deposit successful. New Balance: %.2f\n", acc->balance);
}

void withdraw() {
    int acc_no; float amt;
    printf("Account Number: "); scanf("%d", &acc_no);
    Account* acc = find_account(acc_no);
    if (!acc) { printf("No such account.\n"); return; }
    printf("Withdraw Amount: "); scanf("%f", &amt);
    if (amt > acc->balance) { printf("Insufficient funds.\n"); return; }
    acc->balance -= amt;
    push_trans(&(acc->top), "Withdraw", amt);
    printf("Withdrawal successful. New Balance: %.2f\n", acc->balance);
}

void show_accounts() {
    Account *p = head;
    printf("\n%-12s%-15s%-10s\n", "ACC_NO", "NAME", "BALANCE");
    while(p) {
        printf("%-12d%-15s%-10.2f\n", p->acc_no, p->name, p->balance);
        p = p->next;
    }
}

void show_transactions() {
    int acc_no;
    printf("Account Number: "); scanf("%d", &acc_no);
    Account* acc = find_account(acc_no);
    if (!acc) { printf("No such account.\n"); return; }

    Transaction* t = acc->top;
    printf("Transaction History for %s:\n", acc->name);
    while(t) {
        printf("%s: %.2f\n", t->type, t->amount);
        t = t->next;
    }
}

void undo_transaction() {
    int acc_no;
    char type[16];
    printf("Account Number: "); scanf("%d", &acc_no);
    Account* acc = find_account(acc_no);
    if (!acc || !(acc->top && acc->top->next)) {
        printf("Nothing to undo (initial deposit cannot be undone).\n");
        return;
    }

    float amt = pop_trans(&(acc->top), type);
    if (strcmp(type, "Deposit") == 0)
        acc->balance -= amt;
    else if (strcmp(type, "Withdraw") == 0)
        acc->balance += amt;
    printf("Undone %s of %.2f. New Balance: %.2f\n", type, amt, acc->balance);
}

void delete_account() {
    int acc_no;
    printf("Account Number: "); scanf("%d", &acc_no);
    Account *p=head, *prev=NULL;
    while(p) {
        if(p->acc_no == acc_no) {
            if(prev) prev->next = p->next;
            else head = p->next;
            while(p->top) {
                char tmptype[16]; pop_trans(&(p->top), tmptype);
            }
            free(p);
            printf("Account deleted.\n");
            return;
        }
        prev = p; p = p->next;
    }
    printf("No such account.\n");
}

int main() {
    int ch;
    do {
        printf("\n1. Add Account\n2. Show All Accounts\n3. Deposit\n4. Withdraw\n5. Show Transactions\n6. Undo Last Transaction\n7. Delete Account\n8. Exit\nChoice: ");
        scanf("%d", &ch);
        switch(ch) {
            case 1: add_account(); break;
            case 2: show_accounts(); break;
            case 3: deposit(); break;
            case 4: withdraw(); break;
            case 5: show_transactions(); break;
            case 6: undo_transaction(); break;
            case 7: delete_account(); break;
        }
    } while(ch != 8);
    return 0;
}