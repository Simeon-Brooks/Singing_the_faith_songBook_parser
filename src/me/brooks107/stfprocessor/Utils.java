package me.brooks107.stfprocessor;

import java.util.Arrays;

/**
 * Random methods (all static) that really should already be the the java packages 
 * but as they're not these are some basic implementations suitable for this project
 * 
 * @author Simeon Brooks (simeon26@gmail.com)
 *
 */
public class Utils {
	/**
	 * Chars that aren't allowed in a Windows or UNIX filename
	 */
	final private static int[] illegalChars = {34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47};
	
	/*
	 * The chars should be sorted to speed up the process. Really should just be written 
	 * in order but they aren't and this is quick...
	 */
	static {
	    Arrays.sort(illegalChars);
	}
	
	/**
	 * Cleans a string ready to be a windows or UNIX filename
	 * 
	 * @param badFileName Input filename to be cleaned
	 * @return The filename ready to be used
	 */
	public static String cleanFileName(String badFileName) {
	    StringBuilder cleanName = new StringBuilder();
	    
	    /*
	     * Goes through each char in the filename, and for each char that isn't illegal, 
	     * it gets added to the result. Otherwise it is discarded
	     */
	    for (int i = 0; i < badFileName.length(); i++) {
	        int c = (int)badFileName.charAt(i);
	        if (Arrays.binarySearch(illegalChars, c) < 0) {
	            cleanName.append((char)c);
	        }
	    }
	    
	    // Returns the clean result
	    return cleanName.toString();
	}

	/**
	 * Counts the number of occurrences of a string in another string. Really feel like this 
	 * should be in the {@link java.lang java.lang} package but c'est la vie
	 * 
	 * @param searchString String to search for
	 * @param mainString String to search in
	 * @return The number of occurrences of searchString in mainString as an {@link Integer Integer}.
	 */
	public static int countSubstringInString(String searchString, String mainString) {
		int lastIndex = 0;
		int count =0;
		
		// if lastIndex < 0 then failed to be found so no more occurrences and return count
		while (lastIndex != -1) {
				
			// Find next occurrences of the searchString (so starting at the location of the last one)
			lastIndex = mainString.indexOf(searchString,lastIndex);
	
			if (lastIndex != -1) {
				// Add to count now to avoid an off by one error
				count++;
		        lastIndex += searchString.length();
			}
		}
		
		return count;
	}

	/**
	 * Converts an {@link Integer int} into a {@link String string} with trailing zeros 
	 * to match a given length. Another util method that I really thing should be in the 
	 * standard Java classes (namely {@link Integer}) but hey ho.
	 * 
	 * @param value The number to be represented as a {@link String}
	 * @param digits The total number of digits, should always be as big as the number of digits in value
	 * @return A {@link String} representation of value that has {@link String#length()} =  digits
	 */
	public static String intToString(int value, int digits) {
		String res = String.valueOf(value);
		while (res.length() < digits)
			res = "0" + res;
		
		return res;
	}
}