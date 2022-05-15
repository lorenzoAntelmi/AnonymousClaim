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

/* [0] because getElementsByClassName gives us an Array, and
we want to get the first element (even if theres only one element)*/
var closeBtn = document.getElementsByClassName('close')[0]; 

modalBtn.addEventListener('click', openModal);
closeBtn.addEventListener('click', closeModal);

function openModal(){
	//sets display from none (hidden) to block
	modal.style.display = 'block';
}

function closeModal(){
	//sets display from block to none
	modal.style.display = 'none';
}