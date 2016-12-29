package me.brooks107.stfprocessor;

/**
 * Represents a Chorus in a Hymn
 * 
 * @author Simeon Brooks (simeon26@gmail.com)
 *
 */
public class Chorus extends SongBlock {
	
	/**
	 * Constructs a Chorus with the words provided
	 * 
	 * @param text The words of the chorus (not including the tag)
	 */
	protected Chorus(String text) {
		super(text);
	}
	
	/**
	 * Gets the words of the Chorus with the tag "Chorus"
	 */
	@Override
	public String getBlock(int totalVerses) {
		return "Chorus\n" + mText + "\n";
	}
	
}
