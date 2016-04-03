package classifiers;

import utils.*;

/**
 * An implementation of the bagging classification algorithm
 * 
 * @author Nemo Li
 * @version 1.0
 * 
 */

public class CLBag extends Classifier{

	protected CLData src;
	protected CLTree[] models;

	protected int k;


	
	/**
	 * Constructor, trains the classifier from training data set
	 * 
	 * @param src training data set 
	 * @param k the number of models to be generated in the bag
	 */
	public CLBag(CLData src, int k){

		this.k = k;
		this.src = src;
		constructModels();
	}


	
	/*
	 * (non-Javadoc)
	 * @see classifiers.Classifier#query(utils.CLRecord)
	 */
	public int query(CLRecord record){

		int voteSum = 0;

		for (int i = 0; i < k; i++){
			voteSum += models[i].query(record);
		}

		if (voteSum >= 0)
			return u.P;
		else
			return u.N;
	}



	/**
	 * Helper function that constructs a set of models based on
	 * source data set, using bootstrap sampling
	 */
	private void constructModels(){

		int poolSize = src.getNumRecords();
		models = new CLTree[k];

		int[] permutation = null;
		for (int i = 0; i < k; i++){
			permutation = u.bootstrap(poolSize);
			CLData sample = new CLData(src, permutation, 1);
			models[i] = new CLTree(sample, 1);
		}
	}

}