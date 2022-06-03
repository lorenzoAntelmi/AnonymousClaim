/**author Hanna Kropf  */


/** Logout handling
	Attention: Token still exists on server (jwt does not allow "logout", only expiration of token)*/
		
		var logoutBtn = document.getElementById('logoutBtn');
		logoutBtn.addEventListener('click', logout);

		function logout() {
			localStorage.removeItem('accessToken');
			  window.location.href='login-DE.html';
				}

/**Ranking */
  function buildRanking() {

	 /**The lobby assumes that the user already has logged in.
	    When a user is logged in, an accessToken can be found in the localStorage.
	    The accessToken is needed to access secured endpoints in the server.*/
  
  const jwt = localStorage.getItem("accessToken");
  
   /**The url of the endpoint we use.*/
  const url ='/ranking';
  
   /**We build a request by providing the url we want to access
	    We also need to provide the method of the request
	    We put the Accept-Header with the value of 'application/json' indicating,
	    that we want the response to be in JSON-format. This probably is not needed. 
	    We set the Authoriatzion-Header and put the token into it, which is prefixed by 'Bearer'.*/
		var request = new Request(url, {
	      method: 'GET',
	      headers: new Headers({
	          'Authorization' : 'Bearer ' + jwt
	        })
	  	});
	  	
	  	/**Once we have built the request, we can send it to the server.
	     We use the fetch-API from JavaScript see. w3c.org
	   	 The fetch-function returns a Promise which is an object that promises something in the future.
	     We can configure the promise with a function, so the promise knows what to do, if it has completed
	     its task. We do that with the .then-function.*/
	  	fetch(request)
	    
	    /**status contains the response code of the server (i.e. 200 for OK).
	    Json is the actual response data (in the body).
	    "ok" is just a flag - if everything is ok (from response.ok)*/
	    
	    .then(({ status,json }) => {
	     
	    /**We switch to the response code and handle accordingly.
	    If the response code is 200, we have a list of the ranked user 
	    with the score in our json-data*/
	      switch (status) {
	        case 400:
	          console.log("Bad Request: " + JSON.stringify(message));
	          break;
	        case 201:
	        case 200: 
	        /**Fill the ranking table with backend results*/
				for (let i = 0; i <10; i++) {
					if (json.length > i) {
					var usernameId = document.getElementById("username" + i);
					var usernameText = document.createTextNode(json[i].username);
					usernameId.appendChild(usernameText);
					var scoreId = document.getElementById("score" + i);
					var scoreText = document.createTextNode(json[i].score);
					scoreId.appendChild(scoreText);
				}
				else {
					var usernameId = document.getElementById("username" + i);
					var usernameText = document.createTextNode("-");
					usernameId.appendChild(usernameText);
					var scoreId = document.getElementById("score" + i);
					var scoreText = document.createTextNode("-");
					scoreId.appendChild(scoreText);
				}
				}
	          break;
	        case 500:
	        default:
	    }
	  })
	 }
  
  	

 /**Show open games*/
	function showOpenGames() {
		console.log("show open games!"); 
		
	 /**The lobby assumes that the user already has logged in.
	    When a user is logged in, an accessToken can be found in the localStorage.
	    The accessToken is needed to access secured endpoints in the server.*/

		const jwt = localStorage.getItem("accessToken");
		
		 /**The url of the endpoint we use.*/
		const url ='/opengames';
		console.log("Token: " + jwt);
		
	 /**We build a request by providing the url we want to access
	    We also need to provide the method of the request
	    We put the Accept-Header with the value of 'application/json' indicating,
	    that we want the response to be in JSON-format. This probably is not needed. 
	    We set the Authoriatzion-Header and put the token into it, which is prefixed by 'Bearer'.*/

		var request = new Request(url, {
	      method: 'GET',
	      headers: new Headers({
	          'Accept': 'application/json',
	          'Authorization' : 'Bearer ' + jwt
	        })
	  	});
	  	
	  	/**Once we have built the request, we can send it to the server.
	     We use the fetch-API from JavaScript see. w3c.org
	   	 The fetch-function returns a Promise which is an object that promises something in the future.
	     We can configure the promise with a function, so the promise knows what to do, if it has completed
	     its task. We do that with the .then-function.*/

		fetch(request)
	    .then((response) => {
		
	      return new Promise((resolve) => response.json()
	      .then((json) => resolve({
	        status: response.status,
	        ok: response.ok,
	        json
	      })));
	    })
	    /**status contains the response code of the server (i.e. 200 for OK).
	    Json is the actual response data (in the body).
	    "ok" is just a flag - if everything is ok (from response.ok)*/
	    
	    .then(({ status, json, ok }) => {
	      const message = json;
	      
	    /**We switch to the response code and handle accordingly.
	    If the response code is 200, we have a list of open games in our json-data
	    and we will build bup the the HTML stuff with the buttons to select or join an
	    open game*/
	 
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

 /**Builds clickable buttons to join an openGame*/
	function buildHTMLOpenGames(games) {
	
		 /**Getting the HTML container, which shall contain the buttons*/
		const openGames = document.getElementById("openGames");
		
		/**We create at most 5 buttons to join a game.
		All the other opened games will not be visible*/
		for (let i = 0; i < games.length && i < 5; i++) {
			/**Create a new div-element*/
			const div = document.createElement("div");
			
			 /**We add some classes to the newly generated div-element.*/
			div.classList.add("btn");
			div.classList.add("button" + (i+1));  /**single creation of buttons - iteration of their className.*/ 
			div.addEventListener('click', function(){
				joinGame(games[i].id);
			},false);
			
			 /**We set an inner HTML to the div element by providing an 
			 html code using the back tricks to replace some data with variables.*/
			div.innerHTML = (`<span>RAUM ${i+1} <br> SPIEL BEITRETEN</span>`);  /**iteration of Button content.*/ 
			
			 /**finally we add the generated div element to the children of the container.*/
			openGames.appendChild(div);
		}
	}


 /**Start a new Game.*/
	function startGame() {
		console.log("starting game!");
		 /**The lobby assumes that the user already has logged in.
	    When a user is logged in, an accessToken can be found in the localStorage.
	    The accessToken is needed to access secured endpoints in the server.*/
	    
		const jwt = localStorage.getItem("accessToken");
		
	 /**The url of the endpoint we use.*/
		const url ='/games';
		console.log("Token: " + jwt);
		
	/** We build a request by providing the url we want to access
	    We also need to provide the method of the request
	    We put the Accept-Header with the value of 'application/json' indicating,
	    that we want the response to be in JSON-format. This probably is not needed. 
	    We set the Authoriatzion-Header and put the token into it, which is prefixed by 'Bearer'.*/
		var request = new Request(url, {
	      method: 'POST',
	      headers: new Headers({
	          'Content-Type': 'application/json',
	          'Authorization' : 'Bearer ' + jwt
	        })
	  	});
	  	
	  /**Once we have built the request, we can send it to the server.
	     We use the fetch-API from JavaScript see. w3c.org
	   	 The fetch-function returns a Promise which is an object that promises something in the future.
	     We can configure the promise with a function, so the promise knows what to do, if it has completed
	     its task. We do that with the .then-function.*/
		fetch(request)
	    .then((response) => {
	      return new Promise((resolve) => response.json()
	      .then((game) => resolve({
	        status: response.status,
	        ok: response.ok,
	        game
	      })));
	    })
	    
	  /**status contains the response code of the server (i.e. 200 for OK).
	    Json is the actual response data (in the body).
	    "ok" is just a flag - if everything is ok (from response.ok)*/
	    .then(({ status, game, ok }) => {
		
	  /**We switch to the response code and handle accordingly.*/
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


 /**Join a existing Game*/
	function joinGame(gameId) {

		 /**The lobby assumes that the user already has logged in.
	    When a user is logged in, an accessToken can be found in the localStorage.
	    The accessToken is needed to access secured endpoints in the server.*/
		const jwt = localStorage.getItem("accessToken");
		
		 /**The url of the endpoint we use.*/
		const url =`/games/${gameId}/join`;

	/** We build a request by providing the url we want to access
	    We also need to provide the method of the request
	    We put the Accept-Header with the value of 'application/json' indicating,
	    that we want the response to be in JSON-format. This probably is not needed. 
	    We set the Authoriatzion-Header and put the token into it, which is prefixed by 'Bearer'.*/
		var request = new Request(url, {
	      method: 'GET',
	      headers: new Headers({
	          'Content-Type': 'application/json',
	          'Authorization' : 'Bearer ' + jwt
	        })
	  	});

	  /**Once we have built the request, we can send it to the server.
	     We use the fetch-API from JavaScript see. w3c.org
	   	 The fetch-function returns a Promise which is an object that promises something in the future.
	     We can configure the promise with a function, so the promise knows what to do, if it has completed
	     its task. We do that with the .then-function.*/
	  	fetch(request).then(response => {
		
		  /**We switch to the response code and handle accordingly.*/
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
