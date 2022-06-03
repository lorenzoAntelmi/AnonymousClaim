Claim – Duel for the Throne

Description
  •	The project "CLAIM Anonymous" contains the entire code to play the online card game "Claim - Duel for the Throne". 
  •	The aim of the game is to collect as many followers as possible from all fractions in order to be allowed to succeed the king.
  •	The online card game is a card game for exactly two people. The card set contains a total of 52 cards and distinguishes the following 5 "fractions",                  each of which represents a population group of the kingdom: 
    o	14 Goblins: Card values from 0-9 
    o	10 Dwarfs: Card values from 0-9
    o	10 Undeads: Card values from 0-9  
    o	10 Doubles: Card values from 0-9  
    o	8 Knights: Card values from 2-9  
  •	The players go through two game phases before the final winner can be determined. After each phase of the game, points are awarded fraction by fraction: the cards collected are counted per fraction and the player who was able to get hold of the higher number of cards of a fraction receives a point. The winner at the end of the game is the player who has collected the most points. 
  •	Further information on the exact rules of the game can be found in the game via the help button.

Features
Basic
  •	Client-/Server implementation
  •	Server implemented in Java 
  •	Client is browser-based (HTML / CSS / JavaScript) 
  •	The game runs in a standard browser for example Chrome or Safari
  •	Stateless communication (i.e., RESTful) implemented
  •	Fixed number of players supported (two) 
  •	Implementation game phase 1 
  •	Only legal moves are allowed
  •	After game phase1 the winner will be calculated, and a short message is displayed
  •	Game can end after the message about the winner: routing back to the Lobby
Additional 
o	General Features
  o	Implementation game phase 2 according to the rules of the game
  o	WebSocket tunnel for messaging
  o	Game with options: start a new game or join an ongoing game
  o	Cancel game and routing back tot he lobby; game is deleted from the database 
  o	Database in background  Storing plays, players, high score etc.
  o	Advanced design 
  o	Different languages (DE/EN) supported for all four websites (Login, Registration, Lobby, Game) 
  o	Responsiveness supported for all four websites (Login, Registration, Lobby, Game) with Chrome and minimum zoom of 80% with other browsers
  o	For security reasons a jwt-token-validation is implemented: jwt-token is generated when login and then this token is checked for validity with each request from the user
o	Login-Page
  o	Spam protection with Captcha
  o	Validation of the inputfields and corresponding error messages
  o	Passwords are stored hashed with SHA-256 
  o	Resetting password  User get a new password by e-mail
  o	Help button with the rules of the game
o	Registration-Page
  o	Validation of the inputfields and corresponding error messages
  o	Creating a new account
•	Lobby-Page
  o	The actual ranking of the 10 best players can be viewed every time when entering the Lobby
  o	Possibility to create and join a game 
  o	Logout 


How to Use the Project (User Guide)
  Before to start using this project you need
  •	MySQL Workbench  local instance must be available
  •	MySQL Installer-Community 
  •	Internet connection  If you want to play the game with someone else, make sure you are both in the same network with your clients
  •	Spring Tool Suite 4 
  •	Java Development Kit (JDK)
  •	Browser Chrome /Safari
  For installing the project and start the game follow these steps: 
    1.	Clone the Git Repository https://github.com/lorenzoAntelmi/ClaimAnonymous.git locally in a new spring tool suite workspace
    2.	Import the project from the Git repository into your local workspace 
    3.	Set your MySQL-Password in the file application.properties
    4.	Run the Spring Boot Java Application – Typ ClaimServerApplication
    5.	Enter http://localhost:8080/login-DE.html in your Browser and press the button “Registrieren” for creating a new account
    6.	To play the game, you have two options: 
        •	Option 1: You can start a new game (generate new game room) and then you have to wait until a second person with a separate client in the same  network has logged into this game room.
        •	Option 2: You can enter directly into a room that has already been opened and can start playing directly, provided that a second player is    present: ATTENTION: it is not possible to see from the lobby whether someone is already in the opened room. When you enter an opened room, you can find out by playing a first card. If someone is already there, you can play the card, otherwise you will receive a message when someone has joined the room and you can start the game.
