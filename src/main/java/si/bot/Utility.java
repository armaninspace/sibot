package si.bot;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import opennlp.tools.stemmer.PorterStemmer;

public class Utility {

	public static List<String> stemQuestion(String txt) {
		List<String> res = stem(stripNonChar(txt));
		if (endsWithQuesiton(txt))
			res.add("?");
		return res;
	}

	public static List<String> stem(String text) {
		String parts[] = text.split(" ");
		List<String> textl = new ArrayList<String>();
		for (String part : parts)
			textl.add(part);
		return stem(textl);
	}

	public static List<String> stem(List<String> input) {
		List<String> output = new ArrayList<String>();
		PorterStemmer stemmer = new PorterStemmer();
		for (String part : input) {
			output.add(stemmer.stem(part)); // get the stemmed word
		}
		return output;
	}

	public static List<String> mkList(String txt) {
		List<String> res = new ArrayList<String>();
		for (String tok : txt.split(",")) {
			res.add(tok.trim());
		}
		return res;
	}

	public static String stripNonChar(String txt) {
		return txt.replaceAll("[^A-Za-z ]", "");
	}

	public static String stripQuesiton(String txt) {
		return txt.replace("?", "");
	}

	public static boolean endsWithQuesiton(String txt) {
		return txt.endsWith("?");
	}

	public static String rmStopWords(String txt) {
		String[] stopWords = new String[] { "a", "able", "about", "above", "according", 
				"accordingly", "across", "actually", "after", "afterwards", "again", 
				"against", "all", "i", "do", "me", "in", "are", "to", "is",
				"my" };
		String stopWordsPattern = String.join("|", stopWords);
		Pattern pattern = Pattern.compile("\\b(?:" + stopWordsPattern + ")\\b\\s*", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(txt);
		return matcher.replaceAll("");
	}

	public static Map<String, Integer> mkDict() {
		return (new HashMap<String, Integer>());
	}

	public static void add2Dict(String key, Map<String, Integer> dict) {
		if (!dict.containsKey(key)) {
			dict.put(key, dict.size());
		}
	}

	public static void add2Dict(List<String> keys, Map<String, Integer> dict) {
		for (String key:keys) add2Dict(key, dict);
	}
	
	public static List<String> dictHeader(Map<String, Integer> dict) {
		return dictHeader(dict, "");
	}
	
	public static List<String> dictHeader(Map<String, Integer> dict,String prefix) {
		List<String> res = new ArrayList<String>();
		for (String keys:dict.keySet()) {
			res.add("");
		}
		for (String key:dict.keySet()) {
			res.set(dict.get(key), prefix+key+"."+dict.get(key));
		}
		return res;
	}

	public static List<String> dictVector(Map<String, Integer> dict,List<String> toks) {
		List<String> res = new ArrayList<String>();
		for (String keys:dict.keySet()) {
			res.add("0");
		}
		for (String key:toks) {
			if (dict.containsKey(key)) 
				res.set(dict.get(key), "1");
		}
		return res;
	}
	
	public static List<List<String>> colcat(List<List<String>> l, List<List<String>> r) {
		List<List<String>> res = new ArrayList<List<String>>();
		for (int i=0; i<l.size(); i++) {
			List<String> row=new ArrayList<String>();
			row.addAll(l.get(i));
			row.addAll(r.get(i));
			res.add(row);
		}
		return res;
	}
	
	public static void writeAugmentedData(String fname, List<List<String>> data) throws IOException, ParseException {
		CSVWriter csvw = new CSVWriter(new FileWriter(fname));
		for (List<String> row: data) {
			String rw[] = new String[row.size()];
			for (int j=0; j<rw.length; j++) {
				rw[j]=row.get(j);
			}
			csvw.writeNext(rw);
		}
		csvw.flush();
		csvw.close();
	}

	public static List<List<String>> readData(String fname) throws IOException, ParseException {
		CSVReader csvr = new CSVReader(new FileReader(fname));
		List<List<String>> res = new ArrayList<List<String>>();
		String row[] = null;
		while ((row = csvr.readNext()) != null) {
			List<String> rowl = new ArrayList<String>();
			for (String cell:row) {
				rowl.add(cell);
			}
			res.add(rowl);
		}
		csvr.close();
		return res;
	}

}
