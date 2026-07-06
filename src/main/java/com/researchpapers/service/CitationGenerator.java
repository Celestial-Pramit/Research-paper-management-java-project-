package com.researchpapers.service;

import com.researchpapers.model.Paper;

public class CitationGenerator {

    public static String generateIeee(Paper p) {
        String authors = formatIeeeAuthors(p.getAuthors());
        String title = p.getTitle() != null ? p.getTitle() : "Untitled";
        String venue = p.getPublicationVenue() != null ? p.getPublicationVenue() : "[Unpublished]";
        int year = p.getPublicationYear() > 0 ? p.getPublicationYear() : java.time.Year.now().getValue();
        String doi = p.getDoi() != null && !p.getDoi().isEmpty() ? ", doi: " + p.getDoi() : "";

        return authors + ", \"" + title + ",\" " + venue + ", " + year + "." + doi;
    }

    private static String formatIeeeAuthors(String authors) {
        if (authors == null || authors.isBlank()) return "[Anonymous]";
        String[] parts = authors.split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.isEmpty()) continue;
            String[] nameParts = part.split("\\s+");
            if (nameParts.length == 2) {
                sb.append(nameParts[1].charAt(0)).append(". ").append(nameParts[0]);
            } else if (nameParts.length >= 3) {
                sb.append(nameParts[nameParts.length - 1].charAt(0)).append(". ");
                sb.append(String.join(" ", java.util.Arrays.copyOf(nameParts, nameParts.length - 1)));
            } else {
                sb.append(part);
            }
            if (i < parts.length - 2) {
                sb.append(", ");
            } else if (i == parts.length - 2) {
                sb.append(", and ");
            }
        }
        return sb.toString();
    }
}