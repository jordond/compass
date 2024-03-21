# 🖥️ Artifacts

Compass publishes the following artifacts that support these platforms:



<table data-full-width="true"><thead><tr><th width="448">Artifact</th><th width="90" align="center">Status</th><th width="95" data-type="checkbox">Android</th><th width="60" data-type="checkbox">iOS</th><th width="67" data-type="checkbox">JVM</th><th width="92" data-type="checkbox">Native</th><th data-type="checkbox">Browser</th></tr></thead><tbody><tr><td><code>compass-core</code></td><td align="center">✅</td><td>true</td><td>true</td><td>true</td><td>true</td><td>true</td></tr><tr><td><code>compass-geocoder</code></td><td align="center">✅</td><td>true</td><td>true</td><td>true</td><td>true</td><td>true</td></tr><tr><td><code>compass-geocoder-mobile</code></td><td align="center">✅</td><td>true</td><td>true</td><td>false</td><td>false</td><td>false</td></tr><tr><td><code>compass-geocoder-web</code></td><td align="center">✅</td><td>true</td><td>true</td><td>true</td><td>true</td><td>true</td></tr><tr><td><code>compass-geocoder-web-mapbox</code></td><td align="center">✅</td><td>true</td><td>true</td><td>true</td><td>true</td><td>true</td></tr><tr><td><code>compass-geocoder-web-googlemaps</code></td><td align="center">✅</td><td>true</td><td>true</td><td>true</td><td>true</td><td>true</td></tr><tr><td><code>compass-geolocation</code></td><td align="center">🚧</td><td>true</td><td>true</td><td>true</td><td>true</td><td>true</td></tr><tr><td><code>compass-geolocation-mobile</code></td><td align="center">🚧</td><td>true</td><td>true</td><td>false</td><td>false</td><td>false</td></tr></tbody></table>

{% hint style="info" %}
If you plan on using Compass Geocoder in a project that targets both mobile and non-mobile platforms (desktop, browser, etc). Then you will need to make sure use `expect/actual` to provide a `PlatformGeocoder` object. See [mixed-platforms.md](usage/mixed-platforms.md "mention")
{% endhint %}

Go to the [add-dependencies.md](setup/add-dependencies.md "mention") page to learn how to add them to your project.
