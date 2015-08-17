package pacman;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import java.util.Random;

public class Ghost {

    static final int DELAY_MAX = 10;
    static final int ESTADO_NORMAL = 1;
    static final int ESTADO_VULNERAVEL = 2;
    static final int ESTADO_OLHOS = 3;
    static final int ESTADO_PRESO = 4;
    static final int TIME_ARRESTED = 100;

    protected Sprite personagem;
    protected TextureRegion frames[];
    protected int estado;
    protected int frameAtual;
    protected int direcao;
    protected float velocidade;
    protected float delay;
    protected SpriteBatch batch;
    protected float timeToBeFree;

    public Ghost(float x, float y, TextureRegion[] sprites, float velocidade, int ghostId) {
        frames = sprites;
        this.personagem = new Sprite(frames[0]);
        this.personagem.setPosition(x, y);
        this.estado = (ghostId == 3) ? ESTADO_NORMAL : ESTADO_PRESO;
        this.frameAtual = 0;
        this.direcao = (ghostId == 3) ? App.DIREITA : App.PARADO;
        this.velocidade = velocidade;
        this.delay = DELAY_MAX;
        this.batch = new SpriteBatch();
        this.timeToBeFree = (ghostId == 3) ? 0 : TIME_ARRESTED* (ghostId+1);
    }

    public void anda(MapObjects objects) {
        if (this.timeToBeFree == 0) {
            if (this.estado == ESTADO_PRESO) {
                this.personagem.setPosition(12 * 24, 14 * 24);
                this.estado = ESTADO_NORMAL;
            }
            if (colidiu(objects, direcao) || direcao == App.PARADO) {
                do {
                    direcao = new Random().nextInt(4);
                } while (colidiu(objects, direcao));
            }
            switch (direcao) {
                case App.ESQUERDA:
                    if (!colidiu(objects, App.ESQUERDA)) {
                        this.personagem.translateX(-velocidade);
                    }
                    break;
                case App.DIREITA:
                    if (!colidiu(objects, App.DIREITA)) {
                        this.personagem.translateX(velocidade);
                    }
                    break;
                case App.CIMA:
                    if (!colidiu(objects, App.CIMA)) {
                        this.personagem.translateY(velocidade);
                    }
                    break;
                case App.BAIXO:
                    if (!colidiu(objects, App.BAIXO)) {
                        this.personagem.translateY(-velocidade);
                    }
                    break;
            }
        } else {
            timeToBeFree--;
        }
    }

    public void animate() {
        if (this.direcao != App.PARADO) {
            if (this.delay == 0) {
                this.frameAtual = (this.direcao==App.PARADO)?0:this.direcao+1;
                this.personagem.setRegion(frames[frameAtual]);
                delay = DELAY_MAX;
            } else {
                this.delay--;
            }
        } else if (this.direcao == App.PARADO) {
            this.personagem.setRegion(this.frames[0]);
        }
        this.batch.begin();
        this.personagem.draw(this.batch);
        this.batch.end();
    }

    boolean colidiu(MapObjects objects, int direcao) {
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
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();
            if (Intersector.overlaps(rectangle, r)) {
                return true;
            }
        }
        return false;
    }
}
