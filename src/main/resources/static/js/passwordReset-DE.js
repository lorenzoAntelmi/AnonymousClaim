
/** @author Deborah Vanzin und Lorenzo Antelmi*/ 

function passwordReset(email) {
  const data = {
      email: email,
  }

  //const url ='http://localhost:8080/authenticate';
  const url ='/account/password';
  
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
    
      switch (status) {
        case 400:
        case 401:
          console.log("Bad Request: " + JSON.stringify(message));
          const errorDiv = document.getElementById("Error");
          errorDiv.style.display = 'block';
          errorDiv.innerText = "Passwordreset fehlgeschlagen!";
          break;
        case 201:
        case 200:
            console.log("Bad Request: " + JSON.stringify(message));
          const successDiv = document.getElementById("resetSuccessfull");
          successDiv.style.display = 'block';
          successDiv.innerText = "Password gesendet!";
          break;
        case 500:
        default:
          console.log("Error 500: " + message);
    }
  })
}