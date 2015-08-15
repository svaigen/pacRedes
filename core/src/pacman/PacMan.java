package pacman;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import sun.security.x509.X500Name;

public class PacMan extends ApplicationAdapter {
    
    float velocidade = 1.0f;

    boolean andaDireita = false;
    boolean andaEsquerda = false;
    boolean andaCima = false;
    boolean andaBaixo = false;

    boolean proxAndaDireita = false;
    boolean proxAndaEsquerda = false;
    boolean proxAndaCima = false;
    boolean proxAndaBaixo = false;

    SpriteBatch batch;
    Texture pacImg;
    TiledMap tiledMap;
    OrthographicCamera camera;
    TiledMapRenderer tiledMapRenderer;
    Sprite pac;

    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 600, 600);
        camera.update();
        tiledMap = new TmxMapLoader().load("maps/level1.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        pacImg = new Texture(Gdx.files.internal("sprites/pacman.png"));
        pac = new Sprite(pacImg);
        pac.setPosition(w / 2 - pac.getWidth() / 2, (h / 2 - pac.getHeight() / 2) - 48);

        //Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        MapObjects objects = tiledMap.getLayers().get(2).getObjects();

        ativaProxPasso();
        tentaProxPasso(objects);
        andar(objects);
        
        batch.begin();
        pac.draw(batch);
        batch.end();

    }

    @Override
    public void dispose() {
        batch.dispose();
        pacImg.dispose();
    }

    private boolean colidiu(MapObjects objects, int direcao) {
        float x = pac.getX();
        float y = pac.getY();
        
        switch(direcao){
            case 0: //esquerda
                x -= velocidade;
                break;
            case 1: //direita
                x += velocidade;
                break;
            case 2: //baixo
                y -= velocidade;
                break;
            case 3://cima
                y += velocidade;
                break;
        }
        Rectangle r = new Rectangle(x, y, pac.getWidth(), pac.getHeight());
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) {
            Rectangle rectangle = rectangleObject.getRectangle();           
            if (Intersector.overlaps(rectangle, r)) {
                return true;
            }
        }
        return false;
    }

    private void ativaProxPasso() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            proxAndaEsquerda = true;
            proxAndaDireita = false;
            proxAndaCima = false;
            proxAndaBaixo = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            proxAndaEsquerda = false;
            proxAndaDireita = true;
            proxAndaCima = false;
            proxAndaBaixo = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            proxAndaEsquerda = false;
            proxAndaDireita = false;
            proxAndaCima = true;
            proxAndaBaixo = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            proxAndaEsquerda = false;
            proxAndaDireita = false;
            proxAndaCima = false;
            proxAndaBaixo = true;
        }
    }

    private void tentaProxPasso(MapObjects objects) {
        if (proxAndaEsquerda) {
            if (!colidiu(objects,0)) {
                andaEsquerda = true;
                andaDireita = false;
                andaCima = false;
                andaBaixo = false;
            }
        }
        if (proxAndaDireita) {
            if (!colidiu(objects,1)) {
                andaEsquerda = false;
                andaDireita = true;
                andaCima = false;
                andaBaixo = false;
            }
        }
        if (proxAndaCima) {
            if (!colidiu(objects,3)) {
                andaEsquerda = false;
                andaDireita = false;
                andaCima = true;
                andaBaixo = false;
            }
        }
        if (proxAndaBaixo) {
            if (!colidiu(objects,2)) {
                andaEsquerda = false;
                andaDireita = false;
                andaCima = false;
                andaBaixo = true;
            }
        }
    }

    private void andar(MapObjects objects) {
        if (andaEsquerda) {
            if (!colidiu(objects,0)) {
                pac.translateX(-velocidade);
            }
        }
        if (andaDireita) {
            if (!colidiu(objects,1)) {
                pac.translateX(velocidade);
            }
        }
        if (andaCima) {
            if (!colidiu(objects,3)) {
                pac.translateY(velocidade);
            }
        }
        if (andaBaixo) {
            if (!colidiu(objects,2)) {
                pac.translateY(-velocidade);
            }
        }

    }
}
