#ifndef __LOGICA_H
#define __LOGICA_H
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/*DEFINICOES*/
#define JANELA_ALTURA 600
#define JANELA_LARGURA 600
#define DOCES_TOTAIS 236
#define FATOR_PROBABILIDADE  0.1

#define ABERTURA 0
#define NIVEL_1 1
#define NIVEL_2 2
#define NIVEL_3 3
#define AJUDA 4
#define VENCEDOR 5

#define ESQUERDA 0
#define CIMA 1
#define DIREITA 2
#define BAIXO 3
#define PARADO 4

#define ESTADO_ABERTURA 0
#define ESTADO_INICIO 1
#define ESTADO_JOGANDO 2
#define ESTADO_PACMAN_MORTO 3
#define ESTADO_FIM 4
#define ESTADO_NIVEL_COMPLETO 5

/*Definicoes Ghosts*/
#define ESTADO_NORMAL  1
#define ESTADO_VULNERAVEL  2
#define ESTADO_OLHOS  3
#define ESTADO_PRESO  4
#define TEMPO_PRESO  100
#define TEMPO_VULNERAVEL  800

#define PONTO_DOCE_PEQUENO 10
#define PONTO_DOCE_GRANDE 50
#define PONTO_GHOST 250
#define PONTO_MORRE -500
#define PONTO_FRUTA 500

/*DADOS GERAIS*/
int janelaLargura;
int janelaAltura;
int estadoJogo;
int nivel;
int pontos;
int tempo;
int tempoInicial;
char *caminhoTiledMap;
char *caminhoMapa;
char *caminhoSprites;
char *resposta;
int teclaLida;
int docesRestantes;
int indiceParedes;
int indicePontosDecisao;
int indiceDoces;

/*DADOS PACMAN*/
int pacManVidas;
int pacManVivo;
int pacManDirecaoAtual;
int pacManDirecaoPretendida;
float pacManVelocidade;
float pacManX;
float pacManY;

/*DADOS GHOSTS*/
struct ghost {
    int estado;
    int direcao;
    float velocidade;
    int tempoSerLivre;
    int tempoInvulneravel;
    int seguePacMan;
    int x;
    int y;
};
typedef struct ghost ghost;
ghost ghosts[4];

/*DADOS FRUTA*/
int frutaVisivel;
float frutaProbabilidade;
int frutaX;
int frutaY;


void inicializaDados();
void inicializaGhosts();
void inicializaPacMan(int vidas);
void inicializaFruta();
void geraRespostaOp002();
char *op001();
char *op002(char nivel);
char *op003(char teclaPressionada[]);
char *op004();
char *op005();
char *op006(char teclaPressionada[]);
char *op007(float x, float y, int dirPretLivre);
char *op008();
char *op009(char id, char direcao);
char *op010(int id, int x, int y);
char *op011(int id, float x, float y);
char *op012(char id, char estado);
char *op013(char id);
char *op014(char id);
char *op015(char id);
char *op016();
char *op017();
char *op018();
char *op019();
char *op020(char doce);
char *op021();
char *op022();
char *op023();
#endif /* __LOGICA_H */
