package pacman;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public class Pacman {

    static final int NUM_FRAMES = 20;
    static final int DELAY_MAX = 10;
    protected Sprite personagem;
    protected TextureRegion frames[];
    protected boolean isAlive;
    protected int frameAtual;
    protected int direcaoAtual;
    protected int direcaoPretendida;
    protected float velocidade;
    protected float delay;

    public Pacman(float width, float height, Texture sprites, float velocidade) {
        frames = new TextureRegion[NUM_FRAMES];
        for (int i = 0; i < NUM_FRAMES; i++) {
            frames[i] = new TextureRegion(sprites, i * 24, 0, 24, 24);
        }
        this.personagem = new Sprite(frames[0]);
        this.personagem.setPosition(width / 2 - this.personagem.getWidth() / 2, (height / 2 - this.personagem.getHeight() / 2) - 48);
        this.isAlive = true;
        this.frameAtual = 0;
        this.direcaoAtual = App.PARADO;
        this.direcaoPretendida = App.PARADO;
        this.velocidade = velocidade;
        this.delay = DELAY_MAX;
    }

    public void animate() {
        if (isAlive && this.direcaoAtual != App.PARADO) {
            if (this.delay == 0) {
                if ((this.frameAtual % 2) == 0) {
                    this.frameAtual = (this.direcaoAtual*2) + 1;
                } else {
                    this.frameAtual = (this.direcaoAtual*2);
                }
                this.personagem.setRegion(frames[frameAtual]);
                delay = DELAY_MAX;
            } else {
                this.delay--;
            }
        }
    }

    public void anda(MapObjects objects) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            this.direcaoPretendida = App.ESQUERDA;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            this.direcaoPretendida = App.DIREITA;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            this.direcaoPretendida = App.CIMA;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            this.direcaoPretendida = App.BAIXO;
        }

        if (!colidiu(objects, direcaoPretendida)) {
            direcaoAtual = direcaoPretendida;
        }
        switch (direcaoAtual) {
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
        if (colidiu(objects, direcaoAtual)) {
            this.direcaoAtual = App.PARADO;
        }

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
