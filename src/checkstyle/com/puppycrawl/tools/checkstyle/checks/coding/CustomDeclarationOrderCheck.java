package com.puppycrawl.tools.checkstyle.checks.coding;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.beanutils.ConversionException;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.Utils;

public class CustomDeclarationOrderCheck extends Check
{
    /** List of order declaration customizing by user */
    private final ArrayList<FormatMatcher> mCustomOrderDeclaration = new ArrayList<FormatMatcher>();
    
	@Override
	public int[] getDefaultTokens() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void visitToken(DetailAST aAST) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void leaveToken(DetailAST aAST)
	{
        if(aAST.getType() == TokenTypes.OBJBLOCK) {
           // mScopeStates.pop();
        }
	}
	
	/**
	 * Parsing input line with custom declaration of order into massive
	 * @param aInputOrderDeclaration The string line with the user custom declaration 
	 */
	public void setCustomDeclarationOrder(String aInputOrderDeclaration) {
		for (String currentState : aInputOrderDeclaration.split("\\s*###\\s*")) {
			mCustomOrderDeclaration.add(new FormatMatcher(currentState));
		}
	}
		
    /**
     * Set whether or not the match is case sensitive.
     * @param aCaseInsensitive true if the match is case insensitive.
     */
	public void setIgnoreCase(boolean aCaseInsensitive)
	{
		if (aCaseInsensitive) {
			for (FormatMatcher currentRule : mCustomOrderDeclaration) {
				currentRule.setCompileFlags(Pattern.CASE_INSENSITIVE);
			}
		}
	}
    
    /**
     * private class for members of class and their patterns
     */
	private static class FormatMatcher
    {
	 /** mClassMember position in parsed input massive*/
	 private static final int mClassMemberPosition = 0;
	 /** mRegExp position in parsed input massive*/
	 private static final int mRegExpPosition = 1; 
     /** The regexp to match against */
     private Pattern mRegExp;
     /** The Member of Class*/
     private String mClassMember;
     /** The string format of the RegExp */
     private String mFormat;
     
     /**
      * Creates a new <code>FormatMatcher</code> instance. 
      * Parse into Definition and RegEx.
      * Defaults the compile flag to 0 (the default).
      * @param aInputRule input string with ClassDefinition and RegEx
      * @throws ConversionException unable to parse aDefaultFormat
      */
     public FormatMatcher(String aInputRule)
         throws ConversionException
     {
         this(aInputRule, 0);
     }

     /**
      * Creates a new <code>FormatMatcher</code> instance.
      * @param aInputRule input string with ClassDefinition and RegEx
      * @param aCompileFlags the Pattern flags to compile the regexp with.
      * See {@link Pattern#compile(java.lang.String, int)}
      * @throws ConversionException unable to parse aDefaultFormat
      */
     public FormatMatcher(final String aInputRule, final int aCompileFlags)
         throws ConversionException, ArrayIndexOutOfBoundsException
     {
    	 String inputRule[] = aInputRule.split("[()]");
    	 String aDefaultFormat = null;
    	 mClassMember = inputRule[mClassMemberPosition];
    	 if (inputRule.length < 2)
    		// if RegExp is null
    		 aDefaultFormat = "$^"; // the empty RegExp
    	 else aDefaultFormat = inputRule[mRegExpPosition];
         updateRegexp(aDefaultFormat, aCompileFlags);
     }

     /** @return the regexp to match against */
     public final Pattern getRegexp()
     {
         return mRegExp;
     }
     
     /**
      * Set the compile flags for the regular expression.
      * @param aCompileFlags the compile flags to use.
      */
     public final void setCompileFlags(int aCompileFlags)
     {
         updateRegexp(mFormat, aCompileFlags);
     }

     /**
      * Updates the regular expression using the supplied format and compiler
      * flags. Will also update the member variables.
      * @param aFormat the format of the regular expression.
      * @param aCompileFlags the compiler flags to use.
      */
     private void updateRegexp(String aFormat, int aCompileFlags)
     {
         try {
             mRegExp = Utils.getPattern(aFormat, aCompileFlags);
             mFormat = aFormat;
         }
         catch (final PatternSyntaxException e) {
             throw new ConversionException("unable to parse " + aFormat, e);
         }
     }
 }

}