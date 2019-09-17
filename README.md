# Android Location Tracker
Location tracker for android

Using service to track user location. Unfortunately we cannot use work manager (which is far more efficient) for this task because it requires that periodic work
task be more than or equal to 15 mins. However, for the use case where we need user location in fast interval we can instead use 
foreground service. We are using dagger to inject dependency which helps write clean testable code.
