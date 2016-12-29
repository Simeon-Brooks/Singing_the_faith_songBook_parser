package me.brooks107.stfprocessor;

/**
 * Super-class block of a song. This is either a Verse, Chorus or Bridge (in this project).
 * 
 * @author Simeon Brooks (simeon26@gmail.com)
 *
 */
public abstract class SongBlock {
	// Text of the block, i.e. the words
	protected String mText;
	
	/**
	 * Creates a block with the words provided
	 * 
	 * @param text The words of the block
	 */
	protected SongBlock(String text) {
		mText = text;
	}
	
	/**
	 * Gets the words of the block as they were supplied, without a tag
	 * 
	 * @return The words of the block
	 */
	public String getText() {
		return mText;
	}
	
	/**
	 * Adds a line of words to the block
	 * 
	 * @param line A {@link String} of the line's words
	 */
	public void appendLine(String line) {
		// Avoids an empty new line at the start, so if empty then doesn't prefix with \n
		if (mText.length() != 0)
			mText += "\n" + line;
		else
			mText += line;
	}
	
	/**
	 * Gets the words of the block along with the appropriate tag (which is supplied by the sub-classes)
	 * 
	 * @param totalVerses The total number of verses in the Hymn. Only relevant for Verses
	 * @return The words as a single {@link String}
	 */
	public abstract String getBlock(int totalVerses);
}
