# SpaceView
Display all Meetings for your room on a Display from your Nextcloud RoomVox Instance

## Development

### Change Version
Open the [gradle.properties](gradle.properties) file and change the following values:
- `app.version`: for the global App Version (1.0.0)
- `android.versionCode`: for the Android Build number (needs to be counted +1 for every Play Store Build)
- `ios.buildNumber`: for the iOS Build number (needs to be counted +1 for every App Store Build)

After changing the values, run `./gradlew syncIosVersion` to update the iOS version.