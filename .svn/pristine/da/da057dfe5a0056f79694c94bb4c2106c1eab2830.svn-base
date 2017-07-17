/**
 * 
 */
package cwinsor.com.speech_recognition;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.Instances;

/**
 * @author cwinsor
 * InstancesFrontend39Features
 * A set of N samples of InstanceFrontend39Features
 * for example as received from the Sphinx4 frontend.
 */
public class InstancesFrontend39Features extends Instances {

	public static final int NUMBER_OF_FEATURES_FROM_FRONTEND = 39;

	// a structure that has the attribute names
	// the frontend provides 13 features, their derivative, and double-derivative
	// a total of 39 features
	public static final ArrayList<Attribute> attList = new ArrayList<Attribute>() {{
		add(new Attribute("F00"));
		add(new Attribute("F01"));
		add(new Attribute("F02"));
		add(new Attribute("F03"));
		add(new Attribute("F04"));
		add(new Attribute("F05"));
		add(new Attribute("F06"));
		add(new Attribute("F07"));
		add(new Attribute("F08"));
		add(new Attribute("F09"));
		add(new Attribute("F10"));
		add(new Attribute("F11"));
		add(new Attribute("F12"));

		add(new Attribute("dF00"));
		add(new Attribute("dF01"));
		add(new Attribute("dF02"));
		add(new Attribute("dF03"));
		add(new Attribute("dF04"));
		add(new Attribute("dF05"));
		add(new Attribute("dF06"));
		add(new Attribute("dF07"));
		add(new Attribute("dF08"));
		add(new Attribute("dF09"));
		add(new Attribute("dF10"));
		add(new Attribute("dF11"));
		add(new Attribute("dF12"));

		add(new Attribute("ddF00"));
		add(new Attribute("ddF01"));
		add(new Attribute("ddF02"));
		add(new Attribute("ddF03"));
		add(new Attribute("ddF04"));
		add(new Attribute("ddF05"));
		add(new Attribute("ddF06"));
		add(new Attribute("ddF07"));
		add(new Attribute("ddF08"));
		add(new Attribute("ddF09"));
		add(new Attribute("ddF10"));
		add(new Attribute("ddF11"));
		add(new Attribute("ddF12"));
	}};

/**
 * constructor
 * @param name - name of the instances set
 */
	public InstancesFrontend39Features(String name) {
		super(name, attList, 0);
	}

}