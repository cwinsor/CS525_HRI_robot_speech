package cwinsor.com.controlThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.Calendar;

//import cwinsor.com.LatticeDefines;
import cwinsor.com.freetts.BasicFreetts;
import cwinsor.com.lejos.BasicLejos;
import cwinsor.com.loadImageApp.LoadImageApp;
import cwinsor.com.speech_recognition.InstancesFrontend39Features;
import cwinsor.com.speech_recognition.SpeechRecognitionEventInterface;
import cwinsor.com.speech_recognition.SpeechRecognitionEventObject;
import edu.cmu.sphinx.frontend.FloatData;



import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.clusterers.DensityBasedClusterer;
import weka.clusterers.EM;
import weka.clusterers.MakeDensityBasedClusterer;
import weka.clusterers.SimpleKMeans;

import weka.core.converters.ConverterUtils.DataSource;


public class ControlThread extends Thread implements SpeechRecognitionEventInterface {

	// use human words
	boolean USE_WORDS = true;

	// verbose switch
	int VERBOSE = 2;

	// references to things I drive
	LoadImageApp loadImageApp;
	BasicLejos basicLejos;
	BasicFreetts basicFreetts;

	// incoming events (variables which capture them) 
	boolean done;
	boolean speechEventSeen;
	List<String> speechEventMessage;
	InstancesFrontend39Features speechEventFrontendSamples;


	// states
	private static final String STATE_IDLE = "STATE_IDLE";
	private static final String STATE_INTRO_SEARCHING_FOR_FRIEND = "STATE_INTRO_SEARCHING_FOR_FRIEND";
	private static final String STATE_INTRO_FOUND_FRIEND_SMALL_CELEBRATION_BEFORE_TASK = "STATE_INTRO_FOUND_FRIEND_SMALL_CELEBRATION_BEFORE_TASK";
	private static final String STATE_INTRO_1_WAIT_FOR_SPEECH = "STATE_INTRO_1_WAIT_FOR_SPEECH";
	private static final String STATE_INTRO_1_WAIT_FOR_SPEECH_AGAIN = "STATE_INTRO_1_WAIT_FOR_SPEECH_AGAIN";
	private static final String STATE_INTRO_2_WAIT_FOR_SPEECH = "STATE_INTRO_2_WAIT_FOR_SPEECH";

	private static final String STATE_GAME_START_ROUND = "STATE_GAME_START_ROUND";

	private static final String STATE_PROMPT_FOR_WORD = "STATE_PROMPT_FOR_WORD";
	private static final String STATE_GAME_WAIT_FOR_THEN_CAPTURE_WORD = "STATE_GAME_WAIT_FOR_THEN_CAPTURE_WORD";
	private static final String STATE_PROMPT_FOR_FEEDBACK = "STATE_PROMPT_FOR_FEEDBACK";
	private static final String STATE_WAIT_FOR_THEN_CAPTURE_FEEDBACK = "STATE_WAIT_FOR_THEN_CAPTURE_FEEDBACK";
	private static final String STATE_HANDLE_FEEDBACK = "STATE_HANDLE_FEEDBACK";
	private static final String STATE_EXPERIMENT = "STATE_EXPERIMENT";

	private static final String STATE_EXPERIMENT_CAPTURE_CAC = "STATE_EXPERIMENT_CAPTURE_CAC";
	private static final String STATE_EXPERIMENT_CAPTURE_CAR = "STATE_EXPERIMENT_CAPTURE_CAR";
	private static final String STATE_EXPERIMENT_CAPTURE_CAT = "STATE_EXPERIMENT_CAPTURE_CAT";
	private static final String STATE_EXPERIMENT_CAPTURE_RAC = "STATE_EXPERIMENT_CAPTURE_RAC";
	private static final String STATE_EXPERIMENT_CAPTURE_RAR = "STATE_EXPERIMENT_CAPTURE_RAR";
	private static final String STATE_EXPERIMENT_CAPTURE_RAT = "STATE_EXPERIMENT_CAPTURE_RAT";
	private static final String STATE_EXPERIMENT_CAPTURE_TAC = "STATE_EXPERIMENT_CAPTURE_TAC";
	private static final String STATE_EXPERIMENT_CAPTURE_TAR = "STATE_EXPERIMENT_CAPTURE_TAR";
	private static final String STATE_EXPERIMENT_CAPTURE_TAT = "STATE_EXPERIMENT_CAPTURE_TAT";
	private static final String STATE_EXPERIMENT_DONE = "STATE_EXPERIMENT_DONE";


	String currentState;
	String nextState;
	String capturedWordMessage;
	InstancesFrontend39Features capturedWordFrontendSamples;
	EM eMCapturedWordFrontendSamples;
	String[] options;

	String capturedFeedback;

	// total log density
	double[] tld = new double[9];


	// search timestamp, width and base
	long nextSearchStartTimestamp;
	private static final int SEARCH_BASE_IN_MS   = 1000;
	private static final int SEARCH_WIDTH_IN_MS =  1500;

	int gameRoundNumber;
	long gameRoundStartTime;
	private static final int GAME_ROUND_MINIMUM_IN_MS   = (10 * 1000);
	private static final int GAME_ROUND_MAXIMUM_MS   = (30 * 1000);
	int gameRoundScore;
	private static final int GAME_ROUND_MAXIMUM_SCORE = 1;

	long lastPromptForSpeechTimestamp;
	private static final int  MAX_MILLISECONDS_BEFORE_WORD_REPROMPT = 8000;

	// feedback time
	// the time for something spoken by robot to be recognized by it
	// that is - the time needed to wait until the receive channel clears
	// after having the robot speak something
	private static final int FEEDBACK_TIME = 900;











	// random number generator
	Random randomGenerator;



	/**
	 * create
	 */
	public void create(
			LoadImageApp loadImageApp,
			BasicLejos basicLejos,
			BasicFreetts basicFreetts) {
		System.out.println(this.getClass().getPackage().getName() + "----- create");

		this.loadImageApp = loadImageApp;
		this.basicLejos = basicLejos;
		this.basicFreetts = basicFreetts;

		//a random generator object
		randomGenerator = new Random();

	}


	/**
	 * inbound events
	 */
	public void aSpeechRecognitionEvent(SpeechRecognitionEventObject theEventObject) {

		speechEventSeen = true;
		speechEventMessage = theEventObject.theMessage();
		speechEventFrontendSamples = theEventObject.theInstancesFrontend39Features();
	}


	/**
	 * totalLogDensity
	 */
	public double totalLogDensity(InstancesFrontend39Features myInstances) {
		double ttlLogDens = 0;

		try {
			for (int sample = 0; sample<myInstances.numInstances(); sample++) {
				ttlLogDens += eMCapturedWordFrontendSamples.logDensityForInstance(myInstances.get(sample));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ttlLogDens;
	}



	/**
	 * run
	 */
	public void run() {
		try {


			System.out.println(this.getClass().getPackage().getName() + "----- run");


			// initialize state
			currentState = STATE_IDLE;
			nextState    = STATE_IDLE;

			// initialize internal state variables
			done = false;
			speechEventSeen = false;
			nextSearchStartTimestamp = Calendar.getInstance().getTimeInMillis() +
					randomGenerator.nextInt(SEARCH_WIDTH_IN_MS) + SEARCH_BASE_IN_MS;

			gameRoundNumber = -1;



			while (!done) {

				// minimum loop 100ms
				done = 	sleepSafe(100);


				switch (currentState) {

				case STATE_IDLE:

					nextState = STATE_INTRO_SEARCHING_FOR_FRIEND;
					sleepSafe(2000);
					speechEventSeen = false;
					break;

				case STATE_INTRO_SEARCHING_FOR_FRIEND: 

					// if we got friended then go to that state
					if (speechEventSeen) {
						nextState = STATE_INTRO_FOUND_FRIEND_SMALL_CELEBRATION_BEFORE_TASK;
					}

					// else - perform actions that are searchlike
					else  {

						// if current time is beyond the last search time
						if (Calendar.getInstance().getTimeInMillis() > nextSearchStartTimestamp) {

							// physically search
							basicLejos.taskRandomIdleSearch();

							// make an utterance
							switch (randomGenerator.nextInt(6)) {
							case 0:
								basicFreetts.speak("are you there");
								break;	
							case 1:						
								basicFreetts.speak("hello");
								break;
							case 2: 
								basicFreetts.speak("are you there");
								break;
							case 3: 
								basicFreetts.speak("hello");
								break;
							case 4: 
								basicFreetts.speak("are you there");
								break;
							case 5: 
								basicFreetts.speak("hello");
								break;
							default:
								System.out.println(this.getClass().getPackage().getName() + "----- error - hit the default case during random idle search");
							}

							sleepSafe(FEEDBACK_TIME);
							speechEventSeen = false;

							// schedule the next search
							nextSearchStartTimestamp = Calendar.getInstance().getTimeInMillis() +
									randomGenerator.nextInt(SEARCH_WIDTH_IN_MS) + SEARCH_BASE_IN_MS;

						}
					}			
					break;

				case STATE_INTRO_FOUND_FRIEND_SMALL_CELEBRATION_BEFORE_TASK:

					basicLejos.taskScreenToFaceFast();

					basicFreetts.speak("hi");
					basicFreetts.speak("how are you today");
					basicFreetts.speak("my name is robot");
					basicFreetts.speak("what is your name please");

					nextState = STATE_INTRO_1_WAIT_FOR_SPEECH;
					sleepSafe(FEEDBACK_TIME);
					speechEventSeen = false;

					break;

				case STATE_INTRO_1_WAIT_FOR_SPEECH:
					if (speechEventSeen) {
						basicFreetts.speak("it is a pleasure to meet you");
						sleepSafe(600);

						basicFreetts.speak("be fore we get started");
						basicFreetts.speak("can you say the following words");
						sleepSafe(400);

						basicFreetts.speak("9 oh 2 1 oh");
						basicFreetts.speak("is the zip code for beverly hills");

						sleepSafe(FEEDBACK_TIME);

						speechEventSeen = false;
						nextState = STATE_INTRO_1_WAIT_FOR_SPEECH_AGAIN;
					}
					break;

				case STATE_INTRO_1_WAIT_FOR_SPEECH_AGAIN:
					if (speechEventSeen) {
						/*

						basicFreetts.speak("OK one more time");
						sleepSafe(400);
						basicFreetts.speak("9 oh 2 1 oh");
						basicFreetts.speak("is the zip code for beverly hills");

						sleepSafe(FEEDBACK_TIME);
						 */
						speechEventSeen = false;
						nextState = STATE_INTRO_2_WAIT_FOR_SPEECH;
					}
					break;


				case STATE_INTRO_2_WAIT_FOR_SPEECH:
					if (true) {
						//					if (speechEventSeen) {
						basicFreetts.speak("thank you");
						basicFreetts.speak("");
						basicFreetts.speak("");
						basicFreetts.speak("Now lets start the game");
						// basicFreetts.speak("Here is the game");
						basicFreetts.speak("");
						basicFreetts.speak("");
						basicFreetts.speak("On the screen you will see a picture");
						basicFreetts.speak("");
						basicFreetts.speak("");
						basicFreetts.speak("Tell me what it is");
						basicFreetts.speak("");
						basicFreetts.speak("");
						basicFreetts.speak("I will see if I can figure out what you said");


						nextState = STATE_GAME_START_ROUND;
					}
					break;

				case STATE_GAME_START_ROUND:

					gameRoundNumber++;
					gameRoundScore = 0;
					gameRoundStartTime = Calendar.getInstance().getTimeInMillis();

					/*
				c.add("bird2.gif");
				c.add("bird3.gif");
				c.add("bird11.gif");
				c.add("bird12.gif");
				c.add("cat3.gif");
				c.add("cat25.gif");
				c.add("cat24.gif");
				c.add("cat35.gif");
				c.add("dog3.gif");
				c.add("dog9.gif");
				c.add("dog27.gif");
				c.add("dog29.gif");
				c.add("fish2.gif");
				c.add("fish5.gif");
				c.add("fish6.gif");
				c.add("fish8.gif");
				c.add("horse3.gif");
				c.add("horse11.gif");
				c.add("horse12.gif");
				c.add("horse14.gif");
				c.add("snake1.gif");
				c.add("snake2.gif");
				c.add("snake5.gif");
				c.add("turtle2.gif");
				c.add("turtle2.gif");
				c.add("turtle4.gif");
				c.add("turtle10.gif");
					 */

					// focus on the screen prior to presenting image

					loadImageApp.clearImage();
					basicLejos.taskFaceToScreen();
					sleepSafe(1000);

					/*
					loadImageApp.displayImage("cat24.gif");
					sleepSafe(1000);
					loadImageApp.clearImage();
					loadImageApp.displayImage("rat01.jpg");
					sleepSafe(1000);
					loadImageApp.clearImage();
					loadImageApp.displayImage("car01.jpg");
					sleepSafe(1000);
					loadImageApp.clearImage();
					loadImageApp.displayImage("cat35.gif");
					sleepSafe(1000);	
					loadImageApp.clearImage();
					loadImageApp.displayImage("rat02.jpg");
					sleepSafe(1000);
					loadImageApp.clearImage();
					loadImageApp.displayImage("car02.gif");
					sleepSafe(1000);
					loadImageApp.clearImage();
					 */

					switch (gameRoundNumber % 4) {
					case 0:
						loadImageApp.displayImage("cat24.gif");
						break;
					case 1:
						loadImageApp.displayImage("rat01.jpg");
						break;
					case 2:
						loadImageApp.displayImage("car01.jpg");
						break;
					case 3:
						loadImageApp.displayImage("rat02.jpg");
						break;	
					case 4:
						loadImageApp.displayImage("car02.gif");
						break;
					default:
						System.out.println(this.getClass().getName() + "----- ERROR when loading image - hit the default case!");
					}

					sleepSafe(1000);
					basicLejos.taskScreenToFace();


					nextState = STATE_PROMPT_FOR_WORD;
					break;

				case STATE_PROMPT_FOR_WORD: 
					lastPromptForSpeechTimestamp = Calendar.getInstance().getTimeInMillis();

					//basicLejos.taskGestureFaceToScreenToFace();
					basicFreetts.speak("what is this thing");

					nextState = STATE_GAME_WAIT_FOR_THEN_CAPTURE_WORD;
					sleepSafe(FEEDBACK_TIME);
					speechEventSeen = false;
					break;

				case STATE_GAME_WAIT_FOR_THEN_CAPTURE_WORD: 

					if (speechEventSeen) {
						capturedWordMessage = speechEventMessage.toString();
						capturedWordFrontendSamples = this.speechEventFrontendSamples;
						System.out.println(this.getClass().getName() + "----- captured frontendSamples size " + capturedWordFrontendSamples.numInstances());


						nextState = STATE_PROMPT_FOR_FEEDBACK;
					}

					// else - check to re-prompt
					else  {
						if (Calendar.getInstance().getTimeInMillis() > (lastPromptForSpeechTimestamp + MAX_MILLISECONDS_BEFORE_WORD_REPROMPT)) {
							nextState = STATE_PROMPT_FOR_WORD;
						}
					}
					break;


				case STATE_PROMPT_FOR_FEEDBACK:
					basicLejos.taskScreenToFace();
					basicFreetts.speak("did you say");
					basicFreetts.speak(capturedWordMessage);

					nextState = STATE_WAIT_FOR_THEN_CAPTURE_FEEDBACK;
					sleepSafe(FEEDBACK_TIME);
					speechEventSeen = false;
					break;


				case STATE_WAIT_FOR_THEN_CAPTURE_FEEDBACK: 

					if (speechEventSeen) {
						capturedFeedback = speechEventMessage.toString();

						nextState = STATE_HANDLE_FEEDBACK;
					}
					break;


				case STATE_HANDLE_FEEDBACK:
					if (capturedFeedback.contains("no") || capturedFeedback.contains("bad")) {
						basicLejos.taskShakeHeadSidetoSide();
						basicFreetts.speak("that was not right");
						basicFreetts.speak("");
						basicFreetts.speak("");
						basicFreetts.speak("let me try some experiments on my own");
						basicFreetts.speak("make sure you hold the microphone up to the speakers");
						nextState = STATE_EXPERIMENT;
					} else

						if (capturedFeedback.contains("yes") || capturedFeedback.contains("good")) {
							gameRoundScore++;
							basicLejos.taskBobHeadCelebrateSmall();
							basicFreetts.speak("yay I got it right");
							basicFreetts.speak("");
							basicFreetts.speak("");
							basicFreetts.speak("thanks");
							nextState = STATE_GAME_START_ROUND;
						}
						else {
							basicLejos.taskScreenToFace();
							basicFreetts.speak("i'm sorry");
							basicFreetts.speak("");
							basicFreetts.speak("did you say");
							basicFreetts.speak(capturedWordMessage);


							nextState = STATE_WAIT_FOR_THEN_CAPTURE_FEEDBACK;
							sleepSafe(FEEDBACK_TIME);
							speechEventSeen = false;
						}
					break;


				case STATE_EXPERIMENT:
					basicLejos.taskFaceToScreen();

					basicFreetts.speak("hold on one second please while I prepare");
					// calculate EM on the frontend data
					{
						// create EM model using speech we collected from frontend
						eMCapturedWordFrontendSamples = new EM();
						options = weka.core.Utils.splitOptions(" -I 100 -N -1 -M 1.0E-6 -S 100 ");
						eMCapturedWordFrontendSamples.setOptions(options);
						eMCapturedWordFrontendSamples.buildClusterer(capturedWordFrontendSamples);

						// print the number of clusters...
						System.out.println(this.getClass().getName() + "----- numClusters " +
								eMCapturedWordFrontendSamples.getNumClusters());
						double[] foo = eMCapturedWordFrontendSamples.getClusterPriors();
						System.out.println(this.getClass().getName() + "----- numClusterPriors " + foo.length); 

						// print the log density of the sample itself
						System.out.println(this.getClass().getName() + "----- totalLogDensity (self) " +
								totalLogDensity(capturedWordFrontendSamples));
					}

					basicFreetts.speak("here we go");
					sleepSafe(FEEDBACK_TIME);
					speechEventSeen = false;
					basicFreetts.speak("cac");
					nextState = STATE_EXPERIMENT_CAPTURE_CAC;
					break;

				case STATE_EXPERIMENT_CAPTURE_CAC: 
					if (speechEventSeen) {
						// capture data from frontend, calculate/ print total log density using EM model from original sample	
						InstancesFrontend39Features mySamples = this.speechEventFrontendSamples;
						//System.out.println(this.getClass().getName() + "----- totalLogDensity (cac) " + totalLogDensity(mySamples));
						tld[0] = totalLogDensity(mySamples);

						sleepSafe(FEEDBACK_TIME);
						speechEventSeen = false;
						basicFreetts.speak("car");
						nextState = STATE_EXPERIMENT_CAPTURE_CAR;
					}
					break;
				case STATE_EXPERIMENT_CAPTURE_CAR:
					if (speechEventSeen) {
						// capture data from frontend, calculate/ print total log density using EM model from original sample	
						InstancesFrontend39Features mySamples = this.speechEventFrontendSamples;
						//System.out.println(this.getClass().getName() + "----- totalLogDensity (car) " + totalLogDensity(mySamples));
						tld[1] = totalLogDensity(mySamples);

						sleepSafe(FEEDBACK_TIME);
						speechEventSeen = false;
						basicFreetts.speak("cat");
						nextState = STATE_EXPERIMENT_CAPTURE_CAT;
					}
					break;
				case STATE_EXPERIMENT_CAPTURE_CAT:
					if (speechEventSeen) {
						// capture data from frontend, calculate/ print total log density using EM model from original sample	
						InstancesFrontend39Features mySamples = this.speechEventFrontendSamples;
						//System.out.println(this.getClass().getName() + "----- totalLogDensity (cat) " + totalLogDensity(mySamples));
						tld[2] = totalLogDensity(mySamples);

						sleepSafe(FEEDBACK_TIME);
						speechEventSeen = false;
						basicFreetts.speak("rac");
						nextState = STATE_EXPERIMENT_CAPTURE_RAC;
					}
					break;
				case STATE_EXPERIMENT_CAPTURE_RAC:
					if (speechEventSeen) {

						// capture data from frontend, calculate/ print total log density using EM model from original sample	
						InstancesFrontend39Features mySamples = this.speechEventFrontendSamples;
						//System.out.println(this.getClass().getName() + "----- totalLogDensity (rac) " + totalLogDensity(mySamples));
						tld[3] = totalLogDensity(mySamples);

						sleepSafe(FEEDBACK_TIME);
						speechEventSeen = false;
						basicFreetts.speak("rar");
						nextState = STATE_EXPERIMENT_CAPTURE_RAR;
					}
					break;
				case STATE_EXPERIMENT_CAPTURE_RAR:
					if (speechEventSeen) {
						// capture data from frontend, calculate/ print total log density using EM model from original sample	
						InstancesFrontend39Features mySamples = this.speechEventFrontendSamples;
						//System.out.println(this.getClass().getName() + "----- totalLogDensity (rar) " + totalLogDensity(mySamples));
						tld[4] = totalLogDensity(mySamples);

						sleepSafe(FEEDBACK_TIME);
						speechEventSeen = false;
						basicFreetts.speak("rat");
						nextState = STATE_EXPERIMENT_CAPTURE_RAT;
					}
					break;
				case STATE_EXPERIMENT_CAPTURE_RAT: 
					if (speechEventSeen) {
						// capture data from frontend, calculate/ print total log density using EM model from original sample	
						InstancesFrontend39Features mySamples = this.speechEventFrontendSamples;
						//System.out.println(this.getClass().getName() + "----- totalLogDensity (rat) " + totalLogDensity(mySamples));
						tld[5] = totalLogDensity(mySamples);

						sleepSafe(FEEDBACK_TIME);
						speechEventSeen = false;
						basicFreetts.speak("tac");
						nextState = STATE_EXPERIMENT_CAPTURE_TAC;
					}
					break;
				case STATE_EXPERIMENT_CAPTURE_TAC: 
					if (speechEventSeen) {
						// capture data from frontend, calculate/ print total log density using EM model from original sample	
						InstancesFrontend39Features mySamples = this.speechEventFrontendSamples;
						//System.out.println(this.getClass().getName() + "----- totalLogDensity (tac) " + totalLogDensity(mySamples));
						tld[6] = totalLogDensity(mySamples);

						sleepSafe(FEEDBACK_TIME);
						speechEventSeen = false;
						basicFreetts.speak("tar");
						nextState = STATE_EXPERIMENT_CAPTURE_TAR;
					}
					break;
				case STATE_EXPERIMENT_CAPTURE_TAR: 
					if (speechEventSeen) {
						// capture data from frontend, calculate/ print total log density using EM model from original sample	
						InstancesFrontend39Features mySamples = this.speechEventFrontendSamples;
						//System.out.println(this.getClass().getName() + "----- totalLogDensity (tar) " + totalLogDensity(mySamples));
						tld[7] = totalLogDensity(mySamples);

						sleepSafe(FEEDBACK_TIME);
						speechEventSeen = false;
						basicFreetts.speak("tat");
						nextState = STATE_EXPERIMENT_CAPTURE_TAT;
					}
					break;
				case STATE_EXPERIMENT_CAPTURE_TAT: 
					if (speechEventSeen) {
						// capture data from frontend, calculate/ print total log density using EM model from original sample	
						InstancesFrontend39Features mySamples = this.speechEventFrontendSamples;
						//System.out.println(this.getClass().getName() + "----- totalLogDensity (tat) " + totalLogDensity(mySamples));
						tld[8] = totalLogDensity(mySamples);

						sleepSafe(FEEDBACK_TIME);
						speechEventSeen = false;
						basicFreetts.speak("we are done");
						nextState = STATE_EXPERIMENT_DONE;
					}
					break;



				case STATE_EXPERIMENT_DONE: 

					double largestTldVal= 0;
					int largestTldIndex = -1;
					for (int i=0; i<tld.length; i++) {
						if ((largestTldIndex == -1) || (tld[i] > largestTldVal)) {						
							largestTldVal = tld[i];
							largestTldIndex = i;
						}
					}

					String foo =
							(largestTldIndex==0) ? "cac" : 
								(largestTldIndex==1) ? "car" : 
									(largestTldIndex==2) ? "cat" : 
										(largestTldIndex==3) ? "rac" : 
											(largestTldIndex==4) ? "rar" : 
												(largestTldIndex==5) ? "rat" : 
													(largestTldIndex==6) ? "tac" : 
														(largestTldIndex==7) ? "tar" : 
															(largestTldIndex==8) ? "tac" : "";

					basicFreetts.speak("based on my analysis");
					basicFreetts.speak("the most likely word is" + foo);

					sleepSafe(2000);
					nextState = STATE_GAME_START_ROUND;
					break;




				default:
					System.out.println(this.getClass().getPackage().getName() + "----- ERROR in state machine - hit the default case!");
				}



				if (VERBOSE > 0)
					System.out.println(this.getClass().getPackage().getName() + "----- going to state " + nextState);
				currentState = nextState;
			}
			System.out.println(this.getClass().getPackage().getName() + "----- run (DONE) ");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * getTotalLogDensity
	 */
	double getTotalLogDensity() {
		double totalLogDensity = 0;
		// loop through the samples (each sample has 39 attributes)
		for (int inst=0; inst<capturedWordFrontendSamples.size(); inst++) {
			DenseInstance thisInstance = null;

			Double[] foo = capturedWordFrontendSamples.toArray(new Double[39]);
			double[] bar = new double[39];
			if (foo.length != 39) {
				System.out.println(this.getClass().getName() + "----- ERROR 5566");
			}
			for (int x=0; x<foo.length; x++)										
				bar[x] = foo[x];
			thisInstance = new DenseInstance(1, bar);

			// now use our EM to compute the log density for this Instances
			try {
				totalLogDensity += eMCapturedWordFrontendSamples.logDensityForInstance(thisInstance);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return totalLogDensity;
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



	private void cleanUp() {
		if (VERBOSE >= 2) 
			System.out.println(this.getClass().getPackage().getName() + "----- cleanUp");
	}


}