package cwinsor.com.speech_recognition;

import java.util.EventObject;
import java.util.List;

import edu.cmu.sphinx.frontend.FloatData;

public class SpeechRecognitionEventObject extends EventObject {

	private List<String> theMessage;
	private InstancesFrontend39Features instancesFrontend39Features;

	public SpeechRecognitionEventObject( Object source,
			List<String> theMessage,
			InstancesFrontend39Features instancesFrontend39Features) {
		super( source);
		this.theMessage = theMessage;
		this.instancesFrontend39Features = instancesFrontend39Features;
	}

	public List<String> theMessage() {
		return theMessage;
	}
	public InstancesFrontend39Features theInstancesFrontend39Features() {
		return instancesFrontend39Features;
	}
}

