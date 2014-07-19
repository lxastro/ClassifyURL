package xlong.classifyURL.converter;

import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.tokenizers.CharacterDelimitedTokenizer;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Splits a string into an n-gram with min and max grams.
 * <p/>
 <!-- globalinfo-end -->
 * 
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -delimiters &lt;value&gt;
 *  The delimiters to use
 *  (default ' \r\n\t.,;:'"()?!').</pre>
 * 
 * <pre> -max &lt;int&gt;
 *  The max size of the Ngram (default = 6).</pre>
 * 
 * <pre> -min &lt;int&gt;
 *  The min size of the Ngram (default = 3).</pre>
 * 
 * <pre> -exword
 *  Don't use words as features.</pre>
 *  
 <!-- options-end -->
 *
 * @author  Sebastian Germesin (sebastian.germesin@dfki.de)
 * @author  FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1.4 $
 */
public class NCharGramTokenizer
  extends CharacterDelimitedTokenizer {

  /** for serialization */
  private static final long serialVersionUID = -2181896254171647219L;

  /** the maximum number of N */
  protected int m_NMax = 6;
  
  /** the minimum number of N */
  protected int m_NMin = 3;
  
  /** use words or not*/
  protected boolean m_Word = true;
  
  /** the current length of the N-grams */
  protected int m_N;
  
  /** the number of strings available */
  protected int m_MaxPosition;
  
  /** the current position for returning elements */
  protected int m_CurrentPosition;
  
  /** the current character for returining elements */
  protected int m_CurrentChar;
  
  /** all the available grams */
  protected String[] m_SplitString;
  
  /**
   * Returns a string describing the stemmer
   * 
   * @return 		a description suitable for displaying in the 
   * 			explorer/experimenter gui
   */
  public String globalInfo() {
    return "Splits a string into an n-gram with min and max grams.";
  }
  
  /**
   * Returns an enumeration of all the available options..
   *
   * @return 		an enumeration of all available options.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
public Enumeration listOptions() {
    Vector	result;
    Enumeration enm;
    
    result = new Vector();
    
    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    result.addElement(new Option(
	"\tThe max size of the Ngram (default = 6).",
	"max", 1, "-max <int>"));

    result.addElement(new Option(
	"\tThe min size of the Ngram (default = 3).",
	"min", 1, "-min <int>"));
    
    result.addElement(new Option(
    "\tDon't use words as features.",
    "exword", 0, "-exword"));
    
    return result.elements();
  }
  
  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return 		the list of current option settings as an array of 
   * 			strings
   */
  public String[] getOptions() {
    Vector<String>	result;
    String[]		options;
    int			i;
    
    result = new Vector<String>();
    
    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);
    
    result.add("-max");
    result.add("" + getNGramMaxSize());

    result.add("-min");
    result.add("" + getNGramMinSize());
    
    if (getExWord()){
    	result.add("-exword");
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Parses a given list of options. <p/>
   *
   <!-- options-start -->
   * Valid options are: <p/>
   * 
   * <pre> -delimiters &lt;value&gt;
   *  The delimiters to use
   *  (default ' \r\n\t.,;:'"()?!').</pre>
   * 
   * <pre> -max &lt;int&gt;
   *  The max size of the Ngram (default = 3).</pre>
   * 
   * <pre> -min &lt;int&gt;
   *  The min size of the Ngram (default = 1).</pre>
   * 
   <!-- options-end -->
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String	value;
    
    super.setOptions(options);

    value = Utils.getOption("max", options);
    if (value.length() != 0)
      setNGramMaxSize(Integer.parseInt(value));
    else
      setNGramMaxSize(3);

    value = Utils.getOption("min", options);
    if (value.length() != 0)
      setNGramMinSize(Integer.parseInt(value));
    else
      setNGramMinSize(1);
    
    setExWord(Utils.getFlag("exword",  options));
  }
  
  /**
   * Gets the max N of the NGram.
   * 
   * @return 		the size (N) of the NGram.
   */
  public int getNGramMaxSize() {
    return m_NMax;
  }

  /**
   * Sets the max size of the Ngram.
   * 
   * @param value 	the size of the NGram.
   */
  public void setNGramMaxSize(int value) {
      m_NMax = value;
  }

  public void setExWord(boolean value){
	  m_Word = !value;
  }
  
  public boolean getExWord(){
	  return m_Word;
  }
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String NGramMaxSizeTipText() {
    return "The max N of the NGram.";
  }

  /**
   * Sets the min size of the Ngram.
   * 
   * @param value 	the size of the NGram.
   */
  public void setNGramMinSize(int value) {
    if (value < 1)
      m_NMin = 1;
    else
      m_NMin = value;
  }

  /**
   * Gets the min N of the NGram.
   * 
   * @return 		the size (N) of the NGram.
   */
  public int getNGramMinSize() {
    return m_NMin;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String NGramMinSizeTipText() {
    return "The min N of the NGram.";
  }

  /**
   * returns true if there's more elements available
   * 
   * @return		true if there are more elements available
   */
  public boolean hasMoreElements() {
    return (m_MaxPosition > 0 && m_N <= m_NMax);
  }
  
  /**
   * Returns N-grams and also (N-1)-grams and .... and 1-grams.
   * 
   * @return		the next element
   */
  public String nextElement() {
	 String retValue = "";
	  
    if (m_N == 0){
    	retValue = m_SplitString[m_CurrentPosition];
    	m_CurrentPosition++;
    	if (m_CurrentPosition == m_MaxPosition){
    		m_CurrentPosition = 0;
    		m_CurrentChar = 0;
    		m_N = m_NMin;
    		filterOutShortStrings(m_N);
    	}
    }
    else{
	    
	    retValue = m_SplitString[m_CurrentPosition].substring(m_CurrentChar, m_CurrentChar+m_N);
	    
	    m_CurrentChar++;
	    
	    if (m_CurrentChar + m_N - 1 == m_SplitString[m_CurrentPosition].length()){
	    	m_CurrentChar = 0;
	    	m_CurrentPosition++;
	    }
	    
	    if (m_CurrentPosition == m_MaxPosition) {
	      m_CurrentPosition = 0;
	      m_N++;
	      filterOutShortStrings(m_N);
	    }
    }

    return retValue;
  }

  /** 
   * filters out empty strings in m_SplitString and
   * replaces m_SplitString with the cleaned version.
   * 
   * @see #m_SplitString
   */
  protected void filterOutShortStrings(int len) {
    String[] newSplit;
    LinkedList<String> clean = new LinkedList<String>();

    for (int i = 0; i < m_SplitString.length; i++) {
      if (m_Word){
    	  if(m_SplitString[i].length()>len) clean.add(m_SplitString[i]);
      }
      else{
    	  if(m_SplitString[i].length()>=len) clean.add(m_SplitString[i]);
      }
      
    }

    newSplit = new String[clean.size()];
    for (int i = 0; i < clean.size(); i++) 
      newSplit[i] = clean.get(i);

    m_SplitString = newSplit;
    m_MaxPosition     = m_SplitString.length;
  }
  
  /**
   * Sets the string to tokenize. Tokenization happens immediately.
   * 
   * @param s		the string to tokenize
   */
  public void tokenize(String s) {
    if (m_Word){
    	m_N = 0;
    }
    else{
    	m_N = m_NMin;
    }
    m_SplitString = s.split("[" + getDelimiters() + "]");
    
    filterOutShortStrings(m_N);

    m_CurrentPosition = 0;
    m_CurrentChar = 0;
    m_MaxPosition     = m_SplitString.length;
  }
  
  /**
   * Returns the revision string.
   * 
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 1.4 $");
  }

  /**
   * Runs the tokenizer with the given options and strings to tokenize.
   * The tokens are printed to stdout.
   * 
   * @param args	the commandline options and strings to tokenize
   */
  public static void main(String[] args) {
    runTokenizer(new NCharGramTokenizer(), args);
  }
}

