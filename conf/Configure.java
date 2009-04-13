
package org.de.metux.treebuild.conf;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Enumeration;

public class Configure
{
    Hashtable features    = new Hashtable();
    Hashtable headers     = new Hashtable();
    Hashtable substitutes = new Hashtable();
    
    Feature    features_cache[];
    HeaderFile headers_cache[];

    public Feature[] getFeatures()
    {
	if (features_cache==null)
	{
	    features_cache = new Feature[features.size()];
	    int x=0;
	    for (Enumeration e=features.keys(); e.hasMoreElements(); )
		features_cache[x++] = (Feature)features.get((String)e.nextElement());
	}
	return features_cache;
    }

    public HeaderFile[] getHeaderFiles()
    {
	if (headers_cache==null)
	{
	    headers_cache = new HeaderFile[features.size()];
	    int x=0;
	    for (Enumeration e=headers.keys(); e.hasMoreElements(); )
		headers_cache[x++] = (HeaderFile)headers.get((String)e.nextElement());
	}
	return headers_cache;
    }

    public Configure()
    {
    }
    
    public void addFeature(Properties prop)
    {
	Feature f = new Feature(prop);
	features.put(f.name, f);
    }
    
    public void addHeader(Properties prop)
    {
	HeaderFile h = new HeaderFile(prop);
	headers.put(h.name, h);
    }
    
    public void addSubstitute(Properties prop)
    {
	Substitute s = new Substitute(prop);
	substitutes.put(s.name, s);
    }
}
