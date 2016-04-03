package classifiers;

import utils.*;

/**
 * An implementation of the random forest classification algorithm
 * 
 * @author Nemo Li
 * @version 1.0
 * 
 */

public class CLForrest extends CLBag {

	private double fRatio;
	
	
	
	/**
	 * Constructor, trains the classifier from training data set
	 * 
	 * @param src training data set 
	 * @param k the number of models to be generated in the bag
	 * @param fRatio F/numAttributes
	 */
	public CLForrest(CLData src, int k, double fRatio){
		
		super(src, k);
		this.fRatio = Math.max(fRatio,1); // at least use one attribute
		constructModels();
	}
	
	
	
	/**
	 * Helper function that constructs a set of models based on
	 * source data set, using bootstrap sampling, and randomly
	 * choosing F attributes as splitting candidates at each node
	 */
	private void constructModels(){

		int poolSize = src.getNumRecords();
		models = new CLTree[k];

		int[] permutation = null;
		for (int i = 0; i < k; i++){
			permutation = u.bootstrap(poolSize);
			CLData sample = new CLData(src, permutation, fRatio);
			models[i] = new CLTree(sample, fRatio);
		}
	}
}
