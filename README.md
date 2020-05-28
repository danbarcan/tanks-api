# tanks-api

A very simple tank battle simulator API developed using Java, Spring Boot, JPA and multithreading with ExecutorService, Callable and Future.

With this API you can see the tanks stored in DB, simulate battle between two tanks one time or multiple times using multithreadin an seeing the simulation for a gameId.
For each simulation you get the response as a Game entity containing the tanks, the map with the obstacles, the list of rounds played with details for each round (tank position, orientation, hp) and the winner.

Deployed at http://142.93.173.130:5000

http://142.93.173.130:5000/tanks : see the list and details of all tanks

http://142.93.173.130:5000/tanks/battles/{tank1}/{tank2} : Simulate the battle between selected tanks. Tank1 and tank2 are the ids of the tanks. 

http://142.93.173.130:5000/tanks/battles/{tank1}/{tank2}/{noOfBattles} : Simulate battle between two tanks multiple times. NoOfBatlles is the number of simulations the will run in parallel.

http://142.93.173.130:5000/tanks/battles//tanks/battles/{battleId} : Review a previous simulation. BattleId is the is of a simulation you want to review.
