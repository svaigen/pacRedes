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

    sockfd = socket(AF_INET, SOCK_STREAM, 0);

    if (sockfd < 0) {
        perror("Erro ao abrir o socket do servidor");
        exit(1);
    }

    bzero((char *) &serv_addr, sizeof (serv_addr));
    portno = 50002;

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(portno);

    if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof (serv_addr)) < 0) {
        perror("Erro ao realizar o binding");
        exit(1);
    }

    listen(sockfd, 5);
    clilen = sizeof (cli_addr);

    /* Accept actual connection from the client */
    newsockfd = accept(sockfd, (struct sockaddr *) &cli_addr, &clilen);
    if (newsockfd < 0) {
        perror("ERROR on accept");
        exit(1);
    }
    inicializaDados();
    while (1) {
        bzero(buffer, 1024);
        n = read(newsockfd, buffer, 1023);
        printf("Mensagem recebida: %s\n", buffer);
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
            case 3:
            {
                char teclaPressionada[3] = {buffer[3], buffer[4], '\0'};
                resposta = op003(teclaPressionada); 
                break;
            }
            case 4:
                resposta = op004();
                break;
            case 5:
                resposta = op005();
                break;
            case 6:
            {
                char teclaPressionada[3] = {buffer[3], buffer[4], '\0'};
                resposta = op006(teclaPressionada);
                break;
            }
            case 7:
            {
                float x;
                float y;
                int direcaoPretendidaLivre;
                int lixo;
                sscanf(buffer, "%d#%f#%f#%d\n", &lixo, &x, &y, &direcaoPretendidaLivre);
                resposta = op007(x, y, direcaoPretendidaLivre);
                break;
            }
            case 8:
                resposta = op008();
                break;
            case 9:
                resposta = op009(buffer[3], buffer[4]);
                break;
            case 10:
            {
                int x;
                int y;
                int id;
                int lixo;
                sscanf(buffer, "%d#%d#%d#%d\n", &lixo, &id, &x, &y);
                resposta = op010(id, x, y);
                break;
            }
            case 11:
            {
                float x;
                float y;
                int id;
                int lixo;
                sscanf(buffer, "%d#%d#%f#%f\n", &lixo, &id, &x, &y);
                resposta = op011(id, x, y);
                break;
            }
            case 12:
                resposta = op012(buffer[3], buffer[4]);
                break;
            case 13:
                resposta = op013(buffer[3]);
                break;
            case 14:
                resposta = op014(buffer[3]);
                break;
            case 15:
                resposta = op015(buffer[3]);
                break;
            case 16:
                resposta = op016();
                break;
            case 17:
                resposta = op017();
                break;
            case 18:
                resposta = op018();
                break;
            case 19:
                resposta = op019();
                break;
            case 20:
                resposta = op020(buffer[3]);
                break;
            case 21:
                resposta = op021();
                break;
            case 22:
                resposta = op022();
                break;
            case 23:
                resposta = op023();
                break;
        }
        printf("Resposta enviada: %s\n", resposta);
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
