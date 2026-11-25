package com.joysistvi.recordingapp.view;

import com.joysistvi.recordingapp.model.Album;
import com.joysistvi.recordingapp.service.AlbumService;
import java.util.List;
import java.util.Scanner;

public class AlbumView {
    private final AlbumService albumService;
    private final Scanner scanner;

    public AlbumView(AlbumService albumService, Scanner scanner) {
        this.albumService = albumService;
        this.scanner = scanner;
    }

    public void displayAllAlbums() {
        List<Album> albums = albumService.getAllActiveAlbums();
        printAlbumTable("ACTIVE ALBUMS", albums);
    }

    public void promptCreateAlbum() {
        System.out.print("Enter Album Name: ");
        String name = scanner.nextLine();
        int year = readInt("Enter Release Year (1900-2100): ");
        int artistId = readInt("Enter Artist ID: ");

        if (albumService.createAlbum(name, year, artistId)) {
            System.out.println("Album created successfully.");
        }
    }

    public void promptUpdateAlbum() {
        displayAllAlbums();
        int id = readInt("Enter Album ID to update: ");
        System.out.print("Enter New Album Name: ");
        String name = scanner.nextLine();
        int year = readInt("Enter New Release Year: ");
        int artistId = readInt("Enter New Artist ID: ");

        if (albumService.updateAlbum(id, name, year, artistId)) {
            System.out.println("Album updated successfully.");
        } else {
            System.out.println("Failed to update album.");
        }
    }

    public void promptDeleteAlbum() {
        displayAllAlbums();
        int id = readInt("Enter Album ID to permanently delete: ");
        if (albumService.deleteAlbum(id)) {
            System.out.println("Album deleted permanently.");
        } else {
            System.out.println("Failed to delete album.");
        }
    }

    public void promptArchiveAlbum() {
        displayAllAlbums();
        int id = readInt("Enter Album ID to archive: ");
        if (albumService.archiveAlbum(id)) {
            System.out.println("Album archived successfully.");
        } else {
            System.out.println("Failed to archive album.");
        }
    }

    public void promptRestoreAlbum() {
        List<Album> archived = albumService.getAllArchivedAlbums();
        if (archived.isEmpty()) {
            System.out.println("No archived albums found.");
            return;
        }
        printAlbumTable("ARCHIVED ALBUMS", archived);
        int id = readInt("Enter Album ID to restore: ");
        if (albumService.restoreAlbum(id)) {
            System.out.println("Album restored successfully.");
        } else {
            System.out.println("Failed to restore album.");
        }
    }

    public void promptSearchAlbums() {
        System.out.print("Enter Album search keyword: ");
        String keyword = scanner.nextLine();
        List<Album> results = albumService.searchAlbums(keyword);
        printAlbumTable("SEARCH RESULTS", results);
    }

    private void printAlbumTable(String title, List<Album> albums) {
        System.out.println("\n=== " + title + " ===");
        if (albums.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.printf("%-5s | %-25s | %-6s | %-20s%n", "ID", "Album Name", "Year", "Artist");
        System.out.println("-----------------------------------------------------------------");
        for (Album al : albums) {
            System.out.printf("%-5d | %-25s | %-6d | %-20s%n",
                    al.getId(), al.getName(), al.getYear(), al.getArtistName());
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