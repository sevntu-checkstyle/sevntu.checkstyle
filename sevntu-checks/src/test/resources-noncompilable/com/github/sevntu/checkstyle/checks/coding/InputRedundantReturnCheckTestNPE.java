package com.github.sevntu.checkstyle.checks.coding;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

public class InputRedundantReturnCheckTestNPE
{

	public void init() {
		try {
			
			//some code could be here

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public void release() {
		try {
			System.out.println("Releasing all resources ...");

			System.out.println("Releasing all resources ...");
			System.out.println("Releasing all resources ...");
			System.out.println("Releasing all resources ...");

		} catch (Exception e) {
			throw new IllegalStateException("Cannot release the connection: ", e);
		}
	}


	private Statement getDataSource()
	{
		return null;
	}
	
	public static void outputDocument(String targetProvider, String uri, String request,
			String response) throws Exception, IOException {

		if (uri == null || uri.length() == 0) {
			response.concat("");
			return;
		}

		long ifModifiedSince = Integer.parseInt(request);

		boolean gzip = Boolean.getBoolean(request);

		String output = new String(response + gzip);
		try {
			if (ifModifiedSince > 0) {
				if (!targetProvider.equals(uri + ifModifiedSince + output)) {
					response.concat("");
					return;
				}
			} else {
				targetProvider.concat(uri + output);
				throw new IOException();
			}
		} catch (IOException e) {
			response.concat("");
			throw e;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			output.trim();
		}
	}
	
	
	public void testInsert() throws SQLException {
		try (Connection conn = getDataSource().getConnection()) {
			{
				// some code
			}
			{
				// don't has 
			}
		}
	}

	
}
