
package org.de.metux.treebuild.parser;

import java.util.Properties;
import org.de.metux.treebuild.nodes.PackageNode;
import org.de.metux.treebuild.base.TreebuildConfig;

class ManualObjectParser extends ObjectParser
{
    class ManualPageParser extends ObjectParser
    {
	void handle_Close()
	{
	    throw new RuntimeException("unimplemented");
	}

	ManualPageParser(TreebuildConfig cf, Properties attrs)
	{
	    super(cf, attrs,"MANUAL->PAGE");
	}
    }

    PackageNode pkgnode; 
    
    void handle_Sub(String qName, Properties attrs)
    {
	if (qName.equals("manpage"))
	    setSubParser(new ManualPageParser(config, attrs));
	else if (qName.equals("property"))
	    setSubParser(new CommonPropertyParser(config, attrs,pkgnode));
	else 
	    super.handle_Sub(qName, attrs);
    }
	
    void handle_Close()
    {
	throw new RuntimeException("unimplemented");
    }

    ManualObjectParser(TreebuildConfig cf, Properties attrs, PackageNode pkg)
    {
	super(cf, attrs,"MANUAL");
	pkgnode = pkg;
    }
}
