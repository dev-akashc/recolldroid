# RecollDroid (Android UI)
RecollDroid is an Android (API >=26) front end and Python based back end for providing access to your recoll database(s) while away from your desktop.  Tested with a Linux back end, may work on others.

Back end tested with Nginx acting as reverse proxy for handling security and forwarding and providing file access via WebDAV.  Server makes use of FastAPI to provide a RESTful API and runs in the Unit app server (for easy integration with Nginx).

Test server platform is a Raspberry Pi-4/4Gb RAM running Raspberry Pi-OS 64 (concurrently acting as local DNS server, fileserver, firewall/gateway).

My recoll indexes are computed independently on x86 hardware and rsync'd to the server although you can run Recoll on a Pi of course if you're building a small index or don't mind waiting.

## Important
The recolldroid android app requires that a signed cert be installed on the server when connecting.  If you don't already have one, this is free and straightforward to set up.  You will need a domain name visible to the internet (static or ddns) and a valid certificate for that domain (e.g. via letsencrypt / certbot). Self-signed certificates won't work, unfortunately.  There's currently no mechanism to upload a self-signed certificate that you trust to your phone to be used with the recolldroid app.

# Status
New project, still settling in so installation is rough just now (but improving).

To install the UI at the moment, you'll need to install AndroidStudio and then grab the source and install to your device via a remote debug session.  The intention is upload this to FDroid and possibly Google Play Store (in that order).

Docker images are provided for the server side - you'll need to choose amd64 or arm64 as the recoll packages for those two platforms come from different package repos.

<img width="290" src="images/RecollDroidUi_0.jpg?ref_type=heads">
<img width="290" src="images/RecollDroidUi_1.jpg?ref_type=heads">
<img width="290" src="images/RecollDroidUi_2.jpg?ref_type=heads">
<img width="290" src="images/RecollDroidUi_3.jpg?ref_type=heads">

- [Short demo](https://www.youtube.com/watch?v=6-GL3RcGAZk)


## UI Installation
To install the UI you'll have to install AndroidStudio on your machine and upload the app via a remote debug session.  The goal is to make the UI available on FDroid and possibly Google Play Store in the near future.
To install the server, download one of the docker images from [here](https://gitlab.com/gbygrave/recolldroid-server/container_registry), making sure to select the right image for your hardware (either, amd64 or arm64).
Docker images are made available to ease installation of the server in the first instance, however I imagine one would typically run nginx/nginx-unit/fast-api independently of recolldroid - perhaps in an incus system container or vm - to provide self hosted services generally and install recolldroid on top of this infrastructure.

### Server Installation
The RecollDroid server project can be found [here](https://gitlab.com/gbygrave/recolldroid-server).  The server side code is fairly minimal is it only deals with connectivity to the recoll python api and leaves security, marshalling and communication to other things.  The only hard pre-requisites (without modifying the code) are FastAPI, the use of basic auth.  I personally set this up using Nginx and Nginx-Unit and that's what the docker images do, but other setups are certainly conceivable.

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
Make it easier to insatll and setup.  Upload front end apk to F-Droid and Google Play Store.  Provide documentation on setting up the backend.

## Contributing
Patches, suggestions, complaints welcome.

## Authors and acknowledgment
Graham Bygrave

## License
GPL3 or later

## Project status
Active development.
