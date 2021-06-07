package com.ofsaa.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Swati NC
 *
 */
public class HtmlReader {

	static int count = 0;

	public static void main(String args[]) {

		// Creating a File object for directory
		File directoryPath = new File("C:\\FilesToChange\\");
		FilenameFilter textFilefilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".htm")) {
					return true;
				} else {
					return false;
				}

			}
		};

		// List of all the text files
		String filesList[] = directoryPath.list(textFilefilter);

		List<Document> parsedDocs = getDocuments(directoryPath.list(textFilefilter));
		StringBuffer sbuf = new StringBuffer();
		for (int i = 0; i < parsedDocs.size(); i++) {
			if (sbuf.length() == 0) {
				sbuf = sbuf.append(filesList[i]);
			} else if (sbuf != null) {
				sbuf = sbuf.delete(0, sbuf.length());
				sbuf = sbuf.append(filesList[i]);
			}
			getTablesDeatils(parsedDocs.get(i), sbuf);
		}

	}

	// Get all the .htm document from given directory

	public static List<Document> getDocuments(String filesList[]) {
		Document doc = null;
		File file = null;
		List<Document> parsedDocs = new ArrayList<Document>();
		for (String fileName : filesList) {
			file = new File("C:\\FilesToChange\\" + fileName);

			try {
				doc = Jsoup.parse(file, "utf-8");
				parsedDocs.add(doc);
			} catch (IOException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return parsedDocs;
	}

	// get the list of htm files
	public static String[] getHtmFiles(File directoryPath) {

		FilenameFilter textFilefilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".htm")) {
					return true;
				} else {
					return false;
				}
			}
		};
		return directoryPath.list(textFilefilter);
	}

	// Get tables from each document
	public static void getTablesDeatils(Document doc, StringBuffer sb) {

		int tableCount = doc.getElementsByTag("table").size();
		Document updatedDoc = null;
		count = 0;
		Element table = null;
		if (tableCount != 0) {
			if (doc.getElementsByTag("tr").get(0).getElementsByTag("td").isEmpty()) {
				System.out.println("There are no tables to convert in this file");
			} else {
				for (int i = 0; i < tableCount; i++) {
					table = doc.getElementsByTag("table").get(i);
					table.attr("role", "grid").attr("class", "data").attr("tabindex", "0");
					table.childNode(0).before("<caption align='top'></caption>");
					updatedDoc = UpdateThAttribute(doc, table);
				}
				//System.out.println("updatedDoc html \n\n"+updatedDoc.outerHtml());
				saveTheFiles(updatedDoc.outerHtml(), sb);
				System.out.println("VPAT updated file is saved at C:\\UpdatedFiles\\"+sb);
			}
		} else {
			//System.out.println("Please do check the remaining files for tables");
		}

	}

	public static void saveTheFiles(String doc, StringBuffer sb) {

		File SaveUpdatedFiles = new File("C:\\UpdatedFiles\\");
		if (!SaveUpdatedFiles.exists()) {
			SaveUpdatedFiles.mkdirs();
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("C:\\UpdatedFiles\\" + sb));
			writer.write(doc);
			writer.flush();
		} catch (IOException e) {
			try {
				writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	// Update <td with <th
	public static Document UpdateThAttribute(Document doc1, Element table) {

		Element firstRow = table.getElementsByTag("tr").get(0);
		String newstr = firstRow.toString().replaceAll("<td", "<th id=col scope=col");
		table.getElementsByTag("tr").get(0).getElementsByTag("td").remove();
		table.getElementsByTag("tr").get(0).append(newstr);

		if (count == 0)
			for (int i = 0; i < firstRow.childrenSize(); i++) {
				table.getElementsByTag("tr").get(0).getElementsByTag("th").get(i).attr("id", "col" + (i + 1));
				count++;
			}
		else {
			for (int i = 0; i < firstRow.childrenSize(); i++) {
				count++;
				table.getElementsByTag("tr").get(0).getElementsByTag("th").get(i).attr("id", "col" + count);
			}
		}
		Document modifiedDoc = UpdateTdAttribute(doc1, table);

		return modifiedDoc;
	}

	// Add headers attribute to corresponding <td element
	public static Document UpdateTdAttribute(Document doc, Element table) {

		Element firstRow = table.getElementsByTag("tr").get(0);
		try {
			for (int i = 1; i < table.getElementsByTag("tr").size(); i++) {

				for (int n = 0; n < firstRow.childrenSize(); n++) {
					table.getElementsByTag("tr").get(i).getElementsByTag("td").get(n).attr("headers",
							table.getElementsByTag("tr").get(0).getElementsByTag("th").get(n).attr("id"));
				}
			}
		} catch (Exception e) {
			// System.out.println("Supressed Exception");
		}
		return doc;
	}
}
