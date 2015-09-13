#include <stdio.h>
#include <stdlib.h>
#include <netdb.h>
#include <netinet/in.h>
#include <string.h>
#include "logica.h"

int main(int argc, char *argv[]) {
    char *resposta = malloc(1024 * sizeof (char));

    int sockfd, newsockfd, portno, clilen;
    char buffer[1024];
    struct sockaddr_in serv_addr, cli_addr;
    int n;

    /* First call to socket() function */
    sockfd = socket(AF_INET, SOCK_STREAM, 0);

    if (sockfd < 0) {
        perror("Erro ao abrir o socket do servidor");
        exit(1);
    }

    /* Initialize socket structure */
    bzero((char *) &serv_addr, sizeof (serv_addr));
    portno = 50002;

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(portno);

    /* Now bind the host address using bind() call.*/
    if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof (serv_addr)) < 0) {
        perror("Erro ao realizar o binding");
        exit(1);
    }

    /* Now start listening for the clients, here process will
     * go in sleep mode and will wait for the incoming connection
     */

    listen(sockfd, 5);
    clilen = sizeof (cli_addr);

    /* Accept actual connection from the client */
    newsockfd = accept(sockfd, (struct sockaddr *) &cli_addr, &clilen);
    if (newsockfd < 0) {
        perror("ERROR on accept");
        exit(1);
    }
    while (1) {
        /* If connection is established then start communicating */
        inicializaDados();
        bzero(buffer, 1024);
        n = read(newsockfd, buffer, 1023);
        if (n < 0) {
            perror("ERROR reading from socket");
            exit(1);
        }
        if (strcmp(buffer, "/q") == 0) {
            n = write(newsockfd, "Finalizando a conexão\n", 18);
            printf("Conexão finalizada...\n");
            break;
        }

        char operacao[4] = {buffer[0], buffer[1], buffer[2], '\0'};
        int op = atoi(operacao);
        switch (op) {
            case 1:
                resposta = op001();
                break;
            case 2:
            {                
                char nivel = buffer[3];
                resposta = op002(nivel);
                break;
            }
        }
        n = write(newsockfd, resposta, strlen(resposta));
        //printf("Mensagem recebida pelo servidor global: %s\n",buffer);

        if (n < 0) {
            perror("Erro ao escrever para o socket");
            exit(1);
        }
    }
    close(newsockfd);
    close(sockfd);
    return 0;
}
