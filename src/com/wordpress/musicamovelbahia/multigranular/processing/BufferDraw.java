package com.wordpress.musicamovelbahia.multigranular.processing;

import processing.core.PApplet;
import processing.core.PGraphics;

/* IMPORTANTE para ter em conta na hora de programar
 * 
 * 1- Quando cria uma nova clase o mínimo necessario é criar uma variavel de tipo PApplet
 * essa variavel vai permitir usar dentro da clase os metodos de processing.
 * 
 * 2- é preciso declara se as variaveis e os metodos sāo public ou private.
 * tem em conta que em Processing todos os metodos e variaveis sāo public por padrāo
 */

public class BufferDraw {

    PGraphics Bdraw; //imagen que vāo guardar o desenho da onda
    public float[] BufferIn;  //array que vāo receber de PD
    public int bufferSize; //o tamanho do buffer (ou seja do array)
    PApplet p5; //objeto que vāo permitir utilizar métodos de Processing na clase
    public int drawWidth, drawHeight, centerBufferDraw; //dados do tamanho e posiçāo do desenho
   
    BufferDraw (PApplet _p5, float[] bufferArrayIn) {
        p5 = _p5;
        BufferIn = bufferArrayIn;
        bufferSize = BufferIn.length;
        drawWidth = (int)(p5.width);
        drawHeight = (int)(p5.height);
        centerBufferDraw = drawHeight;
        Bdraw = p5.createGraphics(drawWidth, drawHeight);
        createBufferDraw();
    }
    
    public void bufferDraw() {
        p5.image(Bdraw, 0, 0, p5.width, p5.height);
    }
   
    private void createBufferDraw(){
        p5.pushMatrix();
        p5.pushStyle();
        Bdraw.beginDraw();
        Bdraw.fill(0, 0);
        Bdraw.rect(0, 0, drawWidth, drawHeight);
        Bdraw.stroke(255, 255);
        float escaleBuf = bufferSize/(int)(p5.width*2);
        for (float i=escaleBuf ; i < bufferSize ; i+=escaleBuf) {
            float Y2 = BufferIn[(int)i];
            float Y1 = BufferIn[(int)(i-escaleBuf)];
            Y2 = PApplet.map(Y2, -1,1, drawHeight, 0);
            float X1 = PApplet.map(i, 0, bufferSize, 0, drawWidth);
            Y1 = PApplet.map(Y1, -1,1, drawHeight, 0);
            float X2 = PApplet.map(i, 0, bufferSize, 0, drawWidth);
            Bdraw.line (X1, Y1, X2, Y2);
        }
        Bdraw.endDraw();
        p5.popStyle();
        p5.popMatrix();
    }
   
 
}
