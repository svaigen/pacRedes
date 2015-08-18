package pacman;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public class Pacman {

    static final int DELAY_MAX = 10;
    protected Sprite personagem;
    protected TextureRegion frames[];
    protected boolean alive;
    protected int frameAtual;
    protected int direcaoAtual;
    protected int direcaoPretendida;
    protected float velocidade;
    protected float delay;
    protected SpriteBatch batch;

    public Pacman(float width, float height, TextureRegion[] sprites, float velocidade) {
        frames = sprites;
        this.personagem = new Sprite(frames[0]);
        this.personagem.setPosition(width / 2 - this.personagem.getWidth() / 2, (height / 2 - this.personagem.getHeight() / 2) - 48);
        this.alive = true;
        this.frameAtual = 0;
        this.direcaoAtual = App.PARADO;
        this.direcaoPretendida = App.PARADO;
        this.velocidade = velocidade;
        this.delay = DELAY_MAX;
        this.batch = new SpriteBatch();
    }

    public void animate() {
        if (alive && this.direcaoAtual != App.PARADO) {
            if (this.delay == 0) {
                if ((this.frameAtual % 2) == 0) {
                    this.frameAtual = (this.direcaoAtual * 2) + 1;
                } else {
                    this.frameAtual = (this.direcaoAtual * 2);
                }
                this.personagem.setRegion(frames[frameAtual]);
                delay = DELAY_MAX;
            } else {
                this.delay--;
            }
        } else if (!alive) {
            if (this.delay == 0) {
                if (this.frameAtual < 19) {
                    this.frameAtual++;
                    this.personagem.setRegion(frames[frameAtual]);
                    this.delay = DELAY_MAX;
                }
            } else {
                this.delay--;
            }
        }
        this.batch.begin();
        this.personagem.draw(batch);
        this.batch.end();
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

        if (!colidiuWalls(objects, direcaoPretendida)) {
            direcaoAtual = direcaoPretendida;
        }
        switch (direcaoAtual) {
            case App.ESQUERDA:
                if (!colidiuWalls(objects, App.ESQUERDA)) {
                    this.personagem.translateX(-velocidade);
                }
                break;
            case App.DIREITA:
                if (!colidiuWalls(objects, App.DIREITA)) {
                    this.personagem.translateX(velocidade);
                }
                break;
            case App.CIMA:
                if (!colidiuWalls(objects, App.CIMA)) {
                    this.personagem.translateY(velocidade);
                }
                break;
            case App.BAIXO:
                if (!colidiuWalls(objects, App.BAIXO)) {
                    this.personagem.translateY(-velocidade);
                }
                break;
        }
        if (colidiuWalls(objects, direcaoAtual)) {
            this.direcaoAtual = App.PARADO;
        }

    }

    boolean colidiuWalls(MapObjects objects, int direcao) {
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

    public boolean colidiuGhosts(Ghost[] ghosts) {
        Rectangle rPac = new Rectangle(this.personagem.getX(), this.personagem.getY(),
                this.personagem.getWidth(), this.personagem.getHeight());

        for (Ghost ghost : ghosts) {
            Rectangle rGhost = new Rectangle(ghost.personagem.getX(), ghost.personagem.getY(),
                    ghost.personagem.getWidth(), ghost.personagem.getHeight());
            if (Intersector.overlaps(rGhost, rPac)) {
                this.alive = false;
                this.delay = DELAY_MAX;
                this.frameAtual = 8;
                this.personagem.setRegion(frames[frameAtual]);
                return true;
            }
        }
        return false;
    }
}
