package ws.codewash.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ws.codewash.analyzer.reports.ClassReport;
import ws.codewash.analyzer.reports.MemberReport;
import ws.codewash.analyzer.reports.Report;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ReportWriter {

	public static final String TAG = "ReportWriter";

	static final File RESULTS_ROOT = new File(Paths.get("results\\").toUri());

	public String writeReport(List<Report> reports) {

		GsonReport[] gsonReports = new GsonReport[reports.size()];
		Gson gson = new GsonBuilder().setPrettyPrinting().create();



		String asGson = gson.toJson(gsonReports);
		try {
			RESULTS_ROOT.mkdirs();
			Path p = Paths.get(RESULTS_ROOT + "/results.json");
			Files.write(p, asGson.getBytes());
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return asGson;
	}

	private class GsonReport {

		private String mClass;
		private String mCodeSmell;
		private String mPackage;
		boolean mMemberReport = false;
		private String mWarning;

		GsonReport(String _class, String _package, String codeSmell, String warning) {
			mClass = _class;
			mCodeSmell = codeSmell;
			mPackage = _package;
			mWarning = warning;
		}
	}

	private class GsonMemberReport extends GsonReport {

		private String[] mProblemMembers;

		GsonMemberReport(String parentClass, String codeSmell, String _package, String[] members, String warning) {
			super(parentClass, codeSmell, _package, warning);
			mProblemMembers = members;
			mMemberReport = true;
		}
	}
}



