package cwinsor.com.speech_recognition;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

import com.sun.speech.freetts.lexicon.Lexicon;

import edu.cmu.sphinx.decoder.ResultListener;
import edu.cmu.sphinx.decoder.scorer.SimpleAcousticScorer;
import edu.cmu.sphinx.decoder.search.ActiveList;
import edu.cmu.sphinx.decoder.search.Token;
import edu.cmu.sphinx.frontend.FloatData;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.ConfidenceResult;
import edu.cmu.sphinx.result.ConfidenceScorer;
import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.LatticeOptimizer;
import edu.cmu.sphinx.result.Path;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;

/**
 * SpeechRecognition simply receives events from the
 * recognizer and passes them on to its own list of event listeners
 * Although it is a task the run() task does not stick around - just exits
 *
 */
public class SpeechRecognition extends Thread implements ResultListener {

	private static DecimalFormat format = new DecimalFormat("#.#####");

	/**
	 * verbose switch
	 */
	public int VERBOSE = 11;


	/**
	 * outbound events - list of people listening for events
	 */
	private List _listeners = new ArrayList();	

	// configuration manager and recognizer
	ConfigurationManager cm;
	Recognizer recognizer;

	/////////////////////
	// reference to the scorer - to get raw frontend data
	SimpleAcousticScorer scorer = null;


	/**
	 * create
	 */
	public void create() {

		try {
			URL configURL;
			configURL = SpeechRecognition.class.getResource("config.xml");

			System.out.printf(this.getClass().getPackage().getName() + " Loading Recognizer...\n");
			cm = new ConfigurationManager(configURL);
			recognizer = (Recognizer) cm.lookup("recognizer");

			/* allocate the resource necessary for the recognizer */
			recognizer.allocate();

		} catch (PropertyException e) {
			System.err.println("Problem configuring Confidence: " + e);
			e.printStackTrace();
		}

		// sign up for events !
		recognizer.addResultListener(this);

		/////////////////////
		// enable capture of raw frontend data
		scorer = recognizer.getDecoder().getSearchManager().getScorer();
		scorer.enableSquirrelData(true);
	}


	/**
	 * run
	 */
	public void run() {
		System.out.printf(this.getClass().getPackage().getName() + " running...\n"); 

		Microphone microphone = (Microphone) cm.lookup("microphone");

		if (microphone.startRecording()) {
			// can return
		} else {
			System.out.println("Cannot start microphone.");
			recognizer.deallocate();
			System.exit(1);
		}

		// run forever
		while (true) {
			recognizer.recognize();
		}

		//		while (true) {
		//
		//			System.out.println("Start speaking. Press Ctrl-C to quit.\n");
		//
		//			/* decode the audio file */
		//			Result result = recognizer.recognize();
		//
		//			if (result != null) {
		//
		//				ConfidenceScorer cs = (ConfidenceScorer) cm.lookup
		//						("confidenceScorer");
		//				ConfidenceResult cr = cs.score(result);
		//				Path best = cr.getBestHypothesis();
		//
		//				/* confidence of the best path */
		//				System.out.println(best.getTranscription());
		//				System.out.println
		//				("     (confidence: " +
		//						format.format(best.getLogMath().logToLinear
		//								((float) best.getConfidence()))
		//								+ ')');
		//				System.out.println();
		//
		//			}
		//		}

		//		boolean done = false;
		//		while (!done) {
		//			try {
		//				sleep(1000);
		//			} catch (InterruptedException e) {
		//				done = true;
		//			}
		//		}
		//
		//		System.out.printf(this.getClass().getPackage().getName() + " done...\n"); 
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
	 * inbound events
	 */

	@Override
	public void newResult(Result result) {

		if (VERBOSE >= 2)
			System.out.println(this.getClass().getPackage().getName() + "-----> received event");

		if (result != null) {
			// System.out.println(this.getClass().getPackage().getName() + "-----> result.getBestFinalResultNoFiller()" + result.getBestFinalResultNoFiller());
			// System.out.println(this.getClass().getPackage().getName() + "-----> result.getBestPronunciationResult()" + result.getBestPronunciationResult());
			// System.out.println(this.getClass().getPackage().getName() + "-----> result.getBestResultNoFiller()     " + result.getBestResultNoFiller());
			// System.out.println(this.getClass().getPackage().getName() + "-----> result.getReferenceText()          " + result.getReferenceText());

			// get the confidence scorer results
			ConfidenceScorer cs = (ConfidenceScorer) cm.lookup("confidenceScorer");
			ConfidenceResult cr = cs.score(result);
			Path best = cr.getBestHypothesis();

			///////////////					
			// use a collection to pass the word list to event listeners
			WordResult[] words = best.getWords();
			// System.out.println(this.getClass().getPackage().getName() + "-----> words.toString()                  " + words.toString());
			List<String> myList = new ArrayList<String>();
			for (WordResult wr : words) {
				if (!wr.isFiller())
					myList.add(wr.toString());
			}

			//////////////////////
			// create a lattice
			//Lattice lattice = new Lattice(result);
			//LatticeOptimizer optimizer = new LatticeOptimizer(lattice);
			//optimizer.optimize();
			//lattice.dumpAllPaths();

			///////////////////////
			// give some other examples - bestResults, bestFinalToken, activeTokens, ResultTokens
			// String resultText = resultText = result.getBestResultNoFiller();
			// System.out.println("getBestResultNoFiller: " + resultText + '\n');

			// Token foo = result.getBestFinalToken();
			// foo.dumpTokenPath(false);

			//ActiveList foo2 = result.getActiveTokens();
			// List<Token> foo3 = result.getResultTokens();


			/////////////////////
			// get reference to the raw frontend data
			InstancesFrontend39Features instancesFrontend39Features = scorer.getSqirrelData();
			System.out.println(this.getClass().getName() + "-----> here scorer.getSqirrelData().size()     " + scorer.getSqirrelData().size());
			scorer.clearSquirrelData();
			System.out.println(this.getClass().getName() + "-----> here instancesFrontend39Features.size() " + instancesFrontend39Features.size());

			///////////////////////////
			// if there is something there, fire an event
			// fire event for any listeners
			if (myList.size() > 0) {
				_fireEvent(myList, instancesFrontend39Features);
			} else {
				if (VERBOSE >= 2)
					System.out.println(this.getClass().getPackage().getName() + "-----> received event was empty (will not fire event)");
			}
		} else {
			System.out.println(this.getClass().getPackage().getName() + "-----> I got a result that was null?!!!");
		}








	}

	@Override
	public void newProperties(PropertySheet ps) throws PropertyException {
		// TODO Auto-generated method stub

	}



	/**
	 * outbound events
	 * add/remove listeners
	 * fire the event
	 * @param l
	 */

	public synchronized void addListener( SpeechRecognitionEventInterface l ) {
		_listeners.add( l );
	}

	public synchronized void removeEmotionListener( SpeechRecognitionEventInterface l ) {
		_listeners.remove( l );
	}

	private synchronized void _fireEvent(List<String> theMessage,
			InstancesFrontend39Features	instancesFrontend39Features) {
		if (VERBOSE >= 1) {
			System.out.println(this.getClass().getPackage().getName() + "-----> firing event with words: " + theMessage);
			System.out.println(this.getClass().getPackage().getName() + "-----> firing event with instancesFrontend39Features.size() " + instancesFrontend39Features.size());
		}
		SpeechRecognitionEventObject eventObject = new SpeechRecognitionEventObject(
				this,
				theMessage,
				instancesFrontend39Features);
		Iterator listeners = _listeners.iterator();
		while( listeners.hasNext() ) {
			( (SpeechRecognitionEventInterface) listeners.next() ).aSpeechRecognitionEvent( eventObject );
		}
	}




}
