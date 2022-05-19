/*

inspiration: 
https://dribbble.com/shots/2292415-Daily-UI-001-Day-001-Sign-Up



let form = document.querySelecter('form');

form.addEventListener('submit', (e) => {
  e.preventDefault();
  return false;
});

*/

var modal = document.getElementById('myModal');
var modalBtn = document.getElementById('menuBtn');
//var startGameBtn = document.getElementById('startGameBtn');

/* [0] because getElementsByClassName gives us an Array, and
we want to get the first element (even if theres only one element)*/
var closeBtn = document.getElementsByClassName('close')[0]; 

modalBtn.addEventListener('click', openModal);
closeBtn.addEventListener('click', closeModal);


function showOpenGames() {
	console.log("show open games!");
	const jwt = localStorage.getItem("accessToken");
	const url ='http://localhost:8080/opengames';
	console.log("Token: " + jwt);
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
      .then((json) => resolve({
        status: response.status,
        ok: response.ok,
        json
      })));
    })
    .then(({ status, json, ok }) => {
      const message = json;
      let color = 'black';
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

function buildHTMLOpenGames(games) {
	console.log("building open games");
	const openGames = document.getElementById("openGames");
	for (let i = 0; i < games.length && i < 5; i++) {
		const div = document.createElement("div");
		div.classList.add("btn");
		div.classList.add("button" + (i+1));
		div.innerHTML = (`<span input type="button" onclick="joinGame(${games[i].id})" >RAUM ${i+1} <br> SPIEL BEITRETEN</span>`);
		openGames.appendChild(div);
	}
}


function openModal(){
	//sets display from none (hidden) to block
	modal.style.display = 'block';
}

function closeModal(){
	//sets display from block to none
	modal.style.display = 'none';
}



function startGame() {
	console.log("starting game!");
	const jwt = localStorage.getItem("accessToken");
	const url ='http://localhost:8080/games';
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
	const url =`http://localhost:8080/games/${gameId}/join`;
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
