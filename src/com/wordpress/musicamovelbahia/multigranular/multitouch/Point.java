package com.wordpress.musicamovelbahia.multigranular.multitouch;

import processing.core.PApplet;
import processing.core.PVector;

/** Classe para identificar cada ponto de toque
 * @author javier
 */
class Point {

	float posX,posY;
	int pointID;
	int textSize = 15;
	PApplet p5;
	PVector pos;
	float grainWidth;
	
	Point(PApplet _p5, int id, float x, float y) {
		p5 = _p5;
		pointID = id;
		posX    = x;
		posY    = y;
		pos = new PVector (posX, posY);
		grainWidth = 0;
	}
	
	void update(float x, float y) {
		posX = x;
		posY = y;
		pos = new PVector (posX, posY);
	}
	
	void drawIt() {

    	p5.fill(255, 0, 0, ((posY/p5.height)*240)+15);
    	p5.rect(posX, 0, grainWidth, p5.height);
//    	p5.textSize(textSize);
//    	p5.text("X: " + posX + " Y: " + posY, posX-100, posY-100);
//    	p5.text("ID: " + pointID, posX-100, posY-100-textSize);
	}
	
	public PVector getPVectorPos(){
		return pos;
	}
	public void setGrainWidth(float gw) {
		grainWidth = gw;
	}
}

