/**
 * 
 */
package cwinsor.com.speech_recognition;

import weka.core.DenseInstance;
import weka.filters.SupervisedFilter;

/**
 * @author cwinsor
 *
 */
public class InstanceFrontend39Features extends DenseInstance {
	public static final int NUMBER_OF_FEATURES_RECEIVED_FROM_FRONTEND = 39;
	public static final int NUMBER_OF_FEATURES_USED_FROM_FRONTEND = 13;

	/*
	 * constructor
	 * creates an instance with 39 features
	 */
	public InstanceFrontend39Features(float[] values) {
		super(NUMBER_OF_FEATURES_USED_FROM_FRONTEND);
		
		// sanity check
		if (values.length != NUMBER_OF_FEATURES_RECEIVED_FROM_FRONTEND) {
			System.out.println(this.getClass().getName() + "----- ERROR - number of features" +
					"expected " + NUMBER_OF_FEATURES_USED_FROM_FRONTEND +
					"observed " + values.length);
		}

		// capture the given values into the Instance
		// for (int i=0; i<NUMBER_OF_FEATURES_USED_FROM_FRONTEND; i++) {
			for (int i=0; i<13; i++) {
			this.setValue(i, values[i+13]);
		}
	}
}
