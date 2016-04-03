package classification;

import classifiers.*;
import utils.*;

/**
 * A driver for bagging classification method, reads files given
 * by command line arguments
 * 
 * @author Nemo Li
 * @version 1.0
 * 
 */

public class Bagging{

	public static void main(String[] args){

		if (args.length < 3){
			System.err.println(
					"Usage: java classification.Bagging k trainFileName testFileName \n where" +
					" k is the number of trees used in the bag, should be less than 100"
			);
			return;
		}
		
		boolean full = args.length <= 3 ? false : true;

		int k = Integer.parseInt(args[0]);
		CLData source = new CLData(args[1], 1);
		CLData test  = new CLData(args[2], 1);
		
		Classifier bag = new CLBag(source, k);
		bag.test(test);
		bag.report(full);
	}
}