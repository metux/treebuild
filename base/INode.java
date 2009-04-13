
package org.de.metux.treebuild.base;

import java.util.Enumeration;
import java.util.Date;
import org.de.metux.propertylist.IPropertylist;

public interface INode 
{
    // get the unique ID of this node (ie. its name)
    public String 	getID();

    // get nodetype identifier
    public String       getNodetype();
    
    // enumerate childs
    public INode []	getChilds();
    
    // add an child
    public void 	addChild(INode child);
    
    // do we have to update this node or some of its childs ?
    public boolean	Expired() throws EDependencyMissing;
    
    // access the parent reference
    public void setParent(INode p);
    public INode getParent();
    
    // run stages 
    public void		run_Configure() throws EDependencyMissing;
    public void 	run_Autodep();
    public void 	run_Build() throws EDependencyMissing;
    public void		run_Install();

    public void 	setProperty(String name, String value);
    public String	getProperty(String name);
    public String 	getPropertyRec(String name);

    IPropertylist	getPropertylist();
}
