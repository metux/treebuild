
import java.io.File;
import org.de.metux.util.FileOps;

class _test_main
{
    static public void runtest()
    {
	File f1 = new File("deflate.c");
	File dir = new File("/home/devel/projects/pi-packages/zlib/");
    
	System.err.println("file1 -> toString():"+f1);
	System.err.println("      -> getAbsolutePath(): "+f1.getAbsolutePath());
	
	System.err.println("dir -> abs: "+dir.getAbsolutePath());
	
	File f2 = new File(dir,"/bummi/deflate.c");
	System.err.println("f2 -> str() :"+f2);
	System.err.println("f2 -> abs() :"+f2.getAbsolutePath());
    }
    
    static public void main(String args[])
    {
//	runtest();
//	FileOps.chdir("/home/devel/projects/pi-packages/zlib/");
//	runtest();
    }
}
