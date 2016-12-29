package me.brooks107.stfprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/*
 * Format has following flags:
 * 		v - Verse
 * 		c - Chorus
 * 		b - Bridge
 * 		x - Repeat up to then x number times
 * 
 * or options:
 * 		s - skip song
 * 		o - show output exactly how StF had it
 * 			nothing => use outputted format
 * 		a - copy actual content straight through
 * 		q - quit
 * 
 * None of this is case sensitive
 */

/**
 * 
 * Defines an entire song or hymn with all verses and choruses etc.
 * 
 * This class is also executable, and should be called with either no arguments, or the first 
 * hymn number to parse. If no arguments, will start at 0, and always goes up to 840 (inc.).
 * 
 * @author Simeon Brooks (simeon26@gmail.com)
 */
public class Hymn {
	
	// Holds all the verses, choruses and bridges of the hymn
	private ArrayList<SongBlock> mBlocks;
	
	// Number of *verses* for the "(/ <NoOfVerses>)" part of the verse tags
	private int mTotalVerses;
	
	// Used to indicate that the hymn should be skipped
	public boolean ready = true;
	
	/**
	 * Initialises internal variables
	 */
	private Hymn() {
		super();
		mBlocks = new ArrayList<SongBlock>();
	}
	
	/**
	 * Main constructor called by {@link #main(String[]) main(args)}, and parses 
	 * the supplied {@link File File} into a {@link Hymn Hymn}
	 * 
	 * @param file to be parsed
	 * @throws IOException Thrown if the file cannot be read
	 * @throws FileNotFoundException Thrown if the file was not found
	 */
	public Hymn(File file) throws IOException, FileNotFoundException {
		// Do basic init for class
		this();
		
		// Create a reader for the file
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		// Verse/Chorus/Bridge blocks to store the parsed line in
		SongBlock currentSongBlock = null;
		SongBlock lastSongBlock = null;
		ArrayList<SongBlock> tempBlocks = new ArrayList<SongBlock>();
		
		// Current line (discards first line - always blank in StF) and input text builder
		String currentLine = br.readLine();
		StringBuilder wholeThingFromStf = new StringBuilder();
		
		// Read all lines in file
		while ((currentLine = br.readLine()) != null) {
			
			// Add line to total input builder
			wholeThingFromStf.append(currentLine + "\n");
			
			if (currentSongBlock != null) {
				// Continuing a song block
				
				if (!currentLine.equals("")) {
					// Actual line of text
					
					/*
					 * Verse -> Chorus has no line break, but extra indent. So if 
					 * a verse then the indent has to be checked
					 */
					if (currentSongBlock instanceof Verse) {
						
						
						if (currentLine.length()>5) {
							if (currentLine.substring(0,6).equals("      ")) {
								/*
								 * The line's indent is that of a chorus:
								 * 		Finish the verse block and stash that
								 * 		Start a new chorus block and add the current line to it
								 */
								
								tempBlocks.add(currentSongBlock);
								lastSongBlock = currentSongBlock;
								currentSongBlock = new Chorus("");
								currentSongBlock.appendLine(currentLine.trim());
								continue;
							}
						}
					}
					
					// Just another line in whatever block there is at the moment so add that on
					currentSongBlock.appendLine(currentLine.trim());
					
				} else {
					/*
					 * Line break = end of block. So stashes the current one, and set current to 
					 * null for the next iteration
					 */
					
					tempBlocks.add(currentSongBlock);
					lastSongBlock = currentSongBlock;
					currentSongBlock = null;
				}
			} else {
				// There is no current song block. Ie. either start of hymn or after line break
				
				if (currentLine.length()>5) {
					/*
					 * The must be over 5 chars, but if it wasn't the next bit would be a shipwreck 
					 * so checks to be on the safe side... I mean why not?
					 */
					
					if (currentLine.substring(0,6).equals("      ")) {
						// Large indent = Chorus or Bridge
						
						if (lastSongBlock instanceof Chorus) {
							// Never two choruses in a row, so if the last is a chorus, this is a bridge
							
							currentSongBlock = new Bridge("");
							currentSongBlock.appendLine(currentLine.trim());
						}
						else {
							// Otherwise assume is a chorus - hence pre-bridges are not supported
							
							currentSongBlock = new Chorus("");
							currentSongBlock.appendLine(currentLine.trim());
						}
					}
					else {
						// Smaller indent = Verse
						
						try {
							// Get the verse number from the start of the line
							
							currentSongBlock = new Verse(Integer.parseInt(currentLine.substring(0, 1)));
						}
						catch (NumberFormatException e) {
							// If invalid verse number, just use 0. The numbers are only asthetic anyway
							
							currentSongBlock = new Verse(0);
						}
						
						// Add the line (trimmed and without the verse number) to the verse
						currentSongBlock.appendLine(currentLine.substring(3).trim());
					}
				}
				else {
					// The obvious check for length failed = break and get out of here.
					
					break;
				}
			}
		}
		
		// All of file read, close reader
		br.close();
		
		// Prints to the user the first line (ie. title of the hymn) to identify it
		System.out.println("FIRST LINE:" + tempBlocks.get(0).getBlock(0).split("\n")[1]);
		
		// Prints out the format of the blocks for the user to check against the book
		System.out.println("STF FORMAT:" + getFormat(tempBlocks));
		
		// Reads in the format from the user - but if blank then uses the assumed format
		BufferedReader cmdBr = new BufferedReader(new InputStreamReader(System.in));
		String format = null;
		
		while(true) {
			if (Utils.countSubstringInString("V", getFormat(tempBlocks).toUpperCase()) == getFormat(tempBlocks).length()) {
				format = getFormat(tempBlocks);
				break;
			}
			
			System.out.print("Enter the format: ");
			
			try {
				// No loop - only a single line
				format = cmdBr.readLine();
			} catch (IOException ioe) {
				System.out.println("IO error trying to read your name!");
			}
			
			format = format.toUpperCase();
			
			// Format stored in format - now checks for options
			
			if (format.length() == 0) {
				// Blank format - format is the deduced format from the initial parse
				
				format = getFormat(tempBlocks);
			}
			else if (format.charAt(0) == 'S') {
				// S = skip hymn. Sets ready flag as false which will skip later
				
				ready = false;
				return;
			}
			else if (format.charAt(0) == 'O') {
				// O = Print entire input from stf - does that and then skips
				
				System.out.println(wholeThingFromStf.toString());
				continue;
			}
			else if (format.charAt(0) == 'Q') {
				// Q = quit - just exits
				
				System.exit(0);
			}
			else if (format.charAt(0) == 'A') {
				// Ignores the parsing and just puts STF input straight through to the output
				
				mBlocks = tempBlocks;
				mTotalVerses = tempBlocks.size();
				return;
			}
			
			break;
		}
		
		// Format var now has the format in it and mBlocks has all the text data
		
		// Count number of verses in the format
		mTotalVerses = Utils.countSubstringInString("V", format);
		
		// Process hymn, block by block = char by char in the format string
		for (int i = 0 ; i < format.length() ; i++) {
			// Gets next char in the format
			char c = format.charAt(i);
			
			for (SongBlock sb:tempBlocks) {
				/*
				 * Once the desired type of block is found, the next of that type in the 
				 * tempBlocks is found and then transfered to the output (mBlocks)
				 */
				
				// Indicates a match, and to move on through the format
				boolean found = false;
				switch(c) {
					case 'C':
						//CHORUS - add to mBlocks and set found. Do not remove for repeats
						if (sb instanceof Chorus) {
							mBlocks.add(sb);
							found = true;
						}
						break;
						
					case 'B':
						// BRIDGE - add to mBlocks and set found. Do not remove for repeats
						if (sb instanceof Bridge) {
							mBlocks.add(sb);
							found = true;
						}
						
						break;
					
					case 'V':
						// VERSE - add to mBlocks and set found. Also remove verse since verses are never repeated
						if (sb instanceof Verse) {
							mBlocks.add(sb);
							tempBlocks.remove(sb);
							found = true;
						}
						
						break;
					
					default:
						// Not CBV, should be a digit to indicate repeats of all blocks so far
						try {
							int j = Integer.parseInt(String.valueOf(c));
							int numberOfBlocks = mBlocks.size();
							
							for ( ; j > 1 ; j--)
								for (int k  = 0 ; k < numberOfBlocks ; k++)
									mBlocks.add(mBlocks.get(k));
							
							found = true;
						}
						catch (NumberFormatException e) {
							// If not a number, but another unknown char, leave.
							break;
						}
				}
				
				// Leave loop if block found
				if (found == true) break;
			}
		}
	}
	
	/**
	 * Gets the full text of the hymn, in the order of the STF input (although without indents and 
	 * verse numbers.
	 * 
	 * @return The full text of the hymn as a ({@link String String})
	 */
	public String getEasyWorshipText() {
		StringBuilder sb = new StringBuilder();
		
		// Goes through each block and appends all the lines with line breaks
		
		for (SongBlock block:mBlocks)
			sb.append(block.getBlock(mTotalVerses) + "\n");
		
		return sb.toString();
	}
	
	/**
	 * Gets all the lines in STF input order. This method cheats and simply takes the output of 
	 * {@link #getEasyWorshipText()} and splits the line breaks. Slower but easier
	 * @return The lines as a {@link String}[]
	 */
	public String[] getEasyWorshipLines() {
		return getEasyWorshipText().split("\n");
	}
	
	/**
	 * Gets the first line of the hymn (for the title of the hymn)
	 * @return The first line as a {@link String}
	 */
	public String getFirstLine() {
		// No first line = "" not an exception
		if (mBlocks.isEmpty()) return "";
		
		return mBlocks.get(0).mText.split("\n")[0];
	}
	
	/**
	 * Gets the format string from a list of blocks. 
	 * Does not support of the compression down into numbers for repeated sections.
	 * 
	 * @param blocks The {@link SongBlock blocks} to translate into a format {@link String string}
	 * @return The String
	 */
	public static String getFormat(List<SongBlock> blocks) {
		
		// StrBuilder to put the result into
		StringBuilder sb = new StringBuilder();
		
		// For each block, test the class type and add on the corresponding letter.
		for (SongBlock song:blocks) {
			if (song instanceof Verse) sb.append("V");
			else if (song instanceof Chorus) sb.append("C");
			else if (song instanceof Bridge) sb.append("B");
		}
		
		return sb.toString();
	}
	
	/**
	 * Executable method. This runs the parser on files with paths matching "./STF/H[Hymn Number].txt", 
	 * Where [Hymn Number] ranges from the number given from the first argument (or 0 if no args given) 
	 * up to 840. The outputs are deposited in "./STF output/[Hymn Number with 3 digits] [Hymn first line].txt
	 * 
	 * @param args First value can be starting hymn number to parse. Can also be empty, but not null
	 */
	public static void main(String[] args) {
		// Count variable - defined outside of for-loop so can be set from the args
		int i;
		
		// If a number is is in the args, uses that. Should really have a try catch for NumberFormatException
		if (args.length == 0)
			i = 0;
		else
			i = Integer.parseInt(args[0]);
		
		// Loops through all of the hymns
		for ( ; i < 841 ; i++) {
			
			// Finds the file using the path specified in the JavaDoc
			File input = new File("STF/H" + i + ".txt");
			
			/*
			 * A *little* lazy maybe, but since the only real fix for thrown exceptions is to alert 
			 * the user and move on to the next hymn, the entire process (per Hymn) is in a huge 
			 * try-catch block.
			 */
			try {
				// Alerts user to the hymn being processed
				System.out.println("\nHYMN:" + i);
				
				// Parses the hymn and checks that it was successful (not skipped). Otherwise goes to next hymn
				Hymn h = new Hymn(input);
				if (!h.ready)
					continue;
				
				// Generates a filename from the first line of the hymn (cleaned up)
				String firstLine = h.getFirstLine();
				if (firstLine.length() == 0) firstLine = ",";
				char lastChar = firstLine.charAt(firstLine.length() - 1);
				if (lastChar == ',' || lastChar == '!' || lastChar == '.' || lastChar == ';' || lastChar == ':') {
					firstLine = firstLine.substring(0, firstLine.length() - 1);
				}
				firstLine = Utils.cleanFileName(firstLine);
				
				// Creates a writer for the output file
				PrintWriter writer = new PrintWriter("STF output/" + Utils.intToString(i, 3) + " " + firstLine + ".txt", "UTF-8");
				
				// Fetches the text to output to the output file
				String[] lines = h.getEasyWorshipLines();
				
				// Write all the lines nad close the file
				for (int line = 0 ; line < lines.length ; line++)
					writer.println(lines[line]);
				
				writer.close();
			}
			catch (FileNotFoundException e) {
				// If the file isn't found, alert the user of this and move on
				
				System.out.println("Hymn " + i + " not found");
				continue;
			} catch (IOException e) {
				// IOException is very general, so just give the stack trace and move on
				
				System.out.println("Error");
				e.printStackTrace();
			}
		}
	}
}
