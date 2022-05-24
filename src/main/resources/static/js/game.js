
/**Represents services for Game
	 * @author Rocco Saracino & Valentina Caldana
	 * Game initializing and getting images
	 */



/**This method gets the handCards from PlayerController
 */
function getHandCards(){
	const jwt = localStorage.getItem("accessToken");
	const url ='/getHand';
	console.log("Token: " + jwt);
	console.log("requesteing hands");
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
      .then((cards) => resolve({
        status: response.status,
        ok: response.ok,
        cards
      })));
    })
    .then(({ status, cards, ok }) => {
      switch (status) {
        case 400:
          console.log("Bad Request: " + JSON.stringify(cards));
          break;
        case 201:
        case 200:
          console.log("Game started!");
          console.log(cards);
          let div = document.getElementById("hand");
          
          /**An iterration through handCards 
          plus styling*/
          let i = 1;
          let y = 0;
          for(let card of cards){
			
			var child = document .createElement("div");
			child.classList.add('card')
			console.log("test");
			
			/**Styling*/
			child.style.backgroundImage= "url('/image/"+card.fraction +"_"+ card.value+".png')";		
			child.style.zIndex= i;
			child.style.left= y +"cm";
			div.appendChild(child);
			y = y + 2.5;
			i++;
		}
          break;
        case 500:
        default:
          console.log("Error 500: " + message);
    }
  })
}

