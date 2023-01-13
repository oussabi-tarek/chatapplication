pour executer ce projet:

executer en premier le server class(package: cahtudpserverapplication) pour que le server attende les requetes des clients
l interface graphique du server s affiche:

![MyServer 1_13_2023 1_36_32 AM](https://user-images.githubusercontent.com/104314459/212221113-5255be71-e29d-4311-b70b-b8c79163117d.png)

ensuite executer le ClientRegister(package: chatclientudpapplication) class plusieurs fois pour l authentification des clients

![Screenshot 1_13_2023 1_39_20 AM](https://user-images.githubusercontent.com/104314459/212221265-3a4fd82d-4c60-46e8-a08c-5e984d0f2fb0.png)

-> entrer votre nom et votre mot de passe et cliquer sur le bouton "connexion" pour s authentifier

apres la reussite de la connection des clients au server, la fenetre ClientRegister se ferme et la fenetre MyClient s'ouvre pour que les clients puissent discuter entre eux

![Screenshot 1_13_2023 1_50_25 AM](https://user-images.githubusercontent.com/104314459/212221365-1b175994-6bb6-4708-895c-246853bdbec7.png)



NB: N oublier d ajouter les librairie suivante:
- mysql-connector-java-8.0.27-bin.jar // pour la connection avec la base de donnees
