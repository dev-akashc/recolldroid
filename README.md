# RecollDroid
Android (API >=26) front end and Python based back end for providing access to your recoll database(s) while away from your desktop.  Tested with a Linux back end, may work on others.

Back end tested with Nginx acting as reverse proxy for handling security and forwarding and providing file access via WebDAV.  Server makes use of FastAPI to provide a RESTful API and runs in the Unit app server (for easy integration with Nginx).

Test server platform is a Raspberry Pi-4/4Gb RAM running Raspberry Pi-OS 64 (concurrently acting as local DNS server, fileserver, firewall/gateway).

Recoll indexes computed independently on x86 hardware and transferred to back end.

# Status
New project, still settling in so installation is rough just now (but improving).

To install the UI at the moment, you'll need to install AndroidStudio and then grab the source and install to your device via a remote debug session.  The intention is upload this to FDroid and possibly Google Play Store (in that order).

To install the server side follow the fairly rough steps below.  It'll help if you're familiar with Nginx at least (and are willing to read howtos about Unit and FastAPI).


<img width="290" src="images/RecollDroidUi_0.jpg?ref_type=heads">
<img width="290" src="images/RecollDroidUi_1.jpg?ref_type=heads">
<img width="290" src="images/RecollDroidUi_2.jpg?ref_type=heads">
<img width="290" src="images/RecollDroidUi_3.jpg?ref_type=heads">

- [Short demo](https://www.youtube.com/watch?v=6-GL3RcGAZk)


## Installation
To install the UI you'll have to install AndroidStudio on your machine and upload the app via a remote debug session.  The goal is to make the UI available on FDroid and possibly Google Play Store in the near future.  To install the server side, follow the instructions below.  It'll help if you're familiar with Nginx and willing to read a howto on Unit and FastAPI.

### Server Installation
The server side code is fairly minimal is it only deals with connectivity to the recoll python api and leaves security, marshalling and communication to other things.  The only hard pre-requisites (without modifying the code) are FastAPI, basic auth and user home dirs on the server machine (to host a .recolldroid.conf.json config file).  I personally set this up using Nginx and Unit but other setups are certainly conceivable.

Hence the **supported** setup is:
> - Nginx
>   - Acting as authenticator, checking the http auth headers (basic auth over https is the only supported auth mechanism currently) and ensuring you're allowed to access the recoll server api.
>   - Acting as reverse proxy (i.e. taking the https://my.domain.com/recoll?query_str=x&page=n... requests, noticing the 'recoll' bit and accordingly passing them through to the RecollDroid server instance running in Unit).
>   - _**Optional**_: Providing direct access to the documents referenced in the recoll databases via its WebDAV module.  You'll need this if you want to click 'Open' in the GUI.
> - Unit
>   - Acting as a python Application Server for the recoll server code.
> - FastAPI
>   - Creating the API endpoints that respond to calls and performing the json marshalling of the responses.

#### Nginx Setup
First you'll want to install nginx, ideally with a legit certificate.  You can find out how to do this with a simple search, which should throw up a page such as this one...
> [Nginx/Let's Encrypt Setup](https://www.digitalocean.com/community/tutorials/how-to-secure-nginx-with-let-s-encrypt-on-ubuntu-20-04)

_**Optional**_ If you want to make your documents available for download/viewing via WebDAV, then install Nginx's WebDAV module and set up the appropriate location block in your nginx sites file.  If you're running Debian or a derivative then both NGinx and its WebDAV module are available via apt.

#### Unit / FastAPI Setup
The tested setup uses the Nginx's _**Unit**_ application server to host the server side code.  To set this up, once again an internet search will help.  For instance...

> [Unit/FastAPI Setup](https://unit.nginx.org/howto/fastapi/)

Choose a directory to house your server side code (e.g. /opt/recolldroid).  A few Python libs are required to get the server side running - the recoll python api of course, and also basicauth, cachetools, fastapi, fastapi-cli.  pip install these.

**Note:**__ You'll need to install a very recent version of the recoll Python API (> 1.40.3) as the server side code needs a fix for a small bug in the python api in v1.40.3 and earlier (missing arg in the call to Db.getDoc).

Your best bet for getting very recent versions of recoll and its python API is to add the official recoll repos to your sources list and then apt install recoll and python3-recoll.

See [Getting Latest Recoll](https://www.recoll.org/pages/download.html).

After this is done, and you have the relevant sections in your Nginx sites file, such as...

```
  location /recoll/ {
    auth_basic "Recoll Authorization";
    auth_basic_user_file /some/path/to/recoll.htpasswd;
    proxy_pass        http://<unithost>:<unitport>;
    proxy_set_header  Host $host;
    proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
    # Long timeout because recoll can take a while to dig out internal documents from big files.
    proxy_read_timeout 120;
  }

  location /webdav {
    auth_basic "WebDAV Authorisation";
    auth_basic_user_file /some/path/to/webdav.htpasswd;

    autoindex on;
    autoindex_exact_size off;

    root /home/$remote_user/;

    dav_methods PUT DELETE MKCOL COPY MOVE;
    dav_ext_methods PROPFIND OPTIONS;
    dav_access user:rw group:rw all:rw;

    create_full_put_path on; 
    client_body_temp_path /tmp/upload_tmp;
    client_max_body_size 0;
  }
```
... then you can hopefully start nginx and unit and fire up 
a browser and test some endpoints.

`systemctl start nginx unit`

In a browser with relevant network access to your server, go to 

`https://mydomain/recoll/search?query_str=some search terms`

and you should hopefully see some confirmatory json appear in your browser (after you've supplied your user and password of course).

```
{
  "query_str": "SVM",
  "n_results": 808,
  "query_ms": 6298,
  "first": 0,
  "last": 9,
  "retrieval_ms": 6299,
  "results": [
    {
      "url": "https://grating.mooo.com/webdav/media/books/Machine Learning/Natural Language Processing/Feature Selection using Linear Classifier Weights: Interaction with Classification Models, Mladenić ",
      "sig": "3054111279907638",
      "ipath": "",
      "rcludi": "/data/media/books/Machine Learning/Natural Language Processing/Feature Selection using Linear Classifier Weights: Interaction wiR1tTIpE5n7LCMWPtqYg09Q",
      "title": "Microsoft Word - p181-mladenic-final2.doc",
      "fbytes": "305411",
      "abstract": "Feature Selection using Linear Classifier Weights: Interaction with Classification Models Dunja Mladenić Janez Brank Marko Grobelnik Natasa Milic-Frayling Jožef Stefan Institute Ljubljana, Slovenia Tel.: +386 1 477 3900 Jožef Stefan",
      "author": "Janez,PScript5.dll Version 5.2",
      "caption": "Microsoft Word - p181-mladenic-final2.doc",
      "dbytes": "40593",
      "filename": "Feature Selection using Linear Classifier Weights: Interaction with Classification Models, Mladenić   et al.pdf",
      "relevancyrating": "100%",
      "fmtime": "01279907638",
      "mtype": "application/pdf",
      "origcharset": "CP1252",
      "mtime": "01279907638",
      "pcbytes": "305411",
      "snippets_abstract": " [P. 5] nonzero components per vector over the training set odds ratio †svm‡-1 information gain perceptron-1/16 †svm‡-1/16 perceptron-1/4 †svm‡-1/4 perceptron-1 5 0.5208 1 †svm‡... [P. 4] these sets results in three normal vectors for each method: †svm‡-1, †svm‡-1/4, and †svm‡... [P. 5] nonzero components per vector over the training set odds ratio †svm‡-1 information gain perceptron-1/16 †svm‡-1/16 perceptron-1/4 †svm‡-1/4 perceptron-1 Still higher performance with..."
    },
    {
      "url": "https://grating.mooo.com/webdav/media/books/Machine Learning/.cs229/proj2009/IngleKwon.pdf",
      "sig": "5859031272307843",
      "ipath": "",
      "rcludi": "/data/media/books/Machine Learning/.cs229/proj2009/IngleKwon.pdf|",
      "title": "Microsoft Word - IngleKwon-AutomaticSegmentationUsingLearningAlgorithmsForHighResoluti
```

## Support
If you get stuck, send direct email to recolldroid@bygrave.org

## Roadmap
Make it easier to insatll and setup.  Upload front end apk to F-Droid and Google Play Store.  Provide documentation on setting up the backend.

## Contributing
Patches, suggestions, complaints welcome.

## Authors and acknowledgment
Graham Bygrave

## License
GPL2

## Project status
Active development.
