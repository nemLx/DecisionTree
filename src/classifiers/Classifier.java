package classifiers;

import utils.*;

/**
 * An abstract classifier class, provides common functionalities test
 * and report, as well as abstract interface query function
 * 
 * @author Nemo Li
 * @version 1.1
 * 
 */

public abstract class Classifier {
	
	/**
	 * A class utilizes calculation of measures used to examine
	 * the performance of the classifier
	 * 
	 * @author nemo
	 * @version 1.0
	 */
	static class Measure{
		
		public int TP;
		public int TN;
		public int FP;
		public int FN;
		public int P;
		public int N;
		
		public double AC;
		public double ER;
		public double SE;
		public double SP;
		public double PR;
		public double RC;
		
		
		
		/**
		 * Constructor
		 * 
		 * @param TP true positive
		 * @param TN true negative
		 * @param FP false positive
		 * @param FN false negative
		 * @param P total positive
		 * @param N total negative
		 */
		public Measure (int TP, int TN, int FP, int FN, int P, int N){
			this.TP = TP;
			this.TN = TN;
			this.FP = FP;
			this.FN = FN;
			this.P = P;
			this.N = N;
			
			AC = ((double)(TP+TN))/(P+N);
			ER = ((double)(FP+FN))/(P+N);
			SE = (double)TP/P;
			SP = (double)TN/N;
			PR = (double)TP/(TP+FP);
			RC = (double)TP/(TP+FN);
		}
		
		
		
		/**
		 * Calculates the F-score given a beta value
		 * 
		 * @param beta
		 * @return F-score based on beta
		 */
		public double getFB(double beta){
			return (1+beta*beta)*PR*RC/(beta*beta*PR+RC);
		}
		
		
		
		/**
		 * @param full true if were to print complete measures
		 * @return
		 */
		public String toString(boolean full){
			String retVal = "";
			
			if (full){
				retVal += "True Positive: "+TP+"\n";
				retVal += "True Negative: "+TN+"\n";
				retVal += "False Positive: "+FP+"\n";
				retVal += "False Negative: "+FN+"\n";
				retVal += "Accuracy: "+AC+"\n";
				retVal += "Error Rate: "+ER+"\n";
				retVal += "Sensitivity: "+SE+"\n";
				retVal += "Specificity: "+SP+"\n";
				retVal += "Precision: "+PR+"\n";
				retVal += "Recall: "+RC+"\n";
				retVal += "F-"+1+": "+getFB(1)+"\n";
				retVal += "F-"+0.5+": "+getFB(0.5)+"\n";
				retVal += "F-"+2+": "+getFB(2)+"\n"+"\n";
				
				retVal += d(AC)+" & "+d(ER)+" & "+d(SE)+" & "+d(SP)+" & "+
									d(PR)+" & "+d(RC)+" & "+d(getFB(1))+" & "+
									d(getFB(0.5))+" & "+d(getFB(2))+" \\\\" + "\n";
			}else{
				retVal += TP+"\n"+TN+"\n"+FP+"\n"+FN+"\n";
			}
			
			return retVal;
		}
		
		
		
		/**
		 * Formats a double to 3 decimal points
		 * 
		 * @param d double to be formatted
		 * @return
		 */
		private String d(double d){
			return String.format("%1$,.3f", d);
		}
	}
	
	
	
	private Measure m;
	protected static CLUtil u = new CLUtil(); 
	
	
	
	/**
	 * A function returns class label based on the classifier trained
	 * from the training data set
	 * 
	 * @param r a record whose class is to be determined
	 * @return class label of predicted class
	 */
	public abstract int query(CLRecord r);
	
	
	
	/**
	 * Runs the classifier on a test data set and records
	 * performance metrics
	 * 
	 * @param test the test data set
	 */
	public void test(CLData test){
		
		int predicted = 0;
		int actual = 0;
		
		int TP = 0;
		int TN = 0;
		int FP = 0;
		int FN = 0;
		
		int P = 0;
		int N = 0;
		
		for (int i = 0; i < test.getNumRecords(); i++){

			predicted = query(test.getRecord(i));
			actual = test.getRecordLabel(i);

			if (predicted == u.P){
				if (actual == u.P){
					TP++;
					P++;
				}else{
					FP++;
					N++;
				}
			}else{
				if (actual == u.N){
					TN++;
					N++;
				}else{
					FN++;
					P++;
				}
			}
		}
		
		m = new Measure(TP, TN, FP, FN, P, N);
	}
	
	
	
	/**
	 * Displays the results of the performance testing
	 * 
	 * @param full whether to print in the full version
	 * 				or the abbreviated version
	 */
	public void report(boolean full){
		
		System.out.println(m.toString(full));
	}
	
	
	
	//below are all public getters
	
	public int getTruPos(){
		return m.TP;
	}
	
	public int getTruNeg(){
		return m.TN;
	}
	
	public int getFalsePos(){
		return m.FP;
	}
	
	public int getFalseNeg(){
		return m.FN;
	}
	
}
