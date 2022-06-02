
/**Represents services for Game
	 * @author Rocco Saracino & Valentina Caldana
	 * Game initializing and getting images*/

// Vaible damit der aktuellen Game geholt und gesetzt werden kann
var currentGame;

// Variable damit die Karte zum spielen genutzt werden kann
// gespielte Karte
var selectedCard;

// Variable damit herausfinden werden kann, welchen Player man ist
var ownPlayer;

// Variable damit die 2Phase gestartet werden kann
var phase2started=false;

// ---- Messaging ----- //

// Inspiration from: https://www.callicoder.com/spring-boot-websocket-chat-example/
var stompClient;

function connect(event) {
   
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
	console.log("connection successfull");
    // Subscribe to the Public Topic
    console.log(currentGame);
    stompClient
    	.subscribe('/topic/game/'+currentGame.id, onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser/"+currentGame.id,
        {},
        JSON.stringify({sender: ownPlayer.account.username, type: 'JOIN'})
    )
    
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

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


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    
    
    switch (message.type) {
	
	// OK
	case("JOIN"): 	if (message.sender !== ownPlayer.account.username) {
					alert("Gegenspieler gefunden "+ message.sender);
					}
					console.log("JOIN");
	break;
	
	// OK
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
					console.log("MOVE");
	break;
	
	// testen
	case("BADREQUEST"):
					if (message.sender !== ownPlayer.account.username) {
					alert("Lieber Spieler, falls du diesen Nachricht erhalten hast gibt es zwei Möglichkeiten" +
					"\n1. Du bist aktuell nicht dran" + 
					"\n2. Du hast eine invalide Karte gespielt");
					console.log("BADREQUEST");
					}
	break;
	
	// OK
	case("ROUNDwinner"):
					alert("Runden gewonnen von: "+ message.content);
					getCurrentGame();
					console.log("ROUNDwinner");
	break;
	
	case("LEAVE"):
					alert(message.sender + " hat das Spiel verlassen. Das Spiel wird gelöscht");
					window.location.href='lobby-DE.html';
	break;
	
	case("STARTPHASE2"):
					alert("Phase 2 wird initialisiert!");
	break;
	
	case("PHASEWinner"):
					alert("Game gewonnen von: " + message.content + 
					"\nDu kehrst in der Lobby zurück!");
	break;
	
	case("WAIT"):
					alert("Warte, dass ein Spieler sich einloggt!")
	break;
	}
}

// ---- Spiel-Logik ----- //

// Methode für PlayerA oder B holen
function getPlayer(){
	const jwt = localStorage.getItem("accessToken");
	const url ='/getPlayer';

	var request = new Request(url, {
      method: 'GET',
      headers: new Headers({
          'Content-Type': 'application/json',
          'Authorization' : 'Bearer ' + jwt
        })
  	});
  	
	fetch(request)
    .then((response) => {
	
      return new Promise((resolve) => response.json()
      .then((serverResponse) => resolve({
        status: response.status,
        ok: response.ok,
        serverResponse
      })));
    })
    .then(({ status, serverResponse}) => {
      switch (status) {   
        case 200:
          	ownPlayer= serverResponse;  	
        	break;
        default:
          console.log("Error" + serverResponse);
    }
  })
}

/**This method gets the handCards from Game
Game -> Player*/
function renderHandCards(){
	
	// finde heraus ob man player A ist oder player B
		let div = document.getElementById("hand");
          while (div.firstChild) {
  			div.removeChild(div.lastChild)
			}
          /**An iterration through handCards 
          plus styling*/
          let i = 1;
          let y = 0;
          let player ;
          
          // Wenn ownPlayer gleich ist Player A, dann player = playerA
          // sonst ist Player B
          if(ownPlayer.id === currentGame.playerA.id) {
			player = currentGame.playerA;
			} else {
				player = currentGame.playerB;
			}
          
          // wenn card die selektierte Karte aus Hand ist und sie gleich sind
          // dann überspringe else
          for(let card of player.hand){				
				if(selectedCard && card.id === selectedCard.id){
				continue;
				}
				
			var child = document.createElement("div");
			child.classList.add('card');
			
			/**Binding clicked card*/
			child.addEventListener('click',function(){
				binding(this);
				selectedCard = card;
			},false
			)
			 
			/**Styling*/
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

function renderTopCard(){
	
	let div = document.getElementById("revealedCard");
	
	// methode damit mit der Interval die Karten immer wieder gesetzt werden
	while (div.firstChild) {
  			div.removeChild(div.lastChild)
			}
	
	/**Get index of last card of List of cards (top card)*/
	let index = currentGame.cardDeck.cards.length-1 ;
	
	/**Get the topCard with index out of List of cards*/
	let topCard = currentGame.cardDeck.cards[index];
	
	if (topCard === undefined) {
		return; 
	}
	
	/**create a div and put the retrieved topCard into it*/
	let child = document.createElement("div");
	child.classList.add('topCard');
	
	/**set Image*/
	child.style.backgroundImage= "url('/image/"+topCard.fraction +"_"+ topCard.value+".png')";
	
	/**put this topCard into the revealedCard Container in View*/
	div.appendChild(child);
	
	/**Style*/
	child.style.backgroundSize="cover";
	child.style.backgroundPosition="center";
	child.style.backgroundRepeat="no-repeat";
	child.style.width="150px";
	child.style.height="230px";
}

/**This method gets the current Game
so we have every single related object or 
ArrayList of Game (e. g. Player and his lists)
@author Rocco Saracino*/
function getCurrentGame(){
	
	const jwt = localStorage.getItem("accessToken");
	const url ='/getCurrentGame';

	var request = new Request(url, {
      method: 'GET',
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
    .then(({ status, game}) => {
      switch (status) {
        case 400:
          	sendMessage(ownPlayer.account.username, "BADREQUEST", "");
          break;
        case 201:
        case 200:
          currentGame = game;
			// wenn der stompClient definiert ist
			// dann verbinde dich
			if(stompClient==undefined){
				connect();
			}
			
			let oppositePlayer;
       		let currentOwnPlayer;
			       		
			// dann identification welchen Player gegner ist
			if (ownPlayer.id === currentGame.playerA.id) {
				oppositePlayer = currentGame.playerB;
				currentOwnPlayer = currentGame.playerA;
			} else {
				oppositePlayer = currentGame.playerA;
				currentOwnPlayer= currentGame.playerB;
			}
			
//			if (oppositePlayer.account === null) {
//				sendMessage(currentOwnPlayer.account.username, "WAIT" , "");
//			}
          
          	if(currentOwnPlayer.hand.length === 0 && oppositePlayer.hand.length === 0
			&& currentOwnPlayer.cardsPhase2.length == 13 & oppositePlayer.cardsPhase2.length == 13){
				sendMessage(currentOwnPlayer.account.username, "STARTPHASE2", "");
				phase2started = true;
				// Stapler und CardDeck muss gelöscht werden
				document.getElementById("depositStackTitle").remove();
				document.getElementById("depositStack").remove();
				document.getElementById("deck").remove();
				document.getElementById("deckTitle").remove();
				document.getElementById("revealedCard").remove();
			}
			console.log(phase2started);
			
			if(!phase2started){
				renderHandCards();
				renderTopCard();
			}else{
				renderHandCardsPhase2();
			}
			
			// wenn Phasen Gewinner gibt dann gebe ihn aus
			if (currentGame.phaseWinner !== null) {
						sendMessage(currentOwnPlayer.account.username, "PHASEWinner", currentGame.phaseWinner);
					window.location.href='lobby-DE.html';
					//deleteGame ---> noch einbauen
			}
			
			if (deletedGame) {
          		window.location.href='lobby-DE.html';
			}
			break;
			case 500:
			default:
    }
  })	
}

//phase1
function phase1(){
	
	const jwt = localStorage.getItem("accessToken");
	const url ='/phase1/'+ selectedCard.id;

	var request = new Request(url, {
      method: 'GET',
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
    .then(({ status, game}) => {
      switch (status) {
        case 400:
	       sendMessage("", "BADREQUEST", "");
          break;
        case 201:
        case 200:
          currentGame= game;
          renderHandCards();
          renderTopCard();

       		let oppositePlayer;
       		let currentOwnPlayer;
			       		
			// dann identification welchen Player gegner ist
			if (ownPlayer.id === currentGame.playerA.id) {
				oppositePlayer = currentGame.playerB;
				currentOwnPlayer = currentGame.playerA;
			} else {
				oppositePlayer = currentGame.playerA;
				currentOwnPlayer= currentGame.playerB;
			}

			// message für den MOVE (Karte wird dem gegenspieler angezeigt)
			sendMessage(currentOwnPlayer.account.username, "MOVE" , selectedCard.fraction + "_" + selectedCard.value );
			
			
			// Wenn ein Round Winner gibt dann gebe ihn aus
       		if (currentGame.roundWinner !== null && stompClient !== null ) {
				sendMessage(currentOwnPlayer.account.username, "ROUNDwinner", currentGame.roundWinner);
			}
			
			clearSelectedCardAndTopCard();

          break;
        default: 
        sendMessage(ownPlayer.account.username, "WAIT", "");
    }
  })
}



//phase2
function phase2(){
	
	const jwt = localStorage.getItem("accessToken");
	const url ='/phase2/'+ selectedCard.id;

	var request = new Request(url, {
      method: 'GET',
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
    .then(({ status, game}) => {
      switch (status) {
        case 400:
          sendMessage("", "BADREQUEST", "");
          break;
        case 201:
        case 200:
          currentGame= game;
          renderHandCardsPhase2();
          
              	if (ownPlayer.id === currentGame.playerA.id) {
					currentOwnPlayer = currentGame.playerA;
				} else {
					currentOwnPlayer= currentGame.playerB;
				}
				
				// message für den MOVE (Karte wird dem gegenspieler angezeigt)
				sendMessage(currentOwnPlayer.account.username, "MOVE" , selectedCard.fraction + "_" + selectedCard.value );
				
				// Wenn ein Round Winner gibt dann
				if(currentGame.roundWinner !== null){
					sendMessage(currentOwnPlayer.account.username, "ROUNDwinner", currentGame.roundWinner);
				}
				// Karte top entfernen
				clearSelectedCardAndTopCard();
				
          break;
        case 500:
        default:
          console.log("Error 500: " + message);
    }
  })
}

// funktion für den Button Karte spielen
// überprüft immer ob der die 2Phase angefangen hat und 
// adressiert die entsprechenden Methoden
function playCard(){
	if (phase2started){
		phase2();
	} else {
		phase1();
	}
}

// leert selected card bzw displyed Card und top card nach Runde
function clearSelectedCardAndTopCard() {
	let div = document.getElementById("displayed-card");
          while (div.firstChild) {
  			div.removeChild(div.lastChild)
			}
}

// holt die Karte für die Phase2
function renderHandCardsPhase2(){
	
	// finde heraus ob man player A ist oder player B
		let div = document.getElementById("hand");
          while (div.firstChild) {
  			div.removeChild(div.lastChild)
			}
          /**An iterration through handCards 
          plus styling*/
          let i = 1;
          let y = 0;
          let player ;
          
          // Wenn ownPlayer gleich ist Player A, dann player = playerA
          // sonst ist Player B
          if(ownPlayer.id === currentGame.playerA.id) {
			player = currentGame.playerA;
			} else {
				player = currentGame.playerB;
			}
          
          // wenn card die selektierte Karte aus cardsforHand2 ist und sie gleich sind
          // dann überspringe else
          for(let card of player.cardsPhase2){
				if( selectedCard !== undefined && card.id === selectedCard.id){
				continue;
				}
			var child = document.createElement("div");
			
			child.classList.add('card');
			
			/**Binding clicked card*/
			child.addEventListener('click',function(){
				binding(this);
				selectedCard = card;
			},false
			)
			 
			/**Styling*/
			child.style.backgroundImage= "url('/image/"+card.fraction +"_"+ card.value+".png')";		
			child.style.zIndex= i;
			child.style.left= y +"cm";
			div.appendChild(child);
			y = y + 2.5;
			i++;
			
}}

// ----- Game Löschen ----- //
// Funktion für den Exitbutton
// Playern kehren in der Lobby zurück und das Game wird gelöscht
function deleteGame() {
	const jwt = localStorage.getItem("accessToken");
	const url ='/games/';

	var request = new Request(url, {
      method: 'DELETE',
      headers: new Headers({
          'Content-Type': 'application/json',
          'Authorization' : 'Bearer ' + jwt
        })
  	});
  	
	fetch(request)
    .then((response) => {
      switch (response.status) {
        case 200:
          			sendMessage(ownPlayer.account.username ,"LEAVE","");
          break;
        default:
    }
  })
}
