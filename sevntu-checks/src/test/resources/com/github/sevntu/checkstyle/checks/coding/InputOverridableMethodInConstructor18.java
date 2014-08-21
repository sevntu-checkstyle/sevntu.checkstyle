package com.github.sevntu.checkstyle.checks.coding;
import java.io.IOException;
import java.io.Serializable;

import com.puppycrawl.tools.checkstyle.gui.Main;

public class InputOverridableMethodInConstructor18 {

	private class Object1 implements Serializable {

		 public void readObject(java.io.ObjectInputStream in) throws
		 IOException, ClassNotFoundException {

			 registerColumnType( 50, "tinyint" );

		 }

        private void registerColumnType(int i, String string) {
            
        }

	}


}