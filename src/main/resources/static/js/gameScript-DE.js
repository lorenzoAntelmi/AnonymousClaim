/**@author Valentina Caldana*/ 
/**Modal*/ 
var modal = document.getElementById('gameModal');
var modalBtn = document.getElementById('help');
var closeBtn = document.getElementsByClassName('close')[0];

modalBtn.addEventListener('click', openModal);
closeBtn.addEventListener('click', closeModal);

		function openModal() {
			  modal.style.display = 'block';
			}
			
		function closeModal() {
			  modal.style.display = 'none';
			}
			
			
/**@author Rocco Saracino
   source: https://www.callicoder.com/spring-boot-websocket-chat-example/*/

/**to get and set the current game*/
var currentGame;

/**represents a card which shall be played by user, is the displayed card binded*/
var selectedCard;

/**to find out which player is the current player*/
var ownPlayer;

/**to start the second phase - will be set true*/
var phase2started=false;

/**to enable/show the corresponding message - will be set true*/
var messageDisplayed=false;

/**represents the user who is currently receiving a message*/
var stompClient;

var endedGame = false;

// ---- MESSAGING ----- //


/**Get the Web-Socket endpoint and connect it*/
function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

/**enable Connection*/
function onConnected() {
    
    // Subscribe to the Public Topic
    stompClient
    	.subscribe('/topic/game/'+currentGame.id, onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser/"+currentGame.id,
        {},
        JSON.stringify({sender: ownPlayer.account.username, type: 'JOIN'})
    )
    
}

/**Message when Web-Socket connection failed*/
function onError() {
    connectingElement.textContent = 'Konnte keine Verbindung zum WebSocket erstellen. Bitte aktualisiere die Seite und versuche es erneut!';
    connectingElement.style.color = 'red';
}

/**To send a message*/
function sendMessage(sender, type, content) {
	
    if(stompClient) {
        var chatMessage = {
            sender: sender,
            content: content,
            type: type
        };
        stompClient.send("/topic/game/"+currentGame.id, {}, JSON.stringify(chatMessage));
    }
}

/**To receive the correct message type*/
function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    
    /**Switch through message types*/
    switch (message.type) {
	
	/**Found opponent when second player joins*/
	case("JOIN"): 	if (message.sender !== ownPlayer.account.username) {
					alert("Gegenspieler gefunden: "+ message.sender);
					}
	break;
	
	/**Same message in case other player is current player*/
	case("CURRENT_PLAYER"): 
							let p =	document.getElementById("oppositePlayerInfo");
							p.textContent = '"'+ message.content + '" spielt gerade!';
							
							/**Styling*/
							p.style.fontWeight = 'bold';
							p.style.fontStyle = 'italic';
							p.style.color = '#c0392b';
							p.style.fontSize = '30px';
							p.style.position = 'absolute';
							p.style.top = '58%';
							p.style.left = '9%';	
	break;
	
	/**Gets and shows the opponentsCard in View*/
	case("MOVE"): 	
					if (message.sender !== ownPlayer.account.username) {
						
					let div = document.getElementById("opponentsCard");
					/**Styling*/
					div.style.backgroundColor= null;
					div.style.backgroundImage= "url('/image/"+message.content+".png')";
					div.style.backgroundSize="cover";
					div.style.backgroundPosition="center";
					div.style.backgroundRepeat="no-repeat";
					div.style.width="220px";
					div.style.height="340px";
					
					}
	break;
	
	/**Message handling illegal moves and wrong turn*/
	case("BADREQUEST"):
					if (message.sender === ownPlayer.account.username) {
					alert("Lieber Spieler, falls du diese Nachricht erhalten hast, gibt es zwei Möglichkeiten:" +
					"\n1. Du bist aktuell nicht dran" + 
					"\n2. Du hast eine invalide Karte gespielt, sieh dir die Regeln im Help-Button an.");
					}
	break;
	
	/**Message telling the round winner*/
	case("ROUNDwinner"):
					alert("Runde gewonnen von: "+ message.content);
					getCurrentGame();
					
					if (message.sender === ownPlayer.account.username) {
					clearOpponentsCard();
					}
	break;
	
	/**Message handling when the opponent leaves the game*/
	case("LEAVE"):
					if (endedGame) {
						alert(message.sender + "Spiel beendet, du kehrst in der Lobby zurück.");
						window.location.href='lobby-DE.html';
					} else {
						alert(message.sender + "hat das Spiel verlassen. Das Spiel wird gelöscht.");
						window.location.href='lobby-DE.html';
					}
	break;
	
	/**Message when starting phase 2*/
	case("STARTPHASE2"):
					alert("Phase 2 wird initialisiert!");
	break;
	
	/**Message telling the phase winner*/
	case("GAMEWinner"):
					alert("Spiel gewonnen von: " + message.content);
					deleteGame();
	break;
	
	/**Message for player to wait for an opponent*/
	case("WAIT"):
					alert("Warte, dass ein Spieler sich einloggt!")
	break;
	}
}

// ---- Spiel-Logik ----- //

/**To get Player A or Player B */
function getPlayer(){
	
	/**The gameSurface assumes that the user already has logged in.
	    When a user is logged in, an accessToken can be found in the localStorage.
	    The accessToken is needed to access secured endpoints in the server.*/
	    
	const jwt = localStorage.getItem("accessToken");
	
	/**The url of the endpoint we use.*/
	const url ='/getPlayer';

 
   /**We build a request by providing the url we want to access
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
	fetch(request)
    .then((response) => {
	
      return new Promise((resolve) => response.json()
      .then((serverResponse) => resolve({
        status: response.status,
        ok: response.ok,
        serverResponse
      })));
    })
   
   	 /**status contains the response code of the server (i.e. 200).
	    serverResponse is the actual response data (in the body).*/
    .then(({ status, serverResponse}) => {

     /**We switch to the response code and handle accordingly.
	    If the response code is 200, the current player should be the serverResponse */
      switch (status) {   
        case 200:
          	ownPlayer= serverResponse;  	
        	break;
        default:
          console.log("Error" + serverResponse);
    }
  })
}

/**@author Valentina Caldana*/

/**Gets the handCards from Game and consequently from a player*/
function renderHandCards(){
	
/**Get div for hand-area from HTML */
		let div = document.getElementById("hand");

	/**empty the div, before filling it with new content
	otherwise there will be multiple cards in one single card-div */
          while (div.firstChild) {
  			div.removeChild(div.lastChild)
			}
       
          /**if current player is = Player A, then player = Player A
          	else Player B*/
          let player ;
          
          if(ownPlayer.id === currentGame.playerA.id) {
			player = currentGame.playerA;
			} else {
				player = currentGame.playerB;
			}
			
		/**For an iterration through handCards 
          plus styling*/
          let i = 1;
          let y = 0;		 
          
		/**iteration through handCards*/
          for(let card of player.hand){	
	
			/**If the displayed card (meaning its value + fraction) and the hand card ID 
			(of corresponding hand card, which was clicked by user to put it into display area)
			is = displayed card ID*/			
				if(selectedCard && card.id === selectedCard.id){
				continue; 		/**skip everything because binding works - there is no else-part*/
				}
			
			/**Create a card-div in hand*/	
			var child = document.createElement("div");
			child.classList.add('card');
			
			/**Binding clicked card*/
			child.addEventListener('click',function(){
				binding(this);
				selectedCard = card;
			},false
			)
			 
			/**Styling the handCards to make sure it sticks to original css*/
			child.style.backgroundImage= "url('/image/"+card.fraction +"_"+ card.value+".png')";		
			child.style.zIndex= i;
			child.style.left= y +"cm";
			div.appendChild(child);
			y = y + 2.5;
			i++;
			
}}

/**binding hand with displayed-card*/
function binding(cardDiv){
	
	var newDisplay = document.getElementById("displayed-card");
	
	/**if div is not empty, put handcard back*/
	if(newDisplay.getElementsByTagName("div").length>0){
		
		/**oldChild = card which was in newDisplay
		and has to be replaced now with another 
		clicked handCard */
		oldChild = newDisplay.firstElementChild;
		
		let div = document.getElementById("hand");
		/**Put oldChild back to hand and adjust style */
		div.appendChild(oldChild);
		oldChild.style.width="150px";
		oldChild.style.height="230px";
	}
			
	/**Put clicked handCard into display and adjust style*/
	newDisplay.appendChild(cardDiv);
	cardDiv.style.backgroundSize="cover";
	cardDiv.style.backgroundPosition="center";
	cardDiv.style.backgroundRepeat="no-repeat";
	cardDiv.style.height="340px";
	cardDiv.style.width="220px";
	
}

/**Get the top card of deck*/
function renderTopCard(){
	
	let div = document.getElementById("revealedCard");
	
	/**empty the div, before filling it with new content
	otherwise there will be multiple cards in one single card-div */
	while (div.firstChild) {
  			div.removeChild(div.lastChild)
			}
	
	/**Get index of last card of List of cards (top card)*/
	let index = currentGame.cardDeck.cards.length-1 ;
	
	/**Get the topCard with index out of List of cards*/
	let topCard = currentGame.cardDeck.cards[index];
	
	/**if topCard object is null then skip everything, go out of method*/
	if (topCard === undefined) {
		return; 
	}
	
	/**create a div and put the retrieved topCard into it*/
	let child = document.createElement("div");
	child.classList.add('topCard');
	
	/**set Image*/
	child.style.backgroundImage= "url('/image/"+topCard.fraction +"_"+ topCard.value+".png')";
	
	/**put this child (which contains the image of topCard) into the revealedCard Container in View*/
	div.appendChild(child);
	
	/**Style*/
	child.style.backgroundSize="cover";
	child.style.backgroundPosition="center";
	child.style.backgroundRepeat="no-repeat";
	child.style.width="150px";
	child.style.height="230px";
}

/**@author Rocco Saracino*/
/**Gets the current Game
so we have every single related object or 
ArrayList of Game (e. g. Player and his lists)*/
function getCurrentGame(){

 /**The gameSurface assumes that the user already has logged in.
	    When a user is logged in, an accessToken can be found in the localStorage.
	    The accessToken is needed to access secured endpoints in the server.*/
	const jwt = localStorage.getItem("accessToken");
	
 /**The url of the endpoint we use.*/
	const url ='/getCurrentGame';

   /**We build a request by providing the url we want to access
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
	fetch(request)
    .then((response) => {
	
      return new Promise((resolve) => response.json()
      .then((game) => resolve({
        status: response.status,
        ok: response.ok,
        game
      })));
    })
    
   /**status contains the response code of the server (i.e. 200).
	game is the actual response data (in the body).*/
    .then(({status, game}) => {
	
	/**We switch to the response code and handle accordingly.
	   If the response code is 200, we get a game. */
      switch (status) {
        case 400:
          	sendMessage(ownPlayer.account.username, "BADREQUEST", "");
          break;
        case 201:
        case 200:
          currentGame = game;
          
			/**If the stompClient is undefined, there will be a connection
			 to the Web-Socket*/
			if(stompClient==undefined){
				connect();
			}

			/**If the stompClient is defined, identify which Player is the opponent*/
			let oppositePlayer;
       		let currentOwnPlayer;
			       		
			if (ownPlayer.id === currentGame.playerA.id) {
				oppositePlayer = currentGame.playerB;
				currentOwnPlayer = currentGame.playerA;
			} else {
				oppositePlayer = currentGame.playerA;
				currentOwnPlayer= currentGame.playerB;
			}
			
			/**If there is no message and no opponent, then send a corresponding alert*/			
			if(messageDisplayed==false && oppositePlayer.account === null) {
				alert("Warte auf einen Gegner");
				messageDisplayed=true;
			}

			/**This "if" is needed for the beginning of a game, when a player is alone in 
			a room and waiting for an opponent. As soon as the status is "CONNECTED"...*/	
			if(currentGame.currentPlayer!==null && stompClient.status === "CONNECTED"){
				
			/**Message tells who entered the room (which user joined)*/
			sendMessage(currentOwnPlayer.account.username, "CURRENT_PLAYER", currentGame.currentPlayer.account.username);
			
			/**The else part refers to the following rounds (we assume it is connected)*/	
			} else if (currentGame.currentPlayer!==null){
				
			/**Text appears, which tells who's turn it is, meaning it shows the current player*/
			let p =	document.getElementById("oppositePlayerInfo");
				p.textContent = '"'+ game.currentPlayer.account.username + '" spielt gerade!';
				
				/**Styling*/
				p.style.fontWeight = 'bold';
				p.style.color = '#c0392b';
				p.style.fontStyle = 'italic';
				p.style.fontSize = '30px';
				p.style.position = 'absolute';
				p.style.top = '58%';
				p.style.left = '9%';
			}
          
          /**If the current player has no more cards in his hand and his opponent too
          and if the current player and his opponent have hand cards for the second phase*/
          	if(currentOwnPlayer.hand.length === 0 && oppositePlayer.hand.length === 0
			&& currentOwnPlayer.cardsPhase2.length == 13 & oppositePlayer.cardsPhase2.length == 13){

			/**send message that second phase is starting*/				
				sendMessage(currentOwnPlayer.account.username, "STARTPHASE2", "");
				phase2started = true;
				
				/**remove all irrelevant divs like e. g. cardDeck */	
				document.getElementById("depositStackTitle").remove();
				document.getElementById("depositStack").remove();
				document.getElementById("deck").remove();
				document.getElementById("deckTitle").remove();
				document.getElementById("revealedCard").remove();
			}

			/**But if second phase has not started (variable = false)
			go on and render hand cards and top card for first phase*/
			if(!phase2started){
				renderHandCards();
				renderTopCard();
			}else{
			/**otherwise start to render hand cards for second phase*/
				renderHandCardsPhase2();
			}
			
			/**if there is a game winner, send the corresponding message and
			relocate to lobby */
			if (currentGame.phaseWinner !== null) {
					endedGame = true;
					sendMessage(currentOwnPlayer.account.username, "GAMEWinner", currentGame.phaseWinner);
			}
			
			break;
			case 500:
			default:
    }
  })	
}

/**Defines phase 1 (gets a game, renders top card and hand cards, etc.)*/
function phase1(){
	
	/**The gameSurface assumes that the user already has logged in.
	 When a user is logged in, an accessToken can be found in the localStorage.
	 The accessToken is needed to access secured endpoints in the server.*/
	const jwt = localStorage.getItem("accessToken");
	
	/**The url of the endpoint we use + selected card ID.*/
	const url ='/phase1/'+ selectedCard.id;

 
   /**We build a request by providing the url we want to access
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
	fetch(request)
    .then((response) => {
	
      return new Promise((resolve) => response.json()
      .then((game) => resolve({
        status: response.status,
        ok: response.ok,
        game
      })));
    })
    
    /**status contains the response code of the server (i.e. 200).
	   game is the actual response data (in the body).*/
    .then(({ status, game}) => {
      switch (status) {
        case 400:
	       sendMessage(ownPlayer.account.username, "BADREQUEST", "");
          break;
        case 201:
        case 200:
          currentGame= game;
          renderHandCards();
          renderTopCard();

	/**Identify which Player is the current player*/

       		let currentOwnPlayer;
			       		
			if (ownPlayer.id === currentGame.playerA.id) {
				currentOwnPlayer = currentGame.playerA;
			} else {
				currentOwnPlayer= currentGame.playerB;
			}			

			/**Message to show the played card of the opponent*/
			sendMessage(currentOwnPlayer.account.username, "MOVE" , selectedCard.fraction + "_" + selectedCard.value );
			
			/**Message to switch the current player*/
			sendMessage(currentOwnPlayer.account.username, "CURRENT_PLAYER", currentGame.currentPlayer.account.username);
	
			/**If there is a round winner send the corresponding message*/
       		if (currentGame.roundWinner !== null && stompClient !== null ) {
				sendMessage(currentOwnPlayer.account.username, "ROUNDwinner", currentGame.roundWinner);
			}
			
			/**clears selectedCard and top card after every round*/
			clearSelectedCardAndTopCard();
			console.log("opponentscard funktioniert");
			/**FIXME: same with opponent card*/
          break;
        default: 
 	/**Message if there is no opponent the player has to wait for one*/       
        sendMessage(ownPlayer.account.username, "WAIT", "");
    }
  })
}



/**Defines phase 2 */
function phase2(){

	/**The gameSurface assumes that the user already has logged in.
	 When a user is logged in, an accessToken can be found in the localStorage.
	 The accessToken is needed to access secured endpoints in the server.*/	
	const jwt = localStorage.getItem("accessToken");
	
	/**The url of the endpoint we use + selected card ID.*/
	const url ='/phase2/'+ selectedCard.id;

 	/**We build a request by providing the url we want to access
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
	fetch(request)
    .then((response) => {
	
      return new Promise((resolve) => response.json()
      .then((game) => resolve({
        status: response.status,
        ok: response.ok,
        game
      })));
    })
    
     /**status contains the response code of the server (i.e. 200).
	   game is the actual response data (in the body).*/
    .then(({ status, game}) => {
      switch (status) {
        case 400:
          sendMessage(ownPlayer.account.username, "BADREQUEST", "");
          break;
        case 201:
        case 200:
          currentGame= game;
          renderHandCardsPhase2();
          
          	/**Identify which Player is the current player*/
              	if (ownPlayer.id === currentGame.playerA.id) {
					currentOwnPlayer = currentGame.playerA;
				} else {
					currentOwnPlayer= currentGame.playerB;
				}
				
				/**Message to show the played card of the opponent*/
				sendMessage(currentOwnPlayer.account.username, "MOVE" , selectedCard.fraction + "_" + selectedCard.value );
				
				/**Message to switch the current player*/
				sendMessage(currentOwnPlayer.account.username, "CURRENT_PLAYER", currentGame.currentPlayer.account.username);
				
				/**If there is a round winner send the corresponding message*/
				if(currentGame.roundWinner !== null){
					sendMessage(currentOwnPlayer.account.username, "ROUNDwinner", currentGame.roundWinner);
				}
				/**clears selectedCard after every round*/
				clearSelectedCardAndTopCard();
				
          break;
        case 500:
        default:
          console.log("Error 500: " + message);
    }
  })
}

/**Method for "play card" button, which
checks if the second phase has been initialized
and addresses to the corresponding phase-method (1 or 2)*/
function playCard(){
	if (phase2started){
		phase2();
	} else {
		phase1();
	}
}

/**clears selectedCard and top card after every round*/
function clearSelectedCardAndTopCard() {
	let div = document.getElementById("displayed-card");
          while (div.firstChild) {
  			div.removeChild(div.lastChild)
			}			
}

/**clears opponentCard after every round*/
function clearOpponentsCard() {
	let div = document.getElementById("opponentsCard");
	
	div.style.backgroundImage= null;
	div.style.backgroundColor= "grey";	
	div.style.backgroundSize="cover";
	div.style.backgroundPosition="center";
	div.style.backgroundRepeat="no-repeat";
	div.style.width="220px";
	div.style.height="340px";		
}


/**@author Valentina Caldana*/

/**Gets the handCards for phase 2 from Game and consequently from a player*/
function renderHandCardsPhase2(){
	
	/**Get div for hand-area from HTML */
		let div = document.getElementById("hand");
		
		/**empty the div, before filling it with new content
		otherwise there will be multiple cards in one single card-div */
          while (div.firstChild) {
  			div.removeChild(div.lastChild)
			}
		
		/**if current player is = Player A, then player = Player A
          	else Player B*/
          		
          let player ;
          
          if(ownPlayer.id === currentGame.playerA.id) {
			player = currentGame.playerA;
			} else {
				player = currentGame.playerB;
			}
        
         /**An iterration through handCards 
          plus styling*/
          let i = 1;
          let y = 0;  
          
		/**iteration through handCards*/
          for(let card of player.cardsPhase2){
	
			/**If the displayed card (meaning its value + fraction) and the hand card ID 
			(of corresponding hand card, which was clicked by user to put it into display area)
			is = displayed card ID*/
				if( selectedCard !== undefined && card.id === selectedCard.id){
				continue; /**skip everything because binding works - there is no else-part*/
				}
				
			/**Create a card-div in hand*/	
			var child = document.createElement("div");
			child.classList.add('card');
			
			/**Binding clicked card*/
			child.addEventListener('click',function(){
				binding(this);
				selectedCard = card;
			},false
			)
			 
			/**Styling the handCards to make sure it sticks to original css*/
			child.style.backgroundImage= "url('/image/"+card.fraction +"_"+ card.value+".png')";		
			child.style.zIndex= i;
			child.style.left= y +"cm";
			div.appendChild(child);
			y = y + 2.5;
			i++;
			
}}

/**Method for exit Button to get to Lobby and delete the current game*/
function deleteGame() {
	
	/**The gameSurface assumes that the user already has logged in.
	 When a user is logged in, an accessToken can be found in the localStorage.
	 The accessToken is needed to access secured endpoints in the server.*/	
	const jwt = localStorage.getItem("accessToken");
	
	/**The url of the endpoint we use*/
	const url ='/games/';

	/**We build a request by providing the url we want to access
	    We also need to provide the method of the request
	    We put the Accept-Header with the value of 'application/json' indicating,
	    that we want the response to be in JSON-format. This probably is not needed. 
	    We set the Authoriatzion-Header and put the token into it, which is prefixed by 'Bearer'.*/
	var request = new Request(url, {
      method: 'DELETE',
      headers: new Headers({
          'Content-Type': 'application/json',
          'Authorization' : 'Bearer ' + jwt
        })
  	});
  	
  	/**Once we have built the request, we can send it to the server.
	   We use the fetch-API from JavaScript see. w3c.org*/
	fetch(request)
	
	/**status contains the response code of the server (i.e. 200).*/
    .then((response) => {
      switch (response.status) {
        case 200:   		
          			sendMessage(ownPlayer.account.username ,"LEAVE","");
          break;
        default:
    }
  })
}
