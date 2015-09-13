package pacman;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import java.math.BigInteger;
import java.util.Random;

public class Fruta {
    protected final float FATOR_PROBABILIDADE = 0.1f;
    
    protected Sprite sprite;
    protected boolean visivel;
    protected SpriteBatch batch;
    protected float probabilidade;

    public Fruta(float width, float height, TextureRegion textureRegion) {
        this.visivel = false;
        this.sprite = new Sprite(textureRegion);
        this.sprite.setPosition(width / 2 - this.sprite.getWidth() / 2, (height / 2 - this.sprite.getHeight() / 2) - 48);
        batch = new SpriteBatch();
        probabilidade = FATOR_PROBABILIDADE;
    }

    public Fruta(int visivel, float probabilidade, int x, int y, TextureRegion sprite) {
        this.visivel = visivel==1;
        this.probabilidade = probabilidade;
        this.sprite = new Sprite(sprite);
        this.sprite.setPosition(x, y);
        batch = new SpriteBatch();
    }

    public void animate() {
        calculaProbabilidadefruta();
        if (visivel) {
            batch.begin();
            sprite.draw(batch);
            batch.end();
        }
    }

    private void calculaProbabilidadefruta() {
        Random gerador = new Random();
        float numero = gerador.nextInt(100000000);
        numero = numero / 100;
        if (numero < probabilidade) {
            this.visivel = true;
        }else{
            probabilidade = probabilidade + FATOR_PROBABILIDADE;
        }
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public boolean isVisivel() {
        return visivel;
    }

    public void setVisivel(boolean visivel) {
        this.visivel = visivel;
    }

    public float getProbabilidade() {
        return probabilidade;
    }

    public void setProbabilidade(float probabilidade) {
        this.probabilidade = probabilidade;
    }

    boolean foiComida(Pacman pacMan) {
        if(visivel){
            Rectangle rPac = new Rectangle(pacMan.personagem.getX(), pacMan.personagem.getY(), pacMan.personagem.getWidth(), pacMan.personagem.getHeight());
            Rectangle rFruta = new Rectangle(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getWidth());        
            if (rPac.overlaps(rFruta)){
                visivel = false;
                probabilidade = FATOR_PROBABILIDADE;
                return true;
            }
        }
        return false;
    }

    void reiniciaProbabilidade() {
        probabilidade = FATOR_PROBABILIDADE;
    }
    
    

}
