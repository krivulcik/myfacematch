# myfacematch

## Brief overview

Myfacematch is a procrastination tool for fans of TV series. Using this tool, procrastination will be become much easier, wasting obscene amounts of potentially productive time of the brightest minds of the mankind.

By selecting which one of the two images is nicer, the players will rank the images and also provide a dataset of preferences for future research (like what kind of visual characteristics tend to be significant in evaluation of visual attractiveness of female characters).

This software will provide a *platform* for creating *instances* of the game. Each *instance* will have one *administrator* who will be able to add the visual content and adjust the visual appearance of the game. Each instance will have its unique URL which will identify it. The non-administrator users, *players*, will be shown pairs of images selected from the administrator-supplied content by several algorithms (currently only random). The player will decide which image they like better and click the corresponding button. After this, another pair of images will be shown etc etc.

The votes for individual images will be stored server-side and kept for further processing. Statistics for individual images will include:

* Number of displays total
* Number of wins total
* Number of loses total
* Number of displays with each of the competing images
* Number of wins against each of the competing images
* Number of loses against each of the competing images

The players will be able to view a ranking screen with the images sorted by the image score - number of total wins divided by number of total displays.

The players will be able to select one image and see its detailed rankings - score agains each (or a subset) of the competing images

The players will be able to select two images and see their detailed rankings against each other - score, displays, wins, loses.


## Frontend interface


### Main frontend page

https://moqups.com/m8r-x6jbv31/5cLQwzWz/p:a16b2c2a4

The main page shows a background image (admin-editable) and following foreground elements:

* Title (admin-editable)
* Two placeholders for images (position and size admin-editable)
* One button under each of the images ("Like")
* Footer: Contact, TOS, Privacy policy, github link, admin link

Upon loading, a button is overlaid over the screen to start the game. After clicking it, the placeholders are populated by two randomly selected images. The user is able to click on one of the "like" buttons under the images. This will send the vote to the server and load another pair of images. After completing admin-editable number of face matches, the user will be shown a link to see image rankings in an overlay. The user can continue the game without clicking the rankings link, the matches counter will reset.

The game is playable without any registration/login, a cookie session ID is generated in this case and assigned to the user.

The players are able to login using facebook or twitter. After logging in, the current session ID and all data collected so far are assigned to the user.

All non-full-size images should be clickable to show full-size image in a lightbox-like overlay.


### Images ranking screen

https://moqups.com/m8r-x6jbv31/5cLQwzWz/p:a84b6220f


### Image vs. other images screen

https://moqups.com/m8r-x6jbv31/5cLQwzWz/p:a2d5fe0ea


### Two images screen

https://moqups.com/m8r-x6jbv31/5cLQwzWz/p:aeeb9e81f


## Backend interface


### Login screen

https://moqups.com/m8r-x6jbv31/5cLQwzWz/p:a06a2c331

Displays facebook and twitter login buttons. After logging in using facebook or login, the user is redirected to the main admin page.


### Main admin page

https://moqups.com/m8r-x6jbv31/5cLQwzWz/p:aacc3fcf2

Shows square icons for administration of the instance aspects (listed below) and a logout icon.


#### Configurable instance aspects

* Game properties
  * Number of face matches to show rankings link
* Screen layout
  * Text, position, size and color of the title
  * Background image
  * Size and position of the two image placeholders
  * Position of the "like" buttons
* Images
  * Images to be shown to the players
    * After upload, crop interface will be shown, will crop to the correct ratio (to fit image placeholders aspect ratio)
      * Cropped images won't be created immediately, they will be rendered on-demand, on-the-fly. Rendering parameters: requested size, requested crop
      * Original images will be kept, if aspect ratio of the image placeholders will change, the images will get re-cropped automatically using a guess (keep center of the image, adjust the sizes to fit aspect ratio - enlarge if possible, if not, shrink). Admin will be notified to check the cropping of the images.
    * Each image will need to have following information:
      * Character name
      * Season/episode/timing information
      * Image itself


## Frontend API

* Provide a pair of randomly selected images
  * GET /images
  * Input:
    * None
  * Output:
    * Array of two image descriptors
      * Image descriptor contains: image URL, Character name, Season/episode/timing information
    * Validity token (JWT token encoding image IDs and session ID, valid for 5 minutes, all used tokens are stored server-side to prevent double submit)
* Add a vote to one of the images selected above
  * POST /vote
  * Input:
    * Validity token
    * Liked image ID
  * Output:
    * Success/failure status
  * Notes:
    * Should add the JWT to list of used tokens to prevent double submits.
    * After expiration of the tokens, they can be removed from the table of used tokens.
    * The token contains the session ID and IDs of the images and this information needs to be verified so that only the user that has seen the image can vote for it.
* Retrieve a list of images
  * Input:
    * Sorting criteria (score, displays, wins, loses)
    * Limit, offset
  * Output:
    * Number of requested images
    * Number of returned images
    * Array of image descriptors
      * Image descriptor contains: image URL, Character name, Season/episode/timing information, displays, wins, losses total
* Retrieve a list of competing images for given image
  * Input:
    * Image ID
    * Sorting criteria (score, displays, wins, loses)
    * Limit, offset
  * Output:
    * Number of requested images
    * Number of returned images
    * Array of image descriptors
      * Image descriptor contains: image URL, Character name, Season/episode/timing information, displays, wins, losses against given image
* Retrieve details for a pair of images
  * Input:
    * Image IDs
  * Output:
    * Array of two image descriptors
      * Image descriptors contains: image URL, Character name, Season/episode/timing information, displays, wins, losses against given image


### Examples


#### Provide a pair of randomly selected images

```
$ curl -i -X GET 'http://localhost:3000/mockapi/v1/images' 
HTTP/1.1 200 OK
Date: Sun, 31 Aug 2014 21:20:36 GMT
Content-Type: application/json;charset=ISO-8859-1
Content-Length: 338
Server: Jetty(7.6.8.v20121106)

{"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJteWZhY2VtYXRjaCIsIm1mZGF0YSI6eyJpZDEiOjEsImlkMiI6Miwic2Vzc2lvbmlkIjoiYWJjZGVmZ2hpaiJ9fQ.IXqQuH4Ap6PFBVjWpjiuYvi1K5YKV4xwOx2Dr5946zQ","images":[{"name":"Ygritte","info":"S01E01@12:34","url":"\/images\/1.jpg"},{"name":"Catelyn Stark","info":"S01E01@01:23","url":"\/images\/2.jpg"}]}
```


#### Add a vote to one of the images selected above - success
```
$ curl -i -H 'Content-Type: application/json' -X POST 'http://localhost:3000/mockapi/v1/vote/1' -d '{"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJteWZhY2VtYXRjaCIsIm1mZGF0YSI6eyJpZDEiOjEsImlkMiI6Miwic2Vzc2lvbmlkIjoiYWJjZGVmZ2hpaiJ9fQ.IXqQuH4Ap6PFBVjWpjiuYvi1K5YKV4xwOx2Dr5946zQ"}'
HTTP/1.1 200 OK
Date: Sun, 31 Aug 2014 21:22:37 GMT
Content-Type: application/json;charset=ISO-8859-1
Content-Length: 9
Server: Jetty(7.6.8.v20121106)

"success"
```

#### Add a vote to one of the images selected above - failure - incorrect signature
```
$ curl -i -H 'Content-Type: application/json' -X POST 'http://localhost:3000/mockapi/v1/vote/1' -d '{"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJteWZhY2VtYXRjaCIsIm1mZGF0YSI6eyJpZDEiOjEsImlkMiI6Miwic2Vzc2lvbmlkIjoiYWJjZGVmZ2hpaiJ9fQ.IXqQuH4Ap6PFBVjWpjiuYvi1K5YKV4xwOx2Dr5946z"}'
HTTP/1.1 400 Bad Request
Date: Sun, 31 Aug 2014 21:23:11 GMT
Content-Type: application/json;charset=ISO-8859-1
Content-Length: 55
Server: Jetty(7.6.8.v20121106)

"fail: unable to verify JWT token: verification failed"
```

#### Add a vote to one of the images selected above - failure - invalid token 

```
$ curl -i -H 'Content-Type: application/json' -X POST 'http://localhost:3000/mockapi/v1/vote/1' -d '{"token":"eyJh"}'HTTP/1.1 400 Bad Request
Date: Sun, 31 Aug 2014 21:23:54 GMT
Content-Type: application/json;charset=ISO-8859-1
Content-Length: 74
Server: Jetty(7.6.8.v20121106)

"fail: unable to verify JWT token: JSON error (end-of-file inside string)"
```
