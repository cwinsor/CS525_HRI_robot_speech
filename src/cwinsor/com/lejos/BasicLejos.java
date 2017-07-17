package cwinsor.com.lejos;

import java.io.IOException;
import java.util.Random;

import lejos.nxt.*;
import lejos.nxt.remote.NXTCommand;



import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTCommandConnector;
import lejos.pc.comm.NXTConnector;


/**
 * Sample to spin motors and output Tachometer counts.
 * This sample shows how to control which NXT to connect to and switch on full logging.
 * 
 * @author Lawrie Griffiths and Brian Bagnall
 *
 */
public class BasicLejos extends Thread {

	/**
	 * verbose switch
	 */
	int VERBOSE = 2;


	private static final int FACE_CENTER_APPROXIMATE_HORIZONTAL = 0;
	private static final int FACE_CENTER_APPROXIMATE_VERTICAL = 0;

	private static final int	SHAMED_OFFSET_VERTICAL = 30;

	private static final int SCREEN_CENTER_APPROXIMATE_HORIZONTAL = 100;
	private static final int SCREEN_CENTER_APPROXIMATE_VERTICAL = -20;


	//private static final int MOTOR_SPEED_DEFAULT_NOT_TOO_FAST = 150;
	private static final int MOTOR_SPEED_HORIZONTAL = 120;
	private static final int MOTOR_SPEED_VERTICAL = 90;



	// connection to NXT
	NXTConnector conn;

	int lastH = 0;
	int currentH = 0;
	int lastV = 0;
	int currentV = 0;

	// random number generator
	Random randomGenerator;


	/**
	 * create
	 */
	public void create() {
		System.out.println(this.getClass().getPackage().getName() + ":----- entering");


		//a random generator object
		randomGenerator = new Random();


		conn = new NXTConnector();
		conn.addLogListener(new NXTCommLogListener() {
			public void logEvent(String message) {
				System.out.println(message);				
			}

			public void logEvent(Throwable throwable) {
				System.err.println(throwable.getMessage());			
			}			
		});
		conn.setDebug(true);

		System.out.println(this.getClass().getPackage().getName() + ":----- issue the conn.connectTo(usb)");
		if (!conn.connectTo("usb://")){
			System.err.println("No NXT found using USB");
			System.exit(1);
		}


		System.out.println(this.getClass().getPackage().getName() + "----- do a setNXTCommand()");

		//	if (!conn.connectTo("btspp://NXT", NXTComm.LCP)) {
		//		System.err.println("Failed to connect");
		//		System.exit(1);
		//	}
		NXTCommandConnector.setNXTCommand(new NXTCommand(conn.getNXTComm()));

		// set the max speed of the motors
		Motor.A.setSpeed(MOTOR_SPEED_HORIZONTAL);
		Motor.C.setSpeed(MOTOR_SPEED_VERTICAL);
		//	Motor.A.setPower(-95);
		//	Motor.C.setPower(-95);

		// calibrate
		calibrate();

		// zona - run a couple tests...
		// bobHeadCelebrateBig();
		// safeSleep(1000);
		// bobHeadCelebrateSmall();
		// safeSleep(1000);
		// shakeHeadSidetoSide();
		// safeSleep(1000);
		//faceToScreen();
		//safeSleep(1000);
		//screenToFace();
		//safeSleep(1000);
		//
		//for (int i=0; i<10; i++) {
		//	randomIdleSearch();
		//	safeSleep(800);
		//}
		//screenToFace();
		//
		//	safeSleep(5000000);

		// focus on the face
		taskFocusOnFace();

		System.out.println(this.getClass().getPackage().getName() + "----- lejos create() is done ---");
	}

	/**
	 * run
	 */
	public void run() {

		System.out.println(this.getClass().getPackage().getName() + "----- lejos create() running ---");






		boolean done = false;
		while (!done) {

			done = sleepSafe(60 *60 * 1000);

			System.out.println(this.getClass().getPackage().getName() + "----- lejos create() exiting ---");
		}
	}


	/*
	 * sleepSafe
	 */
	private boolean sleepSafe(int time) {

		try {
			Thread.currentThread().sleep(time);
		} catch (InterruptedException e) {
			System.out.println(this.getClass().getPackage().getName() + "----- InterruptException seen... returning true");
			return true;
		}
		return false;
	}



	/**
	 * calibrate the motors
	 */
	private void calibrate() {
		if (VERBOSE >= 1) 
			System.out.println(this.getClass().getPackage().getName() + "----- calibrate ---");




		// assume motors are set to exactyl face the participant


		//		//////////////////
		//		// motor A
		//		Motor.A.rotateTo(-1000, true);
		//		try {
		//			Thread.currentThread();
		//			Thread.sleep(500);
		//		} catch (InterruptedException e) {
		//			System.out.println(this.getClass().getPackage().getName() + "----- calibrate caught an exception during sleep ?!");
		//		}
		//		// set the tacho count
		//		Motor.A.resetTachoCount();
		//		// back off from the limit
		//		Motor.A.rotateTo(CALIBRATE_BACKOFF_FROM_HARD_LIMIT);
		// now reset the tacho count so that zero is a safe number
		Motor.A.resetTachoCount();

		//////////////////
		// motor C
		//		Motor.C.rotateTo(-1000, true);
		//		try {
		//			Thread.currentThread();
		//			Thread.sleep(500);
		//		} catch (InterruptedException e) {
		//			System.out.println(this.getClass().getPackage().getName() + "----- calibrate caught an exception during sleep ?!");
		//		}
		//		// set the tacho count
		//		Motor.C.resetTachoCount();
		//		// back off from the limit
		//		Motor.C.rotateTo(CALIBRATE_BACKOFF_FROM_HARD_LIMIT);
		// now reset the tacho count so that zero is a safe number
		Motor.C.resetTachoCount();

		lastH = 0;
		currentH = 0;
		lastV = 0;
		currentV = 0;


		if (VERBOSE >= 1) 
			System.out.println(this.getClass().getPackage().getName() + "----- calibrate done ---");

	}

	//private void moveTo(int newH, int newV) {
	//	moveTo(newH, newV, false);
	//}

	/*
	 * moveTo
	 */
	private void moveTo(int newH, int newV) {
		boolean inParallel = true;

		if (VERBOSE >= 2) 
			System.out.println(this.getClass().getPackage().getName() + "----- moveTo  x" + newH + "  y" + newV);
		lastH = currentH;
		lastV = currentV;

		Motor.A.rotateTo( newH,inParallel); // horizontal
		Motor.C.rotateTo(-newV,inParallel); // vertical (inverted)

		if (inParallel) {
			while (Motor.A.isMoving() || Motor.C.isMoving()) {
				this.sleepSafe(10);
			}
		}

		currentH = newH;
		currentV = newV;
	}

	public void taskFaceToScreen() {
		Motor.C.setSpeed(MOTOR_SPEED_VERTICAL/3);
		taskFocusOnScreen();
		Motor.C.setSpeed(MOTOR_SPEED_VERTICAL);
	}

	public void taskScreenToFace() {
		Motor.C.setSpeed(MOTOR_SPEED_VERTICAL/3);
		taskFocusOnFace();
		Motor.C.setSpeed(MOTOR_SPEED_VERTICAL);
	}

	public void taskFaceToScreenFast() {
		Motor.A.setSpeed(MOTOR_SPEED_HORIZONTAL * 2);
		Motor.C.setSpeed(MOTOR_SPEED_VERTICAL);
		taskFocusOnScreen();
		Motor.A.setSpeed(MOTOR_SPEED_HORIZONTAL);
		Motor.C.setSpeed(MOTOR_SPEED_VERTICAL);
	}

	public void taskScreenToFaceFast() {
		Motor.A.setSpeed(MOTOR_SPEED_HORIZONTAL * 2);
		Motor.C.setSpeed(MOTOR_SPEED_VERTICAL);
		taskFocusOnFace();
		Motor.A.setSpeed(MOTOR_SPEED_HORIZONTAL);
		Motor.C.setSpeed(MOTOR_SPEED_VERTICAL);
	}

	public void taskGestureFaceToScreenToFace() {
		Motor.A.setSpeed(MOTOR_SPEED_HORIZONTAL * 2);
		//Motor.C.setSpeed(MOTOR_SPEED_VERTICAL);
		taskFocusOnScreen();
		taskFocusOnFace();
		Motor.A.setSpeed(MOTOR_SPEED_HORIZONTAL);
		//Motor.C.setSpeed(MOTOR_SPEED_VERTICAL);
	}

	private void taskFocusOnScreen()  {
		if (VERBOSE >= 2) 
			System.out.println(this.getClass().getPackage().getName() + "----- focusOnScreen");
		moveTo( SCREEN_CENTER_APPROXIMATE_HORIZONTAL,
				SCREEN_CENTER_APPROXIMATE_VERTICAL);
	}

	private void taskFocusOnFace() {
		if (VERBOSE >= 2) 
			System.out.println(this.getClass().getPackage().getName() + "----- focus on face");

		moveTo( FACE_CENTER_APPROXIMATE_HORIZONTAL,
				FACE_CENTER_APPROXIMATE_VERTICAL);
	}

	public void taskBobHeadCelebrateSmall() {
		if (VERBOSE >= 2) 
			System.out.println(this.getClass().getPackage().getName() + "----- bobHeadCelebrateSmall");

		// squirrel away the current location
		int savedH = currentH;
		int savedV = currentV;

		Motor.C.setSpeed(500);
		for (int i=0; i<1; i++) {
			moveTo(savedH, savedV + 10);
			moveTo(savedH, savedV - 10);
		}
		moveTo(savedH, savedV);
		Motor.C.setSpeed(MOTOR_SPEED_VERTICAL);
	}

	public void taskBobHeadCelebrateBig() {
		if (VERBOSE >= 2) 
			System.out.println(this.getClass().getPackage().getName() + "----- bobHeadCelebrateALot");

		// squirrel away the current location
		int savedH = currentH;
		int savedV = currentV;

		Motor.C.setSpeed(MOTOR_SPEED_VERTICAL * 3);
		for (int i=0; i<1; i++) {
			moveTo(savedH, savedV + 40);
			moveTo(savedH, savedV - 40);
		}
		moveTo(savedH, savedV);
		Motor.C.setSpeed(MOTOR_SPEED_VERTICAL);
	}


	public void taskShakeHeadSidetoSide() {
		if (VERBOSE >= 2) 
			System.out.println(this.getClass().getPackage().getName() + "----- shakeHeadSideToSide");

		// squirrel away the current location
		int savedH = currentH;
		int savedV = currentV;

		//	Motor.A.setSpeed(MOTOR_SPEED_HORIZONTAL /10 );
		for (int i=0; i<1; i++) {
			moveTo(savedH-20, savedV-20);
			moveTo(savedH+20, savedV-20);
		}
		moveTo(savedH, savedV);
		Motor.A.setSpeed(MOTOR_SPEED_HORIZONTAL);

	}

	/**
	 * a random idle search
	 */
	public void taskRandomIdleSearch() {
		if (VERBOSE >= 2) 
			System.out.println(this.getClass().getPackage().getName() + "----- random idle search ---");

		//		int RISEARCH_CENTER_H = (FACE_CENTER_APPROXIMATE_HORIZONTAL + SCREEN_CENTER_APPROXIMATE_HORIZONTAL) / 2;
		//		int RISEARCH_CENTER_V = (FACE_CENTER_APPROXIMATE_VERTICAL + SCREEN_CENTER_APPROXIMATE_VERTICAL) / 2;
		int RISEARCH_CENTER_H = SCREEN_CENTER_APPROXIMATE_HORIZONTAL;
		int RISEARCH_CENTER_V = SCREEN_CENTER_APPROXIMATE_VERTICAL;

		int RISEARCH_H_DELTA_ANGLE = 5;
		int RISEARCH_V_DELTA_ANGLE = 5;

		switch (randomGenerator.nextInt(4)) {
		case 0:
			moveTo( (RISEARCH_CENTER_H - RISEARCH_H_DELTA_ANGLE), (RISEARCH_CENTER_V - RISEARCH_V_DELTA_ANGLE));
			break;
		case 1:
			moveTo( (RISEARCH_CENTER_H - RISEARCH_H_DELTA_ANGLE), (RISEARCH_CENTER_V + RISEARCH_V_DELTA_ANGLE));
			break;
		case 2:
			moveTo( (RISEARCH_CENTER_H + RISEARCH_H_DELTA_ANGLE), (RISEARCH_CENTER_V - RISEARCH_V_DELTA_ANGLE));
			break;
		case 3:
			moveTo( (RISEARCH_CENTER_H + RISEARCH_H_DELTA_ANGLE), (RISEARCH_CENTER_V + RISEARCH_V_DELTA_ANGLE));
			break;
		default: 
			System.out.println(this.getClass().getPackage().getName() + "----- error - random idle search hit the default case");
		}


		// safeSleep(FACE_SEARCH_TIME_BETWEEN_SEARCHPOINTS);
	}




	private void cleanUp() {
		if (VERBOSE >= 2) 
			System.out.println(this.getClass().getPackage().getName() + "----- cleanUp");
		try {
			conn.close();
		} catch (IOException e) {
			System.out.println("--- lejos caught an IOException ---");
			e.printStackTrace();
		}
	}


}
