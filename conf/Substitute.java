
package org.de.metux.treebuild.conf;

import java.util.Properties;

public class Substitute
{
    Properties properties;
    String name;
    
    public Substitute(Properties p)
    {
	properties = p;
	name = p.getProperty("name");
    }
}
