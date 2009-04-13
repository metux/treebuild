
package org.de.metux.treebuild.conf;

import java.util.Properties;
import org.de.metux.util.StrUtil;
import org.de.metux.util.StrSplit;

public class Feature
{
    Properties properties;

    public String name;
    public String description;
    public String default_mode;
    public String symbol_enabled;
    public String symbol_disabled;
    public String require_sys_config[];
    
    public boolean dependencies_okay = false;
    public String mode; 
    public boolean enabled;
    
    public Feature(Properties p)
    {
	properties = p;
	name         = p.getProperty("name");
	description  = p.getProperty("description");
	default_mode = p.getProperty("default");
	mode         = default_mode;
	symbol_enabled = p.getProperty("symbol_enabled");
	symbol_disabled = p.getProperty("symbol_disabled");
	require_sys_config = StrSplit.split(StrUtil.fix_notnull(p.getProperty("require-sys-config")));
	
	if (StrUtil.isEmpty(name))
	    throw new RuntimeException("Feature: missing default mode");
	if (StrUtil.isEmpty(name))
	    throw new RuntimeException("Feature: missing name");
	if (StrUtil.isEmpty(description))
	    throw new RuntimeException("Feature: missing description");
    }
}
