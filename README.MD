<img width="500px" src="art/logo.png" alt="logo"/>
<br />

![Maven Central](https://img.shields.io/maven-central/v/dev.jordond/compass)
[![Kotlin](https://img.shields.io/badge/kotlin-v1.9.23-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Build](https://github.com/jordond/compass/actions/workflows/ci.yml/badge.svg)](https://github.com/jordond/compass/actions/workflows/ci.yml)
[![License](https://img.shields.io/github/license/jordond/compass)](https://opensource.org/license/mit/)

[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-v1.6.1-blue)](https://github.com/JetBrains/compose-multiplatform)
![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat)

Compass is a Kotlin Multiplatform library location toolkit. It provides a set of tools for working
with location data, including geocoding, reverse geocoding, and more. The
library is built with a focus on simplicity and ease of use, providing a straightforward API for
geocoding operations.

## Table of Contents

- [Features](#features)
    - [Planned Features](#planned-features)
- [Artifacts](#artifacts)
  - [`compass-geocoder-mobile`](#compass-geocoder-mobile)
  - [Platforms](#platforms)
- [Setup](#setup)
    - [Version Catalog](#version-catalog)
- [Usage](#usage)
- [Demo](#demo)
- [Feature Requests](#feature-requests)
- [Contributing](#contributing)
- [License](#license)
    - [Changes from original source](#changes-from-original-source)

## Features

- **Geocoding**:
    - Convert an address to a latitude and longitude (forward geocoding)
    - Convert a latitude and longitude to an address (reverse geocoding)
  - Support via native Android/iOS services
  - Support via third party APIs for all platforms (planned)
- **Geolocation**: (planned)
    - Get the current location of the device
    - Monitor the location of the device

## Artifacts

This library includes the following artifacts:

| Artifact                  | Status  |
|---------------------------|---------|
| `compass-geocoder-core`   | 🚧      |
| `compass-geocoder-mobile` | 🚧      |
| `compass-geocoder-api`    | 🚧      |
| `compass-geolocation`     | planned |

### `compass-geocoder-mobile`

This artifact uses built-in services for both iOS and Android. That means that there is restrictions
to its usage. See
the [Android documentation](https://developer.android.com/reference/android/location/Geocoder) as
well as the [iOS documentation](https://developer.apple.com/documentation/corelocation/clgeocoder)
for more information.

You can be throttled or blocked from using the geocoding services if you make too many requests in a
short period of time.

### Platforms

The artifacts are designed to work on the following platforms:

| Artifact                  | Android | iOS | Desktop (jvm) | macOS | Browser (wasm) |
|---------------------------|:-------:|:---:|:-------------:|:-----:|:--------------:|
| `compass-core`            |    ✅    |  ✅  |       ✅       |   ✅   |       ✅        |
| `compass-geocoder-core`   |    ✅    |  ✅  |       ✅       |   ✅   |       ✅        |
| `compass-geocoder-mobile` |    ✅    |  ✅  |       ❌       |   ❌   |       ❌        |

## Setup

You can add this library to your project using Gradle.

### Version Catalog

In `libs.versions.toml`:

```toml
[versions]
compass = "0.1.0"

[libraries]
compass-geocoder = { module = "dev.jordond.compass:compass-geocoder", version.ref = "compass" }
```

To add to a multiplatform project, add the dependency to the common source-set:

```kotlin
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compass.core)
            }
        }
    }
}
```

## Usage

TODO

## Demo

A demo app is available in the `demo` directory. It is a Compose Multiplatform app that runs on
Android, and iOS.

See the [README](demo/README.md) for more information.

## Feature Requests

If you have a feature request, please open an issue. If you would like to implement a feature
request refer to the [Contributing](#contributing) section.

## Contributing

Contributions are always welcome!. If you'd like to contribute, please feel free to create a PR or
open an issue.

## License

See [LICENSE](LICENSE) for more information.