
import org.de.metux.treebuild.parser.LoadTree;
import org.de.metux.treebuild.base.*;

class _test_parser
{
    static public String tabs(int x)
    {
	String t="";
	for (;x>0;x--)
	    t+="   ";
	return t;
    }
    
    static public String dumpNode(INode n, int depth)
    {
	INode childs [] = n.getChilds();

	if (childs.length==0)
	    return 
		tabs(depth)+"<"+n.getNodetype()+
		" id=\""+n.getID()+"\" />\n";
	
	String s = tabs(depth)+"<"+n.getNodetype()+
		" id=\""+n.getID()+"\">\n";
	
	for (int x=0; x<childs.length; x++)
	    s += dumpNode(childs[x],depth+1);

	s += tabs(depth)+"</"+n.getNodetype()+" id=\""+n.getID()+"\">\n";

	return s;
    }

    static public void main(String args[]) throws Exception
    {
    }
}
