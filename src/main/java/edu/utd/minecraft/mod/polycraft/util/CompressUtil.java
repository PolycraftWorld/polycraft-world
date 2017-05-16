package edu.utd.minecraft.mod.polycraft.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressUtil {
	
	public static byte[] compress(final String uncompressed) throws IOException{
		if (uncompressed == null || uncompressed.length() == 0) {
		    return null;
		}
		byte[] compressed = null;
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final GZIPOutputStream gzip = new GZIPOutputStream(out);
	    try
	    {
			gzip.write(uncompressed.getBytes());
	    	gzip.close();
			compressed = out.toByteArray();
	    }
	    finally
	    {
	    	out.close();
	    }
		return compressed;
	}
	
	public static String decompress(final byte[] compressed) throws IOException{
	    if (compressed == null || compressed.length == 0) {
	        return null;
	    }
	    
	    final ByteArrayInputStream in = new ByteArrayInputStream(compressed);
	    final GZIPInputStream gzip = new GZIPInputStream(in);
	    final InputStreamReader reader = new InputStreamReader(gzip);
	    final BufferedReader bufferedReader = new BufferedReader(reader);
	    final StringWriter writer = new StringWriter();

	    try
	    {
	    	String read;
	    	while ((read = bufferedReader.readLine()) != null)
	    		writer.write(read);
	    	return writer.toString();
	    }
	    finally
	    {
	    	writer.close();
	    	bufferedReader.close();
	    	reader.close();
	    	in.close();
	    }
	 }
}
