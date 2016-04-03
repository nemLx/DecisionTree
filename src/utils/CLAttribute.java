package utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;

/**
 * A structure for calculating information gain measures
 * used to determine splitting attributes
 * 
 * @author Nemo Li (jli65@illinois.edu)
 * @version 1.0
 * 
 */

public class CLAttribute{

	private HashMap<Integer, Integer> mapP;
	private HashMap<Integer, Integer> mapN;
	private HashMap<Integer, Integer> keyMap;

	private int numKeys;
	private int numEntries;
	private int currSize;
	private int[] keyCount;
	
	private double info;
	private double splitInfo;
	private double gini;

	private static CLUtil u = new CLUtil();


	
	/**
	 * Constructor creates an CLAttribute structure
	 * corresponds to a dataset with certain number of records
	 *
	 * @param numEntries the number of entries in the dataset
	 * 			this attribute belongs to
	 */
	public CLAttribute(int numEntries){
		this.numEntries = numEntries;
		currSize = 0;
		mapP = new HashMap<Integer, Integer>(numEntries, 1);
		mapN = new HashMap<Integer, Integer>(numEntries, 1);
		info = 0;
		splitInfo = 0;
		gini = 0;
		keyMap = null;
	}


	
	/**
	 * Inserts a value, label pair into this arribute structure
	 * 
	 * @param value value of this arribute associated with a record
	 * @param label class label of the record with the attibute value
	 *
	 * @return 1 if successful, 0 otherwise
	 */
	public int insert(int value, int label){

		if (currSize >= numEntries){
			System.err.println("CLArribute numKeys overflow");
			return 0;
		}

		int temp = 0;
		if (label == u.P){
			if (mapP.containsKey(value)){
				temp = mapP.get(value);
				mapP.put(value, temp+1);
			}else
				mapP.put(value, 1);
		}else if (label == u.N){
			if (mapN.containsKey(value)){
				temp = mapN.get(value);
				mapN.put(value, temp+1);
			}else
				mapN.put(value, 1);
		}

		currSize++;

		if (currSize == numEntries){
			consolidate();
		}

		return 1;
	}


	
	/**
	 * Determines unique values of this attribute, the count,
	 * and the class label count of each value; should be
	 * called only once, after all values had been examined.
	 */
	private void consolidate(){
		
		HashSet<Integer> keySet = new HashSet<Integer>();
		keySet.addAll(mapP.keySet());
		keySet.addAll(mapN.keySet());
		Iterator<Integer> it = keySet.iterator();

		numKeys = keySet.size();
		keyMap = new HashMap<Integer, Integer>();
		keyCount = new int[numKeys];
		
		int i = 0;
		int key = 0;
		int numP = 0;
		int numN = 0;
		double ratio = 0;
		
		while(it.hasNext()){
			key = it.next();
			keyMap.put(key, i);
			
			numP = mapP.containsKey(key) ? mapP.get(key) : 0;
			numN = mapN.containsKey(key) ? mapN.get(key) : 0;
			
			ratio = (numP+numN)/(double)numEntries;
			
			keyCount[i] = numP + numN;
			info += ratio*u.computeInfo(numP, numN);
			splitInfo += ratio*u.log2(ratio);
			gini += ratio*u.computeGini(numP, numN);
			i++;
		}
		
		splitInfo = -splitInfo;
		mapP = null;
		mapN = null;
	}

	
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String retVal = "";

		retVal += "splitInfo: " + splitInfo + " info: "+info+"\n";

		return retVal;
	}



	// below are all public getters
	
	public HashMap<Integer,Integer> getKeys(){
		return keyMap;
	}

	public int getKeyEntryCount(int keyIndex){
		return keyCount[keyIndex];
	}

	public int getNumKeys(){
		return numKeys;
	}

	public double getInfo(){
		return info;
	}

	public double getSplitInfo(){
		return splitInfo;
	}
	
	public double getGini(){
		return gini;
	}
}
