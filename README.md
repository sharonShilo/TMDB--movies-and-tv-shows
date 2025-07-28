Movie & TV Show App
Overview
This is a mobile application developed as part of the Poalim Android. The app allows users to browse popular movies and TV shows,
search for content, view detailed information, play trailers, share content, and manage a favorites list. 
The app uses the TMDB API for data, implements image caching, and follows modern architectural patterns.


Technical Details
Platform

Android: Built using Kotlin with Jetpack Compose for the UI.

Architecture

Follows the MVVM (Model-View-ViewModel) architectural pattern for clean separation of concerns.
Uses Repository Pattern for data handling (API and local storage).
Implements Dependency Injection using Hilt (Android) or manual DI (iOS).

TMDB API Integration

Fetches popular movies and TV shows for the main page.
Supports search functionality for movies and TV shows.
Retrieves detailed information and trailer links for the details page.
Uses the TMDB API key (stored securely in the app configuration).

Image Caching

Images are cached using file system and database with a 1-day expiration policy to optimize performance and reduce network usage.

favorite cache
Favorite details cache in database using room

Unit Tests

Unit tests cover:
ViewModel logic for data processing and state management.


TMDB API key (sign up at TMDB to obtain one).


Notes

The app UI is inspired by modern streaming apps (e.g., IMDb) for a clean and intuitive user experience.
Image caching ensures efficient loading of thumbnails with a 1-day expiration to balance freshness and performance.
The favorites feature uses local storage (Room for Android) to persist user selections.

