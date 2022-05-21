/**
* Function to get all the opengames from Server
@ Hanna Kropf
 */

function showOpenGames() {
	console.log("show open games!");
	// The lobby assumes that the user already has logged in.
	// When a user is logged in, an accessToken can be found in the localStorage.
	// The adccessToken is needed to access secured endpoints in the server.
	const jwt = localStorage.getItem("accessToken");
	
	// The url of the endpoint we use.
	const url ='/opengames';
	console.log("Token: " + jwt);
	
	// We build a request by providing the url we want to access
	// We also need to provide the method of the request
	// We put the Accept-Header with the value of 'application/json' indicating,
	// that we want the response to be in JSON-format. This probably is not needed.
	// We set the Authoriatzion-Header and put the token into it, which is prefixed by 'Bearer'
	var request = new Request(url, {
      method: 'GET',
      headers: new Headers({
          'Accept': 'application/json',
          'Authorization' : 'Bearer ' + jwt
        })
  	});
  	
  	// Once we have built the request, we can send it to the server.
  	// We use the fetch-API from JavaScript see. w3c.org
  	// The fetch-function returns a Promise which is an object that promises something in the future.
  	// We can configure the promise with a function, so the promise knows what to do, if it has completed t
  	// its task. We do that with the .then-function.
	fetch(request)
    .then((response) => {
	
      return new Promise((resolve) => response.json()
      .then((json) => resolve({
        status: response.status,
        ok: response.ok,
        json
      })));
    })
    // status contins the response code of the server (i.e. 200 for OK)
    // json is the actual response data (in the body)
    // ok is just a flag, if everything is ok (from response.ok)
    .then(({ status, json, ok }) => {
      const message = json;
      // we switch to the response code and handle accordingly.
      // if the response code is 200, we have a list of open games in our json-data
      // an we will build bup the the HTML stuff with the buttons to select or join an
      // open game
      switch (status) {
        case 400:
          console.log("Bad Request: " + JSON.stringify(message));
          break;
        case 201:
        case 200:
      		buildHTMLOpenGames(json);
          break;
        case 500:
        default:
          console.log("Error 500: " + message);
    }
  })
}

// build the clickable buttons (as a div) to join an opened game
function buildHTMLOpenGames(games) {
	console.log("building open games");
	// we search for the html element which will contain the buttons.
	const openGames = document.getElementById("openGames");
	
	// we create at most 5 buttons to join a game. 
	// All the other opened games will not be visible
	for (let i = 0; i < games.length && i < 5; i++) {
		// Create a new div element.
		const div = document.createElement("div");
		
		// We add some classes to the newly generated div-element.
		div.classList.add("btn");
		div.classList.add("button" + (i+1));
		
		// We set an inner HTML to the div element by providing an html code using the back tricks ´ to replace some data with variables.
		div.innerHTML = (`<span input type="button" onclick="joinGame(${games[i].id})" >RAUM ${i+1} <br> SPIEL BEITRETEN</span>`);
		
		// finally we add the generated div element to the children of the container.
		openGames.appendChild(div);
	}
}



function startGame() {
	console.log("starting game!");
	const jwt = localStorage.getItem("accessToken");
	const url ='/games';
	console.log("Token: " + jwt);
	var request = new Request(url, {
      method: 'POST',
      headers: new Headers({
          'Content-Type': 'application/json',
          'Authorization' : 'Bearer ' + jwt
        })
  	});
  	
	fetch(request)
    .then((response) => {
	
      return new Promise((resolve) => response.json()
      .then((game) => resolve({
        status: response.status,
        ok: response.ok,
        game
      })));
    })
    .then(({ status, game, ok }) => {
      switch (status) {
        case 400:
          console.log("Bad Request: " + JSON.stringify(game));
          break;
        case 201:
        case 200:
          console.log("Game started!");
          location.href = "gameSurface-DE.html?gameId=" + game.id;
          break;
        case 500:
        default:
          console.log("Error 500: " + message);
    }
  })
}

function joinGame(gameId) {
	console.log("joining game with id: " + gameId);
	const jwt = localStorage.getItem("accessToken");
	const url =`/games/${gameId}/join`;
	var request = new Request(url, {
      method: 'POST',
      headers: new Headers({
          'Content-Type': 'application/json',
          'Authorization' : 'Bearer ' + jwt
        })
  	});
  	
  	
  	
  	fetch(request).then(response => {
		switch (response.status) {
			case 200:
			case 201:
				console.log("Game has been joined successfully!");
				location.href = "gameSurface-DE.html?gameId=" + gameId;
			break;
			case 400:
			case 401:
			console.log("Authorization required!");
			break;
			case 500:
			default:
			console.log("Unknown error.");
		}
	});
}
