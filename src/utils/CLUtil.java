package utils;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;
import java.lang.Math;

/**
 * A utility class containing common generic methods used
 * in classification
 * 
 * @author Nemo Li (jli65@illinois.edu)
 * @version 1.0
 * 
 */

public class CLUtil{

	public final double minDouble = -1.0 * Double.MAX_VALUE;
	public final int P = 1;
	public final int N = -1;


	
	/**
	 * Randomly generate an array of integers
	 * 
	 * @param size regulates the maximum int to be generated, 
	 * 				which is size -1
	 * @param subSize specifies the size of the array
	 * @return int array
	 */
	public int[] subSample(int size, int subSize){
		
		HashSet<Integer> s = new HashSet<Integer>();
		int[] retVal = new int[subSize];
		
		for (int i = 0; i < subSize; i++){
			int ip = i+1;
			while (s.size() != ip){
				int t = (int)(Math.random()*size);
				s.add(t);
				retVal[i] = t;
			}
		}
		
		return retVal;
	}
	
	

	/**
	 * Generates a bootstrap sample of a continuous sequence
	 * of integers up to a certain number
	 * 
	 * @param d the largest number in the integer sequence is d-1
	 * @return bootstrapped int array
	 */
	public int[] bootstrap(int d){
		int[] retVal = new int[d];
		for (int i = 0; i < d; i++){
			retVal[i] = (int)(Math.random()*(d-0));
		}
		return retVal;
	}



	/**
	 * Rounds a double into an int according to the coventional
	 * rounding rule
	 * 
	 * @param x a double to be rounded
	 * @return rounded integer
	 */
	public int round(double x){
		int base = (int)Math.round(x);

		if ( x - base < 0.5)
			return base;
		else
			return base+1;
	}
	


	/**
	 * Displays a split of a set of CLRecords
	 * 
	 * @param split the split to be displayed
	 */
	public void printSplit(ArrayList<Vector<CLRecord>> split){
		int numPartitions = split.size();
		int pSize = 0;
		Vector<CLRecord> curr = null;

		for (int i = 0; i < numPartitions; i++){
			curr = split.get(i);
			pSize = curr.size();
			for (int j = 0; j < pSize; j++){
				System.out.print(curr.get(j).getLabel()+" ");
			}
			System.out.print("\n"+"\n");
		}
	}



	/**
	 * Computes the log based 2 value of a double
	 * 
	 * @param x double whose log base 2 is to be computed
	 * @return computed log base 2
	 */
	public double log2(double x){
		return Math.log(x)/Math.log(2);
	}



	/**
	 * Computes info according to the definition in information
	 * gain theory
	 * 
	 * @param numP number of positive labels
	 * @param numN number of negative labels
	 * @return the computed info
	 */
	public double computeInfo(double numP, double numN){

		if (numN == 0 || numP == 0){
			return 0;
		}

		double rP = numP/(numP+numN);
		double rN = numN/(numP+numN);

		return (-1)*rP*log2(rP) + (-1)*rN*log2(rN);
	}
	
	
	
	/**
	 * Computes the gini index of a partition
	 * 
	 * @param numP number of positive labels
	 * @param numN number of negative labels
	 * @return the computed gini index
	 */
	public double computeGini(double numP, double numN){
		return 1-Math.pow(numP/(numP+numN), 2)-Math.pow(numN/(numP+numN), 2);
	}
	


	/**
	 * Count the number of lines in a file
	 * 
	 * @param filename file name of file to be counted
	 * @return the number of lines in the file
	 */
	public int countLines(String filename){
    try {
    	InputStream is = new BufferedInputStream(new FileInputStream(filename));
    	byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n')
						count++;
					}
				}
				is.close();
				return (count == 0 && !empty) ? 1 : count;
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return -1;
	}
}
