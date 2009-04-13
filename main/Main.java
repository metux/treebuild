
package org.de.metux.treebuild.main;

import org.de.metux.unitool.base.*;

public class Main
{
    public static void main(String args[]) throws EUnitoolError, Exception
    {
	if (args.length<1)
	{
	    System.out.println("treebuild: no params given. defaulting to receipe treebuild.xml");
	    args = new String[] { "recipe", "treebuild.xml" };
	}
	
	if (!(new PIBuild(args).run()))
	    System.exit(-2);
    }
}
