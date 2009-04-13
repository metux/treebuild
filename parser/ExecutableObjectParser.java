
package org.de.metux.treebuild.parser;

import java.util.Properties;
import java.util.Hashtable;

import org.de.metux.treebuild.base.TreebuildConfig;
import org.de.metux.treebuild.nodes.ExecutableTargetNode;
import org.de.metux.treebuild.nodes.ConditionalTargetNode;
import org.de.metux.treebuild.base.INode;

class ExecutableObjectParser extends ObjectParser
{
    ExecutableTargetNode target;
    INode parent;

    // FIXME: should we move it off to an commondConditionalParser ?
    class ConditionalParser extends ObjectParser
    {
	ConditionalTargetNode cond;
	
	void handle_Sub(String qn, Properties attrs)
	{
	    if (qn.equals("include-file"))
	        setSubParser(new IncludeObjectParser(config,attrs,cond));
	    else if (qn.equals("import"))
		setSubParser(new ImportObjectParser(config,attrs,cond));
	    else if (qn.equals("property"))
		setSubParser(new CommonPropertyParser(config,attrs,cond));
	    else
    		super.handle_Sub(qn,attrs);
	}
    
	void handle_Close()
	{
	    target.addSubTarget(cond);
	}

	ConditionalParser(TreebuildConfig cf, Properties attrs)
	{
	    super(cf,attrs,"EXECUTABLE");
	    cond = new ConditionalTargetNode(cf,attributes);
	}
    }
    
    void handle_Sub(String qName, Properties attrs)
    {
	if (qName.equals("property"))
	    setSubParser(new CommonPropertyParser(config,attrs,target));
	else if (qName.equals("alias"))
	    setSubParser(new CommonAliasParser(config,attrs,target));
	else if (qName.equals("import"))
	    setSubParser(new ImportObjectParser(config,attrs,target));
	else if (qName.equals("conditional"))
	    setSubParser(new ConditionalParser(config,attrs));
	else if (qName.equals("include-file"))
	    setSubParser(new IncludeObjectParser(config,attrs,target));
	else if (qName.equals("source"))
	    setSubParser(new SourceObjectParser(config,attrs,target));
	else 
	    super.handle_Sub(qName,attrs);
    }
	
    void handle_Close()
    {
	parent.addChild(target);
    }
	
    ExecutableObjectParser(TreebuildConfig cf, Properties attrs, INode p)
    {
	super(cf,attrs,"EXECUTABLE");
	parent   = p;
	target   = new ExecutableTargetNode(cf,attributes);
    }
    
    public String getNodetype()
    {
	return "EXECUTABLE";
    }
}
