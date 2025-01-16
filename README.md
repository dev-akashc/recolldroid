# RecollDroid (Android UI)
<img src="https://gitlab.com/gbygrave/recolldroid/-/badges/release.svg">
<img src="https://gitlab.com/gbygrave/recolldroid/badges/main/pipeline.svg?ignore_skipped=true">
<img src="https://gitlab.com/gbygrave/recolldroid/badges/main/coverage.svg">

RecollDroid is an Android (API >=26) front end and Python based back end for providing access to your recoll database(s) while away from your desktop.  Tested with a Linux back end, may work on others.

Back end tested with Nginx acting as reverse proxy for handling security and forwarding and providing file access via WebDAV.  Server makes use of FastAPI to provide a RESTful API and runs in the Unit app server (for easy integration with Nginx).

Test server platform is a Raspberry Pi-4/4Gb RAM running Raspberry Pi-OS 64 (concurrently acting as local DNS server, fileserver, firewall/gateway).

My recoll indexes are computed independently on x86 hardware and rsync'd to the server although you can run Recoll on a Pi of course if you're building a small index or don't mind waiting.

## Important
The recolldroid android app requires that a signed cert be installed on the server when connecting.  If you don't already have one, this is free and straightforward to set up.  You will need a domain name visible to the internet (static or ddns) and a valid certificate for that domain (e.g. via letsencrypt / certbot). Self-signed certificates won't work, unfortunately.  There's currently no mechanism to upload a self-signed certificate that you trust to your phone to be used with the recolldroid app.

# Status
The app provides access to most (all?) functionality available via the Python API.  If you spot something missing, let me know.  Server side reporting of errors to the GUI is weak and deemed "ok for now" due to the fact you own the server too so can easily go and look at the logs there.  At some point I'll wrap all the exposed API calls in try/catch blocks.  Most of the errors you're likely to hit will be file permissioning ones.

## Screenshots
<img width="290" src="images/RecollDroidUi_0.jpg?ref_type=heads" alt="User interface Image 1">
<img width="290" src="images/RecollDroidUi_1.jpg?ref_type=heads" alt="User interface Image 2">
<img width="290" src="images/RecollDroidUi_2.jpg?ref_type=heads" alt="User interface Image 3">
<img width="290" src="images/RecollDroidUi_3.jpg?ref_type=heads" alt="User interface Image 4">

## Short Video Demo
[<img src="metadata/en-US/images/Demo Video Still.png" 
       alt="Demo Video"
       height="200">](https://www.youtube.com/watch?v=6-GL3RcGAZk)


## UI Installation
The easiest way to install the UI is via [F-Droid](https://f-droid.org/en/packages/org.grating.recolldroid/).  Alternatively, you can manually grab the [apk](https://gitlab.com/gbygrave/recolldroid/-/releases/v1.1/downloads/app-release.apk), or if you have AndroidStudio you can git clone the repository and install to your device via a remote debug session.  

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="80">](https://f-droid.org/packages/org.grating.recolldroid/)

Or download from the [Releases Section](https://gitlab.com/gbygrave/recolldroid/-/releases) here on Gitlab.

### Server Installation
Docker images are provided for the server side as a _hopefully_ easy way to get setup - you'll need to choose [amd64](https://gitlab.com/gbygrave/recolldroid-server/container_registry/8048769) or [arm64](https://gitlab.com/gbygrave/recolldroid-server/container_registry/8048770) depending on your target platform.

The RecollDroid server project can be found [here](https://gitlab.com/gbygrave/recolldroid-server).  The server side code is fairly minimal is it only deals with connectivity to the recoll python api and leaves security, marshalling and communication to other things.  The only hard pre-requisites (without modifying the code) are FastAPI and the use of basic auth.  I personally set this up using Nginx and Nginx-Unit and that's what the docker images do, but other setups are certainly conceivable.

#### Tested Server Stack
> - Nginx
>   - Acting as authenticator, checking the http auth headers (basic auth over https is the only supported auth mechanism currently) and ensuring you're allowed to access the recoll server api.
>   - Acting as reverse proxy (i.e. taking the https://my.domain.com/recoll?query_str=x&page=n... requests, noticing the 'recoll' bit and accordingly passing them through to the RecollDroid server instance running in Unit).
>   - _**Optional**_: Providing direct access to the documents referenced in the recoll databases via its WebDAV module.  You'll need this if you want to retrieve raw documents via the 'Open' button in the GUI.
> - Unit
>   - Acting as a python Application Server for the recoll server code.
> - FastAPI
>   - Creating the API endpoints that respond to calls and performing the json marshalling of the responses.


## Support
If you get stuck, send direct email to recolldroid@bygrave.org

## Roadmap
Add UI app bar icon as notification when awaiting an internal document extraction.

## Contributing
Patches, suggestions, complaints welcome.

## Authors and acknowledgment
Graham Bygrave

## License
GPL3 or later

## Project status
Active development.
