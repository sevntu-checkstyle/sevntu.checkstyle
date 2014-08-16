package com.github.sevntu.checkstyle.checks.coding;

public class InputAvoidHidingCauseExceptionCheck2
{
   
    public Object convertToObject(String newString, Object previousValue)
            throws DataTypeConversionException
    {
        Object type = null;
        if (String.class.equals(type)) { // fix for #279
            String format = (String) String
                    .valueOf(true);
            format.substring(2);
            try {
            	throw new ParseException();
            }
            catch (ParseException e) {
                ErrorObject errorCode = new ErrorObject(
                        ErrorCodes.ERROR_CODE_BAD_FORMAT, newString);
                throw new DataTypeConversionException(
                        "Could not be converted to String", e); //// INCORRECT WARNING HERE!!
            }
        }
        return convertType(super.toString());
    }

    private Object convertType(String string)
    {
        return null;
    }

    private Object getLocale()
    {
        return null;
    }
    
    private class ErrorObject {

        public ErrorObject(String errorCodeBadFormat, String newString)
        {
        }
        
    }
    
    private static class ErrorCodes {

        public static final String ERROR_CODE_BAD_FORMAT = null;
        
    }
    
    private class DataTypeConversionException extends Throwable {

		public DataTypeConversionException(String string, ParseException e)
		{
			// TODO Auto-generated constructor stub
		}
    	
    }
    
    private class ParseException extends Throwable {
    	
    }
    
    

}
