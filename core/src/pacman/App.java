package pacman;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

public class App extends ApplicationAdapter {

    static final int ESQUERDA = 0;
    static final int DIREITA = 2;
    static final int BAIXO = 3;
    static final int CIMA = 1;
    static final int PARADO = 4;
    static final int NUM_FRAMES = 49;

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
    Texture sprites;
    TextureRegion frames[];
    TiledMap tiledMap;
    OrthographicCamera camera;
    TiledMapRenderer tiledMapRenderer;
    Pacman pacMan;

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
        sprites = new Texture(Gdx.files.internal("sprites/sprites.png"));
        pacMan = new Pacman(w, h, sprites,velocidade);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        MapObjects objects = tiledMap.getLayers().get(2).getObjects();

        //ativaProxPasso();
        //tentaProxPasso(objects);
        //andar(objects);
        pacMan.anda(objects);
        pacMan.animate();
        batch.begin();
        pacMan.personagem.draw(batch);
        batch.end();

    }

    @Override
    public void dispose() {
        batch.dispose();
        sprites.dispose();
    }

}
