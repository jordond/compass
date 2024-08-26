<img width="500px" src="art/logo-full.png" alt="logo"/>
<br />

[![Maven Central Version](https://img.shields.io/maven-central/v/dev.jordond.compass/core)](https://central.sonatype.com/namespace/dev.jordond.compass)
[![Kotlin](https://img.shields.io/badge/kotlin-v2.0.20-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Build](https://github.com/jordond/compass/actions/workflows/ci.yml/badge.svg)](https://github.com/jordond/compass/actions/workflows/ci.yml)
[![License](https://img.shields.io/github/license/jordond/compass)](https://opensource.org/license/mit/)

[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-v1.6.11-blue)](https://github.com/JetBrains/compose-multiplatform)
![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat)
![badge-desktop](http://img.shields.io/badge/platform-desktop-DB413D.svg?style=flat)
![badge-js](http://img.shields.io/badge/platform-js%2Fwasm-FDD835.svg?style=flat)

Compass is a Kotlin Multiplatform library location toolkit. It provides a set of tools for working
with location data, including geocoding, reverse geocoding, and more. The
library is built with a focus on simplicity and ease of use, providing a straightforward API for
geocoding operations.

Check out the [Documentation](https://compass.jordond.dev) for detailed instructions. You can also
view the generated KDoc at [docs.compass.jordond.dev](https://docs.compass.jordond.dev)

## Table of Contents

- [Features](#features)
- [Setup](#setup)
- [Demo](#demo)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Geocoding**:
    - Convert an address to a latitude and longitude (forward geocoding)
    - Convert a latitude and longitude to an address (reverse geocoding)
    - Support via native Android/iOS services
    - Support via third party APIs for all platforms (planned)
- **Geolocation**:
    - Built in permission handling
    - Get the current location of the device
    - Monitor the location of the device
    - JS/WASM browser geolocation support
- **Autocomplete**:
    - Provide suggestions for addresses based on user input
    - Support via native Android/iOS services
    - Support for all platforms via third party geocoder API's
    - Support for Google Places API (planned)

## Setup

Read the [Setup documentation](https://compass.jordond.dev/docs/setup/) for more information on setup and usage.

## Demo

A demo app is available in the `demo` directory. It is a Compose Multiplatform app that runs on
Android, and iOS.

See [Documentation](https://compass.jordond.dev/docs/misc/demo) for more.

## Contributing

Contributions are always welcome!. If you'd like to contribute, please feel free to create a PR or
open an issue.

See [Documentation](https://compass.jordond.dev/docs/misc/contributing) for more.

## License

See [LICENSE](LICENSE) for more information.
