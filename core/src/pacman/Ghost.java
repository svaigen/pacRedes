package pacman;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class Ghost {

    static final int DELAY_MAX = 10;
    static final int ESTADO_NORMAL = 1;
    static final int ESTADO_VULNERAVEL = 2;
    static final int ESTADO_OLHOS = 3;
    static final int ESTADO_PRESO = 4;
    static final int TEMPO_PRESO = 100;
    static final int TEMPO_VULNERAVEL = 800;

    protected Sprite personagem;
    protected TextureRegion frames[];
    protected int estado;
    protected int frameAtual;
    protected int direcao;
    protected float velocidade;
    protected float delay;
    protected SpriteBatch batch;
    protected int tempoParaFicarLivre;
    protected int tempoParaInvulneravel;
    protected boolean seguePacMan;

    public Ghost(float x, float y, TextureRegion[] sprites, float velocidade, int ghostId, boolean segue, int nivel) {
        frames = sprites;
        this.velocidade = velocidade;
        this.batch = new SpriteBatch();
        this.init(x, y, ghostId);
        seguePacMan = segue;
        if (seguePacMan && nivel==3){
            this.velocidade -= 1f;
        }
    }

    Ghost(int estado, int direcao, float velocidade, int tempoSerLivre,
            int tempoInvulneravel, int seguePacMan, int x, int y, TextureRegion[] sprites) {
        frames = sprites;
        this.velocidade = velocidade;
        this.batch = new SpriteBatch();
        this.personagem = new Sprite(frames[0]);
        this.personagem.setPosition(x, y);
        this.estado = estado;
        this.frameAtual = 0;
        this.direcao = direcao;
        this.tempoParaFicarLivre = tempoSerLivre;
        this.tempoParaInvulneravel = tempoInvulneravel;
        this.delay = DELAY_MAX;
        this.seguePacMan = seguePacMan == 1;
    }

    public void init(float x, float y, int ghostId) {
        this.personagem = new Sprite(frames[0]);
        this.personagem.setPosition(x, y);
        this.estado = (ghostId == 3) ? ESTADO_NORMAL : ESTADO_PRESO;
        this.frameAtual = 0;
        this.direcao = (ghostId == 3) ? App.DIREITA : App.PARADO;
        this.tempoParaFicarLivre = (ghostId == 3) ? 0 : TEMPO_PRESO * (ghostId + 1);
        this.tempoParaInvulneravel = 0;
        this.delay = DELAY_MAX;
    }

    public void anda(MapObjects paredes, MapObjects pontosDecisao, float x, float y) {
        if (this.tempoParaFicarLivre == 0) {
            if (this.estado == ESTADO_OLHOS && estaEmPontoDecisao(pontosDecisao)) {
                escolheDirecaoParaObjetivo(x, y, paredes);
            } else if (this.seguePacMan && this.estado != ESTADO_VULNERAVEL && estaEmPontoDecisao(pontosDecisao)) {
                escolheDirecaoParaObjetivo(x, y, paredes);
            } else {
                if (this.estado == ESTADO_PRESO) {
                    this.personagem.setPosition(12 * 24, 14 * 24);
                    this.estado = this.tempoParaInvulneravel == 0 ? ESTADO_NORMAL : ESTADO_VULNERAVEL;
                }
                if (colidiuParedes(paredes, direcao) || direcao == App.PARADO || estaEmPontoDecisao(pontosDecisao)) {
                    int direcaoAnterior = direcao;
                    do {
                        direcao = new Random().nextInt(4);
                    } while (colidiuParedes(paredes, direcao) || (Math.abs(direcao - direcaoAnterior) == 2));
                }
            }

            switch (direcao) {
                case App.ESQUERDA:
                    if (!colidiuParedes(paredes, App.ESQUERDA)) {
                        this.personagem.translateX(-velocidade);
                    }
                    break;
                case App.DIREITA:
                    if (!colidiuParedes(paredes, App.DIREITA)) {
                        this.personagem.translateX(velocidade);
                    }
                    break;
                case App.CIMA:
                    if (!colidiuParedes(paredes, App.CIMA)) {
                        this.personagem.translateY(velocidade);
                    }
                    break;
                case App.BAIXO:
                    if (!colidiuParedes(paredes, App.BAIXO)) {
                        this.personagem.translateY(-velocidade);
                    }
                    break;
            }
        } else {
            tempoParaFicarLivre--;
        }
    }

    public void animate() {
        switch (estado) {
            case ESTADO_PRESO:
                if (this.tempoParaInvulneravel != 0) {
                    if (this.tempoParaInvulneravel > TEMPO_VULNERAVEL / 4) {
                        this.frameAtual = 6;
                        this.personagem.setRegion(frames[frameAtual]);
                    } else {
                        if (this.delay == 0) {
                            this.frameAtual = this.frameAtual == 6 ? 5 : 6;
                            this.personagem.setRegion(frames[frameAtual]);
                            delay = DELAY_MAX;
                        } else {
                            delay--;
                        }
                    }
                }
                break;
            case ESTADO_VULNERAVEL:
                if (this.tempoParaInvulneravel == 0) {
                    this.estado = ESTADO_NORMAL;
                } else if (this.tempoParaInvulneravel > TEMPO_VULNERAVEL / 4) {
                    this.frameAtual = 6;
                    this.personagem.setRegion(frames[frameAtual]);
                } else {
                    if (this.delay == 0) {
                        this.frameAtual = this.frameAtual == 6 ? 5 : 6;
                        this.personagem.setRegion(frames[frameAtual]);
                        delay = DELAY_MAX;
                    } else {
                        delay--;
                    }
                }
                break;
            case ESTADO_OLHOS:
                if (this.personagem.getX() == (12 * 24) && this.personagem.getY() == (14 * 24)) {
                    this.estado = ESTADO_NORMAL;
                }
                this.frameAtual = this.direcao + 7;
                this.personagem.setRegion(frames[frameAtual]);
                break;
            case ESTADO_NORMAL:
                this.frameAtual = (this.direcao == App.PARADO) ? 0 : this.direcao + 1;
                this.personagem.setRegion(frames[frameAtual]);
                break;
        }
        if (this.tempoParaInvulneravel != 0) {
            this.tempoParaInvulneravel--;
        }
        this.batch.begin();
        this.personagem.draw(this.batch);
        this.batch.end();
    }

    boolean colidiuParedes(MapObjects objects, int direcao) {
        float x = this.personagem.getX();
        float y = this.personagem.getY();

        switch (direcao) {
            case App.ESQUERDA: //esquerda
                x -= this.velocidade;
                break;
            case App.DIREITA: //direita
                x += velocidade;
                break;
            case App.BAIXO: //baixo
                y -= velocidade;
                break;
            case App.CIMA://cima
                y += velocidade;
                break;
        }
        Rectangle r = new Rectangle(x, y, this.personagem.getWidth(), this.personagem.getHeight());

        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class
        )) {
            Rectangle rectangle = rectangleObject.getRectangle();

            if (Intersector.overlaps(rectangle, r)) {
                return true;
            }
        }
        return false;
    }

    public boolean estaEmPontoDecisao(MapObjects pontosColisao) {
        Rectangle rGhost = new Rectangle(this.personagem.getX(), this.personagem.getY(), this.personagem.getWidth(), this.personagem.getHeight());

        for (RectangleMapObject rectangleObject : pontosColisao.getByType(RectangleMapObject.class
        )) {
            Rectangle rectangle = rectangleObject.getRectangle();

            if (rectangle.equals(rGhost)) {
                return true;
            }
        }
        return false;
    }

    void transformaVulneravel() {
        this.estado = this.estado != ESTADO_PRESO ? ESTADO_VULNERAVEL : this.estado;
        this.tempoParaInvulneravel = TEMPO_VULNERAVEL;
        this.delay = DELAY_MAX;
    }

    boolean isVulneravel() {
        return this.estado == ESTADO_VULNERAVEL;
    }

    boolean isNormal() {
        return this.estado == ESTADO_NORMAL;
    }

    private void escolheDirecaoParaObjetivo(float x, float y, MapObjects paredes) {
        ArrayList<Integer> direcoes = new ArrayList();
        int movEsquerda = (int) Math.sqrt(Math.pow(((this.personagem.getX() - this.velocidade) - x), 2) + Math.pow(this.personagem.getY() - y, 2));
        int movCima = (int) Math.sqrt(Math.pow((this.personagem.getX() - x), 2) + Math.pow(this.personagem.getY() + velocidade - y, 2));
        int movDireita = (int) Math.sqrt(Math.pow((this.personagem.getX() + velocidade) - x, 2) + Math.pow(this.personagem.getY() - y, 2));
        int movBaixo = (int) Math.sqrt(Math.pow((this.personagem.getX()) - x, 2) + Math.pow(this.personagem.getY() - velocidade - y, 2));
        direcoes.add(movEsquerda);
        direcoes.add(movCima);
        direcoes.add(movDireita);
        direcoes.add(movBaixo);
        boolean escolheu = false;
        int melhorDirecao = -1;
        do {
            int menorMov = Integer.MAX_VALUE;
            for (int i = 0; i < direcoes.size(); i++) {
                if (direcoes.get(i) < menorMov) {
                    menorMov = direcoes.get(i);
                    melhorDirecao = i;
                }
            }
            if (!colidiuParedes(paredes, melhorDirecao)) {
                escolheu = true;
                this.direcao = melhorDirecao;
            } else {
                direcoes.set(melhorDirecao, Integer.MAX_VALUE);
            }
        } while (!escolheu);
    }

    public boolean isSeguidorPacMan() {
        return seguePacMan;
    }
    
    
}
