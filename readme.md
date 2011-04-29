Moteur de BDD : MySql
Nom de la base : COVAMI
User de la base : covami
Mdp du user covami : covami


Code de création de l'user + base :

CREATE USER 'COVAMI'@'localhost' IDENTIFIED BY  'covami';
GRANT USAGE ON * . * TO  'COVAMI'@'localhost' IDENTIFIED BY  '***' WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0 ;
CREATE DATABASE IF NOT EXISTS  `COVAMI` ;
GRANT ALL PRIVILEGES ON  `covami` . * TO  'covami'@'localhost';

Users de test de l'application :
Florian :
	fbezagu@fbezagu.com
	fbezagu
FG :
	fgribreau@fgribreau.com
	fgribreau

// Méthode la plus propre pour les models:
http://d.pr/xlFa

// A exécuter
INSERT INTO  `COVAMI`.`Member` (
`id` ,
`email` ,
`firstname` ,
`lastname` ,
`password` ,
`phone`
)
VALUES (
NULL ,  'fgribreau@fgribreau.com',  'Francois-Guillaume',  'Ribreau',  'fgribreau', NULL
), (
NULL ,  'fbezagu@fbezagu.com',  'Florian',  'Bezagu',  'fbezagu', NULL
);


Ancien code du formulaire

	<p id="email-field">
			<label for="email">&{'secure.email'}</label>
			<input type="email" name="member.email" id="email" placeholder="&{'secure.email'}" value="${member.email}" />
		</p>
		
		<p id="firstname-field">
			<label for="firstname">&{'secure.firstname'}</label>
			<input type="text" name="member.firstname" id="firstname" placeholder="&{'secure.firstname'}" value="${member.firstname}" />
		</p>
		
		<p id="lastname-field">
			<label for="lastname">&{'secure.lastname'}</label>
			<input type="text" name="member.lastname" id="lastname" placeholder="&{'secure.lastname'}" value="${member.lastname}" />
			<span class="error">#{error 'member.lastname' /}</span>
		</p>