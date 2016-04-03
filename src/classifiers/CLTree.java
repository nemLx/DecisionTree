package classifiers;

import utils.*;

import java.util.ArrayList;
import java.util.Vector;
import java.util.HashMap;

/**
 * An implementation of a general purpose decision tree 
 * 
 * @author Nemo Li
 * @version 1.0
 * 
 */

public class CLTree extends Classifier{

	private CLData node;
	private CLTree[] children;

	private int splitAttrIndex;
	private HashMap<Integer, Integer> splitAttrValues;

	private boolean isLeaf;
	private int numChildren;
	private int label;

	
	
	/**
	 * Constructor, generates a decision tree based on a source
	 * data set
	 * 
	 * @param data training data set
	 * @param fRatio F/numAttribute
	 */
	public CLTree(CLData data, double fRatio){
		node = data;
		label = node.getLabel();

		if (node.isPure()){
			
			// terminate the process if the node is pure
			children = null;
			isLeaf = true;
			
		}else{
			
			// splitting attribute is determined according to the value of F
			if (fRatio == 1){
				splitAttrIndex = node.getMaxGainRatioAttribute();
			}else{
				splitAttrIndex = node.getMaxDeltaGiniAttribute();
			}
			
			// terminate the process if there is no eligible splitting attribute
			if (splitAttrIndex == -1){
				children = null;
				isLeaf = true;
				return;
			}

			ArrayList<Vector<CLRecord>> split = node.split(splitAttrIndex);

			numChildren = split.size();
			children = new CLTree[numChildren];
			splitAttrValues = node.getSplitAttrValues();

			for (int i = 0; i < numChildren; i++){
				children[i] = new CLTree(new CLData(split.get(i), fRatio), fRatio);
			}

			isLeaf = false;
			
			//System.out.println(node);
		}
	}

	
	
	/*
	 * (non-Javadoc)
	 * @see classifiers.Classifier#query(utils.CLRecord)
	 */
	public int query(CLRecord record){

		int splitAttrValue;

		if (isLeaf){
			return label;
		}else{
			splitAttrValue = record.getValue(splitAttrIndex);
			if (splitAttrValues.get(splitAttrValue) == null){
				return node.getLabel();
			}
			return children[splitAttrValues.get(splitAttrValue)].query(record);
		}
	}

	
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
			
		String retVal = "";

		if (isLeaf){
			retVal = node.toString() + "\n";
		}else{
			for (int i = 0; i < numChildren; i++){
				retVal += children[i].toString();
			}
		}

		return retVal;
	}

}