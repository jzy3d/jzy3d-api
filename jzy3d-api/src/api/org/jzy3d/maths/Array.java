package org.jzy3d.maths;

import java.util.Date;


public class Array {
	
	public static int[] clone(int[] input){
		return Array.clone(input, input.length);
	}
	
	public static float[] clone(float[] input){
		return Array.clone(input, input.length);
	}
	
	public static double[] clone(double[] input){
		return Array.clone(input, input.length);
	}
	
	public static int[] clone(int[] input, int length){
		int lim;
		if(length>input.length)
			lim = input.length;
		else
			lim = length;
		
		/*// For 1.6
		float [] copy = Arrays.copyOf(data, data.length);*/

		// for 1.5 
		int[] copy = new int[lim];
		System.arraycopy(input, 0, copy, 0, lim); 
		
		return copy;		
	}
	
	public static float[] clone(float[] input, int length){
		int lim;
		if(length>input.length)
			lim = input.length;
		else
			lim = length;
		
		/*// For 1.6
		float [] copy = Arrays.copyOf(data, data.length);*/

		// for 1.5 
		float[] copy = new float[lim];
		System.arraycopy(input, 0, copy, 0, lim); 
		
		return copy;		
	}
	
	public static double[] clone(double[] input, int length){
		int lim;
		if(length>input.length)
			lim = input.length;
		else
			lim = length;
		
		/*// For 1.6
		float [] copy = Arrays.copyOf(data, data.length);*/

		// for 1.5 
		double[] copy = new double[lim];
		System.arraycopy(input, 0, copy, 0, lim); 
		
		return copy;		
	}
	
	/********************************************************************/

	public static int[] append(int[] input, int value){
		int[] copy = new int[input.length+1];
		System.arraycopy(input, 0, copy, 0, input.length); 
		copy[copy.length-1] = value;
		return copy;
	}
	
	/********************************************************************/

	public static double[][] toColumnMatrix(double[] input){
		double[][] output = new double[input.length][1];
		
		for(int i=0; i<input.length; i++)
			output[i][0] = input[i];
		return output;
	}
	
	public static float[][] toColumnMatrix(float[] input){
		float[][] output = new float[input.length][1];
		
		for(int i=0; i<input.length; i++)
			output[i][0] = input[i];
		return output;
	}
	
	public static double[][] toColumnMatrixAsDouble(float[] input){
		double[][] output = new double[input.length][1];
		
		for(int i=0; i<input.length; i++)
			output[i][0] = input[i];
		return output;
	}
	
	/********************************************************************/

	public static boolean find(double[] values, double value){
		for(int i=0; i<values.length; i++)
			if(values[i]==value)
				return true;
		return false;
	}
	
	public static boolean find(int[] values, int value){
		for(int i=0; i<values.length; i++)
			if(values[i]==value)
				return true;
		return false;
	}
	
	/********************************************************************/

	public static double[] merge(double[] array1, double[] array2){
		double[] merged = new double[array1.length+array2.length];
		
		//for (int i = 0; i < array1.length; i++) 
		//	merged[i] = array1[i];
		System.arraycopy(array1, 0, merged, 0, array1.length); 
		
		//for (int i = 0; i < array2.length; i++) 
		//	merged[i+array1.length] = array2[i];
		System.arraycopy(array2, 0, merged, array1.length, array2.length); 
		
		return merged;
	}
	
	public static double[] flatten(double[][] matrix){
		return flatten(matrix, false);
	}
	
	public static double[] flatten(double[][] matrix, boolean ignoreNaN){
		if(matrix.length==0)
			return new double[0];
		
		// search for maximal size of subvector for sizing output vector
		int max = 0;
		for (int i = 0; i < matrix.length; i++) {
			if(max<matrix[i].length)
				max = matrix[i].length;
		}
		
		// copy of element
		int ii = matrix.length;
		int k  = 0;
		double[] vector = new double[ii*max];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if(!(ignoreNaN && Double.isNaN(matrix[i][j])))
					vector[k++] = matrix[i][j];
			}
		}
		
		// reduce length of output to forbid elements containing 0
		return Array.clone(vector,k);
	}
	
	public static float[] flatten(float[][] matrix){
		return flatten(matrix, false);
	}
	
	public static float[] flatten(float[][] matrix, boolean ignoreNaN){
		if(matrix.length==0)
			return new float[0];
		
		// search for maximal size of subvector for sizing output vector
		int max = 0;
		for (int i = 0; i < matrix.length; i++) {
			if(max<matrix[i].length)
				max = matrix[i].length;
		}
		
		// copy of element
		int ii = matrix.length;
		int k  = 0;
		float[] vector = new float[ii*max];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if(!(ignoreNaN && Double.isNaN(matrix[i][j])))
					vector[k++] = matrix[i][j];
			}
		}
		
		// reduce length of output to forbid elements containing 0
		return Array.clone(vector,k);
	}
	
	public static double[] filterNaNs(double[] input){
		int nnan = 0;
		int k    = 0;
		
		for(int i=0; i<input.length; i++)
			if(Double.isNaN(input[i]))
				nnan++;
		
		double[] output = new double[input.length-nnan];
		for(int i=0; i<input.length; i++)
			if(!Double.isNaN(input[i]))
				output[k++] = input[i];
		
		return output;
	}
	
	
	public static int countNaNs(double[] value){
		int count = 0;
		
		for(int i=0; i<value.length; i++)
    		if(Double.isNaN(value[i]))
    			count++;
    	return count;
    }
	
	public static boolean atLeastOneNonNaN(double[] value){
		for(int i=0; i<value.length; i++)
    		if(!Double.isNaN(value[i]))
    			return true;
		return false;
    }
	
	/*********************************************************************/
	
	/** Sort input array, and return the final order of initial values.
	 * Note: input array is modified and sorted after call to this method.
	 */
	public static int[] sortAscending(int input[]){
		int[] order = new int[input.length];
		
		for(int i=0; i<order.length; i++)
			order[i] = i;
		
		for (int i = input.length; --i>=0; ){
			for (int j = 0; j<i; j++) {
				if (input[j] > input[j+1]) {
					// switch values
					int mem  = input[j];
					input[j]   = input[j+1];
					input[j+1] = mem;
					
					//switch ids
					int id     = order[j];
					order[j]   = order[j+1];
					order[j+1] = id;
				}
			}
		}		
		return order;
	}
	
	/** Sort input array, and return the final order of initial values.
	 * Note: input array is modified and sorted after call to this method.
	 */
	public static int[] sortAscending(float input[]){
		int[] order = new int[input.length];
		
		for(int i=0; i<order.length; i++)
			order[i] = i;
		
		for (int i = input.length; --i>=0; ){
			for (int j = 0; j<i; j++) {
				if (input[j] > input[j+1]) {
					// switch values
					float mem  = input[j];
					input[j]   = input[j+1];
					input[j+1] = mem;
					
					//switch ids
					int id     = order[j];
					order[j]   = order[j+1];
					order[j+1] = id;
				}
			}
		}		
		return order;
	}
	
	/** Sort input array, and return the final order of initial values.
	 * Note: input array is modified and sorted after call to this method. 
	 */
	public static int[] sortDescending(int input[]){
		int[] order = new int[input.length];
		
		for(int i=0; i<order.length; i++)
			order[i] = i;
		
		for (int i = input.length; --i>=0; ){
			for (int j = 0; j<i; j++) {
				if (input[j] < input[j+1]) {
					// switch values
					int mem  = input[j];
					input[j]   = input[j+1];
					input[j+1] = mem;
					
					//switch ids
					int id     = order[j];
					order[j]   = order[j+1];
					order[j+1] = id;
				}
			}
		}		
		return order;
	}
	
	/** Sort input array, and return the final order of initial values.
	 * Note: input array is modified and sorted after call to this method. 
	 */
	public static int[] sortDescending(float input[]){
		int[] order = new int[input.length];
		
		for(int i=0; i<order.length; i++)
			order[i] = i;
		
		for (int i = input.length; --i>=0; ){
			for (int j = 0; j<i; j++) {
				if (input[j] < input[j+1]) {
					// switch values
					float mem  = input[j];
					input[j]   = input[j+1];
					input[j+1] = mem;
					
					//switch ids
					int id     = order[j];
					order[j]   = order[j+1];
					order[j+1] = id;
				}
			}
		}		
		return order;
	}
	
	/** Sort input array, and return the final order of initial values.
	 * Note: input array is modified and sorted after call to this method. 
	 */
	public static int[] sortAscending(double input[]){
		int[] order = new int[input.length];
		
		for(int i=0; i<order.length; i++)
			order[i] = i;
		
		for (int i = input.length; --i>=0; ){
			for (int j = 0; j<i; j++) {
				if (input[j] > input[j+1]) {
					// switch values
					double mem = input[j];
					input[j]   = input[j+1];
					input[j+1] = mem;
					
					//switch ids
					int id     = order[j];
					order[j]   = order[j+1];
					order[j+1] = id;
				}
			}
		}		
		return order;
	}
	
	/** Sort input array, and return the final order of initial values.
	 * Note: input array is modified and sorted after call to this method. 
	 */
	public static int[] sortDescending(double input[]){
		int[] order = new int[input.length];
		
		for(int i=0; i<order.length; i++)
			order[i] = i;
		
		for (int i = input.length; --i>=0; ){
			for (int j = 0; j<i; j++) {
				if (input[j] < input[j+1]) {
					// switch values
					double mem = input[j];
					input[j]   = input[j+1];
					input[j+1] = mem;
					
					//switch ids
					int id     = order[j];
					order[j]   = order[j+1];
					order[j+1] = id;
				}
			}
		}		
		return order;
	}
	
	public static int[] sortDescending(Date[] input){
		int[] order = new int[input.length];
		
		for(int i=0; i<order.length; i++)
			order[i] = i;
		
		for (int i = input.length; --i>=0; ){
			for (int j = 0; j<i; j++) {
				if (input[i].before(input[i+1])) {
					// switch values
					Date mem   = input[j];
					input[j]   = input[j+1];
					input[j+1] = mem;
					
					//switch ids
					int id     = order[j];
					order[j]   = order[j+1];
					order[j+1] = id;
				}
			}
		}		
		return order;
	}
	
	public static int[] sortAscending(Date[] input){
		int[] order = new int[input.length];
		
		for(int i=0; i<order.length; i++)
			order[i] = i;
		
		for (int i = input.length; --i>=0; ){
			for (int j = 0; j<i; j++) {
				if (input[i].after(input[i+1])) {
					// switch values
					Date mem   = input[j];
					input[j]   = input[j+1];
					input[j+1] = mem;
					
					//switch ids
					int id     = order[j];
					order[j]   = order[j+1];
					order[j+1] = id;
				}
			}
		}		
		return order;
	}
	
	/*********************************************************************/
	
	public static void print(Coord3d[] input){
		for (int i = 0; i < input.length; i++) {
			System.out.println(input[i]);
		}
	}
	
	
	public static void print(double input[]){
		for (int i = 0; i < input.length; i++) {
			System.out.print(input[i]+"|");
		}
		System.out.println("");
	}
	
	public static void print(float input[]){
		for (int i = 0; i < input.length; i++) {
			System.out.print(input[i]+"|");
		}
		System.out.println("");
	}
	
	public static void print(int input[]){
		for (int i = 0; i < input.length; i++) {
			System.out.print(input[i]+"|");
		}
		System.out.println("");
	}
	
	public static void print(char input[]){
		for (int i = 0; i < input.length; i++) {
			System.out.print(input[i]+"|");
		}
		System.out.println("");
	}
	
	public static void print(double input[][]) {
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].length; j++) {
				System.out.print(input[i][j] + "\t");
			}
			System.out.println();
		}
	}
	
	public static void print(float input[][]) {
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].length; j++) {
				System.out.print(input[i][j] + "\t");
			}
			System.out.println();
		}
	}
	
	public static void print(int input[][]) {
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].length; j++) {
				System.out.print(input[i][j] + "\t");
			}
			System.out.println();
		}
	}
}
