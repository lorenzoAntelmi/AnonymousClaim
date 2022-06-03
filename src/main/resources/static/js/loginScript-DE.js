
/** Modal @author Valentina Caldana*/ 
var modal = document.getElementById('loginModal');
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
			
/**@author Deborah Vanzin
* The form receives an EventListener to prevent the default behaviour,
* so that the form is not submitted via the standard HTML mechanism. 
*/

let form = document.querySelector('form');
let accessToken;

form.addEventListener('submit', (e) => {
  console.log("Preventing default!");
  e.preventDefault();
  return false;
});

/**This function sends email and password to the API to get an access token.*/
function login(email, password) {
  const data = {
      email: email,
      password: password,
  }

  const url ='/authenticate';
  
  console.log("Fetching from " + url);
  const json = JSON.stringify(data);
	console.log("Sending data to login: " + json);
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
    
      switch (status) {
        case 400:
        case 401:
          console.log("Bad Request: " + JSON.stringify(message));
          const errorDiv = document.getElementById("loginError");
          errorDiv.style.display = 'block';
          errorDiv.innerText = "Login fehlgeschlagen!";
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