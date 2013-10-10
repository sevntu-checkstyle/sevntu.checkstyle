package com.smth.cool;

public class SecurityExchangeTool
{

    private Map loadData(final SqlStatement coListSql)
    {
        return (Map) new JdbcTemplate(ds).execute(new ConnectionCallback()
        {
            @Override
            public Object doInConnection(Connection con)
                    throws SQLException,
                    DataAccessException
            {
                map.put(exch, new PartAndTotal(rs.getInt(2), (total != null ? total.intValue() : 0)));
                return map;
            }
        });
    }
    
    private int[] method8()
    {
        Boolean test;
        return test == null? null : new int[4];
    }
}
