package com.researchpapers.service;

import com.researchpapers.model.Paper;

import java.io.PrintWriter;
import java.util.List;

public class ExportService {

    public static void exportToCsv(List<Paper> papers, String filePath) throws Exception {
        try (PrintWriter pw = new PrintWriter(filePath, "UTF-8")) {
            pw.println("Title,Authors,Year,Category,Rating,DOI,Venue,Abstract");
            for (Paper p : papers) {
                pw.printf("\"%s\",\"%s\",%d,\"%s\",%d,\"%s\",\"%s\",\"%s\"%n",
                    escapeCsv(p.getTitle()),
                    escapeCsv(p.getAuthors()),
                    p.getPublicationYear(),
                    escapeCsv(p.getCategory() != null ? p.getCategory() : ""),
                    p.getRating(),
                    escapeCsv(p.getDoi() != null ? p.getDoi() : ""),
                    escapeCsv(p.getPublicationVenue() != null ? p.getPublicationVenue() : ""),
                    escapeCsv(p.getAbstractText() != null ? p.getAbstractText() : "")
                );
            }
        }
    }

    private static String escapeCsv(String s) {
        if (s == null) return "";
        return s.replace("\"", "\"\"");
    }
}