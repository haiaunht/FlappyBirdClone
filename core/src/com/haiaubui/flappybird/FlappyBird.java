package com.haiaubui.flappybird;import com.badlogic.gdx.ApplicationAdapter;import com.badlogic.gdx.Gdx;import com.badlogic.gdx.graphics.Color;import com.badlogic.gdx.graphics.GL20;import com.badlogic.gdx.graphics.Texture;import com.badlogic.gdx.graphics.g2d.BitmapFont;import com.badlogic.gdx.graphics.g2d.SpriteBatch;import com.badlogic.gdx.graphics.glutils.ShapeRenderer;import com.badlogic.gdx.math.Circle;import com.badlogic.gdx.math.Intersector;import com.badlogic.gdx.math.Rectangle;import java.util.Random;public class FlappyBird extends ApplicationAdapter {	SpriteBatch batch;	Texture background;	Texture gameover;	//ShapeRenderer shapeRenderer ;	//for the bird, there are two bird picture -> use array	Texture[] birds;	//flap will be 0 or 1, and birds[flapState] will eliminate the repeat	int flapState = 0;	//x is always in the middle, only y pos is move up or down	float birdY = 0;	float velocity = 0;	Circle birdCircle;	//keep track the state of the game	int gameState = 0;	float gravity = 2;	Texture topTube;	Texture bottomTube;	float gap = 600;	float maxTubeOffset;	Random randomGenerator;	float tubeVelocity = 4;	int numberOfTubes = 4;	float[] tubeX = new float[numberOfTubes];	float[] tubeOffset = new float[numberOfTubes];	float distanceBetweenTubes;	Rectangle[] topTubeRectangle;	Rectangle[] bottomTubeRectangle;	//score	int score = 0;	int scoringTube = 0;	//font	BitmapFont font;	@Override	public void create () {		batch = new SpriteBatch();		background = new Texture("bg.png");		gameover = new Texture("gameover.png");		//shapeRenderer = new ShapeRenderer();		birdCircle = new Circle();		font = new BitmapFont();		font.setColor(Color.WHITE);		font.getData().setScale(10);		birds = new Texture[2];		birds[0] = new Texture("bird.png");		birds[1] = new Texture("bird2.png");		topTube = new Texture("toptube.png");		bottomTube = new Texture("bottomtube.png");		maxTubeOffset = Gdx.graphics.getHeight()/2-gap/2-100;		randomGenerator = new Random();		distanceBetweenTubes = Gdx.graphics.getWidth() *3/4 ;		topTubeRectangle = new Rectangle[numberOfTubes];		bottomTubeRectangle = new Rectangle[numberOfTubes];		startGame();	}	public void startGame(){		birdY = Gdx.graphics.getHeight()/2 - birds[0].getHeight()/2;		for(int i=0; i<numberOfTubes; i++){			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);			tubeX[i] = Gdx.graphics.getWidth()/2 - topTube.getWidth()/2 + Gdx.graphics.getWidth() +  i*distanceBetweenTubes;			//rectangle			topTubeRectangle[i] = new Rectangle();			bottomTubeRectangle[i] = new Rectangle();		}	}	@Override	public void render () {		//begin display the batch		batch.begin();		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());		if( gameState ==1 ) {			//check the score of each tube, there are 4 tubes			if(tubeX[scoringTube] < Gdx.graphics.getWidth() /2){				score++;				Gdx.app.log("Score", String.valueOf(score));				if( scoringTube < numberOfTubes - 1){					scoringTube++;				}else{					scoringTube =0;				}			}			if( Gdx.input.justTouched() ){				velocity = -30;			}			for(int i=0; i<numberOfTubes; i++ ) {				//if tubes of the screen, repeat again				if(tubeX[i] < - topTube.getWidth()){					tubeX[i] += numberOfTubes * distanceBetweenTubes;					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);				}else {					tubeX[i] = tubeX[i] - tubeVelocity;				}				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);				//draw rectangle for each tube				topTubeRectangle[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight() );				bottomTubeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());			}			//when the bird out of the screen			if( birdY > 0  ) {				//increase velocity each time the render loop is called, decrease the y				velocity = velocity + gravity;				birdY -= velocity;			}else{				gameState = 2; //gameover			}		}else if( gameState == 0){			if(Gdx.input.justTouched()){				Gdx.app.log("I did tap", "Yes");				gameState = 1;			}		}else if (gameState == 2){	//game over			batch.draw(gameover, Gdx.graphics.getWidth()/2-gameover.getWidth()/2, Gdx.graphics.getHeight()/2-gameover.getHeight()/2);			if(Gdx.input.justTouched()){				Gdx.app.log("I did tap", "Yes");				gameState = 1;				startGame();				score=0;				scoringTube=0;				velocity=0;			}		}		if (flapState == 0) {			flapState = 1;		} else {			flapState = 0;		}		//draw the bird at center - get left of half of the bird and half up top		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);		font.draw(batch, String.valueOf(score), 100, 200);		birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birds[flapState].getHeight()/2, birds[flapState].getWidth()/2);		//show the score		//circle of the bird		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);		//shapeRenderer.setColor(Color.RED);		//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);		//rectangle shape over tubes		for(int i=0; i<numberOfTubes; i++ ) {			//shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());			if(Intersector.overlaps(birdCircle, topTubeRectangle[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangle[i])){				gameState = 2;			}		}		batch.end();		//shapeRenderer.end();	}	}