package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * A data structure facilitating the DecisionTree algorithm,
 * as well as bagging ensemble method
 * 
 * @author Nemo Li (jli65@illinois.edu)
 * @version 1.0
 * 
 */

public class CLData{

	private CLRecord[] records;
	private CLAttribute[] attributes;
	private HashMap<Integer, Integer> splitAttrValues;

	private int numRecords;
	private int numAttributes;

	private ArrayList<Double> gains;
	private ArrayList<Double> gainRatios;
	private ArrayList<Double> deltaGini;

	private double numP;
	private double numN;

	private int f;
	private int label;
	private boolean isPure;
	
	private double maxGainRatio;
	private int maxGainRatioAttribute;
	private double maxDeltaGini;
	private int maxDeltaGiniAttribute;

	private static CLUtil u = new CLUtil();



	/**
	 * Constructor for data as a vector of CLRecords
	 *
	 * @param a vector of CLRecords to be contained in this CLData
	 */
	public CLData(Vector<CLRecord> rs, double fRatio){
		nullInit();
		recordConstructorHelper(rs);
		constructorComputationHelper(fRatio);
	}


	
	/**
	 * Constructor, creates a CLData with a set of CLRecord from
	 * another CLData, indexed with an array of integers
	 *
	 * @param src the CLData to copy CLRecords from
	 * @param permutation an array containing the indices of
	 * 			CLRecords to be selected from src
	 */
	public CLData(CLData src, int[] permutation, double fRatio){
		int size = permutation.length;
		Vector<CLRecord> rs = new Vector<CLRecord>(size);

		for (int i = 0; i < size; i++){
			rs.add(i, src.getRecord(permutation[i]));
		}

		nullInit();
		recordConstructorHelper(rs);
		constructorComputationHelper(fRatio);
	}		



	/**
	 * Constructor for data on file
	 *
	 * @param fname filename of data file to be read
	 */
	public CLData(String fname, double fRatio){
		nullInit();
		parseFile(fname);
		constructorComputationHelper(fRatio);
	}



	/**
	 * A convenience method that initializes every member variable in 
	 * CLData to be a save default value
	 */
	private void nullInit(){
		records = null;
		attributes = null;
		splitAttrValues = null;
		
		numAttributes = 0;
		numRecords = 0;
		
		gains = null;
		gainRatios = null;
		deltaGini = null;
		
		numP = 0;
		numN = 0;
		
		f = 0;
		isPure = false;
		label = 0;
		maxGainRatioAttribute = -1;
		maxGainRatio = u.minDouble;
		maxDeltaGini = 0;
		maxDeltaGiniAttribute = -1;
	}
	
	
	
	/**
	 * A helper function that calculates all measures used to determine
	 * the splitting attribute
	 * 
	 * @param fRatio the ratio of f/numAttributes
	 */
	private void constructorComputationHelper(double fRatio){
		
		if (fRatio == 1){
			computeMaxGainInfo();
		}else{
			f = (int)(fRatio*numAttributes);
			computeMaxDeltaGini();
			randomMaskAttributes();
		}
		
		computeLabelPurity();
		
		// free up some space, as these variables are no longer needed
		gainRatios = null;
		gains = null;
		deltaGini = null;
	}


	
	/**
	 * Helper method that populates this CLData structure using
	 * a vector of CLRecords
	 *
	 * @param rs a vector of CLRecords to be added to this CLData
	 */
	private void recordConstructorHelper(Vector<CLRecord> rs){
		numRecords = rs.size();
		records = new CLRecord[numRecords];

		int tempLabel = 0;
		for (int i = 0; i < numRecords; i++){
			records[i] = rs.get(i);
			tempLabel = records[i].getLabel();
			if (tempLabel == u.P)
				numP++;
			else if (tempLabel == u.N)
				numN++;
		}

		numAttributes = records[0].getAttrCount();
		constructAttributes();
	}



	/**
	 * Constructs the entire CLData structure from an input file
	 * 
	 * @param fname filename of the file to read from
	 * @return 1 if successful, 0 otherwise
	 */
	private int parseFile(String fname){

		try {
			
			BufferedReader br = new BufferedReader(new FileReader(fname));
			StringTokenizer tLine = null;
			String strLine = null;
			int currLine = 0;
			int currAttr = 0;
			int temp = 0;
			int[] currRecord = null;

			numRecords = u.countLines(fname);
			numAttributes = 0;
			records = new CLRecord[numRecords];
			attributes = null;

			while ((strLine = br.readLine()) != null){

				tLine = new StringTokenizer(strLine);
				if (numAttributes == 0){
					numAttributes = tLine.countTokens()-1;
					attributes = new CLAttribute[numAttributes];
				}
				currRecord = new int[numAttributes+1];

				currAttr = 0;
				while (tLine.hasMoreTokens()){

					temp = Integer.parseInt(tLine.nextToken());
					currRecord[currAttr] = temp;

					if (currAttr > 0){
						if (currLine == 0){
							attributes[currAttr-1] = new CLAttribute(numRecords);
						}
						attributes[currAttr-1].insert(temp, currRecord[0]);
					}

					currAttr++;
				}

				records[currLine] = new CLRecord(currRecord);
				currLine++;

				if (currRecord[0] == u.P){
					numP++;
				}else if (currRecord[0] == u.N){
					numN++;
				}
			}
			
			br.close();
			return 1;

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

		return 0;
	}



	/**
	 * Splits the record in this CLData into segments based on a 
	 * particular attribute
	 * 
	 * @param attribute the value of integer representation of the attribute 
	 * 				to split the record upon
	 *
	 * @return an ArrayList of Vectors of CLRecord, each corresponds to 
	 * 				all the CLRecords with a particular value of the attribute
	 */
	public ArrayList<Vector<CLRecord>> split(int attribute){

		CLAttribute splitAttr = attributes[attribute];
		int numPartitions = splitAttr.getNumKeys();
		ArrayList<Vector<CLRecord>> retVal = new ArrayList<Vector<CLRecord>>(numPartitions);
		splitAttrValues = splitAttr.getKeys();

		int attrValue = 0;
		int partitionIndex = 0;

		for (int i = 0; i < numPartitions; i++){
			retVal.add(new Vector<CLRecord>(splitAttr.getKeyEntryCount(i)));
		}

		for (int i = 0; i < numRecords; i++){
			attrValue = records[i].getValue(attribute);
			if (!splitAttrValues.containsKey(attrValue)){
				System.out.println("bad");
				System.exit(-1);
			}
			partitionIndex = splitAttrValues.get(attrValue);
			retVal.get(partitionIndex).add(records[i]);
		}

		return retVal;
	}



	/**
	 * Construct the CLAttribute structures based on existing CLRecord
	 */
	private void constructAttributes(){
		attributes = new CLAttribute[numAttributes];

		for (int i = 0; i < numAttributes; i++){
			attributes[i] = new CLAttribute(numRecords);
			for (int j = 0; j < numRecords; j++){
				attributes[i].insert(records[j].getValue(i), records[j].getLabel());
			}
		}
	}
	
	
	
	/**
	 * Computes the purity and label of this data set
	 */
	private void computeLabelPurity(){
		
		isPure = numP*numN == 0 && numP+numN > 0;
		
		if (isPure){
			label = records[0].getLabel();
		}else{
			if (numP > numN)
				label = u.P;
			else
				label = u.N;
		}
	}
	
	
	
	/**
	 * Generates a random mask that leaves only f positive Gini
	 * indices, sets the rest to 0, which will never be selected
	 * as splitting attribute
	 */
	private void randomMaskAttributes(){
		
		// its easier to generate the reverse mask, hence the numAttributes-f
		int [] mask = u.subSample(numAttributes, numAttributes-f);
		
		for (int i = 0; i < mask.length; i++){
			deltaGini.set(mask[i], 0.);
		}
	}



	/**
	 * Sets all gainRatios whose corresponding gain is less than the 
	 * average gain of the entire CLData
	 */
	private void purifyGainRatios(){
		double sum = 0;
		double average = 0;

		for (int i = 0; i < gains.size(); i++){
			sum += gains.get(i);
		}

		average = sum/gains.size();
		
		for (int i = 0; i < gainRatios.size(); i++){
			if (gains.get(i) < average)
				gainRatios.set(i, u.minDouble);
		}
	}



	/**
	 * Computes the maxGainRatio of all attributes and the first
	 * attribute with the max value
	 */
	private void computeMaxGainInfo(){
		
		if (gainRatios == null)
			computeGainRatios();

		purifyGainRatios();
		maxGainRatio = (double)Collections.max(gainRatios);

		// if no gainRatio is bigger than minimum double value then
		// no attribute is usable
		if (maxGainRatio == u.minDouble)
			maxGainRatioAttribute = -1;
		else
			maxGainRatioAttribute = gainRatios.indexOf(maxGainRatio);
	}



	/**
	 * Computes the gainRatio of all attributes
	 */
	private void computeGainRatios(){

		if (gains == null){
			computeGains();
		}

		gainRatios = new ArrayList<Double>(numAttributes);

		for (int i = 0; i < numAttributes; i++){
			if (attributes[i].getSplitInfo() != -0.0)
				gainRatios.add(i, gains.get(i)/attributes[i].getSplitInfo());
			else
				gainRatios.add(i, u.minDouble);
		}
	}



	/**
	 * Computes the gain of all attributes
	 */
	private void computeGains(){

		double info = u.computeInfo(numP, numN);
		gains = new ArrayList<Double>(numAttributes);

		for (int i = 0; i < numAttributes; i++){
			gains.add(i, info - attributes[i].getInfo());
		}
	}
	
	
	
	/**
	 * Computes the maximum change in Gini index of all attributes
	 */
	private void computeMaxDeltaGini(){
		
		double gini = u.computeGini(numP, numN);
		
		deltaGini = new ArrayList<Double>(numAttributes);
		
		for (int i = 0; i < numAttributes; i++){
			deltaGini.add(i, gini-attributes[i].getGini());
		}
		
		maxDeltaGini = Collections.max(deltaGini);
		
		// if all delta Gini are 0, no attribute is usable
		if (maxDeltaGini == 0)
			maxDeltaGiniAttribute = -1;
		else
			maxDeltaGiniAttribute = deltaGini.indexOf(maxDeltaGini);
	}



	/**
	 * Displays important information of this CLData structure
	 *
	 * @return a string containing important information of this CLData
	 */
	public String toString(){

		String retVal = "";

		for (int i = 0; i < numRecords; i++){
			retVal += records[i].toString();
			retVal += "\n";
		}

		retVal += "\n";

		for (int i = 0; i < numAttributes; i++){
			retVal += attributes[i].toString();
			retVal += "gain: " + gains.get(i) + "\n";
			retVal += "\n";
		}

		for (int i = 0; i < numAttributes; i++){
			retVal += gains.get(i) + " " + gainRatios.get(i) + "\n";
		}

		return retVal;
	}



	// below are all public getters

	public int getRecordLabel(int i){
		return records[i].getLabel();
	}

	public int getNumRecords(){
		return numRecords;
	}
	
	public int getNumAttributes(){
		return numAttributes;
	}

	public CLRecord getRecord(int i){
		return records[i];
	}

	public int getLabel(){
		return label;
	}

	public HashMap<Integer, Integer> getSplitAttrValues(){
		return splitAttrValues;
	}

	public boolean isPure(){
		return isPure;
	}

	public int getMaxGainRatioAttribute(){
		return maxGainRatioAttribute;
	}
	
	public int getMaxDeltaGiniAttribute(){
		return maxDeltaGiniAttribute;
	}
	
	public int getF(){
		return f;
	}

}
