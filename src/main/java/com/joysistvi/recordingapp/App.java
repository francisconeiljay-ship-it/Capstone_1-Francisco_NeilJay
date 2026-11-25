package com.joysistvi.recordingapp;

import com.joysistvi.recordingapp.config.DbConnection;
import com.joysistvi.recordingapp.repository.*;
import com.joysistvi.recordingapp.service.*;
import com.joysistvi.recordingapp.view.*;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {

        DbConnection dbConnection = new DbConnection();
        Scanner sharedScanner = new Scanner(System.in);


        ArtistRepository artistRepository = new ArtistRepositoryImpl(dbConnection);
        AlbumRepository albumRepository = new AlbumRepositoryImpl(dbConnection);
        UserRepository userRepository = new UserRepositoryImpl(dbConnection);


        ArtistService artistService = new ArtistServiceImpl(artistRepository);
        AlbumService albumService = new AlbumServiceImpl(albumRepository);
        UserService userService = new UserServiceImpl(userRepository);


        ArtistView artistView = new ArtistView(artistService, sharedScanner);
        AlbumView albumView = new AlbumView(albumService, sharedScanner);
        UserView userView = new UserView(userService, sharedScanner);


        System.out.println("=== RECORDING STUDIO APP  ===");


        artistView.promptCreateArtist();
        artistView.displayAllArtists();
        artistView.promptSearchArtists();
        artistView.promptUpdateArtist();
        artistView.promptArchiveArtist();
        artistView.promptRestoreArtist();


        albumView.promptCreateAlbum();
        albumView.displayAllAlbums();
        albumView.promptSearchAlbums();
        albumView.promptUpdateAlbum();
        albumView.promptArchiveAlbum();
        albumView.promptRestoreAlbum();


        userView.promptCreateUser();
        userView.displayAllUsers();
        userView.promptLoginUser();
        userView.promptUpdateUser();
        userView.promptArchiveUser();
        userView.promptRestoreUser();

        sharedScanner.close();
    }
}