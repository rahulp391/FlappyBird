package com.rahul.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture coin;
	Texture gameover;
	Texture[] birds;
	Texture toptube;
	Texture bottomtube;
	int flapstate=0;
	float birdY=0;
	float velocity=0;
	int gamestate=0;
	float gravity=2;
	int score=0;
	int scoringtube=0;
	int highscore;
	int chighscore;
	Preferences prefs;
	Preferences pref;
	float gap=500;
	float maxoffset;
	Random randomgenerator;
	int numberoftubes=4;
	float tubevelocity=4;
	float coinvelocity=4;
	float[] coiny=new float[4];
	float tubeoffset[]=new float[numberoftubes] ;
	float distance;
	float distancec;
	float tubex[]=new float[numberoftubes];
	Circle birdcircle;

	//ShapeRenderer shaperenderer;
	Rectangle[] toptuberectangles;
	Rectangle[] bottomtuberectangles;
	BitmapFont font;
	@Override
	public void create () {
		prefs = (Preferences) Gdx.app.getPreferences("My Preferences");
		Preferences pref = Gdx.app.getPreferences("My Preference");
	   highscore = prefs.getInteger("highsco", 0);
		chighscore = pref.getInteger("highsco", 0);
		batch=new SpriteBatch();
		birdcircle=new Circle();
		coin=new Texture("coin.png");

		gameover=new Texture("go.png");
		//shaperenderer=new ShapeRenderer();
		background=new Texture("bg.png");
		font=new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		birds=new Texture[2];
		birds[0]=new Texture("bird.png");
		birds[1]=new Texture("bird2.png");

		toptube=new Texture("toptube.png");
		bottomtube=new Texture("bottomtube.png");
		maxoffset=Gdx.graphics.getHeight()/2 - gap/2 - 100;
		randomgenerator=new Random();
		toptuberectangles=new Rectangle[numberoftubes];
		bottomtuberectangles=new Rectangle[numberoftubes];
		distance=Gdx.graphics.getWidth()*3 /4;


		startgame();
	}
	public void startgame()
	{
		birdY=Gdx.graphics.getHeight()/2 -  birds[0].getHeight()/2;
		for(int i=0;i<numberoftubes;i++)
		{
			tubeoffset[i]=(randomgenerator.nextFloat()-0.45f)*(Gdx.graphics.getHeight()-gap-450);
			tubex[i]= Gdx.graphics.getWidth() + i * distance ;

			toptuberectangles[i]=new Rectangle();
			bottomtuberectangles[i]=new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gamestate==1)
		{
			if(tubex[scoringtube] < Gdx.graphics.getWidth()/2)
			{
				score++;
				if(score==(chighscore+1))
				{
					chighscore++;

				}
				else if(score > chighscore) {

					prefs.putInteger("high", score);
					prefs.flush();


				}

				if (scoringtube < numberoftubes-1)
				{
					scoringtube++;
				}
				else
				{
					scoringtube=0;
				}
			}

			if(Gdx.input.justTouched())
			{
				velocity=-30;

			}
			for(int i=0;i<numberoftubes;i++) {
				if(tubex[i]< -toptube.getWidth())
				{
					tubex[i]+=numberoftubes*distance;
					tubeoffset[i]=(randomgenerator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-200);
				}
				else {
					tubex[i] = tubex[i] - tubevelocity;

				}

				batch.draw(toptube, tubex[i], Gdx.graphics.getHeight()/2+ gap / 2 + tubeoffset[i]);
				batch.draw(bottomtube, tubex[i], Gdx.graphics.getHeight()/2- gap / 2 - bottomtube.getHeight() + tubeoffset[i]);

				toptuberectangles[i]=new Rectangle(tubex[i],Gdx.graphics.getHeight()/2+ gap / 2 + tubeoffset[i],toptube.getWidth(),toptube.getHeight());
				bottomtuberectangles[i]=new Rectangle( tubex[i], Gdx.graphics.getHeight()/2- gap / 2 - bottomtube.getHeight() + tubeoffset[i],bottomtube.getWidth(),bottomtube.getHeight());
			}

			if(birdY>0 ) {
				velocity += gravity;
				birdY -= velocity;
			}
			else
			{
				gamestate=2;
			}
		}
		else if(gamestate==0)
		{
			if(Gdx.input.justTouched())
			{
				gamestate=1;
			}
		}
		else if(gamestate==2)
		{
			if(score > highscore){

				prefs.putInteger("highsco", score);
				prefs.flush();
			}
			batch.draw(gameover,Gdx.graphics.getWidth()/2 - gameover.getWidth()/2,Gdx.graphics.getHeight()/2 - gameover.getHeight()/2);
			if(Gdx.input.justTouched())
			{
				gamestate=1;
				startgame();
				score=0;
				scoringtube=0;
				velocity=0;
			}
		}

		if(flapstate==0)
		{
			flapstate=1;
		}
		else
		{
			flapstate=0;
		}

		batch.draw(birds[flapstate],Gdx.graphics.getWidth()/2 -  birds[flapstate].getWidth()/2,birdY);
		font.draw(batch,String.valueOf(score),100,2000);
		font.draw(batch,String.valueOf(highscore),500,200);
		font.draw(batch,String.valueOf(chighscore),900,2000);
		batch.end();

		birdcircle.set(Gdx.graphics.getWidth()/2,birdY+birds[flapstate].getHeight()/2,birds[flapstate].getWidth()/2);

		//shaperenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shaperenderer.setColor(Color.BLACK);
		//shaperenderer.circle(birdcircle.x,birdcircle.y,birdcircle.radius);

		for(int i=0;i<numberoftubes;i++)
		{
			//shaperenderer.rect(tubex[i],Gdx.graphics.getHeight()/2+ gap / 2 + tubeoffset[i],toptube.getWidth(),toptube.getHeight());
			//shaperenderer.rect(tubex[i], Gdx.graphics.getHeight()/2- gap / 2 - bottomtube.getHeight() + tubeoffset[i],bottomtube.getWidth(),bottomtube.getHeight());
			if(Intersector.overlaps(birdcircle,toptuberectangles[i]) || Intersector.overlaps(birdcircle,bottomtuberectangles[i]))
			{
				gamestate = 2;
			}
		}

		//shaperenderer.end();
	}


}