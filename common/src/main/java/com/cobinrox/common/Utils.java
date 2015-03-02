package com.cobinrox.common;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;


public class Utils {
	static final Logger logger = Logger.getLogger(Utils.class);

	/**
	 * Return properties, given prefix of a properties file residing in
	 * the current class path.
	 * @param caller
	 * @param prefixFileName
	 * @return
	 */
	public static Properties readProps(Object caller, String prefixFileName)
	{
		Properties p = new Properties();
		String fname = prefixFileName + ".properties";
		InputStream is = null;
		try
		{
			is = caller.getClass().getClassLoader().getResourceAsStream(fname);//new FileInputStream(fname);
			if( is != null)
			{
				URL possiblefile = null;
				try
				{
					possiblefile = caller.getClass().getClassLoader().getResource(fname);
				} catch(Throwable t){} // ok, may be stream within a jar file
				logger.info("Reading from resource [" + fname + "]" +
								(possiblefile==null?"":" ["+possiblefile.getPath()+"]"));
				p.load(is);
			}
			
			return p;
		}
		catch(Throwable t)
		{
			logger.error("Reading/accessing properties file [" + fname + "]",t);
			logger.error("Classpath is [" + Utils.classpath() + "]");
			return p;
		}
		finally
		{
			if( is != null )
			{
				try{is.close();}catch(Throwable t){}
			}
		}
	}
	
	public static String classpath()
	{
		return System.getProperty("java.class.path");
	}
	public static String getStackTraceAsString(Throwable t)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}
}
