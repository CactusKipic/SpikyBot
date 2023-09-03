# SpikyBot

Un super bot open-source développé par un joli Cactus (il pique mais il est quand même gentil, promis) !
SpikyBot est écrit entièrement en Java et s'appuie sur la bibliothèque [Javacord](https://javacord.org/) pour les interactions avec l'API [Discord](https://discord.com/). Les données du bot sont stockés à l'intérieur d'une base donnée [PostgreSQL](https://www.postgresql.org/). La communication entre la BDD et l'applicatif SpikyBot est réalisé avec l'ORM [Hibernate](https://hibernate.org/) et JPA via leur implémentation au sein du Framework [Spring Boot](https://spring.io/projects/spring-boot) sur lequel est construit l'applicatif du bot.

## Fonctionnalités

### Tickets
Les tickets permettent de mettre en place sur un salon un message posté par le bot, sur lequel les utilisateurs ayant les permissions suffisantes peuvent cliquer sur un bouton leur fournissant l'autorisation de poster des messages dans ce même salon pendant un temps donné.
Le message du bot étant entièrement personnalisable via les commandes du bot. Ce dernier est reposté par le bot après 15 secondes losqu'un autre message est posté dans le salon par un utilisateur.
#### Mise en place
Un ticket peut être ajouté à un salon grâce à la commande suivante :
`/ticket add <salon>`

*Un ticket avec un format générique apparaîtra dans le salon spécifié.*

Une fois ajouté, le temps de permission d'écriture donné à un utilisateur (30sec par défaut) peut être réglé avec la commande suivante :
`/ticket granttime <salon> <secondes>`

Un niveau de permission pour avoir l'accès au ticket peut être réglé avec la commande suivante :
`/ticket grantlevel <salon> <niveau>`

*Le niveau 0 étant considéré comme public, n'importe qui ayant accès au salon pourra prendre un ticket.*

Le niveau de permission pour l'accès au ticket peut être réglé par rôle avec la commande suivante :
`/ticket rolelevel <rôle> [niveau]`

*Le niveau attribué à un rôle est commun à tout le serveur. Ne pas préciser de niveau, supprime le niveau de permission associé au rôle.*

Le ticket d'un salon peut-être retiré avec la commande suivante :
`/ticket remove <salon>`

### Lecture de PDF
Sur des salons définis, lorsqu'un PDF est posté par un utilisateur, le bot ajoute un émoji 🤖 sur lequel les utilisateurs ayant les permissions suffisantes qui ajoutent à leur tour une réaction recevront par message privé le contenu textuel du PDF, lui évitant ainsi d'avoir à télécharger puis ouvrir le fichier sur son ordinateur ou téléphone.

#### Mise en place
La lecture de PDF peut être ajoutée à un salon grâce à la commande suivante :
`/pdfreading add <salon>`

*Tous les nouveaux PDF publiés se verront affublé d'une réaction 🤖 par SpikyBot.*

Pour ajouter les réactions sur les PDF déjà publié par le passé dans le salon, il est possible de demander au bot d'y ajouter une réaction avec la commande suivante :
`/pdfreading addreaction <salon> [nbr jour]`

*La réaction de SpikyBot n'étant ajouté uniquement pour facilité l'ajout d'une seconde réaction par l'utilisateur via un simple clic, ajouter une réaction 🤖 à un message contenant un PDF sans que ce dernier n'en ai une de la part de SpikyBot fonctionnera tout autant.*

Un niveau de permission pour avoir l'accès à la lecture de PDF peut être réglé avec la commande suivante :
`/pdfreading grantlevel <salon> <niveau>`

*Le niveau 0 étant considéré comme public, n'importe qui ayant accès au salon pourra prendre lire un PDF via le bot.*

Le niveau de permission pour l'accès à la lecture de PDF peut être réglé par rôle avec la commande suivante :
`/pdfreading rolelevel <rôle> [niveau]`

*Le niveau attribué à un rôle est commun à tout le serveur. Ne pas préciser de niveau, supprime le niveau de permission associé au rôle.*

La lecture de PDF sur un salon peut-être retiré avec la commande suivante :
`/ticket remove <salon>`

*Les réactions de SpikyBot sous les messages contenant un PDF ne seront pas retirée.*


