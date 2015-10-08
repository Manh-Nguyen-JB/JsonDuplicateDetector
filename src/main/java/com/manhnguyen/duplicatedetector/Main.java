package com.manhnguyen.checkkey;

import org.json.*;
import java.util.*;
import java.io.File;
import java.nio.file.*;
import java.nio.charset.*;
import java.io.IOException;
import java.security.CodeSource;
import java.net.URISyntaxException;

class JSONObjectDuplicates extends JSONObject {

    public JSONObjectDuplicates(String source) throws JSONException {
        this(new JSONTokenerDuplicates(source));
    }

    public JSONObjectDuplicates(JSONTokenerDuplicates x) throws JSONException {
    	super(x);
    }

    public JSONObject put(String key, Object value) throws JSONException 
    {
    	if (key != null && value != null) {
		    if ((this.opt(key)) != null ) {
		    	System.out.println("Duplicate key: " + key);
		    }
		}
    	return super.put(key, value);
    }
}

class JSONTokenerDuplicates extends JSONTokener {
	public JSONTokenerDuplicates(String s) {
        super(s);
    }
	public Object nextValue() throws JSONException {
        char c = super.nextClean();
        String string;

        switch (c) {
            case '"':
            case '\'':
                return super.nextString(c);
            case '{':
                super.back();
                return new JSONObjectDuplicates(this);
            case '[':
                super.back();
                return new JSONArray(this);
        }

        StringBuilder sb = new StringBuilder();
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
            sb.append(c);
            c = super.next();
        }
        super.back();

        string = sb.toString().trim();
        if ("".equals(string)) {
            throw this.syntaxError("Missing value");
        }
        return string;
    }
}

class Main {

	public static void main(String[] args) 
	{
		try {
			CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
			File jarFile = new File(codeSource.getLocation().toURI().getPath());
			String jarDir = jarFile.getParentFile().getPath();
			String filePath = jarDir + "/" + args[0];
			String content = readFile(filePath);
			JSONObjectDuplicates obj = new JSONObjectDuplicates(content);
		} catch(JSONException e) {
			e.printStackTrace();
	    } catch(IOException e) {
			e.printStackTrace();
		} catch(URISyntaxException e) {
			e.printStackTrace();
		}
		
	}

	static String readFile(String path) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, Charset.defaultCharset());
	}
}