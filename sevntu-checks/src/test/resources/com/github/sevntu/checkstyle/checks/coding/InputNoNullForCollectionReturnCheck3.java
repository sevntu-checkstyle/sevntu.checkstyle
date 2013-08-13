public class InputNoNullForCollectionReturnCheck3
{
    public Object[] scrollTo(BigDecimal revision) throws SQLException {
        Object[] record = null;

        if (isReady()) {
            if (r.compareTo(revision) == 0) {
                record = new Object[resultSet.getMetaData().getColumnCount()];
                for (int index = 0; index < record.length; ++index) {
                    if (getColumnSqlTypes()[index] == Types.TIMESTAMP) {
                        record[index] = resultSet.getTimestamp(index + 1);
                        return null; //!!
                    } else {
                        record[index] = resultSet.getObject(index + 1);
                        return null; //!!
                    }
                }
            }
            record = new Object[6];
        } else {
            record = new Object[6];
        }

        return record; 
    }
    
    public String[] method2()
    {
        String[] result = null;
        if (!checkNotExists) {
            result = new String[] { updateSql };
        } else {
            result = new String[] { updateSql, removeItemsRelatedToDiscontinuedCompany(table, column) };
        }
        return result;
    }
}
