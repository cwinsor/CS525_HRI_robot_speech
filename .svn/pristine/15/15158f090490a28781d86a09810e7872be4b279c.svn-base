package cwinsor.com.freetts;
/**
 * Copyright 2003 Sun Microsystems, Inc.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

//import javax.speech.EngineException;
//import javax.speech.EngineStateError;
//import javax.speech.synthesis.Synthesizer;

import com.sun.speech.freetts.FreeTTSSpeakable;
import com.sun.speech.freetts.Utterance;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.JavaClipAudioPlayer;
import com.sun.speech.freetts.lexicon.Lexicon;

/**
 * Simple program to demonstrate the use of the FreeTTS speech
 * synthesizer.  This simple program shows how to use FreeTTS
 * without requiring the Java Speech API (JSAPI).
 */
public class BasicFreetts extends Thread  {

//	Synthesizer synthesizer;
	Voice helloVoice;

	/**
	 * Example of how to list all the known voices.
	 */
	public static void listAllVoices() {
		System.out.println();
		System.out.println("All voices available:");        
		VoiceManager voiceManager = VoiceManager.getInstance();
		Voice[] voices = voiceManager.getVoices();
		for (int i = 0; i < voices.length; i++) {
			System.out.println("    " + voices[i].getName()
					+ " (" + voices[i].getDomain() + " domain)");
		}
	}

	public void create(String[] args) {

		listAllVoices();

		String voiceName = (args.length > 0)
				? args[0]
						: "kevin16";

				System.out.println();
				System.out.println("Using voice: " + voiceName);

				/* The VoiceManager manages all the voices for FreeTTS.
				 */
				VoiceManager voiceManager = VoiceManager.getInstance();
				helloVoice = voiceManager.getVoice(voiceName);

				if (helloVoice == null) {
					System.err.println(
							"Cannot find a voice named "
									+ voiceName + ".  Please specify a different voice.");
					System.exit(1);
				}

				/* Allocates the resources for the voice.
				 */
				helloVoice.allocate();

				/* Synthesize speech.
				 */
				// helloVoice.speak("Thank you");

				Lexicon lexicon = helloVoice.getLexicon();

				/*
				// create a collection and iterate over it
				List<String> myList = new ArrayList<String>();
				ListIterator<String> litr = null;
				myList.add("odd");
				myList.add("at");
				myList.add("hut");
				myList.add("ought");
				myList.add("cow");
				myList.add("hide");
				myList.add("be");
				myList.add("cheese");
				myList.add("dee");
				myList.add("thee");
				myList.add("red");
				myList.add("hurt");
				myList.add("ate");
				myList.add("fee");
				myList.add("green");
				myList.add("he");
				myList.add("it");
				myList.add("eat");
				myList.add("gee");
				myList.add("key");
				myList.add("lee");
				myList.add("me");
				myList.add("knee");
				myList.add("ping");
				myList.add("oat");
				myList.add("toy");
				myList.add("pee");
				myList.add("reed");
				myList.add("sea");
				myList.add("she");
				myList.add("tea");
				myList.add("theta");
				myList.add("hood");
				myList.add("two");
				myList.add("vee");
				myList.add("we");
				myList.add("yield");
				myList.add("zee");
				myList.add("seizure");

				// iterate forward and backward
				litr=myList.listIterator();
				while(litr.hasNext()) {
					String entry = litr.next();
					System.out.println("---------- " + entry);
					String[] thePhones = lexicon.getPhones(entry, null);
					// foreach phone
					for (String x : thePhones) {
						System.out.println("thePhones " + x.toString());
					}
				}

				//lexicon.addAddendum("zzaazz", null, new String[] {"aa1"});
				//helloVoice.speak("zzaazz");
				//helloVoice.speak("odd");
				 */
				System.out.println("add each PhoneWord to the lexicon, then speak the PhoneWord then an example word that uses it.");

				lexicon.addAddendum("yyaayyy", null, new String[] {"aa1"});
				lexicon.addAddendum("yyaeyyy", null, new String[] {"ae1"});
				lexicon.addAddendum("yyahyyy", null, new String[] {"ah1"});
				lexicon.addAddendum("yyaoyyy", null, new String[] {"ao1"});
				lexicon.addAddendum("yyawyyy", null, new String[] {"aw1"});
				lexicon.addAddendum("yyayyy", null, new String[] {"ay"});
				lexicon.addAddendum("yybyy", null, new String[] {"b"});
				lexicon.addAddendum("yychyy", null, new String[] {"ch"});
				lexicon.addAddendum("yygyy", null, new String[] {"g"});
				lexicon.addAddendum("yyihyyy", null, new String[] {"ih1"});
				lexicon.addAddendum("yydyy", null, new String[] {"d"});
				lexicon.addAddendum("yydhyy", null, new String[] {"dh"});
				lexicon.addAddendum("yyehyyy", null, new String[] {"eh1"});
				lexicon.addAddendum("yyeryyy", null, new String[] {"er1"});
				lexicon.addAddendum("yyeyyyy", null, new String[] {"ey1"});
				lexicon.addAddendum("yyfyy", null, new String[] {"f"});
				lexicon.addAddendum("yyhhyy", null, new String[] {"hh"});
				lexicon.addAddendum("yyiyyyy", null, new String[] {"iy1"});
				lexicon.addAddendum("yyjhyy", null, new String[] {"jh"});
				lexicon.addAddendum("yykyy", null, new String[] {"k"});
				lexicon.addAddendum("yymyy", null, new String[] {"m"});
				lexicon.addAddendum("yynyy", null, new String[] {"n"});
				lexicon.addAddendum("yypyy", null, new String[] {"p"});
				lexicon.addAddendum("yylyy", null, new String[] {"l"});
				lexicon.addAddendum("yyowyyy", null, new String[] {"ow1"});
				lexicon.addAddendum("yyoyyyy", null, new String[] {"oy1"});
				lexicon.addAddendum("yypyy", null, new String[] {"p"});
				lexicon.addAddendum("yyryy", null, new String[] {"r"});
				lexicon.addAddendum("yysyy", null, new String[] {"s"});
				lexicon.addAddendum("yyshyy", null, new String[] {"sh"});
				lexicon.addAddendum("yytyy", null, new String[] {"t"});
				lexicon.addAddendum("yythyy", null, new String[] {"th"});
				lexicon.addAddendum("yyuhyyy", null, new String[] {"uh1"});
				lexicon.addAddendum("yyuwyy", null, new String[] {"uw"});
				lexicon.addAddendum("yyvyy", null, new String[] {"v"});
				lexicon.addAddendum("yywyy", null, new String[] {"w"});
				lexicon.addAddendum("yyyyy", null, new String[] {"y"});
				lexicon.addAddendum("yyzyy", null, new String[] {"z"});
				lexicon.addAddendum("yyzhyy", null, new String[] {"zh"});

				/*
				helloVoice.speak("yykyy yykyy");
				helloVoice.speak("yyaeyyy");
				helloVoice.speak("yytyy yytyy");

				helloVoice.speak("yykyy yykyy yyaeyyy yytyy yytyy");
				 */

				/*
helloVoice.speak("yyaayyy");
helloVoice.speak("odd");

helloVoice.speak("yyaeyyy");
helloVoice.speak("at");

helloVoice.speak("yyahyyy");
helloVoice.speak("hut");

helloVoice.speak("yyaoyyy");
helloVoice.speak("ought");

helloVoice.speak("yyawyyy");
helloVoice.speak("cow");

helloVoice.speak("yyayyy");
helloVoice.speak("hide");

helloVoice.speak("yybyy");
helloVoice.speak("be");

helloVoice.speak("yychyy");
helloVoice.speak("cheese");

helloVoice.speak("yydyy");
helloVoice.speak("dee");

helloVoice.speak("yydhyy");
helloVoice.speak("thee");

helloVoice.speak("yyehyyy");
helloVoice.speak("red");

helloVoice.speak("yyeryyy");
helloVoice.speak("hurt");

helloVoice.speak("yyeyyyy");
helloVoice.speak("ate");

helloVoice.speak("yyfyy");
helloVoice.speak("fee");

helloVoice.speak("yygyy");
helloVoice.speak("green");

helloVoice.speak("yyhhyy");
helloVoice.speak("he");

helloVoice.speak("yyihyyy");
helloVoice.speak("it");

helloVoice.speak("yyiyyyy");
helloVoice.speak("eat");

helloVoice.speak("yyjhyy");
helloVoice.speak("gee");

helloVoice.speak("yykyy");
helloVoice.speak("key");

helloVoice.speak("yylyy");
helloVoice.speak("lee");

helloVoice.speak("yymyy");
helloVoice.speak("me");

helloVoice.speak("yynyy");
helloVoice.speak("knee");

helloVoice.speak("yypyy");
helloVoice.speak("ping");

helloVoice.speak("yyowyyy");
helloVoice.speak("oat");

helloVoice.speak("yyoyyyy");
helloVoice.speak("toy");

helloVoice.speak("yypyy");
helloVoice.speak("pee");

helloVoice.speak("yyryy");
helloVoice.speak("reed");

helloVoice.speak("yysyy");
helloVoice.speak("sea");

helloVoice.speak("yyshyy");
helloVoice.speak("she");

helloVoice.speak("yytyy");
helloVoice.speak("tea");

helloVoice.speak("yythyy");
helloVoice.speak("theta");

helloVoice.speak("yyuhyyy");
helloVoice.speak("hood");

helloVoice.speak("yyuwyy");
helloVoice.speak("two");

helloVoice.speak("yyvyy");
helloVoice.speak("vee");

helloVoice.speak("yywyy");
helloVoice.speak("we");

helloVoice.speak("yyyyy");
helloVoice.speak("yield");

helloVoice.speak("yyzyy");
helloVoice.speak("zee");

helloVoice.speak("yyzhyy");
helloVoice.speak("seizure");

				 */

	}



	/* The the synthesizer to speak and wait for it to
	 * complete.
	 */
	public void run() {
		System.out.printf(this.getClass().getPackage().getName() + "running...\n"); 

		//	synthesizer.speakPlainText("Hello Chris!", null);
		//	synthesizer.speakPlainText("Hello Chris!", null);
		//	synthesizer.speakPlainText("Hello Chris!", null);

		boolean done = false;
		while (!done) {
			try {
				Thread.currentThread();
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				done = true;
			}
		}
	}


	public void speak(String in) {
		System.out.printf(this.getClass().getPackage().getName() + " to speak \"%s\" \n",in);

		helloVoice.speak(in);
	}



	/* Clean up and leave.
	 */

	public void cleanUp() {

		helloVoice.deallocate();
	}

}

