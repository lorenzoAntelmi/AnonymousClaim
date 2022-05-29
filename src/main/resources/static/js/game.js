
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
        	console.log(ownPlayer);
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
				if( selectedCard && card.id === selectedCard.id){
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
          console.log("Bad Request: " + JSON.stringify(cards));
          break;
        case 201:
        case 200:
          currentGame= game;

       		let currentOwnPlayer;
       		let oppositePlayer;
       		if (ownPlayer.id === currentGame.playerA.id) {
					oppositePlayer = currentGame.playerB;
					currentOwnPlayer= currentGame.playerA;
			} else {
					oppositePlayer = currentGame.playerA;
					currentOwnPlayer= currentGame.playerB;
			}
				
			if(currentOwnPlayer.hand.length ===0){
				phase2started = true;
				
			}
			if(!phase2started){
				renderHandCards();
       			renderTopCard();
			}else{
				renderHandCardsPhase2();
			}
			
			if(currentGame.roundWinner !== null){	

				// dann identification welchen Player gegner ist
				
				// und dann gepsielte Karte von Gegner holen und ausgeben
					let oppositeCard = oppositePlayer.depositedCard[0]; 
				alert("Gegner hat die Karte: " + oppositeCard.fraction + " " + oppositeCard.value + " gespielt"+
				"\nRunde gewonnen von:" + currentGame.roundWinner);	
				}
       		
			console.log("--------------");
			console.log(currentOwnPlayer);
			console.log(currentOwnPlayer.hand.length ===0);

          break;
        case 500:
        default:
          console.log("Error 500: " + message);
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
          console.log("Bad Request: " + JSON.stringify(game));
          break;
        case 201:
        case 200:
          currentGame= game;
          renderHandCards();
       		renderTopCard();

       		let oppositePlayer;
       		let currentOwnPlayer;
       		// Wenn ein Round Winner gibt dann
       		if(currentGame.roundWinner !== null){

				// dann identification welchen Player gegner ist
				if (ownPlayer.id === currentGame.playerA.id) {
					oppositePlayer = currentGame.playerB;
					currentOwnPlayer= currentGame.playerA;
				} else {
					oppositePlayer = currentGame.playerA;
					currentOwnPlayer= currentGame.playerB;
				}
				// und dann gepsielte Karte von Gegner holen und ausgeben
					let oppositeCard = oppositePlayer.depositedCard[0]; 
				alert("Gegner hat die Karte: " + oppositeCard.fraction + " " + oppositeCard.value + " gespielt"+
				"\nRunde gewonnen von:" + currentGame.roundWinner);		
				
			}
			
			clearSelectedCardAndTopCard();
			
			if(currentOwnPlayer.hand.length ===0){
				phase2started = true;
			}

          break;
        case 500:
        default:
          console.log("Error 500: " + message);
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
          console.log("Bad Request: " + JSON.stringify(game));
          break;
        case 201:
        case 200:
          currentGame= game;
          renderHandCardsPhase2();
              
          	let oppositePlayer;

       		// Wenn ein Round Winner gibt dann
       		if(currentGame.roundWinner !== null){

				// dann identification welchen Player gegner ist
				if (ownPlayer.id === currentGame.playerA.id) {
					oppositePlayer = currentGame.playerB;
				} else {
					oppositePlayer = currentGame.playerA;
				}
				// und dann gepsielte Karte von Gegner holen und ausgeben
					let oppositeCard = oppositePlayer.depositedCardPhase2[0]; 
				alert("Gegner hat die Karte: " + oppositeCard.fraction + " " + oppositeCard.value + " gespielt"+
				"\nRunde gewonnen von: " + currentGame.roundWinner);	
				
				}
				
				clearSelectedCardAndTopCard();
				
       	// wenn Phasen Gewinner gibt dann gebe ihn aus
		if (currentGame.phaseWinner !== null) {
			alert("Game gewonnen von: " + currentGame.phaseWinner +
				"\nSpiel ist fertig, kehre bitte in der Lobby zurück!");
		}
		
          break;
        case 500:
        default:
          console.log("Error 500: " + message);
    }
  })
}

//phase2
function calcScoreAfterGame(){
	
	const jwt = localStorage.getItem("accessToken");
	const url ='/calcScore';

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
          console.log("Bad Request: " + JSON.stringify(game));
          break;
        case 201:
        case 200:
          currentGame= game;
          
          // Wenn ein Round Winner gibt dann
       		if(currentGame.phaseWinner !== null){				
				alert("Runde gewonnen von:" + currentGame.phaseWinner);	
			}

		
		if (currentGame.phaseWinner !== null) {
			alert("Game gewonnen von: " + currentGame.phaseWinner);
		}
		
          break;
        case 500:
        default:
          console.log("Error 500: " + message);
    }
  })
}

function playCard(){
	if (phase2started){
		phase2();
	} else {
		phase1();
	}
}

// interval setzung damit game sich refresht nach xxx Sekunden
let interval = undefined;
function getGameData(){
	getCurrentGame()
	interval = setInterval(function b(){
		getCurrentGame();
	}, 300000);

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




