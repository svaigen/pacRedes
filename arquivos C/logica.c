#include "logica.h"
#include <time.h>
#include <math.h>

void insereConcatenaNumero(int n) {
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
    strcpy(resposta, "001\0");
    insereConcatenaNumero(estadoJogo);
    insereConcatenaNumero(nivel);
    insereConcatenaNumero(pontos);
    insereConcatenaNumero(tempo);
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
            nivel = ABERTURA;
            caminhoMapa = "maps/level3.tmx";
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
    insereConcatenaFloat(pacManX);
    insereConcatenaFloat(pacManY);
    insereConcatenaNumero(frutaVisivel);
    insereConcatenaNumero(frutaProbabilidade);
    insereConcatenaNumero(frutaX);
    insereConcatenaNumero(frutaY);
    insereConcatenaNumero(docesRestantes);
    int i;
    for (i = 0; i < 4; i++) {
        insereConcatenaNumero(ghosts[i].estado);
        insereConcatenaNumero(ghosts[i].direcao);
        insereConcatenaNumero(ghosts[i].velocidade);
        insereConcatenaNumero(ghosts[i].tempoSerLivre);
        insereConcatenaNumero(ghosts[i].tempoInvulneravel);
        insereConcatenaNumero(ghosts[i].seguePacMan);
        insereConcatenaNumero(ghosts[i].x);
        insereConcatenaNumero(ghosts[i].y);
    }
    insereConcatenaNumero(estadoJogo);
    strcat(resposta, "\n\0");
}

char *op003(char teclaPressionada[]) {
    strcpy(resposta, "003\0");
    estadoJogo = ESTADO_JOGANDO;
    insereConcatenaNumero(estadoJogo);
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
    insereConcatenaNumero(pacManDirecaoAtual);
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
    insereConcatenaNumero(frutaVisivel);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op005() {
    pontos += PONTO_FRUTA;
    frutaVisivel = 0;
    frutaProbabilidade = 0;
    strcpy(resposta, "005\0");
    insereConcatenaNumero(pontos);
    insereConcatenaNumero(frutaVisivel);
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
    insereConcatenaNumero(pacManDirecaoPretendida);
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
    insereConcatenaNumero(pacManDirecaoAtual);
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
    insereConcatenaNumero(pacManDirecaoAtual);
    strcat(resposta, "\n\0");
    return resposta;
}

char *op009(char id, char direcao){
    strcpy(resposta, "009\0");
    int idG = id - '0';
    int dirG = direcao - '0';
    ghosts[idG].direcao = dirG;
    insereConcatenaNumero(dirG);
    strcat(resposta,"\n\0");
    return resposta;
}

char *op010(int i, int x, int y) {
    strcpy(resposta, "010\0");
    int a,b,c,d;
    a =sqrt(pow(((ghosts[i].x - ghosts[i].velocidade) - x), 2) + pow(ghosts[i].y - y, 2));
    b =sqrt(pow((ghosts[i].x - x), 2) + pow(ghosts[i].y + ghosts[i].velocidade - y, 2));
    c= sqrt(pow((ghosts[i].x + ghosts[i].velocidade) - x, 2) + pow(ghosts[i].y - y, 2));
    d =sqrt(pow((ghosts[i].x) - x, 2) + pow(ghosts[i].y - ghosts[i].velocidade - y, 2));
    insereConcatenaNumero(a);
    insereConcatenaNumero(b);
    insereConcatenaNumero(c);
    insereConcatenaNumero(d);
    strcat(resposta,"\n\0");
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