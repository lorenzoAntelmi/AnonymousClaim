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

/**@author Lorenzo Antelmi
Define captcha variables*/
const captcha = document.querySelector(".captcha"),
reloadBtn = document.querySelector(".reload-btn"),
captchaText = document.getElementById("captchaText"),
statusTxt = document.querySelector(".status-text");

/**Captcha symbols are stored in an array*/
let allCharacters = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                     'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
                     'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
                     't', 'u', 'v', 'w', 'x', 'y', 'z', 0, 1, 2, 3, 4, 5, 6, 7, 8, 9];
                     
 /**Captcha function generates random output and passes it to captchaText.innerText */                   
function getCaptcha(){
   
  for (let i = 0; i < 6; i++) { 	 /**getting 6 random characters from the array */ 
    let randomCharacter = allCharacters[Math.floor(Math.random() * allCharacters.length)];
    captchaText.innerText += ` ${randomCharacter}`; 
  }
}

/**Function call */ 
if (captchaText !== undefined) {
	getCaptcha(); 
}

/**Reload button first sets the content to zero and then generates a new content.*/ 
function reloadCaptcha(){
	/**calling getCaptcha & removeContent on the reload btn click*/ 
 captchaText.innerText = "";
  getCaptcha();
  
};
/**Captcha validation function Input is provided with blank characters as we transmit the character incl. blank characters above.*/ 
 function validateCaptcha(){
	  let inputVal = captchaField.value.split('').join(' ');
	  return inputVal === captchaText.innerText; 
}

/**This function sends the data of the form via AJAX to the API*/ 
function register(username, email, password, birthDate) {
if(!validateClientSide()){
    return;
}
	statusTxt.style.display ='none';

/**author Deborah Vanzin.*/
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
      window.location.href='login-EN.html'
      return
    }
    const message = json;
 
    switch (status) {
      case 400:
        console.log("Bad Request: " + JSON.stringify(message));
        showValidationErrors(message);
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

/**Clear possible previous text-messages (make them disappear)*/
function showValidationErrors(errors) {

	const divs = ["error-username", "error-birthdate", "error-email"
	, "error-confirmEmail", "error-password", "error-confirmPassword" ];
	for (let div of divs) {
		let node = document.getElementById(div);
		node.style.display = 'none';	
	}
	
	/**Display errors.*/
	for (let error in errors) {
		let node = document.getElementById("error-" + error);
		node.innerText = errors[error];
		node.style.display = 'block';
		node.style.fontColor ="red";
	}
}

/**validates the confirmation of password and email & validates
if inputs are empty*/
function validateClientSide() {
	/**Get all divs/inputs from html.*/
	
	// inputs
	let username = document.getElementById("username"); 
	let birthdate = document.getElementById("birthDate"); 
	let email = document.getElementById("email");
	let emailConfirm = document.getElementById("confirmEmail");
	let password = document.getElementById("password");
	let passwordConfirm = document.getElementById("confirmPassword");
	
	// div
	let errorUsername = document.getElementById("error-username");
	let errorBirthDate = document.getElementById("error-birthDate");
	let errorEmail = document.getElementById("error-email");
	let errorPassword = document.getElementById("error-password");
	let	emailConfirmError = document.getElementById("error-confirmEmail");
	let passwordConfirmError = document.getElementById("error-confirmPassword");
	
	
	emailConfirmError.style.display = 'none';
	passwordConfirmError.style.display = 'none';
	errorUsername.style.display = 'none';
	errorBirthDate.style.display = 'none';
	errorEmail.style.display = 'none';
	errorPassword.style.display = 'none';
	statusTxt.style.display ='none';
	
	
	let valid = true;
	
	/**Check username*/
	if (username.value == '') {
		errorUsername.innerText = "Username can't me empty!";
		errorUsername.style.display = 'block';
		valid = false;
	}
	
	/**Check birthdate*/
	if (birthdate.value == '') {
		errorBirthDate.innerText = "Birthdate can't me empty!";
		errorBirthDate.style.display = 'block';
		valid = false;
	}
	
	/**Check email*/
	if (email.value == '') {
		errorEmail.innerText = "E-Mail can't me empty!";
		errorEmail.style.display = 'block';
		valid = false;
	}
	
	/**Check password*/
	if (password.value == '') {
		errorPassword.innerText = "Password can't me empty!";
		errorPassword.style.display = 'block';
		valid = false;
	}
	
	/**Check email and confirmation*/
		if (email.value !== emailConfirm.value) {
		emailConfirmError.innerText = "The two e-mail addresses do not match!";
		emailConfirmError.style.display = 'block';
		valid = false;
	}
	
	/**Check password and confirmation*/
	if (password.value != passwordConfirm.value) {
		passwordConfirmError.innerText = "The two passwords do not match!";
		passwordConfirmError.style.display = 'block';
		valid = false;
	}
	
	/**Check captcha*/
	if(!validateCaptcha()){
		statusTxt.style.display ='block';
    	statusTxt.innerText = "Captcha does not match. Try again!";
    	valid = false;
    	}
	
	return valid;
}

/**This function sends a GET request to the API to get a list of games.*/
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
  window.location.href='login-EN.html';
}


			


