package pacman;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class App extends ApplicationAdapter {

    static final int NUM_FRAMES_TOTAL = 49;
    static final int NUM_FRAMES_PACMAN = 20;
    static final int ESQUERDA = 0;
    static final int DIREITA = 2;
    static final int BAIXO = 3;
    static final int CIMA = 1;
    static final int PARADO = 4;

    static final int ESTADO_INICIO = 1;
    static final int ESTADO_JOGANDO = 2;
    static final int ESTADO_PACMAN_MORTO = 3;
    static final int ESTADO_FIM = 4;

    static final int VIDAS_INICIAIS = 3;

    float velocidade = 2f;

    int estadoJogo;
    int nivel;
    Texture sprites;
    TextureRegion frames[];
    TiledMap tiledMap;
    OrthographicCamera camera;
    TiledMapRenderer tiledMapRenderer;
    MapObjects paredes;
    MapObjects pontosDecisao;
    MapObjects doces;
    Pacman pacMan;
    Ghost ghosts[] = new Ghost[4];

    @Override
    public void create() {
        estadoJogo = ESTADO_INICIO;
        nivel = 1;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 600, 600);
        camera.update();
        tiledMap = new TmxMapLoader().load("maps/level1.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        paredes = tiledMap.getLayers().get(2).getObjects();
        pontosDecisao = tiledMap.getLayers().get(3).getObjects();
        doces = tiledMap.getLayers().get(4).getObjects();
        sprites = new Texture(Gdx.files.internal("sprites/sprites.png"));
        pacMan = new Pacman(w, h, geraSpritesPacMan(sprites, NUM_FRAMES_PACMAN), velocidade, VIDAS_INICIAIS);
        inicializaGhosts(ghosts, sprites, velocidade);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        switch (estadoJogo) {
            case ESTADO_INICIO:
                pacMan.animate();
                animaGhosts(ghosts);
                if (UsuarioIniciaJogo()) {
                    estadoJogo = ESTADO_JOGANDO;
                }
                break;
            case ESTADO_JOGANDO:
                pacMan.anda(paredes);
                andaGhosts(ghosts, paredes, pontosDecisao);
                pacMan.animate();
                animaGhosts(ghosts);
                if (pacMan.colidiuGhosts(ghosts)) {
                    estadoJogo = ESTADO_PACMAN_MORTO;
                }
                break;
            case ESTADO_PACMAN_MORTO:
                pacMan.animate();
                animaGhosts(ghosts);
                if (pacMan.terminouAnimacaoMorte()) {
                    if (pacMan.semVidas()) {
                        estadoJogo = ESTADO_FIM;
                    } else {
                        initPersonagens(pacMan, ghosts);
                        estadoJogo = ESTADO_INICIO;
                    }
                }
                break;
            case ESTADO_FIM:
                break;
        }

    }

    @Override
    public void dispose() {
        pacMan.batch.dispose();
        sprites.dispose();
    }

    private TextureRegion[] geraSpritesPacMan(Texture sprites, int numFrames) {
        TextureRegion frames[] = new TextureRegion[numFrames];
        for (int i = 0; i < numFrames; i++) {
            frames[i] = new TextureRegion(sprites, i * 24, 0, 24, 24);
        }
        return frames;
    }

    private void inicializaGhosts(Ghost[] ghosts, Texture sprites, float velocidade) {
        int offset = 20; //deslocamento para iniciar os frames dos ghosts em estado normal
        for (int i = 0; i < ghosts.length; i++) {
            TextureRegion frames[] = new TextureRegion[11];
            for (int j = 0; j < 5; j++) { //5 = frames do ghost no estado normal
                frames[j] = new TextureRegion(sprites, (offset++ * 24), 0, 24, 24);
            }
            int offsetComum = 40;
            for (int j = 5; j < 11; j++) {
                frames[j] = new TextureRegion(sprites, (offsetComum++ * 24), 0, 24, 24);
            }
            int x;
            int y;
            if (i != 3) {
                x = (i + 11) * 24;
                y = 12 * 24;
            } else {
                x = 12 * 24;
                y = 14 * 24;
            }
            ghosts[i] = new Ghost(x, y, frames, velocidade, i);
        }
    }

    private void animaGhosts(Ghost[] ghosts) {
        for (int i = 0; i < ghosts.length; i++) {
            ghosts[i].animate();
        }
    }

    private void andaGhosts(Ghost[] ghosts, MapObjects paredes, MapObjects pontosDecisao) {
        for (Ghost ghost : ghosts) {
            ghost.anda(paredes, pontosDecisao);
        }
    }

    public boolean UsuarioIniciaJogo() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            pacMan.direcaoAtual = App.ESQUERDA;
            return true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            pacMan.direcaoAtual = App.DIREITA;
            return true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            pacMan.direcaoAtual = App.CIMA;
            return true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            pacMan.direcaoAtual = App.BAIXO;
            return true;
        }
        return false;
    }

    private void initPersonagens(Pacman pacMan, Ghost[] ghosts) {
        pacMan.init(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        for (int i = 0; i < ghosts.length; i++) {
            int x;
            int y;
            if (i != 3) {
                x = (i + 11) * 24;
                y = 12 * 24;
            } else {
                x = 12 * 24;
                y = 14 * 24;
            }
            ghosts[i].init(x, y, i);
        }
    }

}
