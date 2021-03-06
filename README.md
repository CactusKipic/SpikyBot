# SpikyBot

Un super bot open-source développé par un joli Cactus (il pique mais il est quand même gentil, promis) !

## Fonctionnalités
Pour le moment ce bot ne possède encore que peu de fonctionnalités fonctionnelle pour tout le monde.
À ce jour, le bot est capable d'ajouter un système de tickets sur des salons pour permettre aux utilisateurs de poster des messages dans ce salon pour une durée limitée, l'objectif étant d'empêcher les utilisateurs de parler dans ces salons afin de ne pas enterrer les messages postés.

Le ticket se compose d'un message posté par le bot sur le salon, le message pouvant être largement personnalisé pour chaque salon individuellement, le bot y ajoute une réaction à laquelle les utilisateurs peuvent réagir, il leur donnera alors la possibilité de poster des messages sur le salon pour une durée limitée après laquelle le bot retire la permission à l'utilisateur et reposte son message de ticket (en supprimant l'ancien) s'il n'est plus le dernier sur le salon.

Les messages sont facilement personnalisables avec une série de commandes permettant d'éditer leur contenu ainsi qu'apparence.

De plus un système de permission interne permet d'autoriser l'utilisation des tickets sur certains salons seulement en fonction du rôle des utilisateurs.

Le reste des fonctionnalités du bot sont des fonctionnalités réglée à la main depuis un fichier de configuration, ces fonctionnalités sont les suivantes :
- Envoie du contenu d'un PDF posté sur un salon en message privé avec la réaction à une réaction postée par le bot sur le message contenant le PDF
- Évènement Tisstober, un évènement inspiré du Inktober où chaque jour un thème est proposé, et les personnes le souhaitant peuvent poster une oeuvre sur ce thème, le bot y ajoute une réaction coeur pour permettre un pseudo vote (limité à une réaction par personne par jour, l'ancienne étant supprimé par le bot)
