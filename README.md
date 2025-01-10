# RecollDroid Android UI

RecollDroid is an Android application (API level 26 or higher) that acts as a front end to provide remote access to your recoll database(s). The back end is Python based and serves the recoll data via a RESTful API. The app is tested primarily with a Linux back end but may work with other platforms as well.

# Overview

RecollDroid allows you to query and interact with your recoll document indexes on the go using your Android device. The back end runs on a server that hosts the recoll database and provides API access through FastAPI and Nginx acting as a reverse proxy. The app connects securely over HTTPS using a valid signed certificate.

# Architecture

The system consists of two main parts. The Android UI app which communicates via REST calls with the server, and the Python back end which exposes the recoll database functionality through FastAPI running under the Unit application server. Nginx acts as a reverse proxy and handles authentication, security, and optionally file access via WebDAV. The architecture supports Raspberry Pi and other Linux servers.

# Tech Stack

The Android UI is written in Java/Kotlin and targets Android API 26 and above. The back end uses Python with FastAPI for API endpoints. Nginx serves as the reverse proxy and authentication layer, and Unit is used as the Python app server. The recoll indexer is used to create and maintain the document database. Docker images are provided for easy deployment of the server stack on amd64 or arm64 platforms.

# Setup Instructions

To set up the Android UI, install it from F-Droid or download the apk manually. For development, clone the repository and deploy from Android Studio.

Server setup requires a domain name with a valid signed certificate (self-signed certificates are not supported). Use the provided Docker images for amd64 or arm64 or set up the stack manually with Nginx, Unit, FastAPI, and recoll. Configure Nginx for basic authentication and as a reverse proxy forwarding API requests to Unit. Optionally enable WebDAV in Nginx for file access.

# Deployment

Install the Android app on your device. Deploy the server stack on your target platform using Docker or manual installation. Ensure HTTPS with a valid signed certificate is configured. Connect the Android UI app to the server domain. The app communicates over secure HTTPS with basic authentication to access the recoll database remotely.

# Features

- Remote access to recoll search database from Android devices
- Full search functionality mirroring the Python API
- Secure connection using HTTPS with signed certificates
- Basic authentication for API access
- Docker images available for easy server deployment
- Nginx reverse proxy setup with optional WebDAV file access
- Tested on Raspberry Pi running Raspberry Pi OS 64-bit
- Supports indexing on separate x86 hardware with rsync to server
- UI app available via F-Droid and apk download
- Open source GPL3 licensed project



# Roadmap

Plans include adding a UI app bar icon notification for pending document extractions.

# Contributing

Contributions in the form of patches, suggestions, and issue reports are welcome.

# Authors and Acknowledgments

Project maintained by Akash.
