# 💾 Contributing

Compass is open to contributions, if there is a bug or feature. Feel free to open up a PR or an issue.

Please make every effort to follow existing conventions and style in order to keep the code as readable as possible.

Contribute code changes through GitHub by forking the repository and sending a pull request. We squash all pull requests on merge.

If you add, remove, or change `public` objects, make sure you update the binary-compatibility definitions by running the following command:

```bash
./gradlew apiDump
```

Then commit and push the changed files.

### Custom Geocoder API

Compass includes a template module: `compass-geocoder-web-template`

If there is a Geocoding Web API that you would wish to add to Compass, follow these steps:

1. Clone the repo: `git clone git@github.com:jordond/compass`
2. Make a copy of the `compass-geocoder-web-template` module
3. Rename the copy to match the web service you're adding:
   1. ex: `compass-geocoder-web-myapi`
4. Search for the word "Template" in the module and replace it with your service name
5. Implement the `HttpApiPlatformGeocoder` interface
6. Customize the URL, query parameters, and the response models
7. Implement the `ForwardEndpoint` and `ReverseEndpoint`
8. Finish the implementation
9. Run `./gradlew apiDump` and commit the changes
10. Push & open a Pull Request

