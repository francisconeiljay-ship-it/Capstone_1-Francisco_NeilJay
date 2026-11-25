package com.joysistvi.recordingapp.view;

import com.joysistvi.recordingapp.model.Artist;
import com.joysistvi.recordingapp.service.ArtistService;
import java.util.List;
import java.util.Scanner;

public class ArtistView {
    private final ArtistService artistService;
    private final Scanner scanner;

    public ArtistView(ArtistService artistService, Scanner scanner) {
        this.artistService = artistService;
        this.scanner = scanner;
    }

    public void displayAllArtists() {
        List<Artist> artists = artistService.getAllActiveArtists();
        printArtistTable("ACTIVE ARTISTS", artists);
    }

    public void promptCreateArtist() {
        System.out.print("Enter Artist Name: ");
        String name = scanner.nextLine();
        if (artistService.createArtist(name)) {
            System.out.println("Artist created successfully.");
        }
    }

    public void promptUpdateArtist() {
        displayAllArtists();
        int id = readInt("Enter Artist ID to update: ");
        System.out.print("Enter New Artist Name: ");
        String name = scanner.nextLine();
        if (artistService.updateArtist(id, name)) {
            System.out.println("Artist updated successfully.");
        } else {
            System.out.println("Failed to update artist (check ID).");
        }
    }

    public void promptDeleteArtist() {
        displayAllArtists();
        int id = readInt("Enter Artist ID to permanently delete: ");
        if (artistService.deleteArtist(id)) {
            System.out.println("Artist deleted permanently.");
        } else {
            System.out.println("Failed to delete artist.");
        }
    }

    public void promptArchiveArtist() {
        displayAllArtists();
        int id = readInt("Enter Artist ID to archive: ");
        if (artistService.archiveArtist(id)) {
            System.out.println("Artist archived successfully.");
        } else {
            System.out.println("Failed to archive artist.");
        }
    }

    public void promptRestoreArtist() {
        List<Artist> archived = artistService.getAllArchivedArtists();
        if (archived.isEmpty()) {
            System.out.println("No archived artists found.");
            return;
        }
        printArtistTable("ARCHIVED ARTISTS", archived);
        int id = readInt("Enter Artist ID to restore: ");
        if (artistService.restoreArtist(id)) {
            System.out.println("Artist restored successfully.");
        } else {
            System.out.println("Failed to restore artist.");
        }
    }

    public void promptSearchArtists() {
        System.out.print("Enter Artist search keyword: ");
        String keyword = scanner.nextLine();
        List<Artist> results = artistService.searchArtists(keyword);
        printArtistTable("SEARCH RESULTS", results);
    }

    private void printArtistTable(String title, List<Artist> artists) {
        System.out.println("\n=== " + title + " ===");
        if (artists.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.printf("%-5s | %-30s%n", "ID", "Artist Name");
        System.out.println("--------------------------------------");
        for (Artist a : artists) {
            System.out.printf("%-5d | %-30s%n", a.getId(), a.getName());
        }
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}