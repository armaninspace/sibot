package si.bot;

import static si.bot.Utility.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class Main {

	final static int QUESTION = 0;
	final static int LINK = 2;
	final static int STAGE = 3;
	final static int INDUSTRY = 4;
	final static int KEYWORDS = 5;
	final static int TYPE = 6;
	final static int TOPIC = 7;

	public static void main(String args[]) throws IOException, ParseException {
		List<List<String>> data = readData("data/kb.csv");
		List<Recommendation> recommendations = mkRecommendations(data);
		dataPrepStep1(recommendations);
		Dictionaries dictionaries=mkDicts(recommendations);
		List<List<String>> newFeatures=mkNewFeatures(dictionaries,recommendations);
		List<List<String>> augmenteddata=colcat(data,newFeatures);
		writeAugmentedData("data/kb_augmented.csv",augmenteddata);
	}
	
	public static List<List<String>> mkNewFeatures(Dictionaries dictionaries, List<Recommendation> recommendations) {
		List<List<String>> res = new ArrayList<List<String>>();
		List<String> newheader = new ArrayList<String>();
		newheader.addAll(dictHeader(dictionaries.questions,"q."));
		newheader.addAll(dictHeader(dictionaries.keywords,"kw."));
		newheader.addAll(dictHeader(dictionaries.industry,"ind."));
		newheader.addAll(dictHeader(dictionaries.type,"typ."));
		newheader.addAll(dictHeader(dictionaries.topic,"top."));
		res.add(newheader);
		for (Recommendation recommendation: recommendations) {
			List<String> row = new ArrayList<String>();
			row.addAll(dictVector(dictionaries.questions,recommendation.pquestion));
			row.addAll(dictVector(dictionaries.keywords,recommendation.pkeywords));
			row.addAll(dictVector(dictionaries.industry,recommendation.pindustry));
			row.addAll(dictVector(dictionaries.type,recommendation.ptype));
			row.addAll(dictVector(dictionaries.topic,recommendation.ptopic));
			res.add(row);
		}		
		return res;
	}
	
	public static List<Recommendation> mkRecommendations(List<List<String>> data) throws IOException, ParseException {
		List<Recommendation> recommendations = new ArrayList<Recommendation>();
		for (int i=1; i<data.size(); i++) {
			Recommendation recommendation = new Recommendation();
			recommendation.question = data.get(i).get(QUESTION);
			recommendation.link  = data.get(i).get(LINK);
			recommendation.industry  = data.get(i).get(INDUSTRY);
			recommendation.keywords  = data.get(i).get(KEYWORDS);
			recommendation.type  = data.get(i).get(TYPE);
			recommendation.topic  = data.get(i).get(TOPIC);
			recommendations.add(recommendation);
		}
		return recommendations;
	}

	public static void dataPrepStep1(List<Recommendation> recommendations) {
		for (Recommendation r : recommendations) {
			r.pquestion = stemQuestion(rmStopWords(r.question.toLowerCase()));
			r.pkeywords = mkList(r.keywords.toLowerCase());
			r.pindustry = mkList(r.industry.toLowerCase());
			r.ptype = mkList(r.type.toLowerCase());
			r.ptopic = mkList(r.topic.toLowerCase());
		}
	}

	public static Dictionaries mkDicts(List<Recommendation> recommendations) {
		Dictionaries dictionaries=new Dictionaries();
		for (Recommendation r : recommendations) {
			add2Dict(r.pquestion, dictionaries.questions);
			add2Dict(r.pindustry, dictionaries.industry);
			add2Dict(r.ptype, dictionaries.type);
			add2Dict(r.ptopic, dictionaries.topic);
			add2Dict(r.pkeywords, dictionaries.keywords);
		}
		return dictionaries;
	}
	
}



