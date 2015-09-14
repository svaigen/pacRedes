#include <stdio.h>
#include <stdlib.h>
#include <netdb.h>
#include <netinet/in.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>

int main(int argc, char *argv[]) {
    int sockfd_local_server, sockfd_global_server, newsockfd_local_server, portno_local_server, portno_global_server, clilen;
    struct sockaddr_in serv_addr_local_server, serv_addr_global_server, cli_addr;
    struct hostent *global_server;
    int n;
    char buffer_local_server[1024], buffer_global_server[1024];

    /*Definicoes e atribuicoes de comunicacao com o servidor global*/
    portno_global_server = 50002;
    sockfd_global_server = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd_global_server < 0) {
        perror("Erro ao abrir o socket com o servidor global");
        exit(1);
    }
    global_server = gethostbyname("127.0.0.1");
    if (global_server == NULL) {
        perror("Host do servidor global desconhecido");
        exit(1);
    }
    bzero((char *) &serv_addr_global_server, sizeof (serv_addr_global_server));
    serv_addr_global_server.sin_family = AF_INET;
    bcopy((char *) global_server->h_addr,
            (char *) &serv_addr_global_server.sin_addr.s_addr,
            global_server->h_length);
    serv_addr_global_server.sin_port = htons(portno_global_server);
    if (connect(sockfd_global_server, (struct sockaddr *) &serv_addr_global_server, sizeof (serv_addr_global_server)) < 0) {
        perror("Não foi possivel estabelecer a conexão entre o servidor local e o global \n");
        exit(1);
    }

    /*Definicoes e atribuicoes de comunicacao desse servidor local com o cliente*/
    sockfd_local_server = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd_local_server < 0) {
        perror("Erro ao abrir o socket do servidor");
        exit(1);
    }
    bzero((char *) &serv_addr_local_server, sizeof (serv_addr_local_server));
    portno_local_server = 50001;
    serv_addr_local_server.sin_family = AF_INET;
    serv_addr_local_server.sin_addr.s_addr = INADDR_ANY;
    serv_addr_local_server.sin_port = htons(portno_local_server);
    if (bind(sockfd_local_server, (struct sockaddr *) &serv_addr_local_server, sizeof (serv_addr_local_server)) < 0) {
        perror("Erro ao realizar o binding");
        exit(1);
    }

    /*Servidor local escuta a porta para ouvir o cliente*/
    listen(sockfd_local_server, 5);
    clilen = sizeof (cli_addr);

    newsockfd_local_server = accept(sockfd_local_server, (struct sockaddr *) &cli_addr, &clilen);
    if (newsockfd_local_server < 0) {
        perror("ERROR on accept");
        exit(1);
    }

    /*Laco de comunicacao*/
    while (1) {
        bzero(buffer_local_server, 1024);
        bzero(buffer_global_server, 1024);

        //Le do cliente
        read(newsockfd_local_server, buffer_local_server, 1023);

        //Verifica se deve encerrar a conexao
        if (strcmp(buffer_local_server, "/q") == 0) {
            strcpy(buffer_global_server, buffer_local_server);
            write(sockfd_global_server, buffer_global_server, strlen(buffer_global_server));
            write(newsockfd_local_server, "Finalizando a conexão\n\0", 23);
            printf("Conexão finalizada...\n");
            break;
        }

        printf("Mensagem repassada: %s\n", buffer_local_server);

        //copia e repassa a mensagem para o servidor global
        strcpy(buffer_global_server, buffer_local_server);
        write(sockfd_global_server, buffer_global_server, strlen(buffer_global_server));
        bzero(buffer_local_server, 1024);
        bzero(buffer_global_server, 1024);
        
        //recebe a resposta do servidor global
        read(sockfd_global_server, buffer_global_server, 1023);
        //printf("buffer global %s\n",buffer_global_server);
        
        /* Repassa a resposta para o cliente */
        strcpy(buffer_local_server, buffer_global_server);
        write(newsockfd_local_server, buffer_local_server, strlen(buffer_local_server));
        //printf("buffer local %s\n",buffer_local_server);
    }
    close(newsockfd_local_server);
    close(sockfd_local_server);
    close(sockfd_global_server);
    return 0;
}
