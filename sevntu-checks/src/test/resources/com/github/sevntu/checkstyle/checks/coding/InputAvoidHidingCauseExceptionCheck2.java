package com.github.sevntu.checkstyle.checks.coding;

public class InputAvoidHidingCauseExceptionCheck2
{
   
    @Override
    public Object convertToObject(String newString, Object previousValue)
            throws DataTypeConversionException
    {
        if (BigDecimal.class.equals(type)) { // fix for #279
            DecimalFormat format = (DecimalFormat) NumberFormat
                    .getNumberInstance(getLocale());
            format.setParseBigDecimal(true);
            try {
                return format.parse(newString);
            }
            catch (ParseException e) {
                ErrorObject errorCode = new ErrorObject(
                        ErrorCodes.ERROR_CODE_BAD_FORMAT, newString);
                throw new DataTypeConversionException(
                        "Could not be converted to BigDecimal", e, errorCode); //// INCORRECT WARNING HERE!!
            }
        }
        return convertType(super.convertToObject(newString, previousValue));
    }

}
