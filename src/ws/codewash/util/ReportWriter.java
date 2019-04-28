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


	static final File RESULTS_ROOT = new File(Paths.get("results\\").toUri());

	public String writeReport(List<Report> reports) {

		GsonReport[] gsonReports = new GsonReport[reports.size()];
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		for (int i = 0; i < reports.size(); i++) {
			if (reports.get(i) instanceof MemberReport) {

				List<String> memberNames = new ArrayList<>();
				((MemberReport) reports.get(i)).getProblemMembers().forEach(cwMember -> memberNames.add(cwMember.getName()));

				String[] problemMembers = memberNames.toArray(new String[memberNames.size()]);
				gsonReports[i] = new GsonMemberReport(((MemberReport) reports.get(i)).getReportClass().getSimpleName(), ((MemberReport) reports.get(i)).getReportClass().getPackageName(), reports.get(i).getCodeSmell(), problemMembers);
			} else {
				gsonReports[i] = (new GsonReport(((ClassReport) reports.get(i)).getClass().getSimpleName(), ((ClassReport) reports.get(i)).getProblemClass().getPackageName(), reports.get(i).getCodeSmell()));
			}
		}

		String asGson = gson.toJson(gsonReports);
		try {

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

		GsonReport(String _class, String _package, String codeSmell) {
			mClass = _class;
			mCodeSmell = codeSmell;
			mPackage = _package;
		}
	}

	private class GsonMemberReport extends GsonReport {

		private String[] mProblemMembers;

		GsonMemberReport(String parentClass, String codeSmell, String _package, String[] members) {
			super(parentClass, codeSmell, _package);
			mProblemMembers = members;
			mMemberReport = true;
		}
	}
}



