
package org.de.metux.treebuild.conf;

import java.util.Properties;
import java.io.File;

public class HeaderFile
{
    Properties properties;
    public String name;
    public File output_file;
    public File input_file;
    
    public HeaderFile(Properties p)
    {
	properties = p;
	name = p.getProperty("name");
	input_file = new File(name+".in");
	output_file = new File(name);
    }
}
