/**
 *
 */

package utils;

/**
 * A data structure used to keep track of single data record
 * entries in a data set, containing the class label and
 * attribute values
 * 
 * @author Nemo Li
 * @version 1.0
 * 
 */

public class CLRecord{

	private int[] raw;
	private int size;
	private int label;

	/**
	 * Constructor, generates a data record from an array
	 * 
	 * @param record an int array read from file, first entry
	 * 				should be the class label of this record
	 */
	public CLRecord(int[] record){
		raw = record;
		size = record.length;
		label = raw[0];
	}



	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){

		String retVal = "";

		for (int i = 1; i < size; i++){
			retVal += raw[i];
			retVal += " ";
		}

		retVal += "-- label: " + label;

		return retVal;
	}


	
	//below are all public getters
	
	public int getAttrCount(){
		return size - 1;
	}

	public int getValue(int attribute){
		return raw[attribute+1];
	}

	public int getLabel(){
		return label;
	}

}