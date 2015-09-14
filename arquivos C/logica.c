#include "logica.h"
#include <time.h>
#include <math.h>

void insereConcatenaInt(int n) {
    char numConvert[5];
    sprintf(numConvert, "%d", n);
    strcat(resposta, numConvert);
    strcat(resposta, "#\0");
}

void insereConcatenaFloat(float n) {
    char numConvert[5];
    sprintf(numConvert, "%.2f", n);
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
    inicializaDados();
    strcpy(resposta, "001\0");
    insereConcatenaInt(estadoJogo);
    insereConcatenaInt(nivel);
    insereConcatenaInt(pontos);
    insereConcatenaInt(tempo);
    strcat(resposta, caminhoSprites);
    strcat(resposta, "#\0");
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
            inicializaPacMan(3);
            inicializaGhosts();
            break;
        case '1':
            estadoJogo = ESTADO_INICIO;
            nivel = NIVEL_2;
            caminhoMapa = "maps/level2.tmx";
            inicializaPacMan(pacManVidas);
            inicializaGhosts();
            break;
        case '2':
            estadoJogo = ESTADO_INICIO;
            nivel = NIVEL_3;
            caminhoMapa = "maps/level3.tmx";
            inicializaPacMan(pacManVidas);
            inicializaGhosts();
            break;
        case '3':
            estadoJogo = ESTADO_FIM;
            nivel = VENCEDOR;
            caminhoMapa = "maps/win.tmx";
            inicializaPacMan(pacManVidas);
            inicializaGhosts();
            break;
    }
    inicializaFruta();
    docesRestantes = DOCES_TOTAIS;
    geraRespostaOp002();
    return resposta;
}

void geraRespostaOp002() {
    strcpy(resposta, "002\0");
    insereConcatenaInt(nivel);
    strcat(resposta, caminhoMapa); //caminho do mapa do nivel
    strcat(resposta, "#");
    insereConcatenaInt(indiceParedes);
    insereConcatenaInt(indicePontosDecisao);
    insereConcatenaInt(indiceDoces);
    insereConcatenaInt(pacManVelocidade);
    insereConcatenaInt(pacManVidas);
    insereConcatenaInt(pacManVivo);
    insereConcatenaInt(pacManDirecaoAtual);
    insereConcatenaInt(pacManDirecaoPretendida);
    insereConcatenaFloat(pacManX);
    insereConcatenaFloat(pacManY);
    insereConcatenaInt(frutaVisivel);
    insereConcatenaInt(frutaProbabilidade);
    insereConcatenaInt(frutaX);
    insereConcatenaInt(frutaY);
    insereConcatenaInt(docesRestantes);
    int i;
    for (i = 0; i < 4; i++) {
        insereConcatenaInt(ghosts[i].estado);
        insereConcatenaInt(ghosts[i].direcao);
        insereConcatenaInt(ghosts[i].velocidade);
        insereConcatenaInt(ghosts[i].tempoSerLivre);
        insereConcatenaInt(ghosts[i].tempoInvulneravel);
        insereConcatenaInt(ghosts[i].seguePacMan);
        insereConcatenaInt(ghosts[i].x);
        insereConcatenaInt(ghosts[i].y);
    }
    insereConcatenaInt(estadoJogo);
    strcat(resposta, "\n\0");
}

char *op003(char teclaPressionada[]) {
    strcpy(resposta, "003\0");
    estadoJogo = ESTADO_JOGANDO;
    insereConcatenaInt(estadoJogo);
    int tecla = atoi(teclaPressionada);
    switch (tecla) {
        case 21: //tecla left
            pacManDirecaoAtual = ESQUERDA;
            break;
        case 22: //tecla right
            pacManDirecaoAtual = DIREITA;
            break;
        case 19: //tecla up
            pacManDirecaoAtual = CIMA;
            break;
        case 20: //tecla down
            pacManDirecaoAtual = BAIXO;
            break;
    }
    insereConcatenaInt(pacManDirecaoAtual);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op004() {
    srand((unsigned) time(NULL));
    float numero = rand() % 1000;
    if (numero < frutaProbabilidade) {
        frutaVisivel = 1;
    } else {
        frutaProbabilidade += FATOR_PROBABILIDADE;
    }
    strcpy(resposta, "004\0");
    insereConcatenaInt(frutaVisivel);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op005() {
    pontos += PONTO_FRUTA;
    frutaVisivel = 0;
    frutaProbabilidade = 0;
    strcpy(resposta, "005\0");
    insereConcatenaInt(pontos);
    insereConcatenaInt(frutaVisivel);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op006(char teclaPressionada[]) {
    strcpy(resposta, "006\0");
    int tecla = atoi(teclaPressionada);
    switch (tecla) {
        case 21: //tecla left
            pacManDirecaoPretendida = ESQUERDA;
            break;
        case 22: //tecla right
            pacManDirecaoPretendida = DIREITA;
            break;
        case 19: //tecla up
            pacManDirecaoPretendida = CIMA;
            break;
        case 20: //tecla down
            pacManDirecaoPretendida = BAIXO;
            break;
    }
    insereConcatenaInt(pacManDirecaoPretendida);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op007(float x, float y, int dirPretLivre) {
    strcpy(resposta, "007\0");
    pacManX = x;
    pacManY = y;
    if (dirPretLivre == 1) {
        pacManDirecaoAtual = pacManDirecaoPretendida;
    }
    insereConcatenaInt(pacManDirecaoAtual);
    switch (pacManDirecaoAtual) {
        case ESQUERDA:
            pacManX -= pacManVelocidade;
            break;
        case DIREITA:
            pacManX += pacManVelocidade;
            break;
        case CIMA:
            pacManY += pacManVelocidade;
            break;
        case BAIXO:
            pacManY -= pacManVelocidade;
            break;
    }
    insereConcatenaFloat(pacManX);
    insereConcatenaFloat(pacManY);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op008() {
    strcpy(resposta, "008\0");
    pacManDirecaoAtual = PARADO;
    insereConcatenaInt(pacManDirecaoAtual);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op009(char id, char direcao) {
    strcpy(resposta, "009\0");
    int idG = id - '0';
    int dirG = direcao - '0';
    ghosts[idG].direcao = dirG;
    insereConcatenaInt(dirG);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op010(int i, int x, int y) {
    strcpy(resposta, "010\0");
    int a, b, c, d;
    a = sqrt(pow(((ghosts[i].x - ghosts[i].velocidade) - x), 2) + pow(ghosts[i].y - y, 2));
    b = sqrt(pow((ghosts[i].x - x), 2) + pow(ghosts[i].y + ghosts[i].velocidade - y, 2));
    c = sqrt(pow((ghosts[i].x + ghosts[i].velocidade) - x, 2) + pow(ghosts[i].y - y, 2));
    d = sqrt(pow((ghosts[i].x) - x, 2) + pow(ghosts[i].y - ghosts[i].velocidade - y, 2));
    insereConcatenaInt(a);
    insereConcatenaInt(b);
    insereConcatenaInt(c);
    insereConcatenaInt(d);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op011(int id, float x, float y) {
    strcpy(resposta, "011\0");
    ghosts[id].x = x;
    ghosts[id].y = y;

    switch (ghosts[id].direcao) {
        case ESQUERDA:
            ghosts[id].x -= ghosts[id].velocidade;
            break;
        case DIREITA:
            ghosts[id].x += ghosts[id].velocidade;
            break;
        case CIMA:
            ghosts[id].y += ghosts[id].velocidade;
            break;
        case BAIXO:
            ghosts[id].y -= ghosts[id].velocidade;
            break;
    }

    insereConcatenaFloat(ghosts[id].x);
    insereConcatenaFloat(ghosts[id].y);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op012(char id, char estado) {
    strcpy(resposta, "012\0");
    int idG = id - '0';
    int estG = estado - '0';
    ghosts[idG].estado = estG;
    insereConcatenaInt(estG);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op013(char id) {
    strcpy(resposta, "013\0");
    int idG = id - '0';
    ghosts[idG].tempoSerLivre--;
    insereConcatenaInt(ghosts[idG].tempoSerLivre);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op014(char id) {
    strcpy(resposta, "014\0");
    int idG = id - '0';
    if (ghosts[idG].tempoInvulneravel == 0) {
        ghosts[idG].tempoInvulneravel = TEMPO_VULNERAVEL;
    } else {
        ghosts[idG].tempoInvulneravel--;
    }
    insereConcatenaInt(ghosts[idG].tempoInvulneravel);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op015(char idG) {
    strcpy(resposta, "015\0");
    int id = idG - '0';
    if (ghosts[id].estado == ESTADO_NORMAL) {
        pontos += PONTO_MORRE;
        estadoJogo = ESTADO_PACMAN_MORTO;
    } else if (ghosts[id].estado == ESTADO_VULNERAVEL) {
        ghosts[id].estado = ESTADO_OLHOS;
        pontos += PONTO_GHOST;
    }
    insereConcatenaInt(pontos);
    insereConcatenaInt(estadoJogo);
    insereConcatenaInt(ghosts[id].estado);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op016() {
    strcpy(resposta, "016\0");
    pacManVidas--;
    pacManVivo = 0;
    insereConcatenaInt(pacManVidas);
    insereConcatenaInt(pacManVivo);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op017() {
    strcpy(resposta, "017\0");
    frutaProbabilidade = FATOR_PROBABILIDADE;
    if (pacManVidas == 0) {
        estadoJogo = ESTADO_FIM;
    } else {
        estadoJogo = ESTADO_INICIO;
    }
    insereConcatenaInt(estadoJogo);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op018() {
    strcpy(resposta, "018\0");
    pacManX = JANELA_LARGURA / 2 - 24 / 2;
    pacManY = (JANELA_ALTURA / 2 - 24 / 2) - 48;
    pacManVivo = 1;
    pacManDirecaoAtual = PARADO;
    pacManDirecaoPretendida = PARADO;
    insereConcatenaInt(pacManX);
    insereConcatenaInt(pacManY);
    insereConcatenaInt(pacManVivo);
    insereConcatenaInt(pacManDirecaoAtual);
    insereConcatenaInt(pacManDirecaoPretendida);
    strcat(resposta, "\n\0");
    return resposta;
}

char*op019() {
    strcpy(resposta, "019\0");
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
        ghosts[i].x = x;
        ghosts[i].y = y;

        insereConcatenaInt(ghosts[i].estado);
        insereConcatenaInt(ghosts[i].direcao);
        insereConcatenaInt(ghosts[i].tempoSerLivre);
        insereConcatenaInt(ghosts[i].tempoInvulneravel);
        insereConcatenaInt(ghosts[i].x);
        insereConcatenaInt(ghosts[i].y);

    }
    strcat(resposta, "\n\0");
    return resposta;
}

char *op020(char d) {
    strcpy(resposta, "020\0");
    int doce = d - '0';
    if (doce == 1) {//se foi um doce grande
       pontos += PONTO_DOCE_GRANDE;
    } else {
        pontos += PONTO_DOCE_PEQUENO;
    }
    docesRestantes--;
    if (docesRestantes == 0) {
        estadoJogo = ESTADO_NIVEL_COMPLETO;
    }
    
    insereConcatenaInt(pontos);
    insereConcatenaInt(docesRestantes);
    insereConcatenaInt(estadoJogo);
    strcat(resposta, "\n\0");
    return resposta;

}

char *op021(){
    strcpy(resposta, "021\0");
    caminhoMapa = "maps/ajuda.tmx";
    strcat(resposta,caminhoMapa);
    strcat(resposta,"#");
    strcat(resposta, "\n\0");
    return resposta;
}

char *op022(){
    strcpy(resposta, "022\0");
    caminhoMapa = "maps/win.tmx";
    strcat(resposta,caminhoMapa);
    strcat(resposta,"#");
    strcat(resposta, "\n\0");
    return resposta;
}