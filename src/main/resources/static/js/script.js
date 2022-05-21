/** 
* @author Deborah Vanzin
* Das Formular erhält einen EventListener, um das Default-Verhalten zu unterbinden,
* damit das Formular nicht über den Standardmechanismus von HTML abgeschickt wird. 
*/


let form = document.querySelector('form');
let accessToken;

form.addEventListener('submit', (e) => {
  console.log("Preventing default!");
  e.preventDefault();
  return false;
});

//Diese Funktion schickt die Daten des Formulars über AJAX zur API
function register(username, email, password, birthDate) {

  const data = {
      username: username,
      password: password,
      email: email,
      birthDate: birthDate
  }
  const url ='/account';
  console.log("Fetching from " + url);
  const json = JSON.stringify(data);

  var request = new Request(url, {
      method: 'POST',
      body: json,
      headers: new Headers({
          'Content-Type': 'application/json',
        })
  }); 
 
  fetch(request).
    then((response) => {
    return new Promise((resolve) => response.json()
      .then((json) => resolve({
        status: response.status,
        ok: response.ok,
        json
      })));
  }).then(({ status, ok, json }) => {
    if(ok) {
      window.location.href='login-DE.html'
      return
    }
    const message = json;
    let color = 'black';
    switch (status) {
      case 400:
        console.log("Bad Request: " + JSON.stringify(message));
        break;
      case 201:
      case 200:
        console.log("Registration OK: " + message);
        break;
      case 500:
      default:
        console.log("Error 500: " + message);
    }
  })
}
//Diese Funktion schickt E-Mail und Password an die API um ein Access-Token zu bekommen
function login(email, password) {
  const data = {
      email: email,
      password: password,
  }

  //const url ='http://localhost:8080/authenticate';
  const url ='/authenticate';
  
  console.log("Fetching from " + url);
  const json = JSON.stringify(data);
	console.log("Senting data to login: " + json);
  var request = new Request(url, {
      method: 'POST',
      body: json,
      headers: new Headers({
          'Content-Type': 'application/json',
          
        })
  }); 

  fetch(request).
    then((response) => {
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
          console.log("JWT: " + JSON.stringify(message));
          accessToken = json.token;
          localStorage.setItem("accessToken", accessToken);
          window.location.href='lobby-DE.html';
          break;
        case 500:
        default:
          console.log("Error 500: " + message);
    }
  })
}
// Diese Funktion schickt einen GET-Request an die API um eine Liste der Spiele zu erhalten
function getGames() {

  const url ='/games';
  console.log("Fetching from " + url);

  var request = {
      method: 'GET',
      headers:{
          'Content-Type': 'application/json',
          "Authorization": "Bearer " + accessToken
        }
  }; 

  fetch(url, request).
    then((response) => {
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
          console.log("games: ", json);
          
          break;
        case 500:
        default:
          console.log("Error 500: " + message);
    }
  })
}

function abort() {
  console.log("Registrierung abgebrochen!");
}

