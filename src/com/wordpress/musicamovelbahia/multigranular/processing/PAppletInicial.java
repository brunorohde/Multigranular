package com.wordpress.musicamovelbahia.multigranular.processing;

/* IMPORTANTE a ter presente:
 * 
 * 1- Para poder importar PApplet, temos primeiro que botar na pasta "libs" o arquivo:
 * processing-android-core.jar que pode ser encontrado nas pasta Processing/Core ou
 * http://www.java2s.com/Code/Jar/p/Downloadprocessingandroidcorejar.htm
 * 
 * 2- Cada sketch de processing que é criado pelo meio de um 'intent' tem que ser declarado
 * no AndroidManifest.xml
 * 
 * 4- No final da classe tem os metodos proprios do ciclo de vida de uma Activity em Android
 * E importante por que no onCreate pegamos dados que podem ser enviados desde o Main Activity
 * 
 * 3- Ao utilizar float tem que agregar ao numero uma 'f' no final. Por exemplo 3.14f
 */

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.puredata.core.PdBase;

import com.wordpress.musicamovelbahia.multigranular.multitouch.MTListenerCallBack;
import com.wordpress.musicamovelbahia.multigranular.multitouch.MultiTouchP;
import com.wordpress.musicamovelbahia.multigranular.pdstuff.PdListenerCallBack;
import com.wordpress.musicamovelbahia.multigranular.pdstuff.PureDataManager;

import controlP5.*;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import processing.core.PApplet;
import processing.core.PFont;

import com.wordpress.musicamovelbahia.multigranular.processing.BufferDraw;


public class PAppletInicial extends PApplet  implements PdListenerCallBack,MTListenerCallBack {
	
	private static final String TAG = "PAppletInicial";
	String stringVal;
	float numeroFloat;
	PureDataManager pdManager;
	MultiTouchP multiTouch;
	BufferDraw bufferDraw;
	
	ControlP5 cp5;
	
	public String audioTrack;
	
	public float grain = 0.25f;
	
	public String saveFilePath;
	public String saveFileName;
	
	public void setup(){
		
		multiTouch = new MultiTouchP(this);
		
		multiTouch.setGrain(grain);
		
		desenhaOnda();
		
		cp5 = new ControlP5(this);
		
		Group grupo = cp5.addGroup("Grupo")
			.setPosition(width-(width/15), height/15)
			.setColorBackground(color(200))
			.setColorForeground(color(200))
			.setWidth(width/15)
			.setBackgroundColor(color(100))
			.setBackgroundHeight(height)
			.setBarHeight(height/15)
			.setColorLabel(color(0,0,0,0))
			.setLabel("")
			.setOpen(false)
			;
		
		Button openFile = cp5.addButton("openFile")
			.setPosition(width/120, height*.03125f)
			.setSize(width/20,height/15)
			.setColorBackground(color(200))
			.setColorForeground(color(200))
			.setColorActive(color(255,0,0))
			.setLabelVisible(false)
			.plugTo(this)
			.setGroup(grupo)
			;
		
		Slider grainSize = cp5.addSlider("grainSize")
			.setPosition(width/120,height*.125f)
			.setSize(width/20,(int)(height*.31))
			.setColorBackground(color(200))
			.setColorForeground(color(255,0,0))
			.setColorActive(color(255,0,0))
			.setLabelVisible(false)
			.setRange(0.01f,1)
			.setValue(.25f)
			.plugTo(this)
			.setGroup(grupo)
			;
		
		Slider delayTime = cp5.addSlider("delayTime")
			.setPosition(width/120,height*.46f)
			.setSize(width/20,height/6)
			.setColorBackground(color(200))
			.setColorForeground(color(255,0,0))
			.setColorActive(color(255,0,0))
			.setLabelVisible(false)
			.setRange(0,1)
			.setValue(0)
			.plugTo(this)
			.setGroup(grupo)
			;
		
		Slider delayFB = cp5.addSlider("delayFB")
			.setPosition(width/120,height*.64f)
			.setSize(width/20,height/6)
			.setColorBackground(color(200))
			.setColorForeground(color(255,0,0))
			.setColorActive(color(255,0,0))
			.setLabelVisible(false)
			.setRange(0,1)
			.setValue(0)
			.plugTo(this)
			.setGroup(grupo)
			;
		
		Toggle record = cp5.addToggle("record")
			.setPosition(width/120, height*.835f)
			.setSize(width/20,height/15)
			.setColorBackground(color(80,0,0))
			.setColorForeground(color(80,0,0))
			.setColorActive(color(255,0,0))
			.setLabelVisible(false)
			.plugTo(this)
			.setGroup(grupo)
			;
		
		cp5.addCallback(new CallbackListener() {
	    	
	        public void controlEvent(CallbackEvent theEvent) {
	        	
	        	if(theEvent.getController().getLabel() == "openFile"){
	        	
	        		switch(theEvent.getAction()) {
	        		
	        	      case(ControlP5.ACTION_RELEASED):
	        	    	  
	        	    	  escolheArquivo(1);
	        	    	  
	        	      break;
	        	    }	
	        	}
	        	
	        	if(theEvent.getController().getLabel() == "grainSize"){
	        		
	        		grain = theEvent.getController().getValue();
	        		multiTouch.setGrain(grain);
	        		PdBase.sendFloat("grainSize", grain);
	        		
	        	}
	        	
	        	if(theEvent.getController().getLabel() == "delayTime"){
	        		
	        		PdBase.sendFloat("delayTime", theEvent.getController().getValue());
	        		
	        	}
	        	
	        	if(theEvent.getController().getLabel() == "delayFB"){
	        		
	        		PdBase.sendFloat("delayFB", theEvent.getController().getValue());
	        		
	        	}
	        	
	        	if(theEvent.getController().getLabel() == "record"){

        			switch(theEvent.getAction()) {

					case (ControlP5.ACTION_PRESSED):

						if (theEvent.getController().getValue() == 1) {
							startRec();
						}

						if (theEvent.getController().getValue() == 0) {
							stopRec();
						}

	        	      break;
		        	}
        		}

	        }
	    });
		
	}
	
	public void draw() {
		background(0);
		multiTouch.drawInfo();
		bufferDraw.bufferDraw();
	}
	
	public void desenhaOnda(){
		
		float [] newArray1 = pdManager.getArrayFromPd("1-audioL");
		
	    bufferDraw = new BufferDraw(this, newArray1);
		
	}
	
	public void escolheArquivo(int seletor){
		
		startActivityForResult(new Intent("com.wordpress.musicamovelbahia.multigranular.processing.FileChooser"), seletor);
		
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				audioTrack = data.getData().toString();
				PdBase.sendSymbol("caminho", audioTrack);
				desenhaOnda();
			}
		}
		
	}

	public void startRec() {
		prepareRec();
		PdBase.sendFloat("Rec", 1);
	}

	public void stopRec () {
		PdBase.sendFloat("Rec", 0);
	}

	private void prepareRec() {

		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/Multigranular");
		myDir.mkdirs();
		SimpleDateFormat formatter = new SimpleDateFormat("MMddHHmm");
		Date now = new Date();
		String fileName = formatter.format(now);
		String fname = "rec_" + fileName;
		saveFilePath = myDir.getAbsolutePath();
		saveFileName = fname + ".wav";
		PdBase.sendSymbol("recPath", myDir + "/" + fname);
	}  
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		//pegamos os valores "extras" que enviamos desde o MainActivity
		Bundle extras=getIntent().getExtras(); //criamos um objeto que pega todos os extras existentes no intent
		stringVal = extras.getString("Um String"); // pegamos o valor segundo a etiqueta dada no MainActivity
		numeroFloat = extras.getFloat("Um float"); // pegamos o valor segundo a etiqueta dada no MainActivity
		
	//Inicializaçāo do objeto que vai gerenciar o Pd
		pdManager = new PureDataManager(this);
		pdManager.openPatch("MULTIGRANULAR.pd", com.wordpress.musicamovelbahia.multigranular.R.raw.patch); 
		pdManager.setTicksPerBuffer(1);
		pdManager.setChanelIn(0);
	//Agrega todos os Strings chaves que vāo receber dados
		pdManager.addSendMessagesFromPD("Sfloat_0");
		pdManager.addSendMessagesFromPD("Sfloat_1");
	}
	@Override
	public void onResume() {
		super.onResume();
		pdManager.onResume();
	}
	@Override
	public void onPause() {
		super.onPause();
		pdManager.onPause();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		pdManager.onDestroy();
	}
	@Override
	public void finish() {
		super.finish();
		pdManager.finish();
	}

	
//CALLBACKS FROM PD As seguentes funçōes sāo chamadas quando tiver alguma mensagem nova desde PD
	@Override
	public void callWhenReceiveFloat(String key, float val) {
		// TODO Auto-generated method stub
//		Log.i(TAG, "recebendo do PD = " + key + ": " + val );
	}
	@Override
	public void callWhenReceiveBang(String key) {
		// TODO Auto-generated method stub	
	}
	@Override
	public void callWhenReceiveSymbol(String key, String symbol) {
		// TODO Auto-generated method stub
	}
	@Override
	public void callWhenReceiveList(String key, Object... args) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void callWhenReceiveMessage(String key, String symbol,
			Object... args) {
		// TODO Auto-generated method stub
	}
	
//Método callback, que é chamado cada vez que tem um novo evento na tela
	@Override
	public void screenTouched(int id, float x, float y) {
		// TODO Auto-generated method stub
		Log.i(TAG, "mutiTouch, toque em x:" + x + " y: " + y + " e id: " + id );
			float x2 = map(x, 0, width, 0, 1);
			float y2 = map(y, 0, height, 0, 1);
			PdBase.sendList("listaIn", id, 1, x2, y2);
	}
	@Override
	public void screenTouchedReleased(int id) {
		// TODO Auto-generated method stub
		Log.i(TAG, "mutiTouch, released no id: " + id );
		PdBase.sendList("listaIn", id, 0);
	}

	@Override
	public void screenTouchedDragged(int id, float x, float y, float dist, float ang) {
		// TODO Auto-generated method stub
		Log.i(TAG, "Touch dragged, toque em x:" + x + " y: " + y + " e id: " + id + " dist: "+dist+" angulo: "+ang);
		float x2 = map(x, 0, width, 0, 1);
		float y2 = map(y, 0, height, 0, 1);
		PdBase.sendList("listaIn", id, 1, x2, y2);
	}

	public boolean surfaceTouchEvent(MotionEvent me) {
		multiTouch.surfaceTouchEvent(me);
	    return super.surfaceTouchEvent(me);
	}
	
}
