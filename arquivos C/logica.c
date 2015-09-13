#include "logica.h"

void insereConcatenaNumero(int n){
    char numConvert[5];
    sprintf(numConvert, "%d", n);
    strcat(resposta, numConvert); 
    strcat(resposta, "#\0");
}

void inicializaDados() {
    resposta = malloc(1024 * sizeof (char));
    janelaAltura = JANELA_ALTURA;
    janelaLargura = JANELA_LARGURA;
    estadoJogo = 0;
    nivel = 0;
    pontos = 0;
    tempo = 0;
    caminhoTiledMap = "maps/inicio.tmx";
    caminhoSprites = "sprites/sprites.png";
    docesRestantes = DOCES_TOTAIS;
    indiceParedes = 2;
    indicePontosDecisao = 3;
    indiceDoces = 1;
}

void inicializaPacMan(int vidas) {
    pacManVelocidade = (nivel == NIVEL_3) ? 3 : 2;
    pacManVidas = vidas;
    pacManVivo = 1;
    pacManDirecaoAtual = PARADO;
    pacManDirecaoPretendida = PARADO;
    pacManX = JANELA_LARGURA / 2 - 24 / 2;
    pacManY = (JANELA_ALTURA / 2 - 24 / 2) - 48;
}

void inicializaGhosts() {
    int i;
    for (i = 0; i < 4; i++) {
        int x;
        int y;
        if (i != 3) {
            x = (i + 11) * 24;
            y = 12 * 24;
        } else {
            x = 12 * 24;
            y = 14 * 24;
        }
        ghosts[i].estado = (i == 3) ? ESTADO_NORMAL : ESTADO_PRESO;
        ghosts[i].direcao = (i == 3) ? DIREITA : PARADO;
        ghosts[i].tempoSerLivre = (i == 3) ? 0 : TEMPO_PRESO * (i + 1);
        ghosts[i].tempoInvulneravel = 0;
        if (nivel == NIVEL_1) {
            ghosts[i].seguePacMan = 0;
            ghosts[i].velocidade = 2;
        } else if (nivel == NIVEL_2) {
            ghosts[i].seguePacMan = (i == 0) ? 1 : 0;
            ghosts[i].velocidade = 2;
        } else {
            ghosts[i].seguePacMan = (i == 0) ? 1 : 0;
            ghosts[i].velocidade = (i == 0) ? 2 : 3;
        }
        ghosts[i].x = x;
        ghosts[i].y = y;
    }
}

void inicializaFruta() {
    frutaVisivel = 0;
    frutaProbabilidade = FATOR_PROBABILIDADE;
    frutaX = JANELA_LARGURA / 2 - 24 / 2;
    frutaY = (JANELA_ALTURA / 2 - 24 / 2) - 48;
}

char *op001() {
    strcpy(resposta, "001\0");
    insereConcatenaNumero(estadoJogo);
    insereConcatenaNumero(nivel);
    insereConcatenaNumero(pontos);
    insereConcatenaNumero(tempo);
    strcat(resposta,caminhoSprites);
    strcat(resposta,"#\0");
    strcat(resposta, caminhoTiledMap);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op002(char n) {
    switch (n) {
        case '0':
            estadoJogo = ESTADO_INICIO;
            nivel = NIVEL_1;
            caminhoMapa = "maps/level1.tmx";
            docesRestantes = DOCES_TOTAIS;
            inicializaPacMan(3);
            inicializaGhosts();
            inicializaFruta();
            geraRespostaOp002();
            return resposta;
            break;
        case '1':
            break;
        case '2':
            break;
        case '3':
            break;
    }
}

void geraRespostaOp002() {
    strcpy(resposta, "002\0");
    insereConcatenaNumero(nivel);
    strcat(resposta, caminhoMapa); //caminho do mapa do nivel
    strcat(resposta, "#");
    insereConcatenaNumero(indiceParedes);
    insereConcatenaNumero(indicePontosDecisao);
    insereConcatenaNumero(indiceDoces);
    insereConcatenaNumero(pacManVelocidade);
    insereConcatenaNumero(pacManVidas);
    insereConcatenaNumero(pacManVivo);
    insereConcatenaNumero(pacManDirecaoAtual);
    insereConcatenaNumero(pacManDirecaoPretendida);
    insereConcatenaNumero(pacManX);
    insereConcatenaNumero(pacManY);
    insereConcatenaNumero(frutaVisivel);
    insereConcatenaNumero(frutaProbabilidade);
    insereConcatenaNumero(frutaX);
    insereConcatenaNumero(frutaY);
    insereConcatenaNumero(docesRestantes);
    int i;
    for(i = 0; i< 4; i++){
        insereConcatenaNumero(ghosts[i].estado);
        insereConcatenaNumero(ghosts[i].direcao);
        insereConcatenaNumero(ghosts[i].velocidade);
        insereConcatenaNumero(ghosts[i].tempoSerLivre);
        insereConcatenaNumero(ghosts[i].tempoInvulneravel);
        insereConcatenaNumero(ghosts[i].seguePacMan);
        insereConcatenaNumero(ghosts[i].x);
        insereConcatenaNumero(ghosts[i].y);
    }
    strcat(resposta,"\n\0");
}