package classification;

import classifiers.*;
import utils.*;

/**
 * A driver for decision tree classification method, reads files given
 * by command line arguments
 * 
 * @author Nemo Li
 * @version 1.0
 * 
 */

public class DecisionTree{
	
	public static void main(String[] args){

		if (args.length < 2){
			System.err.println("Usage: java classification.DecisionTree trainFileName testFileName");
			return;
		}
		
		boolean full = args.length <= 2 ? false : true;

		CLData train = new CLData(args[0], 1);
		CLData test  = new CLData(args[1], 1);

		Classifier t = new CLTree(train, 1);
		t.test(test);
		t.report(full);
	}
}
