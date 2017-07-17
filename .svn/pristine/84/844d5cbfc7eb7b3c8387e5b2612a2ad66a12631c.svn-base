package cwinsor.com.aMain;


import cwinsor.com.speech_recognition.*;
import cwinsor.com.controlThread.ControlThread;
import cwinsor.com.freetts.BasicFreetts;
import cwinsor.com.lejos.*;
import cwinsor.com.loadImageApp.LoadImageApp;


public class AMain {

	private static final int MAX_TEST_DURATION = (60 * 60 * 1000);

	static LoadImageApp loadImageApp;
	static SpeechRecognition speechRecognition;
	static ControlThread controlThread;
	static BasicLejos basicLejos;
	static BasicFreetts basicFreetts;


	


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("main: ----- new the elements");
		loadImageApp = new LoadImageApp();
		speechRecognition = new SpeechRecognition();
		controlThread = new ControlThread();
		basicLejos = new BasicLejos();
		basicFreetts = new BasicFreetts();

		System.out.println("main: ----- configure the callbacks");
		speechRecognition.addListener(controlThread);

		System.out.println("main: ----- create the elements");
		loadImageApp.create();
		speechRecognition.create();
		controlThread.create(loadImageApp, basicLejos, basicFreetts);
		basicLejos.create();
		basicFreetts.create(new String[] { });

		System.out.println("main: ----- start the elements");
		speechRecognition.start();
		controlThread.start();
		basicLejos.start();
		basicFreetts.start();	


		boolean done = false;
		while (!done) {

			// a basic delay for the state loo;
			done = sleepSafe(MAX_TEST_DURATION);

		}
		System.out.println("main: ----- done");
	}



	/*
	 * sleepSafe
	 */
	private static boolean sleepSafe(int time) {

		try {
			Thread.currentThread().sleep(time);
		} catch (InterruptedException e) {
			System.out.println("main:sleepSafe " + "----- InterruptException seen... returning true");
			return true;
		}
		return false;
	}




}
