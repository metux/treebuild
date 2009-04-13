
package org.de.metux.treebuild.parser;

import java.util.Properties;
import java.util.Hashtable;

import org.de.metux.treebuild.base.*;

class ConfigureObjectParser extends ObjectParser
{
    IPackageNode pkgnode;

    class HeaderParser extends ObjectParser
    {
	void handle_Close()
	{
	    String name = attributes.getProperty("name");
	    pkgnode.addConfHeader(attributes);
	}

	HeaderParser(TreebuildConfig cf,Properties attrs)
	{
	    super(cf, attrs,"CONFIGURE->HEADER");
	}
    }

    class SubstituteParser extends ObjectParser
    {
	void handle_Close()
	{
	    pkgnode.addConfSubstitute(attributes);
	}

	SubstituteParser(TreebuildConfig cf, Properties attrs)
	{
	    super(cf, attrs,"CONFIGURE-SUBSTITUTE");
	}
    }

    class FeatureParser extends ObjectParser
    {
	void handle_Close()
	{
	    pkgnode.addConfFeature(attributes);
	}

	FeatureParser(TreebuildConfig cf, Properties attrs)
	{
	    super(cf, attrs,"CONFIGURE->FEAUTURE");
	}
    }

    void handle_Sub(String qName, Properties attrs)
    {
	if (qName.equals("header"))
	    setSubParser(new HeaderParser(config,attrs));
	else if (qName.equals("feature"))
	    setSubParser(new FeatureParser(config,attrs));
	else if (qName.equals("substitute"))
	    setSubParser(new SubstituteParser(config,attrs));
	else
	    super.handle_Sub(qName,attrs);
    }
	
    void handle_Close()
    {
    }
	
    ConfigureObjectParser(TreebuildConfig cf, Properties attrs, IPackageNode n)
    {
	super(cf, attrs,"CONFIGURE");
	pkgnode = n;
    }
}
