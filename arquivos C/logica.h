#ifndef __LOGICA_H
#define __LOGICA_H
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/*DEFINICOES*/
#define JANELA_ALTURA 600
#define JANELA_LARGURA 600
#define DOCES_TOTAIS 256
#define FATOR_PROBABILIDADE  0.1

#define ABERTURA 0
#define NIVEL_1 1
#define NIVEL_2 2
#define NIVEL_3 3
#define AJUDA 4

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

/*DADOS GERAIS*/
int janelaLargura;
int janelaAltura;
int estadoJogo;
int nivel;
int pontos;
int tempo;
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
int pacManX;
int pacManY;

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
#endif /* __LOGICA_H */
