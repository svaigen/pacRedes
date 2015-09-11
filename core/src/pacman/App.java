package pacman;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class App extends ApplicationAdapter {

    static final int NUM_FRAMES_TOTAL = 49;
    static final int NUM_FRAMES_PACMAN = 20;
    static final int ESQUERDA = 0;
    static final int CIMA = 1;
    static final int DIREITA = 2;
    static final int BAIXO = 3;
    static final int PARADO = 4;

    static final int ESTADO_ABERTURA = 0;
    static final int ESTADO_INICIO = 1;
    static final int ESTADO_JOGANDO = 2;
    static final int ESTADO_PACMAN_MORTO = 3;
    static final int ESTADO_FIM = 4;
    static final int ESTADO_NIVEL_COMPLETO = 5;

    static final int VIDAS_INICIAIS = 3;
    static final int PONTO_DOCE_PEQUENO = 10;
    static final int PONTO_DOCE_GRANDE = 50;
    static final int PONTO_GHOST = 250;
    static final int PONTO_MORRE = -500;
    static final int PONTO_FRUTA = 500;

    static final int COORDENADA_BASE_GHOST_X = 12 * 24;
    static final int COORDENADA_BASE_GHOST_Y = 14 * 24;

    float velocidadePac;
    float velocidadeGhost;
    float w;
    float h;
    int pontos;

    int estadoJogo;
    int nivel;
    int docesRestantes;
    Texture sprites;
    TextureRegion frames[];
    TiledMap tiledMap;
    OrthographicCamera camera;
    TiledMapRenderer tiledMapRenderer;
    MapObjects paredes;
    MapObjects pontosDecisao;
    TiledMapTileLayer docesTiled;
    Pacman pacMan;
    Ghost ghosts[] = new Ghost[4];
    Fruta fruta;
    BitmapFont font;
    SpriteBatch batch;
    Map<String, Doce> doces = new HashMap<String, Doce>();
    long inicio, fim, tempo;

    @Override
    public void create() {
        estadoJogo = ESTADO_ABERTURA;
        nivel = 1;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 600, 600);
        camera.update();
        tiledMap = new TmxMapLoader().load("maps/inicio.tmx");
        font = new BitmapFont();
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        batch = new SpriteBatch();
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        pontos = 0;
        tempo = 0;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        switch (estadoJogo) {
            case ESTADO_ABERTURA:
                velocidadePac = 2f;
                velocidadeGhost = 2f;
                if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
                    carregaNivel("maps/level1.tmx", 2, 3, 1, velocidadePac, velocidadeGhost, geraPosicoesDocesGrandes(), 0);
                    estadoJogo = ESTADO_INICIO;
                } else {
                    batch.begin();
                    font.draw(batch, "Aperte qualquer tecla para começar", 180, 150);
                    batch.end();
                }
                break;
            case ESTADO_INICIO:
                pacMan.animate();
                animaGhosts(ghosts);
                if (UsuarioIniciaJogo()) {
                    estadoJogo = ESTADO_JOGANDO;
                }
                escreveInformacoes();
                break;
            case ESTADO_JOGANDO:
                inicio = System.currentTimeMillis();
                fruta.animate();
                pacMan.anda(paredes);
                andaGhosts(ghosts, paredes, pontosDecisao);
                pacMan.animate();
                animaGhosts(ghosts);

                /*Verficacao de colisao do pacman com fantasmas*/
                int idGhost = pacMan.colidiuGhosts(ghosts);
                if (idGhost != -1) {
                    if (ghosts[idGhost].isNormal()) {
                        pontos += PONTO_MORRE;
                        estadoJogo = ESTADO_PACMAN_MORTO;
                    } else if (ghosts[idGhost].isVulneravel()) {
                        ghosts[idGhost].estado = Ghost.ESTADO_OLHOS;
                        pontos += PONTO_GHOST;
                    }
                }

                verificaComeuDoce(pacMan, doces);

                if (fruta.foiComida(pacMan)) {
                    pontos += PONTO_FRUTA;
                }
                escreveInformacoes();
                fim = System.currentTimeMillis();
                tempo+= (fim - inicio);
                break;
            case ESTADO_PACMAN_MORTO:
                fruta.reiniciaProbabilidade();
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
                escreveInformacoes();
                break;
            case ESTADO_NIVEL_COMPLETO:
                nivel++;
                switch (nivel) {
                    case 2:
                        carregaNivel("maps/level2.tmx", 2, 3, 1, velocidadePac, velocidadeGhost, geraPosicoesDocesGrandes(), 1);
                        estadoJogo = ESTADO_INICIO;
                        break;
                    case 3:
                        velocidadeGhost = 3f;
                        velocidadePac = velocidadeGhost;
                        carregaNivel("maps/level3.tmx", 2, 3, 1, velocidadePac, velocidadeGhost, geraPosicoesDocesGrandes(), 1);
                        estadoJogo = ESTADO_INICIO;
                        break;
                    default:
                        estadoJogo = ESTADO_FIM;
                }
                break;
            case ESTADO_FIM:
                this.pontos = 0;
                this.nivel = 1;
                tiledMap = new TmxMapLoader().load("maps/inicio.tmx");
                tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
                estadoJogo = ESTADO_ABERTURA;
                break;
        }
    }

    public void escreveInformacoes() {
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Pontos: " + pontos, 10, 20);
        font.draw(batch, "Vidas: " + pacMan.vidas, 10, 40);
        font.draw(batch, "Nível " + nivel, 10, 60);
        font.draw(batch, "Tempo " + tempo/10, 450, 20);
        batch.end();
    }

    @Override
    public void dispose() {
        font.dispose();
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

    private void inicializaGhosts(Ghost[] ghosts, Texture sprites, float velocidade, int numSeguidoresPac) {
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
            ghosts[i] = new Ghost(x, y, frames, velocidade, i, (numSeguidoresPac--) > 0, nivel);
        }
    }

    private void animaGhosts(Ghost[] ghosts) {
        for (int i = 0; i < ghosts.length; i++) {
            ghosts[i].animate();
        }
    }

    private void andaGhosts(Ghost[] ghosts, MapObjects paredes, MapObjects pontosDecisao) {
        for (Ghost ghost : ghosts) {
            if (ghost.isSeguidorPacMan() && ghost.isNormal()) {
                ghost.anda(paredes, pontosDecisao, pacMan.personagem.getX(), pacMan.personagem.getY());
            } else {
                ghost.anda(paredes, pontosDecisao, COORDENADA_BASE_GHOST_X, COORDENADA_BASE_GHOST_Y);
            }
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

    private void carregaNivel(String caminhoMapa, int indiceParedes,
            int indicePontosDecisao, int indiceDoces, float velocidadePacMan,
            float velocidadeGhosts, String[] posicoesDocesGrandes, int numGhostsSeguemPac) {
        tiledMap = new TmxMapLoader().load(caminhoMapa);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        paredes = tiledMap.getLayers().get(indiceParedes).getObjects();
        pontosDecisao = tiledMap.getLayers().get(indicePontosDecisao).getObjects();
        docesTiled = (TiledMapTileLayer) tiledMap.getLayers().get(indiceDoces);
        doces = realizaMapeamentoDoces(docesTiled, posicoesDocesGrandes);
        sprites = new Texture(Gdx.files.internal("sprites/sprites.png"));
        pacMan = new Pacman(w, h, geraSpritesPacMan(sprites, NUM_FRAMES_PACMAN), velocidadePacMan, VIDAS_INICIAIS);
        fruta = new Fruta(w, h, new TextureRegion(sprites, 46 * 24, 0, 24, 24));
        docesRestantes = 236;
        inicializaGhosts(ghosts, sprites, velocidadeGhosts, numGhostsSeguemPac);
    }

    private void verificaComeuDoce(Pacman pacMan, Map doces) {
        int tipoDoce = pacMan.comeDoce(doces);
        if (tipoDoce != -1) { //se o pacman comeu algum tipo de doce
            if (tipoDoce == 1) {//se foi um doce grande
                for (Ghost ghost : ghosts) {
                    ghost.transformaVulneravel();
                    pontos += PONTO_DOCE_GRANDE;
                }
            } else {
                pontos += PONTO_DOCE_PEQUENO;
            }
            docesRestantes--;
            if (docesRestantes == 0) {
                estadoJogo = ESTADO_NIVEL_COMPLETO;
            }
        }
    }

    private Map<String, Doce> realizaMapeamentoDoces(TiledMapTileLayer docesTiled, String[] posicoesDocesGrandes) {
        Map<String, Doce> mapeamento = new HashMap<String, Doce>();
        for (int x = 0; x < 25; x++) {
            for (int y = 0; y < 25; y++) {
                if (docesTiled.getCell(x, y) != null) {
                    String chaveHash = x + "-" + y;
                    if (isDoceGrande(chaveHash, posicoesDocesGrandes)) {
                        mapeamento.put(chaveHash, new Doce(x, y, Doce.TIPO_GRANDE, false, docesTiled));
                    } else {
                        mapeamento.put(chaveHash, new Doce(x, y, Doce.TIPO_PEQUENO, false, docesTiled));
                    }
                }
            }
        }
        return mapeamento;
    }

    private String[] geraPosicoesDocesGrandes() {
        String[] posicoes = new String[4];
        posicoes[0] = "3-8";
        posicoes[1] = "21-8";
        posicoes[2] = "3-18";
        posicoes[3] = "21-18";
        return posicoes;
    }

    private boolean isDoceGrande(String chaveHash, String[] posicoesDocesGrandes) {
        for (String posicao : posicoesDocesGrandes) {
            if (posicao.equalsIgnoreCase(chaveHash)) {
                return true;
            }
        }
        return false;
    }

}
