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

/**author Deborah Vanzin*/
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

/**Clear possible previous text-messages*/
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

/**validates the confirmation of password and email*/
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
	
	/**validates the confirmation of password and email*/
	let valid = true;
	
	// check username
	if (username.value == '') {
		errorUsername.innerText = "Username darf nicht leer sein";
		errorUsername.style.display = 'block';
		valid = false;
	}
	
	// check birthdate empty
	if (birthdate.value == '') {
		errorBirthDate.innerText = "Geburtsdatum darf nicht leer sein";
		errorBirthDate.style.display = 'block';
		valid = false;
	}
	
//	// check if right birthdate
//	if (!isValidDate(birthdate)) {
//		errorBirthDate.innerText = "Geburtsdatum ist nicht korrekt";
//		errorBirthDate.style.display = 'block';
//		valid = false;
//	}
	
	// check mail
	if (email.value == '') {
		errorEmail.innerText = "Email darf nicht leer sein";
		errorEmail.style.display = 'block';
		valid = false;
	}
	
	// check password
	if (password.value == '') {
		errorPassword.innerText = "Password darf nicht leer sein";
		errorPassword.style.display = 'block';
		valid = false;
	}
	
	//check email1 and email2
		if (email.value !== emailConfirm.value) {
		emailConfirmError.innerText = "Die beiden E-Mailadressen stimmen nicht überein!";
		emailConfirmError.style.display = 'block';
		valid = false;
	}
	
	//check passwor1 and password2
	if (password.value != passwordConfirm.value) {
		passwordConfirmError.innerText = "Die beiden Passwörter stimmen nicht überein!";
		passwordConfirmError.style.display = 'block';
		valid = false;
	}
	
	//check Captcha
	if(!validateCaptcha()){
		statusTxt.style.display ='block';
    	statusTxt.innerText = "Captcha stimmt nicht überein. Versuche es erneut!";
    	valid = false;
    	}
	return valid;
}

/**Source: https://stackoverflow.com/questions/6177975/how-to-validate-date-with-format-mm-dd-yyyy-in-javascript */
// 1:1 kopiert
// Validates that the input string is a valid date formatted as "mm/dd/yyyy"
function isValidDate(dateString) {
    // First check for the pattern
    if(!/^\d{1,2}\/\d{1,2}\/\d{4}$/.test(dateString))
        return false;

    // Parse the date parts to integers
    var parts = dateString.split("/");
    var day = parseInt(parts[1], 10);
    var month = parseInt(parts[0], 10);
    var year = parseInt(parts[2], 10);

    // Check the ranges of month and year
    if(year < 1000 || year > 3000 || month == 0 || month > 12)
        return false;

	var monthLength = [ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 ];

    // Adjust for leap years
    if(year % 400 == 0 || (year % 100 != 0 && year % 4 == 0))
        monthLength[1] = 29;

    // Check the range of the day
    return day > 0 && day <= monthLength[month - 1];
};

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
  window.location.href='login-DE.html';
}


			


