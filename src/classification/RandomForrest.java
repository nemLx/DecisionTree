package classification;

import classifiers.*;
import utils.*;

/**
 * A driver for random forest classification method, reads files given
 * by command line arguments
 * 
 * @author Nemo Li
 * @version 1.0
 * 
 */

public class RandomForrest {
	
	public static void main(String[] args){

		if (args.length < 2){
			System.err.println("Usage: java classification.RandomForrest [k] [fRatio] trainFileName testFileName");
			return;
		}
		
		boolean full = false;
		int k = 15;
		double fRatio = 0.20;
		CLData source = null;
		CLData test = null;
		
		if (args.length > 3){
			full = args.length <= 4 ? false : true;
			k = Integer.parseInt(args[0]);
			fRatio = Double.parseDouble(args[1]);
			source = new CLData(args[2], 1);
			test  = new CLData(args[3], 1);
		}else{
			source = new CLData(args[0], 1);
			test  = new CLData(args[1], 1);
		}
		
		Classifier forrest = new CLForrest(source, k, fRatio);
		forrest.test(test);
		forrest.report(full);
	}
	
}
