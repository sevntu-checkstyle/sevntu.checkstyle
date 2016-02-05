package com.github.sevntu.checkstyle.checks;

import org.junit.Test;

public class DetailAstRootHolderTest {

    @Test
    public void testGetters() throws Exception {
        DetailAstRootHolder holder = new DetailAstRootHolder();
        holder.getDefaultTokens();
        holder.getAcceptableTokens();
        holder.getRequiredTokens();
    }
}
