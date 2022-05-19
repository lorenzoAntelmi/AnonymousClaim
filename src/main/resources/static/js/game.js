
/**Represents services for Game
	 * @author Deborah Vanzin
	 * Game initializing and getting images
	 */


const CARD_TO_IMAGE_MAPPING = {
	
	DWARF: { 
	
		0: '/static/image/Zwerg_0.png', 
		1: '/static/image/Zwerg_1.png',
		2: '/static/image/Zwerg_2.png',
		3: '/static/image/Zwerg_3.png',
		4: '/static/image/Zwerg_4.png',
		5: '/static/image/Zwerg_5.png',
		6: '/static/image/Zwerg_6.png',
		7: '/static/image/Zwerg_7.png',
		8: '/static/image/Zwerg_8.png',
		9: '/static/image/Zwerg_9.png', } ,
	
	UNDEAD: { 
	
	0: '/static/image/Untot_0.png', 
	1: '/static/image/Untot_1.png', 
	2: '/static/image/Untot_2.png', 
	3: '/static/image/Untot_3.png', 
	4: '/static/image/Untot_4.png', 
	5: '/static/image/Untot_5.png', 
	6: '/static/image/Untot_6.png', 
	7: '/static/image/Untot_7.png', 
	8: '/static/image/Untot_8.png', 
	9: '/static/image/Untot_9.png', } ,
	
	DOPPELGANGER: { 
	
	0: '/static/image/Doppel_0.png', 
	1: '/static/image/Doppel_1.png', 
	2: '/static/image/Doppel_2.png', 
	3: '/static/image/Doppel_3.png', 
	4: '/static/image/Doppel_4.png', 
	5: '/static/image/Doppel_5.png', 
	6: '/static/image/Doppel_6.png', 
	7: '/static/image/Doppel_7.png', 
	8: '/static/image/Doppel_8.png', 
	9: '/static/image/Doppel_9.png',  } ,
	
	GOBLIN: { 
	
	0: 'Kobold_0.png', 
	1: 'Kobold_1.png', 
	2: 'Kobold_2.png', 
	3: 'Kobold_3.png', 
	4: 'Kobold_4.png', 
	5: 'Kobold_5.png', 
	6: 'Kobold_6.png', 
	7: 'Kobold_7.png', 
	8: 'Kobold_8.png', 
	9: 'Kobold_9.png', } ,
	
	KNIGHT: { 
	
	2: '/static/image/Ritter_2.png',
	3: '/static/image/Ritter_3.png',
	4: '/static/image/Ritter_4.png',
	5: '/static/image/Ritter_5.png',
	6: '/static/image/Ritter_6.png',
	7: '/static/image/Ritter_7.png',
	8: '/static/image/Ritter_8.png',
	9: '/static/image/Ritter_9.png', } 

}

function initializeGame() 
{
	console.log("Initializing Game Cards!");
	// Fetch listOfMyCards of player from API
	// List of cards
	// let listOfMyCards = fetchFromServer();
	const div = document.getElementById("hand");
	for (let i = 0; i < div.children.length; i++) {
		// let currentCard = listOfMyCards.get(i);
		let cardDiv = document.getElementById("card" + i);
		
		let value = Math.floor(Math.random() * 10);
		let fraktion = "GOBLIN";
		// let value = currentCard.value;
		// let fraktion = currentCard.fraktion;
		
		const image = CARD_TO_IMAGE_MAPPING[fraktion][value];
		cardDiv.style.backgroundImage = `url(/image/${image})`;
	console.log("Set Background image of Card " + i + " to " + image);
		
	}
}